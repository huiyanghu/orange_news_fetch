package com.cki.spider.pro;

import java.io.Serializable;

import java.lang.ref.WeakReference;

import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.handler.codec.http.CookieEncoder;
import org.jboss.netty.handler.codec.http.DefaultHttpRequest;
import org.jboss.netty.handler.codec.http.HttpMethod;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.jboss.netty.handler.codec.http.HttpVersion;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cki.spider.pro.cookie.Cookie;
import com.cki.spider.pro.cookie.CookieStore;
import com.cki.spider.pro.filter.SpiderFilter;
import com.cki.spider.pro.future.SpiderFuture;
import com.cki.spider.pro.netty.ChannelPipelineFactory;
import com.cki.spider.pro.timeout.TimeoutManager;
import com.cki.spider.pro.util.CharsetUtil;
import com.cki.spider.pro.util.HtmlUtil;
import com.cki.spider.pro.util.HttpHeaderUtil;
import com.cki.spider.pro.util.UrlUtil;
import com.cki.spier.pro.host.ExecutionDispatcher;
import com.cki.spier.pro.host.HostContext;
import com.cki.spier.pro.host.HostPortAndUri;

@SuppressWarnings("unchecked")
public class ExecutionContext<V> implements Serializable {

	private static final long serialVersionUID = -2344616580085779974L;

	private final static Logger logger = LoggerFactory.getLogger(ExecutionContext.class);

	private AtomicBoolean done;

	private final SpiderUrl fetchUrl;

	private final SpiderUrlListener listener;

	private SpiderFilter filter;

	private SpiderFuture<V> future;

	private HttpResponse response;

	private HostPortAndUri hostPortAndUri;

	private final int timeout;

	private ResponseBodyCollector responseBodyCollector;

	private ChannelFactory channelFactory;

	private ChannelPipelineFactory channelPipelineFactory;

	private TimeoutManager timeoutManager;

	private SpiderConfig spiderConfig;

	private Executor listenerExecutor;

	private final SpiderData spiderData;

	private ExecutionDispatcher dispatcher;

	private int redirectCounter;

	private CookieStore cookieStore;

	private WeakReference<HostContext.Handler> connection;

	private long startExecuteTime;

	private AtomicBoolean triggerPrefetched;

	public ExecutionContext(SpiderUrl fetchUrl, SpiderUrlListener listener, SpiderFilter filter, int executionTimeout, ChannelFactory channelFactory, ChannelPipelineFactory channelPipelineFactory,
			TimeoutManager timeoutManager, SpiderConfig spiderConfig, Executor listenerExecutor, ExecutionDispatcher dispatcher, CookieStore cookieStore) {

		this.fetchUrl = fetchUrl;
		this.listener = listener;
		this.filter = filter;
		this.timeout = executionTimeout;
		this.future = new SpiderFuture<V>();

		this.channelFactory = channelFactory;
		this.channelPipelineFactory = channelPipelineFactory;
		this.timeoutManager = timeoutManager;

		this.responseBodyCollector = new ResponseBodyCollector();

		this.hostPortAndUri = HostPortAndUri.splitUrl(fetchUrl.getUrl());

		this.spiderConfig = spiderConfig;

		this.listenerExecutor = listenerExecutor;

		this.spiderData = new SpiderData();

		this.redirectCounter = 0;

		this.dispatcher = dispatcher;

		this.cookieStore = cookieStore;

		this.done = new AtomicBoolean(false);

		this.startExecuteTime = -1;

		this.triggerPrefetched = new AtomicBoolean(false);

	}

	private ExecutionContext(SpiderUrl fetchUrl, SpiderUrlListener listener, SpiderFilter filter, int executionTimeout, ChannelFactory channelFactory, ChannelPipelineFactory channelPipelineFactory,
			TimeoutManager timeoutManager, SpiderConfig spiderConfig, Executor listenerExecutor, ExecutionDispatcher dispatcher, CookieStore cookieStore, int redirectCounter, SpiderFuture future) {

		this(fetchUrl, listener, filter, executionTimeout, channelFactory, channelPipelineFactory, timeoutManager, spiderConfig, listenerExecutor, dispatcher, cookieStore);

		this.redirectCounter = redirectCounter;
		this.future = future;

		this.triggerPrefetched.set(true);
	}

	public void dispath() throws InterruptedException {
		this.dispatcher.dispatch(this);
	}

	public void markStart(HostContext.Handler connection) {

		if (this.triggerPrefetched.compareAndSet(false, true) && this.listener != null) {
			this.listenerExecutor.execute(new Runnable() {

				@Override
				public void run() {

					try {
						listener.preFetch(fetchUrl);
					} catch (Throwable t) {
						logger.error("pre fetch occurr error.", t);
					}
				}
			});
		}

		bindConnection(connection);

		startTimeoutManage();
	}

