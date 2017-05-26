package com.it7890.orange.dao;

import com.avos.avoscloud.AVCloudQueryResult;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.it7890.orange.entity.ConArticle;
import com.it7890.orange.entity.FetchArticle;
import com.it7890.orange.entity.GrabDetailRule;
import com.it7890.orange.entity.GrabListRule;
import com.it7890.orange.util.StringUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Repository;

import java.util.Date;

@Repository
public class ConArticleDao {

	private final static Logger logger = LogManager.getLogger(ConArticleDao.class);

	/**
	 * 保存文章属性
	 * @param fetchArticle 抓取文章对象
	 * @param imgCount 图片数
	 * @param keywords 关键字
	 * @param latitude 纬度
	 * @param longitude 经度
	 * @param sourceUrl 来源地址url
	 * @param writer 作者
	 * @param abstracts 简介
	 * @param subTime 提交时间
	 * @param title 标题
	 * @param titlePic 标题图片
	 * @param attr 0文字新闻 1图片新闻 2视频新闻 3 连接新闻 4H5游戏新闻 5竞猜新闻 6游戏新闻
	 * @return 文章id
	 */
	public String saveConArticle(FetchArticle fetchArticle, int imgCount, String keywords, long latitude, long longitude, String sourceUrl, String writer, String abstracts, Date subTime, String title, String titlePic, int attr) {
		String articleId = "";
		if (null != fetchArticle && null != fetchArticle.getGrabListRule() && null != fetchArticle.getGrabDetailRuleInfo()){
			GrabListRule grabListRule = fetchArticle.getGrabListRule();
			GrabDetailRule grabDetailRule = fetchArticle.getGrabDetailRuleInfo();

//			ConArticle articleInfo = new ConArticle();
			AVObject articleInfo = new AVObject("conarticle");
			articleInfo.put("grabListRuleObj", grabListRule);
			articleInfo.put("grabDetailRuleObj", grabDetailRule);
			articleInfo.put("countrycode", grabListRule.getCountryCode());
			articleInfo.put("langid", grabListRule.getLangId());
			articleInfo.put("salt", fetchArticle.getUrlSalt());
//			articleInfo.put("linkurl", fetchArticle.getSourceUrl());
			articleInfo.put("imgcount", imgCount);
			articleInfo.put("keywords", keywords);
			articleInfo.put("latitude", latitude);
			articleInfo.put("longitude", longitude);
			articleInfo.put("sourceurl", sourceUrl);
			articleInfo.put("writer", writer);
			articleInfo.put("abstracts", abstracts);
			articleInfo.put("subtime", subTime);
			articleInfo.put("title", title);
			articleInfo.put("titlepic", titlePic);
			articleInfo.put("attr", attr);
			articleInfo.put("ctype", 0);
			articleInfo.put("status", 0);

			if (StringUtil.isNotEmpty(grabListRule.getPublicationId())) {
				articleInfo.put("publicationObj", AVObject.createWithoutData("con_publications", grabListRule.getPublicationId())); //媒体
			}
			if (StringUtil.isNotEmpty(grabListRule.getChannelId())) {
				articleInfo.put("channelObj", AVObject.createWithoutData("con_channel", grabListRule.getChannelId())); //渠道
			}
			if (StringUtil.isNotEmpty(grabListRule.getTopicId())) {
				articleInfo.put("topicObj", AVObject.createWithoutData("AppTopics", grabListRule.getTopicId())); //话题
			}

			try {
				articleInfo.save();
				articleId = articleInfo.getObjectId();
			} catch (AVException e) {
				e.printStackTrace();
				logger.warn("保存文章出错，cause: {}", e);
			}
		}

		return articleId;
	}

	public String saveConArticle(AVObject articleInfo) {
		String articleId = "";
		if (null != articleInfo) {
			try {
				articleInfo.save();
				articleId = articleInfo.getObjectId();
			} catch (AVException e) {
				e.printStackTrace();
				logger.warn("保存文章出错，cause: {}", e);
			}
		}
		return articleId;
	}

	/**
	 * 文章是否存在
	 * @param salt 文章url md5传
	 * @return 是否已存在
	 */
	public boolean getExistArticleBySalt(String salt) {
		boolean isExist = true;
		if (StringUtil.isNotEmpty(salt)) {
			String cql = " select count(*) from conarticle where salt = ?";
			try {
				AVCloudQueryResult avCloudQueryResult = AVQuery.doCloudQuery(cql, ConArticle.class, salt);
				int articleCount = avCloudQueryResult.getCount();
				if (articleCount == 0) {
					isExist = false;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return isExist;
	}
}
