package com.cki.spider.pro.util;

import java.net.MalformedURLException;
import java.net.URL;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HtmlUtil {

	public static Pattern ALinkPattern = Pattern.compile("<a.*?href=('|\")(.*?)('|\").*?>", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);

	public static String getUrlBase(String url) {

		int index = url.indexOf('/', 8);

		if (index == -1) {
			return url;
		}

		return url.substring(0, index);
	}

	public static String getUrlParentDirectory(String url) {

		int index = url.lastIndexOf('/');

		if (index == -1 || index < 8) {
			return url;
		}

		return url.substring(0, index);
	}

	public static Set<String> extractLinks(String html) {

		Matcher m = ALinkPattern.matcher(html);

		Set<String> links = new HashSet<String>();

		while (m.find()) {
			links.add(m.group(2));
		}

		if (links.isEmpty()) {
			return links;
		}

		return links;
	}

	public static Set<String> extractAbsoluteLinks(String url, String html) {

		if (html == null || html.equals("")) {
			return Collections.emptySet();
		}

		Set<String> links = extractLinks(html);

		if (links.isEmpty()) {
			return links;
		}

		Set<String> result = new HashSet<String>();

		String base = getUrlBase(url);

		String parentDir = getUrlParentDirectory(url);

		for (String link : links) {

			String llink = link.toLowerCase();

			if (llink.startsWith("http://") || llink.startsWith("https://")) {
				result.add(link);
				continue;
			}

			if (link.startsWith("/")) {
				result.add(base + link);
				continue;
			}

			result.add(parentDir + "/" + link);

		}

		return result;
	}

	public static Set<String> extractLinks(String url, String html, String domain) {

		if (html == null || html.equals("")) {
			return Collections.emptySet();
		}

		Set<String> links = extractLinks(html);

		if (links.isEmpty()) {
			return links;
		}

		Set<String> result = new HashSet<String>();

		String base = getUrlBase(url);

		String parentDir = getUrlParentDirectory(url);

		for (String link : links) {

			String llink = link.toLowerCase();

			if (llink.startsWith("http://") || llink.startsWith("https://")) {

				String linkDomain = getDomain(link);

				if (linkDomain.endsWith(domain)) {
					result.add(link);
				}

				continue;
			}

			if (link.startsWith("/")) {
				result.add(base + link);
				continue;
			}

			result.add(parentDir + "/" + link);

		}

		return result;
	}

	public static String getDomain(String url) {

		try {
			URL u = new URL(url);

			return u.getHost();

		} catch (MalformedURLException e) {

			int firstIndex = url.indexOf(':');

			if (firstIndex == -1) {
				firstIndex = 0;
			}

			int lastIndex = url.indexOf('/', 8);

			if (lastIndex == -1) {
				lastIndex = url.length();
			}

			return url.substring(firstIndex, lastIndex);
		}
	}

	public static boolean isAbsoluteUrl(String url) {

		if (url == null) {
			return false;
		}

		if (url.length() < "http://".length()) {
			return false;
		}

		String lower = url.toLowerCase();

		if (lower.startsWith("http://") || lower.startsWith("https://")) {
			return true;
		}

		return false;
	}

}