	protected void buildFetchedData() {

		try {
			if (this.response != null) {

				this.spiderData.setStatusCode(this.response.getStatus().getCode());
				this.spiderData.setFetchUrl(this.fetchUrl);

				HttpHeaders headers = new HttpHeaders();

				for (Map.Entry<String, String> entry : this.response.getHeaders()) {
					headers.setHeader(entry.getKey(), entry.getValue());
				}

				this.spiderData.setResponseHeaders(headers);

				ChannelBuffer channelBuffer = this.getResponseBodyCollector().getBody();

				if (channelBuffer != null) {
					this.spiderData.setContentLength(channelBuffer.readableBytes());
				} else {
					this.spiderData.setContentLength(0);
				}

				Charset charset = HttpHeaderUtil.getAndVerifyCharset(headers);

				if (charset == null) {

					if (channelBuffer != null) {

						byte[] content = new byte[channelBuffer.readableBytes()];

						channelBuffer.getBytes(channelBuffer.readerIndex(), content);

						this.spiderData.setContent(content);

						try {
							charset = Charset.forName(CharsetUtil.analyze(this.spiderData.getContent()));
						} catch (UnsupportedCharsetException e) {
							logger.error("charset error.url:{}", this.fetchUrl.getUrl(), e);

							charset = Charset.defaultCharset();
						}

						this.spiderData.setBody(new String(content, charset));

					}

				} else {

					ChannelBuffer body = this.getResponseBodyCollector().getBody();

					if (body != null) {
						this.spiderData.setBody(body.toString(charset));
					}
				}

				spiderData.setCharset(charset.name());

				if (this.spiderConfig.isDebug()) {

					System.out.println("-------------------------------response headers -------------------------------");
					System.out.println(response.toString());

				}

			} else {

				buildEmptyFetchedData();

			}

		} catch (Exception e) {
			logger.error("build fetched data error.url:{}", this.fetchUrl.getUrl(), e);
			buildEmptyFetchedData();
		}
	}

	private void buildEmptyFetchedData() {

		this.spiderData.setFetchUrl(this.fetchUrl);
		this.spiderData.setResponseHeaders(new HttpHeaders());
		this.spiderData.setContentLength(0);
		this.spiderData.setBody(null);
	}

	private void triggerPostListener() {

		if (this.listener != null) {
			this.listenerExecutor.execute(new Runnable() {

				@Override
				public void run() {

					try {
						listener.postFetch(fetchUrl, spiderData);
					} catch (Throwable t) {
						logger.error("post listener error.", t);
					}
				}

			});
		}
	}

	public boolean markTimeout() {

		if (!this.done.compareAndSet(false, true)) {
			return false;
		}

		try {

			closeConnection();

			this.spiderData.setCause(SpiderFuture.TIMED_OUT);

			buildFetchedData();

			stopTimeoutManage();

		} catch (Exception e) {
			logger.error("mark timeout error.", e);
		}

		triggerPostListener();

		return this.future.setFailure(SpiderFuture.TIMED_OUT);

	}

	private void closeConnection() {

		if (this.connection != null) {

			HostContext.Handler conn = this.connection.get();

			if (conn != null) {

				conn.close();
			}
		}
	}

	public boolean timeout() {

		return (System.currentTimeMillis() - this.startExecuteTime) > this.timeout;
	}

	@SuppressWarnings("unchecked")
	public boolean markSuccess() {

		if (!this.done.compareAndSet(false, true)) {
			return false;
		}

		try {

			buildFetchedData();

			if (this.cookieStore != null) {
				storeCookie(this.spiderData.getResponseHeaders());
			}

			if (shouldRedirect()) {

				try {

					String redirectUrl = buildRedirectUrl(this.spiderData.getResponseHeaders());

					if (redirectUrl == null) {

						logger.error("location is null.fetchUrl:{}", this.fetchUrl.getUrl());

						markFailureAnyway(new RuntimeException("location is null."));

						return false;
					}

					if (redirectUrl.equals(this.fetchUrl.getUrl())) {

						logger.error("redirect url is equal to original url:{}", this.fetchUrl.getUrl());

						markFailureAnyway(new RuntimeException("redirect url is equal to original url"));

						return false;
					}

					this.fetchUrl.setUrl(redirectUrl);

					if (!this.fetchUrl.getFetchMethod().equals(SpiderMethod.GET)) {
						this.fetchUrl.setFetchMethod(SpiderMethod.GET);
					}

					this.fetchUrl.setEntity(null);

					ExecutionContext newContext = new ExecutionContext(fetchUrl, listener, filter, timeout, channelFactory, channelPipelineFactory, timeoutManager, spiderConfig, listenerExecutor,
							dispatcher, cookieStore, this.redirectCounter + 1, this.future);

					this.dispatcher.dispatch(newContext);

					return false;

				} catch (Exception e) {
					logger.error("redirect error.furl:{}", this.fetchUrl.getUrl(), e);
				}
			}

		} catch (Throwable t) {
			logger.error("mark success error", t);
		} finally {
			stopTimeoutManage();
		}

		triggerPostListener();

		return this.future.setSuccess((V) this.spiderData);
	}
	
