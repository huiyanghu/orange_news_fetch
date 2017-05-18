package com.it7890.orange.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

//@Entity
//@Table(name="site_url_item")
public class SiteUrlItem implements Serializable {

	private static final long serialVersionUID = -3414401484102502110L;

	private Long id;
	private int type;		//类型 0详情页 1列表页
	private int status;		//状态 0待抓取 1已抓取
	private Long siteUrlConfigId;	//抓取源id
	private Long itemParentId;		//源上级id
	private String url;		//抓取源
	private String title;	//标题
	private Long createTime;//抓取时间
	private String urlSalt;	//校验唯一项
	private String analysisClass;	//解析类路径
	private String classifyCode;	//分类code
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public Long getSiteUrlConfigId() {
		return siteUrlConfigId;
	}
	public void setSiteUrlConfigId(Long siteUrlConfigId) {
		this.siteUrlConfigId = siteUrlConfigId;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public Long getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Long createTime) {
		this.createTime = createTime;
	}	
	public String getUrlSalt() {
		return urlSalt;
	}
	public void setUrlSalt(String urlSalt) {
		this.urlSalt = urlSalt;
	}
	public String getAnalysisClass() {
		return analysisClass;
	}
	public void setAnalysisClass(String analysisClass) {
		this.analysisClass = analysisClass;
	}
	public Long getItemParentId() {
		return itemParentId;
	}
	public void setItemParentId(Long itemParentId) {
		this.itemParentId = itemParentId;
	}
	public String getClassifyCode() {
		return classifyCode;
	}
	public void setClassifyCode(String classifyCode) {
		this.classifyCode = classifyCode;
	}
}
