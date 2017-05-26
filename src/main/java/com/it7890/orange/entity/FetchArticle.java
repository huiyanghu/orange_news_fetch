package com.it7890.orange.entity;

import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Astro on 17/5/18.
 */
public class FetchArticle {

	private String sourceUrl;    //文章源url
	private String urlSalt;      //源url MD5串
	private GrabDetailRule grabDetailRuleInfo;  //文章详情规则对象
	private GrabListRule grabListRule;          //文章列表规则对象

	private List<String> originTitleImageUrls = new ArrayList<>(); //源标题图片url
	private List<AVFile> titleImageUrls = new ArrayList<>();       //标题图片对象

	private List<String> originContentImageUrls = new ArrayList<>(); //源内容图片url
	private List<AVFile> contentImageUrls = new ArrayList<>();      //内容图片对象

	private AVObject articleInfo;   //文章对象
	private String articleContent;  //文章内容

	public String getSourceUrl() {
		return sourceUrl;
	}

	public void setSourceUrl(String sourceUrl) {
		this.sourceUrl = sourceUrl;
	}

	public GrabDetailRule getGrabDetailRuleInfo() {
		return grabDetailRuleInfo;
	}

	public void setGrabDetailRuleInfo(GrabDetailRule grabDetailRuleInfo) {
		this.grabDetailRuleInfo = grabDetailRuleInfo;
	}

	public GrabListRule getGrabListRule() {
		return grabListRule;
	}

	public void setGrabListRule(GrabListRule grabListRule) {
		this.grabListRule = grabListRule;
	}

	public String getUrlSalt() {
		return urlSalt;
	}

	public void setUrlSalt(String urlSalt) {
		this.urlSalt = urlSalt;
	}

	public List<String> getOriginContentImageUrls() {
		return originContentImageUrls;
	}

	public void setOriginContentImageUrls(List<String> originContentImageUrls) {
		this.originContentImageUrls = originContentImageUrls;
	}

	public List<String> getOriginTitleImageUrls() {
		return originTitleImageUrls;
	}

	public void setOriginTitleImageUrls(List<String> originTitleImageUrls) {
		this.originTitleImageUrls = originTitleImageUrls;
	}

	public List<AVFile> getTitleImageUrls() {
		return titleImageUrls;
	}

	public void setTitleImageUrls(List<AVFile> titleImageUrls) {
		this.titleImageUrls = titleImageUrls;
	}

	public AVObject getArticleInfo() {
		return articleInfo;
	}

	public void setArticleInfo(AVObject articleInfo) {
		this.articleInfo = articleInfo;
	}

	public String getArticleContent() {
		return articleContent;
	}

	public void setArticleContent(String articleContent) {
		this.articleContent = articleContent;
	}

	public List<AVFile> getContentImageUrls() {
		return contentImageUrls;
	}

	public void setContentImageUrls(List<AVFile> contentImageUrls) {
		this.contentImageUrls = contentImageUrls;
	}
}
