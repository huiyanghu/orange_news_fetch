package com.cki.spider.pro.cookie;

import java.util.List;

public interface CookieStore {

	void init();

	void add(Cookie cookie);

	List<Cookie> find(String url);

	void flush();

	void flushExpires();

	void close();

}
