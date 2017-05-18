package com.cki.spider.pro.controller;

import java.util.Set;

import com.cki.spider.pro.SpiderData;

public interface LinkResolver {

	Set<String> resolve(SpiderData data, String domain);

}
