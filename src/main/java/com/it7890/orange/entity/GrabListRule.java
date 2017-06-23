package com.it7890.orange.entity;

import com.avos.avoscloud.AVObject;

/**
 * Created by Astro on 17/5/16.
 */
public class GrabListRule {

	private String objectId;
	private AVObject publicationObj;//媒体对象
	private AVObject nodeObj;       //节点对象
	private AVObject channelObj;    //渠道对象
	private AVObject languageObj;   //语言对象
	private AVObject topicObj;      //话题对象

	private String ruleName;      //规则名称
	private String siteUrl;       //源url
	private String countryCode;   //国家编码
	private String findPre;       //目标文章url规则
	private String constant;      //编码
	private String keywords;      //关键字
	private String cssPath;       //目标区域规则
	private String nextPageCssPath;//下一页规则
	private String titlePicCssPath;//标题图片规则
	private int grabTime;         //抓取周期秒

	private int fetchNextPage;     //是否抓取下一页 1是 0否
	private String targetCssPath;  //指定抓取
	private String itemCssPath;    //列表项规则
	private String itemLinkCssPath;//列表项链接规则
	private String itemTitlePicCssPath;//文章标题图片规则

	private GrabDetailRule grabDetailRule;

	public String getObjectId() {
		return objectId;
	}

	public void setObjectId(String objectId) {
		this.objectId = objectId;
	}

	public AVObject getPublicationObj() {
		return publicationObj;
	}

	public void setPublicationObj(AVObject publicationObj) {
		this.publicationObj = publicationObj;
	}

	public AVObject getNodeObj() {
		return nodeObj;
	}

	public void setNodeObj(AVObject nodeObj) {
		this.nodeObj = nodeObj;
	}

	public AVObject getChannelObj() {
		return channelObj;
	}

	public void setChannelObj(AVObject channelObj) {
		this.channelObj = channelObj;
	}

	public AVObject getLanguageObj() {
		return languageObj;
	}

	public void setLanguageObj(AVObject languageObj) {
		this.languageObj = languageObj;
	}

	public AVObject getTopicObj() {
		return topicObj;
	}

	public void setTopicObj(AVObject topicObj) {
		this.topicObj = topicObj;
	}

	public String getRuleName() {
		return ruleName;
	}

	public void setRuleName(String ruleName) {
		this.ruleName = ruleName;
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

	public String getKeywords() {
		return keywords;
	}

	public void setKeywords(String keywords) {
		this.keywords = keywords;
	}

	public String getCssPath() {
		return cssPath;
	}

	public void setCssPath(String cssPath) {
		this.cssPath = cssPath;
	}

	public String getNextPageCssPath() {
		return nextPageCssPath;
	}

	public void setNextPageCssPath(String nextPageCssPath) {
		this.nextPageCssPath = nextPageCssPath;
	}

	public String getTitlePicCssPath() {
		return titlePicCssPath;
	}

	public void setTitlePicCssPath(String titlePicCssPath) {
		this.titlePicCssPath = titlePicCssPath;
	}

	public int getGrabTime() {
		return grabTime;
	}

	public void setGrabTime(int grabTime) {
		this.grabTime = grabTime;
	}

	public GrabDetailRule getGrabDetailRule() {
		return grabDetailRule;
	}

	public void setGrabDetailRule(GrabDetailRule grabDetailRule) {
		this.grabDetailRule = grabDetailRule;
	}

	public int getFetchNextPage() {
		return fetchNextPage;
	}

	public void setFetchNextPage(int fetchNextPage) {
		this.fetchNextPage = fetchNextPage;
	}

	public String getItemCssPath() {
		return itemCssPath;
	}

	public void setItemCssPath(String itemCssPath) {
		this.itemCssPath = itemCssPath;
	}

	public String getItemLinkCssPath() {
		return itemLinkCssPath;
	}

	public void setItemLinkCssPath(String itemLinkCssPath) {
		this.itemLinkCssPath = itemLinkCssPath;
	}

	public String getItemTitlePicCssPath() {
		return itemTitlePicCssPath;
	}

	public void setItemTitlePicCssPath(String itemTitlePicCssPath) {
		this.itemTitlePicCssPath = itemTitlePicCssPath;
	}

	public String getTargetCssPath() {
		return targetCssPath;
	}

	public void setTargetCssPath(String targetCssPath) {
		this.targetCssPath = targetCssPath;
	}
}
