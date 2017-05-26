package com.it7890.orange.service;

import com.avos.avoscloud.AVObject;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * Created by Astro on 17/5/22.
 */
@Resource(name="articleService")
public interface ArticleService {

	String saveConArticle(AVObject conArticle, String articleContent);
}
