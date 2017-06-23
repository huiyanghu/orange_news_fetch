package com.it7890.orange.dao;

import com.avos.avoscloud.AVCloudQueryResult;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.it7890.orange.entity.ConArticle;
import com.it7890.orange.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;


@Repository
public class ConArticleDao {

	private final static Logger logger = LoggerFactory.getLogger(ConArticleDao.class);

	public String saveConArticle(AVObject articleInfo) {
		String articleId = "";
		if (null != articleInfo) {
			try {
				articleInfo.save();
				articleId = articleInfo.getObjectId();
			} catch (AVException e) {
				e.printStackTrace();
				logger.warn("saveConArticle，cause: {}", e);
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
				logger.warn("getExistArticleBySalt，cause: {}", e);
			}
		}
		return isExist;
	}
}
