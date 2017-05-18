package com.it7890.orange.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

//@Entity
//@Table(name="site_url_config")
public class SiteUrlConfig implements Serializable {

	private static final long serialVersionUID = 8000666345918129650L;

	private Long id;
	private String siteName;
	private String siteUrl;
	private int state;	//状态 0禁用 1启用
	private String classifyCode;
	private String classifyName;
	private Long lastCheckTime;
	private int totalCheckCount;
	private String analysisClass;
	private String scheduled;	//触发时间（Scheduled格式）
	private int type;	//类型 0直接抓取 1抓取列表
	
	public final static int DISABLE_STATE = 0;	//禁用状态
	public final static int ACTIVITY_STATE = 1;	//启用状态
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getSiteName() {
		return siteName;
	}
	public void setSiteName(String siteName) {
		this.siteName = siteName;
	}
	public String getSiteUrl() {
		return siteUrl;
	}
	public void setSiteUrl(String siteUrl) {
		this.siteUrl = siteUrl;
	}
	public int getState() {
		return state;
	}
	public void setState(int state) {
		this.state = state;
	}
	public String getClassifyCode() {
		return classifyCode;
	}
	public void setClassifyCode(String classifyCode) {
		this.classifyCode = classifyCode;
	}
	public String getClassifyName() {
		return classifyName;
	}
	public void setClassifyName(String classifyName) {
		this.classifyName = classifyName;
	}
	public Long getLastCheckTime() {
		return lastCheckTime;
	}
	public void setLastCheckTime(Long lastCheckTime) {
		this.lastCheckTime = lastCheckTime;
	}
	public int getTotalCheckCount() {
		return totalCheckCount;
	}
	public void setTotalCheckCount(int totalCheckCount) {
		this.totalCheckCount = totalCheckCount;
	}
	public String getAnalysisClass() {
		return analysisClass;
	}
	public void setAnalysisClass(String analysisClass) {
		this.analysisClass = analysisClass;
	}
	public String getScheduled() {
		return scheduled;
	}
	public void setScheduled(String scheduled) {
		this.scheduled = scheduled;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
}
