package com.cki.spider.pro.cookie;

import java.io.Serializable;

public class Cookie implements Serializable {

	private static final long serialVersionUID = 1344224588348689548L;

	private String domain;

	private long expireTime;

	private long addTime;

	private String name;

	private String path;

	private String value;

	private String comment;

	private boolean secure;

	public Cookie(String name, String value) {

		this.name = name;
		this.value = value;
		this.expireTime = -1;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getName() {
		return name;
	}

	public boolean isSecure() {
		return secure;
	}

	public void setSecure(boolean secure) {
		this.secure = secure;
	}

	public long getExpireTime() {
		return expireTime;
	}

	public void setExpireTime(long expireTime) {
		this.expireTime = expireTime;
	}

	public long getAddTime() {
		return addTime;
	}

	public void setAddTime(long addTime) {
		this.addTime = addTime;
	}

	@Override
	public String toString() {

		String retValue = "Cookie ( " + "domain = '" + this.domain + "expireTime = '" + this.expireTime + "addTime = '" + this.addTime + "name = '" + this.name + "path = '" + this.path + "value = '"
				+ this.value + "comment = '" + this.comment + "secure = '" + this.secure + "' )";

		return retValue;
	}

}
