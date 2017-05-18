package com.cki.spider.pro.controller;

import java.util.Collections;
import java.util.Set;

import com.cki.spider.pro.SpiderData;
import com.cki.spider.pro.util.HtmlUtil;

public class AHrefBasedLinkResolver implements LinkResolver {

	@Override
	public Set<String> resolve(SpiderData data, String domain) {

		Set<String> allLinks = HtmlUtil.extractLinks(data.getFetchUrl().getUrl(), data.getBody(), domain);

		if (allLinks == null) {
			return Collections.emptySet();
		}

		return allLinks;
	}

}
