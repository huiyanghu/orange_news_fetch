package com.it7890.orange.entity;

import com.avos.avoscloud.AVObject;

/**
 * Created by Astro on 17/5/16.
 */
public class GrabDetailRule {

	private String objectId;
	private AVObject grabListRuleObj; //关联列表规则对象
	private String titleCssPath;    //文章标题区域规则
	private String descCssPath;     //文章描述区域规则
	private String conCssPath;      //文章内容区域规则
	private String conCssPath2;      //文章内容区域规则
	private String conCssPath3;      //文章内容区域规则
	private String replaceCssPath;  //剔除内容区域规则
	private String souCssPath;      //来源区域规则
	private String imgCssPath;      //图片规则
	private String videoCssPath;    //媒体规则
	private String authorCssPath;   //作者规则
	private String keywordCssPath;  //关键字规则
	private String testUrl;         //测试url

	public String getObjectId() {
		return objectId;
	}

	public void setObjectId(String objectId) {
		this.objectId = objectId;
	}

	public String getTitleCssPath() {
		return titleCssPath;
	}

	public void setTitleCssPath(String titleCssPath) {
		this.titleCssPath = titleCssPath;
	}

	public String getDescCssPath() {
		return descCssPath;
	}

	public void setDescCssPath(String descCssPath) {
		this.descCssPath = descCssPath;
	}

	public String getConCssPath() {
		return conCssPath;
	}

	public void setConCssPath(String conCssPath) {
		this.conCssPath = conCssPath;
	}

	public String getSouCssPath() {
		return souCssPath;
	}

	public void setSouCssPath(String souCssPath) {
		this.souCssPath = souCssPath;
	}

	public String getImgCssPath() {
		return imgCssPath;
	}

	public void setImgCssPath(String imgCssPath) {
		this.imgCssPath = imgCssPath;
	}

	public String getVideoCssPath() {
		return videoCssPath;
	}

	public void setVideoCssPath(String videoCssPath) {
		this.videoCssPath = videoCssPath;
	}

	public String getAuthorCssPath() {
		return authorCssPath;
	}

	public void setAuthorCssPath(String authorCssPath) {
		this.authorCssPath = authorCssPath;
	}

	public String getKeywordCssPath() {
		return keywordCssPath;
	}

	public void setKeywordCssPath(String keywordCssPath) {
		this.keywordCssPath = keywordCssPath;
	}

	public String getTestUrl() {
		return testUrl;
	}

	public void setTestUrl(String testUrl) {
		this.testUrl = testUrl;
	}

	public String getReplaceCssPath() {
		return replaceCssPath;
	}

	public void setReplaceCssPath(String replaceCssPath) {
		this.replaceCssPath = replaceCssPath;
	}

	public AVObject getGrabListRuleObj() {
		return grabListRuleObj;
	}

	public void setGrabListRuleObj(AVObject grabListRuleObj) {
		this.grabListRuleObj = grabListRuleObj;
	}

	public String getConCssPath2() {
		return conCssPath2;
	}

	public void setConCssPath2(String conCssPath2) {
		this.conCssPath2 = conCssPath2;
	}

	public String getConCssPath3() {
		return conCssPath3;
	}

	public void setConCssPath3(String conCssPath3) {
		this.conCssPath3 = conCssPath3;
	}
}
