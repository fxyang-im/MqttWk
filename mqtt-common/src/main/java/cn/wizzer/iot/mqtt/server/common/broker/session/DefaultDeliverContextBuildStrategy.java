package cn.wizzer.iot.mqtt.server.common.broker.session;

/**
 * @author yp
 * @date 2021/7/27
 * @since 1.0.0
 * 基于不同的方式或者不同的存储方式来创建DeliverContext
 */
public class DefaultDeliverContextBuildStrategy implements DeliverContextBuildStrategy {

    MessageDeliverContext messageDeliverContext;

    @Override
    public MessageDeliverContext build(DeliverConfig deliverConfig) {

        if(messageDeliverContext == null){
            synchronized (this){
                if(messageDeliverContext == null){
                    MessagePublishHandler messagePublishHandler = new ThreadPoolMessagePublishHandler();
                    messageDeliverContext = new MessageDeliverContext(messagePublishHandler);
                }
            }
        }
        return messageDeliverContext;

    }
}
