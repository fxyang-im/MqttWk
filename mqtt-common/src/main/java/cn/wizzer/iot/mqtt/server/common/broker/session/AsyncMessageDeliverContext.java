package cn.wizzer.iot.mqtt.server.common.broker.session;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @author yp
 * @date 2021/7/14
 * @since 1.0.0
 * 异步消息投递的上下文
 */
public class AsyncMessageDeliverContext {

    public volatile static ConcurrentHashMap<String,Long> WAIT_FOR_DELIVER = new ConcurrentHashMap<>();
    public volatile static ConcurrentLinkedQueue<String> WAIT_FOR_DELIVER_CHANNEL = new ConcurrentLinkedQueue<>();
}
