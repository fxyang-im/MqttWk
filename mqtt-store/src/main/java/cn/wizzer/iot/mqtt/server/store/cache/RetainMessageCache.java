package cn.wizzer.iot.mqtt.server.store.cache;

import cn.wizzer.iot.mqtt.server.common.message.RetainMessageStore;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.nutz.aop.interceptor.async.Async;
import org.nutz.integration.jedis.JedisAgent;
import org.nutz.integration.jedis.RedisService;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Streams;
import redis.clients.jedis.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by wizzer on 2018
 */
@IocBean
public class RetainMessageCache {
    private final static String CACHE_PRE = "mqttwk:retain:";
    @Inject
    private RedisService redisService;
    @Inject
    private PropertiesProxy conf;
    @Inject
    private JedisAgent jedisAgent;

    private Integer cacheNum = 200;

    public RetainMessageStore put(String topic, RetainMessageStore obj) {
//        redisService.set(CACHE_PRE + topic, JSONObject.toJSONString(obj));
        String cacheKey = CACHE_PRE + topic;
        Long llen = redisService.llen(cacheKey);
        if (llen > cacheNum) {
            redisService.ltrim(cacheKey, 0, cacheNum / 2);
        }
        redisService.rpush(cacheKey, JSONObject.toJSONString(obj));
        return obj;
    }

    public List<RetainMessageStore> get(String topic) {
        List<String> lrange = redisService.lrange(CACHE_PRE + topic, 0, -1);
        List<RetainMessageStore> retainMessageStores = new ArrayList<>();
        lrange.forEach(e->retainMessageStores.add(JSON.parseObject(e, RetainMessageStore.class)));
        return retainMessageStores;
//        return JSONObject.parseObject(redisService.get(CACHE_PRE + topic), RetainMessageStore.class);
    }

    public boolean containsKey(String topic) {
        return redisService.exists(CACHE_PRE + topic);
    }

    @Async
    public void remove(String topic) {
        redisService.del(CACHE_PRE + topic);
    }

    public Map<String, List<RetainMessageStore>> all() {
        Map<String, List<RetainMessageStore>> map = new HashMap<>();
        ScanParams match = new ScanParams().match(CACHE_PRE + "*");
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
        for (String key : keys) {
            List<String> lrange = redisService.lrange(key, 0, -1);
            List<RetainMessageStore> retainMessageStores = new ArrayList<>();
            lrange.forEach(e->retainMessageStores.add(JSON.parseObject(e, RetainMessageStore.class)));
            map.put(key.substring(CACHE_PRE.length()), retainMessageStores);
        }
        return map;
    }

    public static void main(String[] args) {
        String s = "[\"{\\\"messageBytes\\\":\\\"ewogICJtc2ciOiAiMSIKfQ==\\\",\\\"mqttQoS\\\":1,\\\"topic\\\":\\\"testtopic\\\"}\",\"{\\\"messageBytes\\\":\\\"ewogICJtc2ciOiAiMSIKfQ==\\\",\\\"mqttQoS\\\":1,\\\"topic\\\":\\\"testtopic\\\"}\",\"{\\\"messageBytes\\\":\\\"ewogICJtc2ciOiAiNSIKfQ==\\\",\\\"mqttQoS\\\":1,\\\"topic\\\":\\\"testtopic\\\"}\",\"{\\\"messageBytes\\\":\\\"ewogICJtc2ciOiAiNCIKfQ==\\\",\\\"mqttQoS\\\":1,\\\"topic\\\":\\\"testtopic\\\"}\",\"{\\\"messageBytes\\\":\\\"ewogICJtc2ciOiAiMyIKfQ==\\\",\\\"mqttQoS\\\":1,\\\"topic\\\":\\\"testtopic\\\"}\",\"{\\\"messageBytes\\\":\\\"ewogICJtc2ciOiAiMiIKfQ==\\\",\\\"mqttQoS\\\":1,\\\"topic\\\":\\\"testtopic\\\"}\",\"{\\\"messageBytes\\\":\\\"ewogICJtc2ciOiAiMSIKfQ==\\\",\\\"mqttQoS\\\":1,\\\"topic\\\":\\\"testtopic\\\"}\",\"{\\\"messageBytes\\\":\\\"ewogICJtc2ciOiAiMSIKfQ==\\\",\\\"mqttQoS\\\":1,\\\"topic\\\":\\\"testtopic\\\"}\",\"{\\\"messageBytes\\\":\\\"ewogICJtc2ciOiAiMSIKfQ==\\\",\\\"mqttQoS\\\":1,\\\"topic\\\":\\\"testtopic\\\"}\"]";
        List<RetainMessageStore> retainMessageStores = JSONArray.parseArray(s, RetainMessageStore.class);
        System.out.println(retainMessageStores);

    }
}
