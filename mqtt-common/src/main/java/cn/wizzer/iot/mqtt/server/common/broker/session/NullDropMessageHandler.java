package cn.wizzer.iot.mqtt.server.common.broker.session;

/**
 * @author yp
 * @date 2021/7/14
 * @since 1.0.0
 * 啥也不做的数据丢弃处理器
 */
public class NullDropMessageHandler implements DropMessageHandler {
    @Override
    public void handle(PublishMessage message) {

    }
}
