package com.cki.spider.pro;

import java.io.Serializable;

import com.cki.spier.pro.host.HostPortAndUri;

public class SpiderUrl implements Serializable {

	private static final long serialVersionUID = -7156684188614807469L;

	private String url;

	private String originalUrl;

	private SpiderMethod fetchMethod;

	private HttpHeaders requestHeaders;

	private int priority;

	private byte[] entity;

	private String charset;

	private Serializable attachment;

	private SpiderProxy fetchProxy;

	private long connectionTimeoutInMillis;

	private int maxExecutionTimeout;

	public SpiderUrl(String url) {

		if (url == null || url.trim().equals("")) {
			throw new RuntimeException("invalid url");
		}

		String urlLowerCase = url.toLowerCase();

		if (!urlLowerCase.startsWith("http://") && !urlLowerCase.startsWith("https://")) {
			url = "http://" + url;
		}

		this.url = url;
		this.originalUrl = url;
		this.fetchMethod = SpiderMethod.GET;
		this.priority = 1;
		this.charset = "UTF-8";
		this.connectionTimeoutInMillis = -1L;
		this.maxExecutionTimeout = -1;

		HostPortAndUri hostPortAndUri = HostPortAndUri.splitUrl(this.url);
		;

		if (hostPortAndUri == null) {
			throw new RuntimeException("invalid url: " + this.url);
		}
	}

	public SpiderUrl(SpiderUrl fetchUrl) {

		this.url = fetchUrl.getUrl();
		this.originalUrl = fetchUrl.getOriginalUrl();
		this.fetchMethod = fetchUrl.getFetchMethod();
		this.requestHeaders = fetchUrl.getRequestHeaders();
		this.priority = fetchUrl.getPriority();
		this.attachment = fetchUrl.getAttachment();
		this.charset = fetchUrl.getCharset();
		this.connectionTimeoutInMillis = fetchUrl.getConnectionTimeoutInMillis();
		this.maxExecutionTimeout = fetchUrl.getMaxExecutionTimeout();
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public HttpHeaders getRequestHeaders() {
		return requestHeaders;
	}

	public void setRequestHeaders(HttpHeaders requestHeaders) {
		this.requestHeaders = requestHeaders;
	}

	public SpiderMethod getFetchMethod() {
		return fetchMethod;
	}

	public void setFetchMethod(SpiderMethod fetchMethod) {
		this.fetchMethod = fetchMethod;
	}

	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

	public Serializable getAttachment() {
		return attachment;
	}

	public void setAttachment(Serializable attachment) {
		this.attachment = attachment;
	}

	public String getOriginalUrl() {
		return originalUrl;
	}

	public byte[] getEntity() {
		return entity;
	}

	public void setEntity(byte[] entity) {
		this.entity = entity;
	}

	public String getCharset() {
		return charset;
	}

	public void setCharset(String charset) {
		this.charset = charset;
	}

	public SpiderProxy getFetchProxy() {
		return fetchProxy;
	}

	public void setFetchProxy(SpiderProxy fetchProxy) {
		this.fetchProxy = fetchProxy;
	}

	public long getConnectionTimeoutInMillis() {
		return connectionTimeoutInMillis;
	}

	public void setConnectionTimeoutInMillis(long connectionTimeoutInMillis) {
		this.connectionTimeoutInMillis = connectionTimeoutInMillis;
	}

	public int getMaxExecutionTimeout() {
		return maxExecutionTimeout;
	}

	public void setMaxExecutionTimeout(int maxExecutionTimeout) {
		this.maxExecutionTimeout = maxExecutionTimeout;
	}

	@Override
	public String toString() {
		return "FetchUrl [url=" + url + ", originalUrl=" + originalUrl + "]";
	}

}
