package com.cki.fetch.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.cki.spider.pro.SpiderUrl;
import com.cki.spider.pro.HttpHeaders;

public class SpiderUrlUtil {

	public static SpiderUrl buildSpiderUrl(String url) {
		// url = encode(url, "GBK");
		SpiderUrl fetchUrl = new SpiderUrl(url);
		HttpHeaders headers = new HttpHeaders();

		headers.setHeader(HttpHeaders.Names.CONNECTION, "keep-alive");
		headers.setHeader(HttpHeaders.Names.USER_AGENT, "Mozilla/5.0 (X11; U; Linux i686; en-US; rv:1.9.1.5) Gecko/20091102 Firefox/3.5.5");
		headers.setHeader(HttpHeaders.Names.ACCEPT, "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
		headers.setHeader(HttpHeaders.Names.ACCEPT_ENCODING, "gzip,deflate");
		headers.setHeader(HttpHeaders.Names.ACCEPT_LANGUAGE, "en-us,zh-cn,zh;q=0.7,en;q=0.3;q=0.5");
		headers.setHeader(HttpHeaders.Names.ACCEPT_CHARSET, "GB2312,ISO-8859-1,utf-8;q=0.7,*;q=0.7");

		fetchUrl.setRequestHeaders(headers);
		return fetchUrl;
	}

	public static String encode(String str, String charset) {
		// 将中文转换成URL编码
		Pattern p = Pattern.compile("[\u4e00-\u9fa5]|\\u201c|\\u201d");
		Matcher m = p.matcher(str);
		StringBuffer b = new StringBuffer();
		while (m.find()) {
			try {
				m.appendReplacement(b, URLEncoder.encode(m.group(), charset));
			} catch (UnsupportedEncodingException ex) {
				ex.printStackTrace();
			}
		}
		m.appendTail(b);
		return b.toString();
	}
}
