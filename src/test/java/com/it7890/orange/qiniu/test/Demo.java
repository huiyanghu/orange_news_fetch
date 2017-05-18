package com.it7890.orange.qiniu.test;

import com.it7890.orange.entity.Topic;
import com.it7890.orange.entity.TopicFetch;
import com.it7890.orange.util.StringUtil;

public class Demo {

	public static void main(String[] args) {
		TopicFetch topicFetch = new TopicFetch();
		topicFetch.setId(101l);
		topicFetch.setTitle("测试内容");
		Topic topic = new Topic();
		
		StringUtil.beanCopy(topicFetch, topic, true);
		
		System.out.println(topic.getTitle());
	}
}
