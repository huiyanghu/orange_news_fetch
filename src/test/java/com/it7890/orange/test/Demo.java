package com.it7890.orange.test;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import top.coolook.utils.UserAgentUtil;

import java.io.IOException;

public class Demo {

	public static void main(String[] args) {
		Connection conn = Jsoup.connect("https://www.wowlavie.com/life.php");
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
