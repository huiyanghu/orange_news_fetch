   
  
  
  
  
  
  

package com.cki.spider.pro;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;

   
  
  
  
  
  
public class SpiderData implements Serializable {

	private static final long serialVersionUID = -8855606877150943428L;

	                
	                                                                                                                          
	private SpiderUrl fetchUrl;

	private int statusCode;

	private HttpHeaders responseHeaders;

	private byte[] content;

	private String body;

	private String charset;

	private int contentLength;

	private MimeType mimeType;

	private Throwable cause;

	                  
	                                                                                                                        
	public SpiderUrl getFetchUrl() {
		return fetchUrl;
	}

	public void setFetchUrl(SpiderUrl fetchUrl) {
		this.fetchUrl = fetchUrl;
	}

	public int getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}

	public HttpHeaders getResponseHeaders() {
		return responseHeaders;
	}

	public void setResponseHeaders(HttpHeaders responseHeaders) {
		this.responseHeaders = responseHeaders;
	}

	public byte[] getContent() {

		if (content != null) {
			return content;
		}

		if (body == null) {
			return null;
		}

		try {
			content = body.getBytes(charset);
		} catch (UnsupportedEncodingException e) {
			content = null;
		}

		return content;
	}

	public void setContent(byte[] content) {
		this.content = content;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public String getCharset() {
		return charset;
	}

	public void setCharset(String charset) {
		this.charset = charset;
	}

	public MimeType getMimeType() {
		return mimeType;
	}

	public void setMimeType(MimeType mimeType) {
		this.mimeType = mimeType;
	}

	public int getContentLength() {
		return contentLength;
	}

	public void setContentLength(int contentLength) {
		this.contentLength = contentLength;
	}

	public Throwable getCause() {
		return cause;
	}

	public void setCause(Throwable cause) {
		this.cause = cause;
	}

}
