package cn.wizzer.iot.mqtt.server.broker.session;

/**
 * @author yp
 * @date 2021/7/14
 * @since 1.0.0 消息投递
 */
public interface MessagePublishHandler{

    /**
     * 将消息进行异步处理
     * @param  clientId 会话
     */
    void handler(String clientId);
}
