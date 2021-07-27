package cn.wizzer.iot.mqtt.server.broker.session;

import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Constructor;

/**
 * @author yp
 * @date 2021/7/27
 * @since 1.0.0
 */
public class DeliverContextHolder {

    public static final String DEFAULT_STRATEGY = "DEFAULT";

    public static final String SYSTEM_PROPERTY = "deliver.context.strategy";

    private static String strategyName = System.getProperty(SYSTEM_PROPERTY);


    private static DeliverContextBuildStrategy strategy;

    private static DeliverConfig deliverConfig;

    public static MessageDeliverContext getDeliverContext() {
        return strategy.build(deliverConfig);
    }

    public static void initialize() {
        if (StringUtils.isEmpty(strategyName)) {
            strategyName = DEFAULT_STRATEGY;
        }
        if (strategyName.equals(DEFAULT_STRATEGY)) {
            strategy = new DefaultDeliverContextBuildStrategy();
        } else {
            try {
                Class<?> clazz = Class.forName(strategyName);
                Constructor<?> customStrategy = clazz.getConstructor();
                strategy = (DeliverContextBuildStrategy) customStrategy.newInstance();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}
