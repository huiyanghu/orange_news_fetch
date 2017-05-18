package com.it7890.orange.dto;

import java.io.Serializable;

public class QiNiuNotifyDTO implements Serializable {

	private static final long serialVersionUID = 7294109137097993688L;
	
	private String persistentId;
	private int code;
	
	public String getPersistentId() {
		return persistentId;
	}
	public void setPersistentId(String persistentId) {
		this.persistentId = persistentId;
	}
	public int getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = code;
	}
}
