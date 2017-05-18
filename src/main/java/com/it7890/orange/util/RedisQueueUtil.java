package com.it7890.orange.util;

import java.io.Serializable;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

@Component
public class RedisQueueUtil {
	
	@Resource
	private JedisUtil jedisUtil;
	
	/**
	 * 插入列表头部，并返回队列list的size  模拟入队列
	 * @param key
	 * @param value
	 * @return
	 */
	public Long put(String key, Object value) {
		return jedisUtil.lpush(key, (Serializable) value);
	}
	
	/**
	 * 返回列表尾部的元素，并把其移除	模拟出队列
	 * @param key
	 * @return
	 */
	public Object take(String key) {
		return jedisUtil.rpop(key);
	}
}
