package cn.wizzer.iot.mqtt.server.common.broker.session;

/**
 * @author yp
 * @date 2021/7/27
 * @since 1.0.0
 *
 */
public interface DeliverContextBuildStrategy {

    /**
     * 基于不同的策略构建不同的投递上下文
     * @param deliverConfig 消息投递配置
     * @return 消息分发上下文
     */
    MessageDeliverContext build(DeliverConfig deliverConfig);

}
