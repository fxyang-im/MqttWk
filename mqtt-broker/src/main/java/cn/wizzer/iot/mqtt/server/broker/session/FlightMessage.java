package cn.wizzer.iot.mqtt.server.broker.session;

import io.netty.handler.codec.mqtt.MqttMessageType;

/**
 * @author yp
 * @date 2021/7/13
 * @since 1.0.0
 * 客户端响应消息
 */
public class FlightMessage {

    /**
     * 客户端ID
     */
    private String clientId;

    /**
     * 主题
     */
    private String topic;

    /**
     * mqtt消息级别
     */
    private int mqttQoS;

    /**
     * 消息ID
     */
    private String messageId;
    /**
     * 消息体
     */
    private byte[] messageBytes;

    /**
     * 消息状态
     */
    private MqttMessageType qosStatus;

    /**
     * 创建时间
     */
    private long createTime;

    /**
     * 上次发送时间
     */
    private long lastSendTime;

    /**
     * 已经发送的次数
     */
    private int alreadySendCount;

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public long getLastSendTime() {
        return lastSendTime;
    }

    public void setLastSendTime(long lastSendTime) {
        this.lastSendTime = lastSendTime;
    }

    public int getAlreadySendCount() {
        return alreadySendCount;
    }

    public void setAlreadySendCount(int alreadySendCount) {
        this.alreadySendCount = alreadySendCount;
    }

    public String getClientId() {
        return clientId;
    }

    public String getTopic() {
        return topic;
    }

    public int getMqttQoS() {
        return mqttQoS;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public void setMqttQoS(int mqttQoS) {
        this.mqttQoS = mqttQoS;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public void setMessageBytes(byte[] messageBytes) {
        this.messageBytes = messageBytes;
    }

    public void setQosStatus(MqttMessageType qosStatus) {
        this.qosStatus = qosStatus;
    }

    public byte[] getMessageBytes() {
        return messageBytes;
    }

    public MqttMessageType getQosStatus() {
        return qosStatus;
    }
}
