package cn.wizzer.iot.mqtt.server.auth.model;


/**
 * @author yp
 * @date 2020/3/28
 * @since 1.0.0
 * 性别枚举
 */

public enum EmqAccessEnum {
    /**
     * acl权限集合
     */
    NONE(0,"无权限"),
    SUBSCRIBE(1,"订阅"),
    PUBLISH(2,"发布"),
    BOTH(3,"发布与订阅");

    EmqAccessEnum(Integer key, String value){
        this.key = key;
        this.value = value;
    }

    private final Integer key;

    private final String value;

    public Integer getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }
}
