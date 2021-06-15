/**
 * Copyright (c) 2018, Mr.Wang (recallcode@aliyun.com) All rights reserved.
 */

package cn.wizzer.iot.mqtt.server.broker.protocol;

import cn.wizzer.iot.mqtt.server.broker.config.BrokerProperties;
import cn.wizzer.iot.mqtt.server.common.message.IDupPublishMessageStoreService;
import cn.wizzer.iot.mqtt.server.common.message.IMessageIdService;
import cn.wizzer.iot.mqtt.server.common.session.ISessionStoreService;
import cn.wizzer.iot.mqtt.server.common.session.SessionStore;
import io.netty.channel.Channel;
import io.netty.channel.ChannelId;
import io.netty.handler.codec.mqtt.MqttMessageIdVariableHeader;
import io.netty.util.AttributeKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * PUBACK连接处理
 */
public class PubAck {

    private static final Logger LOGGER = LoggerFactory.getLogger(PubAck.class);

    private IDupPublishMessageStoreService dupPublishMessageStoreService;
    private ISessionStoreService sessionStoreService;
    private Map<String, ChannelId> channelIdMap;
    private BrokerProperties brokerProperties;

    public PubAck(IDupPublishMessageStoreService dupPublishMessageStoreService, BrokerProperties brokerProperties, ISessionStoreService sessionStoreService, Map<String, ChannelId> channelIdMap) {
        this.dupPublishMessageStoreService = dupPublishMessageStoreService;
        this.sessionStoreService = sessionStoreService;
        this.channelIdMap = channelIdMap;
        this.brokerProperties = brokerProperties;
    }

    public void processPubAck(Channel channel, MqttMessageIdVariableHeader variableHeader) {
        int messageId = variableHeader.messageId();
        String clientId = (String) channel.attr(AttributeKey.valueOf("clientId")).get();
        if (sessionStoreService.containsKey(clientId)) {
            SessionStore sessionStore = sessionStoreService.get(clientId);
            ChannelId channelId = channelIdMap.get(sessionStore.getBrokerId() + "_" + sessionStore.getChannelId());
            if (brokerProperties.getId().equals(sessionStore.getBrokerId()) && channelId != null) {
                sessionStoreService.expire(clientId, sessionStore.getExpire());
            }
        }
        LOGGER.debug("PUBACK - clientId: {}, messageId: {}", (String) channel.attr(AttributeKey.valueOf("clientId")).get(), messageId);
        dupPublishMessageStoreService.remove((String) channel.attr(AttributeKey.valueOf("clientId")).get(), messageId);
    }
}
