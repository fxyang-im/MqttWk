package cn.wizzer.iot.mqtt.server.auth.model;

/**
 * @author yp
 * @date 2021/6/15
 * @since 1.0.0
 * 客户端信息
 */

public class ClientIdInfo {

    private String userId;

    private String deviceId;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }
}
