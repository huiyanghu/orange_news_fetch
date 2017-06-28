package com.it7890.orange.util;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import com.it7890.orange.entity.GrabListRule;
import com.it7890.orange.entity.FetchArticle;

public interface Constants {

	BlockingQueue<GrabListRule> FETCH_LIST_RULE_QUEUE = new LinkedBlockingQueue<GrabListRule>(200);
	BlockingQueue<FetchArticle> FETCH_ARTICLE_QUEUE = new LinkedBlockingQueue<FetchArticle>(200);

	BlockingQueue<FetchArticle> FETCH_ARTICLE_MEDIA_QUEUE = new LinkedBlockingQueue<FetchArticle>(200);

	long SLEEP_MILLIS = 10 * 60 * 1000;

	/**
	 * leancloud key
	 */
	String LEAN_CLOUD_APP_ID = "VV7zErT5UtBfnhkkllg9wboY-MdYXbMMI";
	String LEAN_CLOUD_APP_KEY = "q8QNg0P0npQDGWlsQh8HbtgS";
	String LEAN_CLOUD_MASTER_KEY = "sMQk7iWLaVWsVHmWMlhSyP5P";


	String USER_AGENT = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.36";
}
