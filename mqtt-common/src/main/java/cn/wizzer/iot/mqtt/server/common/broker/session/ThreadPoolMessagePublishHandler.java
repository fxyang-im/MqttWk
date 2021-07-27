package cn.wizzer.iot.mqtt.server.common.broker.session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;

/**
 * @author yp
 * @date 2021/7/27
 * @since 1.0.0
 * 基于线程池的多线程投递
 */
public class ThreadPoolMessagePublishHandler implements MessagePublishHandler {

    private static final Logger logger = LoggerFactory.getLogger(ThreadPoolMessagePublishHandler.class);

    @Override
    public void handler(String clientId) {
        try {
            DeliverThreadPool.execute(clientId,new MessageDeliverRunnable(clientId));
        }catch (Exception ex){
            logger.warn("线程池队列已满，消息重回等待队列");
            DeliverContextHolder.getDeliverContext().waitForDeliverClientIdQueue.offer(clientId);
        }
    }

    public static  class MessageDeliverRunnable implements Runnable{
        private String clientId;

        public MessageDeliverRunnable(String clientId){
            this.clientId = clientId;
        }

        @Override
        public void run() {
            // 执行真正的消息发送
            ClientSession clientSession = DeliverContextHolder.getDeliverContext().clientSessionMap.get(clientId);
            if(clientSession == null){
                // session已清除
                return;
            }
            while(true){
                // 检查飞行队列，超时消息重新投递
                clientSession.inFlightMessageRedeliver();
                //检查飞行队列状态
                if(clientSession.isInflightFull()){
                    // 飞行队列已满，等待回执
                    DeliverThreadPool.ACTIVE_DELIVER_CLIENT_ID.remove(clientId);
                    DeliverContextHolder.getDeliverContext().waitForDeliverClientIdQueue.offer(clientId);
                    break;
                }
                // 消息投递


            }
        }
    }
}
