package com.cki.spider.pro;

import java.util.concurrent.Future;

import com.cki.spider.pro.filter.SpiderFilter;

public interface Spider<T> {

	void init();

	String fetchHtml(SpiderUrl fetchUrl);

	Future<T> fetch(SpiderUrl fetchUrl, SpiderUrlListener listener);

	Future<T> fetch(SpiderUrl fetchUrl, SpiderUrlListener listener, SpiderFilter filter);

	void terminate();

}
