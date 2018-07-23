package cn.binarywang.wx.miniapp.config;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * 
 * <p>
 * Project Name: Qinwell Saas
 * <br>
 * Description: 微信小程序Redis配置
 * <br>
 * File Name: WxMaInRedisConfig.java
 * <br>
 * Copyright: Copyright (C) 2015 All Rights Reserved.
 * <br>
 * Company: 杭州勤淮科技有限公司
 * <br>
 * @author 穷奇
 * @create time：2018年7月23日 下午1:45:39 
 * @version: v1.0
 *
 */
public class WxMaInRedisConfig extends WxMaInMemoryConfig {

	private final static String ACCESS_TOKEN_KEY = "wechat_xcx_access_token_";

	protected final JedisPool jedisPool;

	private String accessTokenKey;

	public WxMaInRedisConfig(JedisPool jedisPool) {
		this.jedisPool = jedisPool;
	}
	
	@Override
	public void setAppid(String appid) {
		super.setAppid(appid);
		this.accessTokenKey = ACCESS_TOKEN_KEY.concat(appid);
	}

	@Override
	public String getAccessToken() {
		try (Jedis jedis = this.jedisPool.getResource()) {
			return jedis.get(this.accessTokenKey);
		}
	}

	@Override
	public boolean isAccessTokenExpired() {
		try (Jedis jedis = this.jedisPool.getResource()) {
			return jedis.ttl(accessTokenKey) < 2;
		}
	}

	@Override
	public synchronized void updateAccessToken(String accessToken, int expiresInSeconds) {
		try (Jedis jedis = this.jedisPool.getResource()) {
			jedis.setex(this.accessTokenKey, expiresInSeconds - 200, accessToken);
		}
	}

	@Override
	public void expireAccessToken() {
		try (Jedis jedis = this.jedisPool.getResource()) {
			jedis.expire(this.accessTokenKey, 0);
		}
	}

}
