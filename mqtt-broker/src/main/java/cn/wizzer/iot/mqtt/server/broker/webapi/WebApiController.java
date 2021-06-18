package cn.wizzer.iot.mqtt.server.broker.webapi;

import cn.wizzer.iot.mqtt.server.broker.cluster.RedisCluster;
import cn.wizzer.iot.mqtt.server.broker.config.BrokerProperties;
import cn.wizzer.iot.mqtt.server.broker.internal.InternalMessage;
import cn.wizzer.iot.mqtt.server.broker.internal.InternalSendServer;
import cn.wizzer.iot.mqtt.server.broker.protocol.ProtocolProcess;
import cn.wizzer.iot.mqtt.server.broker.webapi.param.BatchSubscribeParam;
import cn.wizzer.iot.mqtt.server.broker.webapi.param.MqttTopicSubscriptionParam;
import cn.wizzer.iot.mqtt.server.common.session.SessionStore;
import cn.wizzer.iot.mqtt.server.common.subscribe.SubscribeStore;
import cn.wizzer.iot.mqtt.server.store.session.SessionStoreService;
import com.alibaba.fastjson.JSONObject;
import io.netty.channel.Channel;
import io.netty.channel.ChannelId;
import io.netty.channel.group.ChannelGroup;
import io.netty.handler.codec.mqtt.MqttQoS;
import org.nutz.http.Request;
import org.nutz.http.Response;
import org.nutz.http.Sender;
import org.nutz.integration.jedis.JedisAgent;
import org.nutz.integration.jedis.RedisService;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.json.Json;
import org.nutz.lang.Lang;
import org.nutz.lang.Streams;
import org.nutz.lang.random.R;
import org.nutz.lang.util.NutMap;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.adaptor.JsonAdaptor;
import org.nutz.mvc.annotation.*;
import redis.clients.jedis.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by wizzer on 2019/5/24
 * <p>
 * V1版本只考虑单机版本
 */
@IocBean
@At("/open/api/mqttwk")
public class WebApiController {
    private static final Log log = Logs.get();
    private static final String CACHE_SESSION_PRE = "mqttwk:session:";
    private static final String CACHE_CLIENT_PRE = "mqttwk:client:";
    private final static String SUBNOTWILDCARD_CACHE_PRE = "mqttwk:subnotwildcard:";
    @Inject
    private RedisService redisService;
    @Inject
    private InternalSendServer internalSendServer;
    @Inject
    private BrokerProperties brokerProperties;
    @Inject
    private RedisCluster redisCluster;
    @Inject
    private JedisAgent jedisAgent;
    @Inject
    private ProtocolProcess protocolProcess;
    @Inject
    private ChannelGroup channelGroup;
    @Inject
    private Map<String, ChannelId> channelIdMap;
    @Inject
    private SessionStoreService sessionStoreService;

    /**
     * 向设备发送数据,发送格式见 test_send 方法实例代码
     *
     * @param data
     * @return
     */
    @At("/send")
    @Ok("json")
    @AdaptBy(type = JsonAdaptor.class)
    public ResponseResult send(NutMap data) {
        try {
            String processId = Lang.JdkTool.getProcessId("0");
            InternalMessage message = new InternalMessage();
            message.setBrokerId(brokerProperties.getId());
            message.setProcessId(processId);
            message.setClientId(R.UU32());
            message.setTopic(data.getString("topic", ""));
            message.setRetain(data.getBoolean("retain"));
            message.setDup(data.getBoolean("dup"));
            message.setMqttQoS(data.getInt("qos"));
            message.setMessageBytes(data.getString("message", "").getBytes());
            log.debug("send:::" + Json.toJson(message));
            //如果开启集群功能
            if (brokerProperties.getClusterEnabled()) {
                redisCluster.sendMessage(message);
            } else {
                internalSendServer.sendPublishMessage(message.getClientId(), message.getTopic(), MqttQoS.valueOf(message.getMqttQoS()), message.getMessageBytes(), message.isRetain(), message.isDup());
            }
            return ResponseResult.genSuccessResult();
        } catch (Exception e) {
            log.error(e);
            return ResponseResult.genErrorResult(e.getMessage());
        }
    }

    /**
     * 踢下线client
     *
     * @param clientId
     * @return
     */
    @POST
    @At(value = "/clients/kick")
    @Ok("json")
    public ResponseResult kickClient(String clientId) {
        SessionStore sessionStore = sessionStoreService.get(clientId);
        ChannelId channelId = channelIdMap.get(sessionStore.getBrokerId() + "_" + sessionStore.getChannelId());
        if (channelId != null) {
            Channel channel = channelGroup.find(channelId);
            protocolProcess.disConnect().processDisConnect(channel, null);
            //发送给内部 broker消息
        } else {
            InternalMessage message = new InternalMessage();
            String processId = Lang.JdkTool.getProcessId("0");
            message.setBrokerId(brokerProperties.getId());
            message.setProcessId(processId);
            message.setClientId(R.UU32());
            message.setTopic("");
            message.setRetain(false);
            message.setDup(false);
            message.setMqttQoS(1);
            message.setMessageBytes("".getBytes());
            message.setKick(true);
            redisCluster.sendMessage(message);
        }
        return ResponseResult.genSuccessResult();
    }

