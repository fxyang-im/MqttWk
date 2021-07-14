package cn.wizzer.iot.mqtt.server.common.broker.session;

import java.util.HashMap;
import java.util.Map;

/**
 * @author yp
 * @date 2021/7/13
 * @since 1.0.0
 */
public class ClientSession {

    /**
     * 订阅表：权限信息不用下沉到session中，在授权层做即可
     */
    private Map<String,Integer> subscribeMap = new HashMap<>();
    /**
     * 最大订阅数
      */
    private int maxSubscribeCount = 1000;

    /**
     * 飞行队列
     */
    private InflightWindow flight;

    /**
     * 飞行队列最大长度 默认为10
     */
    private int maxInflightCount = 10;

    /**
     * 待发送的消息队列
     */
    private MessageQueue messageQueue;

    /**
     * 飞行队列重试周期 5000ms
     */
    private int retryInflightInterval = 5000;

    /**
     * 飞行队列下发重试多次依然失败，则断开连接。让客户端重连
     */
    private int maxRetryInflightNum = 3;

    /**
     * session创建时间
     */
    private long createTime;

    private Integer maxMessageQueueNum = 100;

    private DropMessageHandler dropMessageHandler = new NullDropMessageHandler();

    public ClientSession(Integer maxInflightCount,
                         Integer maxMessageQueueNum,
                         Integer maxSubscribeCount,
                         Integer retryInflightInterval,
                         Integer maxRetryInflightNum,
                         DropMessageHandler dropMessageHandler){
        this.maxInflightCount = maxInflightCount == null?this.maxInflightCount:maxInflightCount;
        this.maxMessageQueueNum = maxMessageQueueNum == null?this.maxMessageQueueNum:maxMessageQueueNum;
        this.maxSubscribeCount = maxSubscribeCount == null?this.maxSubscribeCount:maxSubscribeCount;
        this.retryInflightInterval = retryInflightInterval == null?this.retryInflightInterval:retryInflightInterval;
        this.maxRetryInflightNum = maxRetryInflightNum == null?this.maxRetryInflightNum:maxRetryInflightNum;
        this.dropMessageHandler = dropMessageHandler == null?this.dropMessageHandler:dropMessageHandler;

        this.flight = new InflightWindow(this.maxInflightCount);
        this.messageQueue = new MessageQueue(this.maxInflightCount,this.dropMessageHandler);
    }

    public ClientSession(){
        this.flight = new InflightWindow(maxInflightCount);
        this.messageQueue = new MessageQueue(maxMessageQueueNum,dropMessageHandler);
    }

    public boolean isInflightEmpty(){
        return this.flight.isEmpty();
    }

    public boolean isInflightFull(){
        return this.flight.isFull();
    }

    public boolean isMessageQueueEmpty(){
        return this.messageQueue.isEmpty();
    }

    public void messageDeliver(){

        // 1.检查飞行队列投递情况,超时重新投递

        // 2.检查消息队列投递情况
    }
}
