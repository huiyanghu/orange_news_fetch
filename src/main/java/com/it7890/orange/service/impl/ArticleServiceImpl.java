package com.it7890.orange.service.impl;

import com.avos.avoscloud.AVObject;
import com.it7890.orange.dao.ConArticleContentDao;
import com.it7890.orange.dao.ConArticleDao;
import com.it7890.orange.service.ArticleService;
import com.it7890.orange.util.StringUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * Created by Astro on 17/5/22.
 */
@Service("articleService")
public class ArticleServiceImpl implements ArticleService {

	@Resource
	private ConArticleDao conArticleDao;
	@Resource
	private ConArticleContentDao conArticleContentDao;

	@Override
	public String saveConArticle(AVObject conArticle, String articleContent) {
		String articleId = conArticleDao.saveConArticle(conArticle);
		if (StringUtil.isNotEmpty(articleId)) {
			conArticleContentDao.saveArticleContent(articleId, articleContent);
		}
		return articleId;
	}

	@Override
	public boolean getExistArticleBySalt(String salt) {
		return conArticleDao.getExistArticleBySalt(salt);
	}
}
