package com.cki.spider.pro.host.event;

import com.cki.spider.pro.ExecutionContext;

@SuppressWarnings("unchecked")
public class RequestContextEvent implements ContextEvent {

	private ExecutionContext executionContext;

	public RequestContextEvent(ExecutionContext context) {
		this.executionContext = context;
	}

	public ExecutionContext getExecutionContext() {
		return executionContext;
	}

	public void setExecutionContext(ExecutionContext executionContext) {
		this.executionContext = executionContext;
	}

}
