package com.it7890.orange.dto;

import java.io.Serializable;

public class ProcessResultDTO implements Serializable {

	private static final long serialVersionUID = -5724007322487382492L;

	private String key;	//七牛云文件标识
	private int code = 1;	//状态码，0 表示成功，1 表示等待处理，2 表示正在处理，3 表示处理失败，4 表示回调失败。
	private String persistentId;	//持久化处理会话标识
	private int type;	//图片类型， 11、240图片   12、440图片   13、640图片   2、音频   3、视频
	
	private int isProcess;	//类型	0 需要处理	1不需要处理
	
	public String getPersistentId() {
		return persistentId;
	}
	public void setPersistentId(String persistentId) {
		this.persistentId = persistentId;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}	
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public int getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = code;
	}	
	public int getIsProcess() {
		return isProcess;
	}
	public void setIsProcess(int isProcess) {
		this.isProcess = isProcess;
	}
	
	public ProcessResultDTO() {
		super();
	}
	public ProcessResultDTO(String persistentId, int type, int code, int isProcess) {
		super();
		this.persistentId = persistentId;
		this.type = type;
		this.code = code;
		this.isProcess = isProcess;
	}
	public ProcessResultDTO(String key, String persistentId, int type, int code, int isProcess) {
		super();
		this.key = key;
		this.persistentId = persistentId;
		this.type = type;
		this.code = code;
		this.isProcess = isProcess;
	}
}
