package com.it7890.orange.util;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import com.it7890.orange.entity.GrabListRule;
import com.it7890.orange.entity.FetchArticle;

public interface Constants {

	BlockingQueue<GrabListRule> FETCH_LIST_RULE_QUEUE = new LinkedBlockingQueue<GrabListRule>();
	BlockingQueue<FetchArticle> FETCH_ARTICLE_QUEUE = new LinkedBlockingQueue<FetchArticle>();

	BlockingQueue<FetchArticle> FETCH_ARTICLE_MEDIA_QUEUE = new LinkedBlockingQueue<FetchArticle>();

	String RESOURCE_TMP_PATH = "resourceTmpPath";

}
