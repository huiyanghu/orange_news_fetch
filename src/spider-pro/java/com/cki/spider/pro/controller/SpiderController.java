package com.cki.spider.pro.controller;

import java.util.concurrent.Future;

import com.cki.spider.pro.controller.filter.SimilarUrlDetector;

public interface SpiderController {

	Future<TaskStatistics> submit(SpiderTask task, SpiderTaskFilter filter, SpiderTaskListener listener);

	Future<TaskStatistics> submit(SpiderTask task, SimilarUrlDetector similarUrlDetector, LinkResolver linkExtractor, SpiderUrlQueue fetchUrlQueue, SpiderTaskFilter filter,
			SpiderTaskListener listener);

	void termiate();

}
