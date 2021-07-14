package cn.wizzer.iot.mqtt.server.common.broker.session;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author yp
 * @date 2021/7/14
 * @since 1.0.0
 *  每个会话的消息队列
 */
public class MessageQueue {

    private volatile AtomicInteger queueLen = new AtomicInteger(0);
    /**
     * 待发送的消息队列
     */
    private ConcurrentLinkedQueue<PublishMessage> messageQueue;

    private int maxQueueLen = 100;

    private volatile AtomicInteger dropMessageCount = new AtomicInteger(0);

    private DropMessageHandler dropMessageHandler;


    public MessageQueue(Integer maxQueueLen,DropMessageHandler dropMessageHandler){
        this.messageQueue = new ConcurrentLinkedQueue<>();
        this.maxQueueLen = maxQueueLen;
        this.dropMessageHandler = dropMessageHandler;
    }

    public void add(PublishMessage message){
        if(queueLen.get()>=maxQueueLen){
            // 丢弃最早的消息
            PublishMessage poll = messageQueue.poll();
            dropMessageCount.incrementAndGet();
            // todo 丢弃到kafka，离线消息处理
            this.dropMessageHandler.handle(poll);
        }
        messageQueue.add(message);
    }

    public PublishMessage poll(){
        PublishMessage poll = this.messageQueue.poll();
        if(poll == null){
            return null;
        }
        this.queueLen.decrementAndGet();
        return poll;
    }

    public int size(){
        return this.queueLen.get();
    }


    public boolean isEmpty() {
        return this.queueLen.get() == 0;
    }
}
