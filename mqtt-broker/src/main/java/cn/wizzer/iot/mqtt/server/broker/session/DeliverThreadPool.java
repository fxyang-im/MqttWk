package cn.wizzer.iot.mqtt.server.broker.session;

import io.netty.util.concurrent.DefaultThreadFactory;
import io.netty.util.internal.ConcurrentSet;
import org.apache.commons.lang3.StringUtils;

import java.util.concurrent.*;

/**
 * @author yp
 * @date 2021/7/27
 * @since 1.0.0
 * 投递线程池
 */
public class DeliverThreadPool {

    /**
     * 正在投递的设备唯一标识
     */
    public static ConcurrentSet<String> ACTIVE_DELIVER_CLIENT_ID = new ConcurrentSet<>();

    public static ExecutorService threadPool = getThreadPool();

    public static ExecutorService getThreadPool(){
        // 默认容量为最大值
        BlockingQueue<Runnable> blockingDeque = new LinkedBlockingQueue<>();
        ThreadFactory threadFactory = new DefaultThreadFactory("message-deliver-thread-");

        return new ThreadPoolExecutor(5,20,1, TimeUnit.MINUTES,blockingDeque,threadFactory,new ThreadPoolExecutor.AbortPolicy());
    }

    public static void execute(String needToDeliverClientId,Runnable runnable){
        if(StringUtils.isEmpty(needToDeliverClientId)){
            return;
        }
        if(ACTIVE_DELIVER_CLIENT_ID.contains(needToDeliverClientId)){
            // 成功消费
            return;
        }
        threadPool.execute(runnable);

    }
}
