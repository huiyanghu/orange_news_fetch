package com.it7890.orange.entity;

/**
 * Created by Astro on 17/5/16.
 */
public class GrabListRuleEntity {

	private String objectId;
	private String ruleName;     //规则名称
	private String publicationId;//媒体id
	private String nodeId;      //节点id
	private String channelId;   //渠道id
	private String cssPath;     //目标区域规则
	private String siteUrl;     //源url
	private String countryCode; //国家编码
	private String languageId;  //语言id
	private String topicId;     //话题id
	private String findPre;     //目标文章url规则
	private String constant;    //编码
	private String keywords;    //关键字
	private String titlePicCssPath;//标题图片规则
	private String nextPageCssPath;//下一页规则
	private int grabTime;       //抓取周期秒

	public String getObjectId() {
		return objectId;
	}

	public void setObjectId(String objectId) {
		this.objectId = objectId;
	}

	public String getRuleName() {
		return ruleName;
	}

	public void setRuleName(String ruleName) {
		this.ruleName = ruleName;
	}

	public String getNodeId() {
		return nodeId;
	}

	public void setNodeId(String nodeId) {
		this.nodeId = nodeId;
	}

	public String getChannelId() {
		return channelId;
	}

	public void setChannelId(String channelId) {
		this.channelId = channelId;
	}

	public String getCssPath() {
		return cssPath;
	}

	public void setCssPath(String cssPath) {
		this.cssPath = cssPath;
	}

	public String getSiteUrl() {
		return siteUrl;
	}

	public void setSiteUrl(String siteUrl) {
		this.siteUrl = siteUrl;
	}

	public String getCountryCode() {
		return countryCode;
	}

	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

	public String getLanguageId() {
		return languageId;
	}

	public void setLanguageId(String languageId) {
		this.languageId = languageId;
	}

	public int getGrabTime() {
		return grabTime;
	}

	public void setGrabTime(int grabTime) {
		this.grabTime = grabTime;
	}

	public String getTopicId() {
		return topicId;
	}

	public void setTopicId(String topicId) {
		this.topicId = topicId;
	}

	public String getFindPre() {
		return findPre;
	}

	public void setFindPre(String findPre) {
		this.findPre = findPre;
	}

	public String getConstant() {
		return constant;
	}

	public void setConstant(String constant) {
		this.constant = constant;
	}

	public String getPublicationId() {
		return publicationId;
	}

	public void setPublicationId(String publicationId) {
		this.publicationId = publicationId;
	}

	public String getKeywords() {
		return keywords;
	}

	public void setKeywords(String keywords) {
		this.keywords = keywords;
	}

	public String getTitlePicCssPath() {
		return titlePicCssPath;
	}

	public void setTitlePicCssPath(String titlePicCssPath) {
		this.titlePicCssPath = titlePicCssPath;
	}

	public String getNextPageCssPath() {
		return nextPageCssPath;
	}

	public void setNextPageCssPath(String nextPageCssPath) {
		this.nextPageCssPath = nextPageCssPath;
	}
}