	/**
	 * 
	 * @return -1:failed 0：redirect 1:success
	 */
	@SuppressWarnings("unchecked")
	public int markSuccess2() {

		if (!this.done.compareAndSet(false, true)) {
			return -1;
		}

		try {

			buildFetchedData();

			if (this.cookieStore != null) {
				storeCookie(this.spiderData.getResponseHeaders());
			}

			if (shouldRedirect()) {

				try {

					String redirectUrl = buildRedirectUrl(this.spiderData.getResponseHeaders());

					if (redirectUrl == null) {

						logger.error("location is null.fetchUrl:{}", this.fetchUrl.getUrl());

						markFailureAnyway(new RuntimeException("location is null."));

						return -1;
					}

					if (redirectUrl.equals(this.fetchUrl.getUrl())) {

						logger.error("redirect url is equal to original url:{}", this.fetchUrl.getUrl());

						markFailureAnyway(new RuntimeException("redirect url is equal to original url"));

						return -1;
					}

					this.fetchUrl.setUrl(redirectUrl);

					if (!this.fetchUrl.getFetchMethod().equals(SpiderMethod.GET)) {
						this.fetchUrl.setFetchMethod(SpiderMethod.GET);
					}

					this.fetchUrl.setEntity(null);

					ExecutionContext newContext = new ExecutionContext(fetchUrl, listener, filter, timeout, channelFactory, channelPipelineFactory, timeoutManager, spiderConfig, listenerExecutor,
							dispatcher, cookieStore, this.redirectCounter + 1, this.future);

					this.dispatcher.dispatch(newContext);

					return 0;

				} catch (Exception e) {
					logger.error("redirect error.furl:{}", this.fetchUrl.getUrl(), e);
				}
			}

		} catch (Throwable t) {
			logger.error("mark success error", t);
		} finally {
			stopTimeoutManage();
		}

		triggerPostListener();

		if( this.future.setSuccess((V) this.spiderData)){
			return 1;
		}else{
			return -1;
		}
	}
	private boolean shouldRedirect() {

		return isRedirectResponse(this.spiderData.getStatusCode()) && (this.spiderConfig.getMaxRedirects() > 0) && (this.redirectCounter < this.spiderConfig.getMaxRedirects());
	}

	private boolean isRedirectResponse(int statusCode) {

		if ((statusCode > 300 && statusCode < 304) || statusCode == 309) {
			return true;
		}

		return false;
	}

	private String buildRedirectUrl(HttpHeaders headers) {

		String location = null;

		String key = HttpHeaders.Names.LOCATION.toLowerCase();

		for (Map.Entry<String, String> entry : headers.getHeaders().entrySet()) {
			if (entry.getKey().toLowerCase().equals(key)) {
				location = entry.getValue();
				break;
			}
		}

		if (location == null || location.equals("")) {
			return null;
		}

		if (HtmlUtil.isAbsoluteUrl(location)) {
			return location;
		}

		if (location.startsWith("/")) {
			return HtmlUtil.getUrlBase(this.fetchUrl.getUrl()) + location;
		}

		return HtmlUtil.getUrlParentDirectory(this.fetchUrl.getUrl()) + "/" + location;
	}

	private void storeCookie(HttpHeaders headers) {

		if (headers == null) {
			logger.info("headers is null.");

		}

		String cookieHeader = headers.getHeader(HttpHeaders.Names.SET_COOKIE);

		if (cookieHeader == null) {
			return;
		}

		String[] segs = cookieHeader.split(";");

		String[] nameValues = segs[0].trim().split("\\=");

		Cookie cookie = new Cookie(nameValues[0], nameValues[1]);

		cookie.setPath(segs[1].trim().split("\\=")[1]);
		if (segs.length > 2 && segs[2].trim().split("\\=").length == 2) {
			cookie.setDomain(segs[2].trim().split("\\=")[1]);
		} else {

			String domain = HtmlUtil.getDomain(this.getFetchUrl().getUrl());
			if (domain.startsWith("www")) {
				cookie.setDomain(domain);
			} else {
				cookie.setDomain("." + domain);
			}

		}

		cookieStore.add(cookie);
	}

