package cn.wizzer.iot.mqtt.server.broker.webapi.param;

import io.netty.handler.codec.mqtt.MqttTopicSubscription;

import java.util.List;

/**
 * BatchSubscribe
 *
 * @author CDz
 * @date 2021/6/10 15:59
 * @since 1.0.0
 */
public class BatchSubscribeParam {
    private String clientId;
    private List<MqttTopicSubscriptionParam> topicSubscriptions;

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public List<MqttTopicSubscriptionParam> getTopicSubscriptions() {
        return topicSubscriptions;
    }

    public void setTopicSubscriptions(List<MqttTopicSubscriptionParam> topicSubscriptions) {
        this.topicSubscriptions = topicSubscriptions;
    }
}
