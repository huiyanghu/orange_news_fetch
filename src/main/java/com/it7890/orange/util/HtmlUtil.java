package com.it7890.orange.util;

/**
 * Created by Astro on 17/6/16.
 */
public class HtmlUtil {

	/**
	 * 获取资源真实url
	 * @param siteUrl
	 * @param url
	 * @return
	 */
	public static String getRemoteUrl(String siteUrl, String url) {
		String remoteUrl = "";
		if (StringUtil.isNotEmpty(siteUrl) && StringUtil.isNotEmpty(url)) {
			if (url.startsWith("//")) {
				remoteUrl = "http:" + url;
			} else if (!url.startsWith("http://") && !url.startsWith("https://") && !url.startsWith("www.")) {
				if (StringUtil.isNotEmpty(siteUrl) && StringUtil.isNotEmpty(url)) {
					if (siteUrl.contains("/")) {
						if (siteUrl.startsWith("http") || siteUrl.startsWith("www")) {
							siteUrl = siteUrl.substring(0, siteUrl.indexOf("/", 8));
						} else {
							siteUrl = siteUrl.substring(0, siteUrl.indexOf("/"));
						}
					}
					if (!url.contains("javascript")) {
						if (url.startsWith("/")) {
							remoteUrl = siteUrl + url;
						} else {
							remoteUrl = siteUrl + "/" + url;
						}
					}
				}
			} else {
				remoteUrl = url;
			}
			if (remoteUrl.contains("#")) {
				remoteUrl = remoteUrl.substring(0, remoteUrl.indexOf("#"));
			}
		}

		return remoteUrl;
	}

	/**
	 * 清理 HTML
	 * @param html
	 * @return
	 */
	public static String cleanHtml(String html) {
		// 去除boddy
		html = html.replaceAll("<html>", "");
		html = html.replaceAll("</html>", "");
		html = html.replaceAll("<body>", "");
		html = html.replaceAll("</body>", "");
		html = html.replaceAll("<head>", "");
		html = html.replaceAll("</head>", "");
		html = html.replaceAll("<>", "");
		html = html.replaceAll("<figure.*?>", "<p>");
		html = html.replaceAll("</figure>", "</p>");
		html = html.replaceAll("<div.*?>", "<p>");
		html = html.replaceAll("</div>", "</p>");
		html = html.replaceAll("<\\/*span.*?>", "");
		html = html.replaceAll("<\\/*li.*?>", "");
		html = html.replaceAll("<\\/*ul.*?>", "");
		html = html.replaceAll("<\\/*a.*?>", "");
		html = html.replaceAll("<\\/*script.*?>", "");
		html = html.replaceAll("\"//www", "\"http://www");
		html = html.replaceAll("class=\"[^\"]*\"", "");
		html = html.replaceAll("style=\"[^\"]*\"", "");
		html = html.replaceAll("width=\"[^\"]*\"", "width=\"100%\"");
		html = html.replaceAll("height=\"[^\"]*\"", "");
		html = html.replaceAll("\r", "");
		html = html.replaceAll("\n", "");
		html = html.replaceAll("\t", "");
		html = html.replaceAll("<b></b>", "");
		html = html.replaceAll("<p></p>", "");
		html = html.replaceAll("<h1[\\s|\\S]*?>[\\s|\\S]*?</h1>", "");
		html = html.replaceAll("style=\".*?\"", "");
		html = html.replaceAll("<img src=\"//", "<img src=\"http://");
		return html;
	}
}
