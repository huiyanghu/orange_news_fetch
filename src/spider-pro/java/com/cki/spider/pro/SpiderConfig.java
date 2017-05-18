package com.cki.spider.pro;

import java.util.concurrent.Semaphore;

public class SpiderConfig {

	protected static final boolean AUTO_INFLATE = false;
	protected static final int CONNECTION_TIMEOUT_IN_MILLIS = 2000;
	protected static final int REQUEST_TIMEOUT_IN_MILLIS = 2000;
	protected static final int MAX_CONNECTIONS_PER_HOST = 3;
	protected static final int MAX_QUEUED_REQUESTS = Short.MAX_VALUE;
	protected static final int MAX_IO_WORKER_THREADS = 50;
	protected static final int MAX_HOST_CONTEXTS = 2000;
	protected static final int REQUEST_CHUNK_SIZE = 8192;
	protected static final int MAX_REDIRECTS = 4;
	protected static final int MAX_CONTENT_LENGTH = 1024 * 1024 * 10;
	protected static final int MAX_EXECUTION_TIMEOUT = 4 * 60 * 1000;
	protected static final int DISPATCHER_THREAD_COUNT = 10;

	protected static final int MAX_CONNECTION_COUNT = 6;

	protected boolean autoInflate;
	protected int maxConnectionsPerHost;
	protected int maxQueuedRequests;
	protected int connectionTimeoutInMillis;
	protected int requestTimeoutInMillis;
	protected int maxIoWorkerThreads;
	protected int maxHostContext;
	protected int requestChunkSize;
	protected int maxRedirects;
	protected int maxContentLength;
	protected int maxExecutionTimeout;
	protected int dispatcherThreadCount;
	protected boolean debug;

	public static int maxConnectionCount=2;
	public static Semaphore limit = new Semaphore(maxConnectionCount);;

	public SpiderConfig(int maxConnCount) {

		this.autoInflate = AUTO_INFLATE;
		this.connectionTimeoutInMillis = CONNECTION_TIMEOUT_IN_MILLIS;
		this.requestTimeoutInMillis = REQUEST_TIMEOUT_IN_MILLIS;
		this.maxConnectionsPerHost = MAX_CONNECTIONS_PER_HOST;
		this.maxQueuedRequests = MAX_QUEUED_REQUESTS;
		this.maxIoWorkerThreads = MAX_IO_WORKER_THREADS;
		this.maxHostContext = MAX_HOST_CONTEXTS;
		this.requestChunkSize = REQUEST_CHUNK_SIZE;
		this.maxRedirects = MAX_REDIRECTS;
		this.maxContentLength = MAX_CONTENT_LENGTH;
		this.maxExecutionTimeout = MAX_EXECUTION_TIMEOUT;
		this.dispatcherThreadCount = DISPATCHER_THREAD_COUNT;
		this.debug = false;
		maxConnectionCount = maxConnCount;
		limit = new Semaphore(maxConnectionCount);
	}

	public int getMaxConnectionCount() {
		return maxConnectionCount;
	}

	public void setMaxConnectionCount(int maxConnectionCount) {
		this.maxConnectionCount = maxConnectionCount;
	}

	public boolean isAutoInflate() {
		return autoInflate;
	}

	public void setAutoInflate(boolean autoInflate) {
		this.autoInflate = autoInflate;
	}

	public int getMaxConnectionsPerHost() {
		return maxConnectionsPerHost;
	}

	public void setMaxConnectionsPerHost(int maxConnectionsPerHost) {
		this.maxConnectionsPerHost = maxConnectionsPerHost;
	}

	public int getMaxQueuedRequests() {
		return maxQueuedRequests;
	}

	public void setMaxQueuedRequests(int maxQueuedRequests) {
		this.maxQueuedRequests = maxQueuedRequests;
	}

	public int getRequestTimeoutInMillis() {
		return requestTimeoutInMillis;
	}

	public void setRequestTimeoutInMillis(int requestTimeoutInMillis) {
		this.requestTimeoutInMillis = requestTimeoutInMillis;
	}

	public int getMaxIoWorkerThreads() {
		return maxIoWorkerThreads;
	}

	public void setMaxIoWorkerThreads(int maxIoWorkerThreads) {
		this.maxIoWorkerThreads = maxIoWorkerThreads;
	}

	public int getConnectionTimeoutInMillis() {
		return connectionTimeoutInMillis;
	}

	public void setConnectionTimeoutInMillis(int connectionTimeoutInMillis) {
		this.connectionTimeoutInMillis = connectionTimeoutInMillis;
	}

	public int getMaxHostContext() {
		return maxHostContext;
	}

	public void setMaxHostContext(int maxHostContext) {
		this.maxHostContext = maxHostContext;
	}

	public int getRequestChunkSize() {
		return requestChunkSize;
	}

	public void setRequestChunkSize(int requestChunkSize) {
		this.requestChunkSize = requestChunkSize;
	}

	public int getMaxRedirects() {
		return maxRedirects;
	}

	public void setMaxRedirects(int maxRedirects) {
		this.maxRedirects = maxRedirects;
	}

	public boolean isDebug() {
		return debug;
	}

	public void setDebug(boolean debug) {
		this.debug = debug;
	}

	public int getMaxContentLength() {
		return maxContentLength;
	}

	public void setMaxContentLength(int maxContentLength) {
		this.maxContentLength = maxContentLength;
	}

	public int getMaxExecutionTimeout() {
		return maxExecutionTimeout;
	}

	public void setMaxExecutionTimeout(int maxExecutionTimeout) {
		this.maxExecutionTimeout = maxExecutionTimeout;
	}

	public int getDispatcherThreadCount() {
		return dispatcherThreadCount;
	}

	public void setDispatcherThreadCount(int dispatcherThreadCount) {
		this.dispatcherThreadCount = dispatcherThreadCount;
	}

}
