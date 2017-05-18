package com.cki.spider.pro;

import java.io.Serializable;

public class SpiderProxy implements Serializable {

	private static final long serialVersionUID = 2373198098902217490L;

	private String host;

	private int port;

	private String userName;

	private String passwd;

	public SpiderProxy(String host, int port) {
		this(host, port, null, null);
	}

	public SpiderProxy(String host, int port, String userName, String passwd) {

		this.host = host;
		this.port = port;
		this.userName = userName;
		this.passwd = passwd;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPasswd() {
		return passwd;
	}

	public void setPasswd(String passwd) {
		this.passwd = passwd;
	}

	public String toString() {
		return "Proxy[" + this.host + ":" + this.port + "]";
	}

}
