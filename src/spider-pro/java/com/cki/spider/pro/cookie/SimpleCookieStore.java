package com.cki.spider.pro.cookie;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.cki.spider.pro.util.HtmlUtil;

public class SimpleCookieStore implements CookieStore {

	private Map<String, Set<Cookie>> store;

	private int maxStoreCookies;

	private int maxLiveTime;

	private int counter;

	public SimpleCookieStore(int maxStoreCookies, int maxLiveTime) {

		this.store = new HashMap<String, Set<Cookie>>();
		this.maxStoreCookies = maxStoreCookies;
		this.maxLiveTime = maxLiveTime;
		this.counter = 0;
	}

	@Override
	public void init() {

	}

	@Override
	public synchronized void add(Cookie cookie) {

		this.counter++;

		if (this.counter > this.maxStoreCookies) {

			flushExpires();

			if (this.counter > this.maxStoreCookies) {

				throw new RuntimeException("exceed max cookie store:" + this.maxStoreCookies);

			}

		}

		Set<Cookie> set = store.get(cookie.getDomain());

		if (set == null) {

			set = new HashSet<Cookie>();

			store.put(cookie.getDomain(), set);

		}

		Iterator<Cookie> iterator = set.iterator();

		while (iterator.hasNext()) {

			Cookie c = iterator.next();

			if (c.getExpireTime() != -1 && (c.getExpireTime() <= System.currentTimeMillis())) {
				iterator.remove();
				continue;
			}

			if (c.getName().equals(cookie.getName()) && c.getPath().equals(cookie.getPath())) {

				c.setValue(cookie.getValue());

				c.setAddTime(System.currentTimeMillis());
				c.setExpireTime(cookie.getExpireTime());
				c.setSecure(cookie.isSecure());

				return;

			}
		}

		set.add(cookie);

	}

	@Override
	public synchronized void close() {

		if (!this.store.isEmpty()) {
			this.store.clear();
		}

		this.store = null;
	}

	@Override
	public List<Cookie> find(String url) {

		if (url == null) {
			return Collections.emptyList();
		}

		Set<String> domains = new HashSet<String>();

		String d = HtmlUtil.getDomain(url);

		domains.add(d);

		if (!d.startsWith("www")) {

			domains.add("." + d);
		}

		if (d.startsWith("www")) {
			domains.add(d.substring(3));
		}

		boolean secure = url.toLowerCase().startsWith("https://");

		List<Cookie> result = new ArrayList<Cookie>();

		for (String domain : domains) {

			Set<Cookie> set = this.store.get(domain);

			if (set == null || set.isEmpty()) {
				continue;
			}

			for (Cookie c : set) {

				if ((c.getExpireTime() != -1) && (c.getExpireTime() <= System.currentTimeMillis())) {
					continue;
				}

				if (!c.getPath().equals("/")) {

					if (!url.contains(c.getPath())) {
						continue;
					}

				}

				if (secure && !c.isSecure()) {
					continue;
				}

				result.add(c);

			}

		}

		return result;
	}

	public String[] getAllPossibleDomains(String domain) {

		String[] segs = domain.split("\\.");

		String[] domains = new String[segs.length - 1];

		for (int i = 0; i < domains.length; i++) {
			StringBuilder d = new StringBuilder();

			for (int j = i; j < segs.length; j++) {

				d.append(".").append(segs[j]);

			}

			domains[i] = d.toString();
		}

		return domains;

	}

	@Override
	public synchronized void flush() {

		this.store.clear();

	}

	@Override
	public synchronized void flushExpires() {

		throw new UnsupportedOperationException("not implemented");

	}

}
