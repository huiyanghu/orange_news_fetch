package com.cki.spider.pro.timeout;

import com.cki.spider.pro.ExecutionContext;

@SuppressWarnings("unchecked")
public interface TimeoutManager {

	boolean init();

	void terminate();

	void manageRequestTimeout(ExecutionContext context);

	void removeTimeoutManage(ExecutionContext context);
}
