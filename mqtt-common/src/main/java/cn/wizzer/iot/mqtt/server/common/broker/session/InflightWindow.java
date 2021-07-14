package cn.wizzer.iot.mqtt.server.common.broker.session;

import io.netty.handler.codec.mqtt.MqttMessageType;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author yp
 * @date 2021/7/13
 * @since 1.0.0
 * 飞行队列： 用于保证qos1,qos2 进行消息下发
 *
 */
public class InflightWindow {

    /**
     * qos1与qos2的并发飞行队列
     */
    private ConcurrentHashMap<String, FlightMessage> qosMessageMap;

    /**
     * 飞行队列最大长度：飞行队列长度大于1时，意味着这个队列中的数据是并发的，无序的。
     * 这样做的目的是允许小范围的顺序错乱，客户端可以进行修正。
     * 加快下发速率，无需等上一条完全通过，后面一条可以提前下发。
     * 这样做的目的是为了避免某一条下发失败导致其他都下发失败。
     *
     * maxFlightCount=1，针对qos1和qos2 顺序执行
     */
    private Integer maxFlightCount = 1;

    /**
     * 创建时间
     */
    private long createTime;



    /**
     * 当进行重连时，进行飞行队列部分信息重置
     */
    public void reset(){
        // 重置重试次数，主要是防止在客户端断开连接后，服务器推送失败问题。
        qosMessageMap.forEach((messageId,message) -> message.setAlreadySendCount(0));
    }

    public InflightWindow(){
        this.qosMessageMap = new ConcurrentHashMap<>(maxFlightCount);
        this.createTime = System.currentTimeMillis();
    }

    public InflightWindow(Integer maxFlightCount){
        this.maxFlightCount = maxFlightCount;
        this.qosMessageMap = new ConcurrentHashMap<>(maxFlightCount);
        this.createTime = System.currentTimeMillis();
    }

    public boolean add(FlightMessage flightMessage){
        if(qosMessageMap.size()>maxFlightCount){
            return false;
        }
        qosMessageMap.put(flightMessage.getMessageId(),flightMessage);
        return true;
    }

    public boolean isFull(){
        return qosMessageMap.size()>=maxFlightCount;
    }

    public boolean isEmpty(){
        return qosMessageMap.size() == 0;
    }

    public void delete(String messageId){
        qosMessageMap.remove(messageId);
    }

    public boolean update(String messageId,String commend){
        FlightMessage flightMessage = qosMessageMap.get(messageId);
        if(flightMessage == null){
            return true;
        }
        flightMessage.setQosStatus(MqttMessageType.valueOf(commend));
        return true;
    }
}
