package com.it7890.orange.fetch;

import com.avos.avoscloud.AVOSCloud;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;


public class Bootstrap {
	private static final Logger logger = LoggerFactory.getLogger(Bootstrap.class);

	public static void main(String[] args) {
		// 启用北美节点
		AVOSCloud.useAVCloudUS();
		AVOSCloud.initialize("VV7zErT5UtBfnhkkllg9wboY-MdYXbMMI","q8QNg0P0npQDGWlsQh8HbtgS","sMQk7iWLaVWsVHmWMlhSyP5P");

		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("classpath:spring/*.xml");
		logger.info("toupai-fetch start.");
	}
}