    /**
     * 批量订阅
     *
     * @param batchSubscribeParam
     * @return
     */
    @POST
    @At(value = "/clients/batch-subscribe")
    @Ok("json")
    @AdaptBy(type = JsonAdaptor.class)
    public ResponseResult batchSubscribe(BatchSubscribeParam batchSubscribeParam) {
        List<MqttTopicSubscriptionParam> topicSubscriptions = batchSubscribeParam.getTopicSubscriptions();
        List<SubscribeStore> subscribeStoreList = topicSubscriptions.stream().map(e -> new SubscribeStore(e.getClientId(), e.getTopicName(), e.getQos())).collect(Collectors.toList());
        subscribeStoreList.forEach(subscribeStore -> {
                    String topicFilter = subscribeStore.getTopicFilter();
                    String clientId = subscribeStore.getClientId();
                    redisService.hset(SUBNOTWILDCARD_CACHE_PRE + topicFilter, clientId, JSONObject.toJSONString(subscribeStore));
                    redisService.sadd(CACHE_CLIENT_PRE + clientId, topicFilter);
                }
        );
        return ResponseResult.genSuccessResult();
    }

    /**
     * 批量取消订阅
     *
     * @param batchSubscribeParam
     * @return
     */
    @POST
    @At(value = "/clients/batch-unsubscribe")
    @Ok("json")
    @AdaptBy(type = JsonAdaptor.class)
    public ResponseResult batchUnSubscribe(BatchSubscribeParam batchSubscribeParam) {
        List<MqttTopicSubscriptionParam> topicSubscriptions = batchSubscribeParam.getTopicSubscriptions();
        topicSubscriptions.forEach(e -> {
            String topic = e.getTopicName();
            String clientId = e.getClientId();
            redisService.srem(CACHE_CLIENT_PRE + clientId, topic);
            redisService.hdel(SUBNOTWILDCARD_CACHE_PRE + topic, clientId);
        });
        return ResponseResult.genSuccessResult();
    }

    @At("/test_send")
    @Ok("json")
    public Object test_send() {
        NutMap nutMap = NutMap.NEW();
        try {
            Request req = Request.create("http://127.0.0.1:8922/open/api/mqttwk/send", Request.METHOD.POST);
            NutMap message = NutMap.NEW();
            message.addv("topic", "/topic/mqttwk");
            message.addv("retain", true);
            message.addv("dup", true);
            message.addv("qos", 1);
            message.addv("message", "wizzer");
            req.setData(Json.toJson(message));
            Response resp = Sender.create(req).send();
            if (resp.isOK()) {
                nutMap.put("code", 0);
            }
        } catch (Exception e) {
            log.error(e);
            nutMap.put("code", -1);
        }
        return nutMap;
    }

    /**
     * 获取在线设备数量、客户端名称、订阅主题
     * example: {"code":0,"msg":"","data":{"total":0,"list":[{"clientId":"pc-web","topics":["/topic_back"]}]}}
     */
    @At("/info")
    @Ok("json")
    public Object info() {
        NutMap nutMap = NutMap.NEW();
        try {
            NutMap data = NutMap.NEW();
            ScanParams match = new ScanParams().match(CACHE_SESSION_PRE + "*");
            List<String> keys = new ArrayList<>();
            if (jedisAgent.isClusterMode()) {
                JedisCluster jedisCluster = jedisAgent.getJedisClusterWrapper().getJedisCluster();
                for (JedisPool pool : jedisCluster.getClusterNodes().values()) {
                    try (Jedis jedis = pool.getResource()) {
                        ScanResult<String> scan = null;
                        do {
                            scan = jedis.scan(scan == null ? ScanParams.SCAN_POINTER_START : scan.getStringCursor(), match);
                            keys.addAll(scan.getResult());
                        } while (!scan.isCompleteIteration());
                    }
                }
            } else {
                Jedis jedis = null;
                try {
                    jedis = jedisAgent.jedis();
                    ScanResult<String> scan = null;
                    do {
                        scan = jedis.scan(scan == null ? ScanParams.SCAN_POINTER_START : scan.getStringCursor(), match);
                        keys.addAll(scan.getResult());
                    } while (!scan.isCompleteIteration());
                } finally {
                    Streams.safeClose(jedis);
                }
            }
            List<NutMap> dataList = new ArrayList<>();
            for (String k : keys) {
                dataList.add(NutMap.NEW()
                        .addv("clientId", k.substring(k.lastIndexOf(":") + 1))
                        .addv("topics", redisService.smembers(CACHE_CLIENT_PRE + k.substring(k.lastIndexOf(":") + 1)))
                );
            }
            data.addv("total", keys.size());
            data.addv("list", dataList);
            nutMap.put("code", 0);
            nutMap.put("msg", "");
            nutMap.put("data", data);
        } catch (Exception e) {
            log.error(e);
            nutMap.put("code", -1);
            nutMap.put("msg", e.getMessage());
        }
        return nutMap;
    }
}
