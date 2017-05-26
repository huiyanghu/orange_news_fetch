package com.it7890.orange.dao;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.it7890.orange.util.StringUtil;
import org.springframework.stereotype.Repository;

@Repository
public class ConArticleContentDao {

	/**
	 * 保存文章内容
	 * @param articleId 文章id
	 * @param content 文章内容
	 * @return 文章内容id
	 */
	public String saveArticleContent(String articleId, String content) {
		String acId = "";
		if (StringUtil.isNotEmpty(articleId) && StringUtil.isNotEmpty(content)) {
			AVObject conArticleContentInfo = new AVObject("con_articles_content");
			conArticleContentInfo.put("articleObj", AVObject.createWithoutData("conarticle", articleId));
			conArticleContentInfo.put("content", content);

			try {
				conArticleContentInfo.save();
				acId = conArticleContentInfo.getObjectId();
			} catch (AVException e) {
				e.printStackTrace();
			}
		}

		return acId;
	}
}
