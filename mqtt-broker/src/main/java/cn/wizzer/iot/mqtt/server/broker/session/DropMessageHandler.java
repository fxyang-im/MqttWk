package cn.wizzer.iot.mqtt.server.broker.session;

/**
 * @author yp
 * @date 2021/7/14
 * @since 1.0.0
 *  被丢弃消息处理器
 */
public interface DropMessageHandler {

    /**
     * 处理消息丢弃
     * @param message 被丢弃的消息
     */
    void handle(PublishMessage message);
}
