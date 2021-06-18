package cn.wizzer.iot.mqtt.server.broker.webapi.param;

/**
 * SendMsgParam
 *
 * @author CDz
 * @date 2021/6/18 18:52
 * @since 1.0.0
 */
public class SendMsgParam {
    private String topic;
    private String clientid;
    private String payload;
    private int qos;
    private boolean retain;
    private boolean dup;

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getClientid() {
        return clientid;
    }

    public void setClientid(String clientid) {
        this.clientid = clientid;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    public int getQos() {
        return qos;
    }

    public void setQos(int qos) {
        this.qos = qos;
    }

    public boolean isRetain() {
        return retain;
    }

    public void setRetain(boolean retain) {
        this.retain = retain;
    }

    public boolean isDup() {
        return dup;
    }

    public void setDup(boolean dup) {
        this.dup = dup;
    }
}
