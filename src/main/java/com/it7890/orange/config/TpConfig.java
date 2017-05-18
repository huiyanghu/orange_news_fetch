package com.it7890.orange.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class TpConfig {

	@Value("${spider.conn}")
	private int spiderConn;

	public int getSpiderConn() {
		return spiderConn;
	}

	public void setSpiderConn(int spiderConn) {
		this.spiderConn = spiderConn;
	}



}
