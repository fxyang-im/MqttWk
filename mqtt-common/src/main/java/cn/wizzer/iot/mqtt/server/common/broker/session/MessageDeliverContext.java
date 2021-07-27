package cn.wizzer.iot.mqtt.server.common.broker.session;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @author yp
 * @date 2021/7/14
 * @since 1.0.0
 * 异步消息投递的上下文
 * 存储需要进行异步投递的用户id
 */
public class MessageDeliverContext {

    /**
     * 待发送消息用户队列
     */
    public ConcurrentLinkedQueue<String> waitForDeliverClientIdQueue = new ConcurrentLinkedQueue<>();

    /**
     * 当前节点存储的会话列表
     */
    public ConcurrentHashMap<String,ClientSession> clientSessionMap = new ConcurrentHashMap<>();
    /**
     * 消息投递处理器
     */
    public MessagePublishHandler messagePublishHandler;

    public MessageDeliverContext(MessagePublishHandler messagePublishHandler){
        this.messagePublishHandler = messagePublishHandler;
    }

}
