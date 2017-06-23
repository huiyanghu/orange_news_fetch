package com.it7890.orange.test;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import top.coolook.utils.UserAgentUtil;

import java.io.IOException;

public class Demo {

	public static void main(String[] args) {
//		Connection conn = Jsoup.connect("https://cn.nytstyle.com/style/20170608/cold-brew-coffee/zh-hant/");
		Connection conn = Jsoup.connect("https://cn.nytstyle.com/");
		conn.timeout(50 * 1000);
		conn.ignoreContentType(true);
		conn.ignoreHttpErrors(true);
		conn.userAgent(UserAgentUtil.getUserAgent());
		conn.validateTLSCertificates(false);
		conn.timeout(60*1000);
		try {
			Document doc = conn.get();
			System.out.println(doc);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
