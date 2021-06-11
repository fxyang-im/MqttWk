package cn.wizzer.iot.mqtt.server.broker.webapi.param;

/**
 * MqttTopicSubscriptionParam
 *
 * @author CDz
 * @date 2021/6/10 17:13
 * @since 1.0.0
 */
public class MqttTopicSubscriptionParam {
    private String topicName;
    private int qos;

    public String getTopicName() {
        return topicName;
    }

    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }

    public int getQos() {
        return qos;
    }

    public void setQos(int qos) {
        this.qos = qos;
    }
}
