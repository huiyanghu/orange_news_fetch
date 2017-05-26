package com.it7890.orange.qiniu.test;

import com.it7890.orange.entity.Topic;
import com.it7890.orange.entity.TopicFetch;
import com.it7890.orange.util.StringUtil;

import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Demo {

	public static void find(String input, Pattern p, Set<String> set) {
		int l = input.length();
		String output;
		for (int j = l; j >= 0; j--) {
			Matcher m = p.matcher(input.substring(0, j));
			while (m.find()) {
				int n = m.groupCount();
				for (int i = 1; i <= n; i++) {
					output = m.group(i);
					if (output != null) {
						set.add(output);
					}
				}
			}
		}
	}

	public static void main(String[] args) {
		String s = "<p>123<a href=\"http://www.esquire.tw/wp-content/uploads/2017/05/Bobii-Frutii南陽概念門市2.jpg\" rel=\"attachment wp-att-17027\" class=\"cb-lightbox\"><img class=\"aligncenter wp-image-17027 size-large\" src=\"http://www.esquire.tw/wp-content/uploads/2017/05/Bobii-Frutii南陽概念門市2-1024x683.jpg\" alt=\"\" width=\"750\" height=\"500\"></a></p>" +
				"<div id=\"attachment_17025\" style=\"width: 693px\" class=\"wp-caption aligncenter\"><a href=\"http://www.esquire.tw/wp-content/uploads/2017/05/Bobii-Frutii六月款限定新品.jpg\" rel=\"attachment wp-att-17025\" class=\"cb-lightbox\"><img class=\"wp-image-17025 size-large\" src=\"http://www.esquire.tw/wp-content/uploads/2017/05/Bobii-Frutii六月款限定新品-683x1024.jpg\" alt=\"\" width=\"683\" height=\"1024\"></a><p class=\"wp-caption-text\">適逢炎夏，品牌將在6/1推出限定新品，（左）紫曦佳人（紅茶歐蕾、烏龍茶凍、黑醋栗珍珠）_$90；（右）美好時光（黑醋栗檸檬冰沙+多多冰沙+蜂蜜蝶豆花茶凍+原味Bobii）_$100。</p></div>";
		System.out.println(s + "\n");

//		Pattern p = Pattern.compile("<a.*>(<img.*>)</a>");
		Pattern p = Pattern.compile(".*(<a.*>.*<img.*?>.*</a>)");
		Pattern p2 = Pattern.compile("<img.*?>");

		Set<String> set = new TreeSet<>();
		find(s, p, set);
		for (String output : set) {
//			System.out.println(output);
			Matcher matcher = p2.matcher(output);
			while (matcher.find()) {
//				System.out.println("------->: " + matcher.group());
				if (StringUtil.isNotEmpty(matcher.group())) {
					s = s.replace(output, matcher.group());
				}
			}
		}

		System.out.println(s);
	}
}
