/**
 * Copyright (c) 2018, Mr.Wang (recallcode@aliyun.com) All rights reserved.
 */

package cn.wizzer.iot.mqtt.server.common.auth;

import java.util.List;

/**
 * 用户和密码认证服务接口
 */
public interface IAuthService {

	/**
	 * 验证用户名和密码是否正确
	 */
	boolean checkValid(String username, String password);

	/**
	 * 客户端登录认证
	 * @param clientId 客户端id
	 * @param token 登录秘钥
	 * @return 是否认证通过
	 */
	boolean clientAuthentication(String clientId,String token);

	/**
	 * 批量订阅鉴权
	 * @param topics 订阅主题
	 * @param clientId 客户端id
	 * @return 订阅失败的topic
	 */
	List<String> topicBatchSubscribeAuthorization(String clientId,List<String> topics);

	/**
	 * 但topic发布授权
	 * @param topic 发布主题
	 * @param clientId 客户端id
	 * @return 鉴权是否通过
	 */
	boolean topicPublishAuthorization(String clientId,String topic);
}
