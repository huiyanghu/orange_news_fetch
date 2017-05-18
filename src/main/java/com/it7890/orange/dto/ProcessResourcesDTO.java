package com.it7890.orange.dto;

import java.io.Serializable;

/**
 * 资源处理dto
 *
 */
public class ProcessResourcesDTO implements Serializable {

	private static final long serialVersionUID = 5825968361888019575L;
	
	private int status;	//处理资源状态	0待处理	1已处理
	private String localPath;	//本地资源
	
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public String getLocalPath() {
		return localPath;
	}
	public void setLocalPath(String localPath) {
		this.localPath = localPath;
	}
	
	public ProcessResourcesDTO(String localPath) {
		super();
		this.localPath = localPath;
	}
	public ProcessResourcesDTO(int status, String localPath) {
		super();
		this.status = status;
		this.localPath = localPath;
	}
}
