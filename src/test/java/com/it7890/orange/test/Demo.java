package com.it7890.orange.test;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.it7890.orange.entity.ImageInfo;
import com.it7890.orange.util.ImageUtil;
import com.it7890.orange.util.StringUtil;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import top.coolook.utils.UserAgentUtil;

import java.io.IOException;

public class Demo {

	public static void main(String[] args) {
//		Connection conn = Jsoup.connect("https://cn.nytstyle.com/style/20170608/cold-brew-coffee/zh-hant/");

//		Connection conn = Jsoup.connect("https://cn.nytstyle.com/food-wine/zh-hant/");
//		conn.timeout(50 * 1000);
//		conn.ignoreContentType(true);
//		conn.ignoreHttpErrors(true);
//		conn.userAgent(UserAgentUtil.getUserAgent());
//		conn.validateTLSCertificates(false);
//		conn.timeout(60*1000);
//		try {
//			Document doc = conn.get();
//			System.out.println(doc);
//		} catch (IOException e) {
//			e.printStackTrace();
//		}

		String downloadMediaUrl = "https://images2.gamme.com.tw/news2/2016/75/60/pJyYo56cmJ6ZrqQ.jpg";
		byte[] imageBt = ImageUtil.downloadImageByteByUrl(downloadMediaUrl);

		// 文件后缀名
		String fileSuffix = ImageUtil.getSuffixByUrl(downloadMediaUrl);
		if (StringUtil.isEmpty(fileSuffix)) {
			return;
		}

		// 从二进制文件流中读取图片宽高属性
		ImageInfo imageInfo;
		if (fileSuffix.toLowerCase().contains(".gif")) {
			imageInfo = ImageUtil.getGifInfo(imageBt);
		} else {
			imageInfo = ImageUtil.getImageInfo(imageBt);
		}

		System.out.println(imageInfo);
	}
}
