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
    private Integer qos;
    private Boolean retain;
    private Boolean dup;
    //是否为 base64
    private String encoding;

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

    public Integer getQos() {
        return qos;
    }

    public void setQos(Integer qos) {
        this.qos = qos;
    }

    public Boolean getRetain() {
        return retain;
    }

    public void setRetain(Boolean retain) {
        this.retain = retain;
    }

    public Boolean getDup() {
        return dup;
    }

    public void setDup(Boolean dup) {
        this.dup = dup;
    }

    public String getEncoding() {
        return encoding;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }
}
