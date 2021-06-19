/**
 * Copyright (c) 2018, Mr.Wang (recallcode@aliyun.com) All rights reserved.
 */

package cn.wizzer.iot.mqtt.server.auth.service;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.asymmetric.KeyType;
import cn.hutool.crypto.asymmetric.RSA;
import cn.wizzer.iot.mqtt.server.auth.model.EmqAccessEnum;
import cn.wizzer.iot.mqtt.server.common.auth.IAuthService;
import cn.wizzer.iot.mqtt.server.common.constant.MagicValuePool;
import org.nutz.integration.jedis.RedisService;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.json.Json;

import java.security.interfaces.RSAPrivateKey;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 用户名和密码认证服务
 */
@IocBean(create = "init")
public class AuthService implements IAuthService {

	private RSAPrivateKey privateKey;

	@Inject
	private RedisService redisService;

	@Inject("java:$conf.get('redis.domain')")
	private String redisDomain;

	/**
	 * 在用户session中token存储的字段名
	 */
	private static final String REDIS_TOKEN = "token";

	/**
	 * 用户会话消息redis KEY
	 */
	public static final String USER_SESSION = "USER_SESSION";
	/**
	 * 权限消息redis key
	 */
	public static final String EMQ_ACL = "mqtt_acl:";

	@Override
	public boolean checkValid(String username, String password) {
		System.out.println(redisDomain);
		if (StrUtil.isBlank(username)) return false;
		if (StrUtil.isBlank(password)) return false;
		RSA rsa = new RSA(privateKey, null);
		String value = rsa.encryptBcd(username, KeyType.PrivateKey);
		return value.equals(password) ? true : false;
	}

	@Override
	public boolean clientAuthentication(String clientId, String token) {
		if(StrUtil.isEmpty(clientId)){
			return false;
		}
		if(StrUtil.isEmpty(token)){
			return false;
		}
		String[] clientIdArr = clientId.split(MagicValuePool.UNDERSCORE);
		String userId = clientIdArr[0];
		String deviceId = clientIdArr[1];
		String userSessionStr = redisService.hget(getDynamicKey(userId), deviceId);
		if(StrUtil.isEmpty(userSessionStr)){
			return false;
		}
		Map<String, String> userSessionMap = Json.fromJsonAsMap(String.class, userSessionStr);
		if(userSessionMap.containsKey(REDIS_TOKEN)){
			return token.equals(userSessionMap.get(REDIS_TOKEN));
		}
		return false;
	}

	private String getDynamicKey(String dynamicKey) {
		StringBuilder dynamicKeySb = new StringBuilder();
		dynamicKeySb.append(redisDomain).append("_").append(AuthService.USER_SESSION);
		if (StrUtil.isNotEmpty(dynamicKey)) {
			dynamicKeySb.append("_").append(dynamicKey);
		}
		return dynamicKeySb.toString();
	}

	@Override
	public List<String> topicBatchSubscribeAuthorization(String clientId,List<String> topics) {
		if(StrUtil.isEmpty(clientId)){
			return topics;
		}
		if(topics == null){
			return null;
		}
		String[] clientIdArr = clientId.split(MagicValuePool.UNDERSCORE);
		String userId = clientIdArr[0];
		String dynamicKey = EMQ_ACL + userId;
		Map<String,String> aclMap = redisService.hgetAll(dynamicKey);
		List<String> unAuthorizationTopicList = new ArrayList<>();
		for (String topic : topics) {
			String topAcl = aclMap.get(topic);
			if(StrUtil.isEmpty(topAcl)){
				unAuthorizationTopicList.add(topic);
				continue;
			}
			Integer currentAccess = Integer.valueOf(topAcl);
			if ( currentAccess.equals(EmqAccessEnum.BOTH.getKey()) || currentAccess.equals(EmqAccessEnum.SUBSCRIBE.getKey())) {
				continue;
			}
			unAuthorizationTopicList.add(topic);
		}
		return unAuthorizationTopicList;
	}

	@Override
	public boolean topicPublishAuthorization(String clientId,String topic) {
		if(StrUtil.isEmpty(clientId)){
			return false;
		}
		if(StrUtil.isEmpty(topic)){
			return false;
		}
		String[] clientIdArr = clientId.split(MagicValuePool.UNDERSCORE);
		String userId = clientIdArr[0];
		String dynamicKey = EMQ_ACL + userId;
		String topicAcl = redisService.hget(dynamicKey, topic);
		if(StrUtil.isEmpty(topicAcl)){
			return false;
		}
		Integer currentAccess = Integer.valueOf(topicAcl);
		return currentAccess.equals(EmqAccessEnum.BOTH.getKey()) || currentAccess.equals(EmqAccessEnum.PUBLISH.getKey());
	}

	public void init() {
		privateKey = IoUtil.readObj(AuthService.class.getClassLoader().getResourceAsStream("keystore/auth-private.key"));
	}

}