	public boolean markFailure(Throwable e) {

		if (!this.done.compareAndSet(false, true)) {
			return false;
		}

		try {

			this.spiderData.setCause(e);

			buildFetchedData();

			stopTimeoutManage();

		} catch (Exception exception) {
			logger.error("mark failure error.", exception);
		}

		triggerPostListener();

		return this.future.setFailure(e);

	}

	private boolean markFailureAnyway(Throwable e) {

		try {

			this.spiderData.setCause(e);

			stopTimeoutManage();

		} catch (Exception exception) {
			logger.error("mark failure error.", exception);
		}

		triggerPostListener();

		return this.future.setFailure(e);

	}

	public boolean markCanceld(Throwable e) {

		if (!this.done.compareAndSet(false, true)) {
			return false;
		}

		try {

			buildFetchedData();

			closeConnection();

			stopTimeoutManage();

		} catch (Exception exception) {
			logger.error("mark canceld error.", exception);
		}

		triggerPostListener();

		return this.future.cancel(true);

	}

	public HttpRequest getRequest() {

		String uri = null;

		if (this.fetchUrl.getFetchProxy() != null && this.fetchUrl.getFetchProxy().getHost() != null) {
			uri = this.fetchUrl.getUrl();
		} else {
			uri = UrlUtil.getPathAndQueryString(fetchUrl.getUrl());
		}

		HttpRequest request = new DefaultHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.valueOf(fetchUrl.getFetchMethod().toString()), uri);

		HttpHeaders requestHeaders = this.fetchUrl.getRequestHeaders();

		if (requestHeaders != null) {

			for (Map.Entry<String, String> entry : requestHeaders.getHeaders().entrySet()) {

				request.setHeader(entry.getKey(), entry.getValue());

			}

		}

		request.setHeader(HttpHeaders.Names.HOST, hostPortAndUri.asHostAndPort());

		if (this.cookieStore != null && request.getHeader(HttpHeaders.Names.COOKIE) == null) {

			List<Cookie> cookies = this.cookieStore.find(this.fetchUrl.getUrl());

			if (cookies != null && cookies.size() > 0) {

				CookieEncoder httpCookieEncoder = new CookieEncoder(false);

				for (Cookie c : cookies) {

					httpCookieEncoder.addCookie(c.getName(), c.getValue());

				}

				request.setHeader(HttpHeaders.Names.COOKIE, httpCookieEncoder.encode());

			}

		}

		if (this.fetchUrl.getEntity() != null) {

			request.setContent(ChannelBuffers.wrappedBuffer(this.fetchUrl.getEntity()));

			request.setHeader(HttpHeaders.Names.CONTENT_TYPE, "application/x-www-form-urlencoded;charset=" + fetchUrl.getCharset());
			request.setHeader(HttpHeaders.Names.CONTENT_LENGTH, this.fetchUrl.getEntity().length);

		}

		if (this.spiderConfig.isDebug()) {

			System.out.println("------------------------------- request headers -------------------------------");
			System.out.println(request.toString());

		}

		return request;
	}

	public void bindConnection(HostContext.Handler connection) {

		if (connection == null) {
			return;
		}

		this.connection = new WeakReference<HostContext.Handler>(connection);
	}

	public void startTimeoutManage() {

		if (this.timeoutManager == null) {
			return;
		}

		if (this.startExecuteTime == -1) {

			this.startExecuteTime = System.currentTimeMillis();

			this.timeoutManager.manageRequestTimeout(this);

		}
	}

	private void stopTimeoutManage() {

		if (this.timeoutManager != null) {
			this.timeoutManager.removeTimeoutManage(this);
		}
	}

	public boolean isSSL() {

		if (this.fetchUrl.getFetchProxy() != null) {
			return false;
		}

		if (this.fetchUrl.getUrl().toLowerCase().startsWith("https://")) {
			return true;
		}

		return false;
	}

	public void setResponse(HttpResponse response) {
		this.response = response;
	}

	public Future<V> getFuture() {
		return this.future;
	}

	public SpiderUrl getFetchUrl() {
		return fetchUrl;
	}

	public SpiderUrlListener getListener() {
		return listener;
	}

	public SpiderFilter getFilter() {
		return filter;
	}

	public void setFilter(SpiderFilter filter) {
		this.filter = filter;
	}

	public HostPortAndUri getHostPortAndUri() {
		return hostPortAndUri;
	}

	public int getTimeout() {
		return timeout;
	}

	public ResponseBodyCollector getResponseBodyCollector() {
		return responseBodyCollector;
	}

	public ChannelFactory getChannelFactory() {
		return channelFactory;
	}

	public ChannelPipelineFactory getChannelPipelineFactory() {
		return channelPipelineFactory;
	}

	public SpiderConfig getFetcherConfig() {
		return spiderConfig;
	}

	public TimeoutManager getTimeoutManager() {
		return timeoutManager;
	}

}
