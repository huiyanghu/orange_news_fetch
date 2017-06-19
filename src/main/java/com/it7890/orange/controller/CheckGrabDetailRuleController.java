package com.it7890.orange.controller;

import com.alibaba.fastjson.JSON;
import com.cki.fetch.util.SpiderUrlUtil;
import com.cki.spider.pro.Spider;
import com.cki.spider.pro.SpiderData;
import com.cki.spider.pro.SpiderUrl;
import com.it7890.orange.fetch.FetchArticleDetailService;
import com.it7890.orange.util.*;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import sun.misc.BASE64Decoder;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLDecoder;
import java.util.*;

/**
 * Created by Astro on 17/6/14.
 */
@Controller
public class CheckGrabDetailRuleController {

	private final static Logger logger = LoggerFactory.getLogger(CheckGrabListRuleController.class);

	@Autowired
	private Spider<SpiderData> spider;

	@RequestMapping("/checkGrabDetailRule")
	@ResponseBody
	public void checkGrabDetailRule(@RequestParam(value = "grabDetailRuleJSON", defaultValue="") String grabDetailRuleStr, HttpServletResponse response) {
		int resultCode = 1;
		String resultMsg = "success";

		String author = "";
		String articleDescribe = "";
		String articleTitle = "";
		String articleTitleImageUrl = "";
		String articleContent = "";
		String articleContent2 = "";
		String articleContent3 = "";
		String keyword = "";
		String videoHtml = "";

		logger.info("grabDetailJson: {}"+ grabDetailRuleStr);
		if (StringUtil.isNotEmpty(grabDetailRuleStr)) {
			BASE64Decoder decoder = new BASE64Decoder();
			try {
				// base64解码
				byte[] b = decoder.decodeBuffer(grabDetailRuleStr);
				grabDetailRuleStr = new String(b, "utf-8");
				// url解码
				grabDetailRuleStr = URLDecoder.decode(grabDetailRuleStr, "utf-8");
				logger.info("grabDetailRuleStr: "+ grabDetailRuleStr);

				Map<String, String> grabDetailRuleMap = JSON.parseObject(grabDetailRuleStr, Map.class);
				if (null != grabDetailRuleMap) {
					Document doc;
					String fetchUrl = grabDetailRuleMap.get("testUrl");
					if (fetchUrl.toLowerCase().startsWith("https://")) {
						Connection conn = Jsoup.connect(fetchUrl);
						conn.userAgent(UserAgentUtil.getUserAgent());
						conn.validateTLSCertificates(false);
						conn.ignoreContentType(true);
						conn.ignoreHttpErrors(true);
						conn.timeout(50 * 1000);
						doc = conn.post();
					}else {
						SpiderUrl spiderUrl = SpiderUrlUtil.buildSpiderUrl(fetchUrl);
						spiderUrl.setConnectionTimeoutInMillis(50 * 1000);
						spiderUrl.setMaxExecutionTimeout(100 * 1000);
						doc = Jsoup.parse(spider.fetchHtml(spiderUrl));
					}
					FetchArticleDetailService.cleanElement(doc, "script");
					FetchArticleDetailService.cleanElement(doc, "style");
					FetchArticleDetailService.cleanElement(doc, "ol");
					FetchArticleDetailService.cleanElement(doc, "noscript");
					FetchArticleDetailService.cleanElement(doc, "meta");

					if (StringUtil.isNotEmpty(grabDetailRuleMap.get("authorCssPath"))) {
						Elements authorEles = doc.select(grabDetailRuleMap.get("authorCssPath"));
						if (null != authorEles && authorEles.size() > 0) {
							author = authorEles.get(0).text();
						}
					}
					if (StringUtil.isNotEmpty(grabDetailRuleMap.get("titleCssPath"))) {
						Elements titleEles = doc.select(grabDetailRuleMap.get("titleCssPath"));
						if (null != titleEles && titleEles.size() > 0) {
							articleTitle = titleEles.get(0).text();
							logger.debug("文章标题： {}"+ articleTitle);
						}
					}
					if (StringUtil.isNotEmpty(grabDetailRuleMap.get("descCssPath"))) {
						Elements descEles = doc.select(grabDetailRuleMap.get("descCssPath"));
						if (null != descEles && descEles.size() > 0) {
							articleDescribe = descEles.get(0).text();
						}
					}
					if (StringUtil.isNotEmpty(grabDetailRuleMap.get("keywordCssPath"))) {
						Elements keywordEles = doc.select(grabDetailRuleMap.get("keywordCssPath"));
						if (null != keywordEles && keywordEles.size() > 0) {
							keyword = keywordEles.get(0).text();
						}
					}

					logger.info("grabDetailRuleMap.get(\"conCssPath\")  " + grabDetailRuleMap.get("conCssPath"));
					if (StringUtil.isNotEmpty(grabDetailRuleMap.get("conCssPath"))) {
						Elements contEles = doc.select(grabDetailRuleMap.get("conCssPath"));
						logger.info("contEles:     " + contEles);
						if (null != contEles && contEles.size() > 0) {
							// 移除特定规则的内容
							if (StringUtil.isNotEmpty(grabDetailRuleMap.get("replaceCssPath"))) {
								// 多个规则见用"&&"分隔
								String[] replaceCssList = grabDetailRuleMap.get("replaceCssPath").trim().split("&&");
								for (String replaceItem : replaceCssList) {
									doc.select(replaceItem).remove();
								}
							}

							// 文章内容中的图片
							Elements imgEles = contEles.select("img");
							logger.debug("文章内容中的图片>>>>>>{}"+ imgEles);
							if (null != imgEles && imgEles.size() > 0) {
								for (Element imgEle : imgEles) {
									Attributes imgAttrs = imgEle.attributes();
									// 删除标签中多余属性
									for (Attribute imgAttr : imgAttrs) {
										String attrKey = imgAttr.getKey();
										if (!"src".equals(attrKey.toLowerCase())) {
											imgAttrs.remove(attrKey);
										}
									}
									String contentImageUrl = imgEle.attr("src");
									if (StringUtil.isNotEmpty(contentImageUrl)) {
										contentImageUrl = HtmlUtil.getRemoteUrl(fetchUrl, contentImageUrl);

										if (StringUtil.isEmpty(articleTitleImageUrl)) {
											articleTitleImageUrl = contentImageUrl;
										}
									}
								}
							}
							articleContent = FetchArticleDetailService.genTextOrHTMLByCssPath(doc, grabDetailRuleMap.get("conCssPath"), fetchUrl, false);
							articleContent = HtmlUtil.cleanHtml(articleContent); // 移除文章内容中的超链接
						}
					}

					logger.info("2222222222222221232");
					if (StringUtil.isNotEmpty(grabDetailRuleMap.get("conCssPath2"))) {
						Elements contEles = doc.select(grabDetailRuleMap.get("conCssPath2"));
						if (null != contEles && contEles.size() > 0) {
							// 移除特定规则的内容
							if (StringUtil.isNotEmpty(grabDetailRuleMap.get("replaceCssPath"))) {
								// 多个规则见用"&&"分隔
								String[] replaceCssList = grabDetailRuleMap.get("replaceCssPath").trim().split("&&");
								for (String replaceItem : replaceCssList) {
									doc.select(replaceItem).remove();
								}
							}

							// 文章内容中的图片
							Elements imgEles = contEles.select("img");
							logger.debug("文章内容中的图片>>>>>>{}"+ imgEles);
							if (null != imgEles && imgEles.size() > 0) {
								for (Element imgEle : imgEles) {
									Attributes imgAttrs = imgEle.attributes();
									// 删除标签中多余属性
									for (Attribute imgAttr : imgAttrs) {
										String attrKey = imgAttr.getKey();
										if (!"src".equals(attrKey.toLowerCase())) {
											imgAttrs.remove(attrKey);
										}
									}
									String contentImageUrl = imgEle.attr("src");
									if (StringUtil.isNotEmpty(contentImageUrl)) {
										contentImageUrl = HtmlUtil.getRemoteUrl(fetchUrl, contentImageUrl);

										if (StringUtil.isEmpty(articleTitleImageUrl)) {
											articleTitleImageUrl = contentImageUrl;
										}
									}
								}
							}
							articleContent2 = FetchArticleDetailService.genTextOrHTMLByCssPath(doc, grabDetailRuleMap.get("conCssPath2"), fetchUrl, false);
							articleContent2 = HtmlUtil.cleanHtml(articleContent2); // 移除文章内容中的超链接
						}
					}

					logger.info("3333333333333333333333");
					if (StringUtil.isNotEmpty(grabDetailRuleMap.get("conCssPath3"))) {
						Elements contEles = doc.select(grabDetailRuleMap.get("conCssPath3"));
						if (null != contEles && contEles.size() > 0) {
							// 移除特定规则的内容
							if (StringUtil.isNotEmpty(grabDetailRuleMap.get("replaceCssPath"))) {
								// 多个规则见用"&&"分隔
								String[] replaceCssList = grabDetailRuleMap.get("replaceCssPath").trim().split("&&");
								for (String replaceItem : replaceCssList) {
									doc.select(replaceItem).remove();
								}
							}

							// 文章内容中的图片
							Elements imgEles = contEles.select("img");
							logger.debug("文章内容中的图片>>>>>>{}"+ imgEles);
							if (null != imgEles && imgEles.size() > 0) {
								for (Element imgEle : imgEles) {
									Attributes imgAttrs = imgEle.attributes();
									// 删除标签中多余属性
									for (Attribute imgAttr : imgAttrs) {
										String attrKey = imgAttr.getKey();
										if (!"src".equals(attrKey.toLowerCase())) {
											imgAttrs.remove(attrKey);
										}
									}
									String contentImageUrl = imgEle.attr("src");
									if (StringUtil.isNotEmpty(contentImageUrl)) {
										contentImageUrl = HtmlUtil.getRemoteUrl(fetchUrl, contentImageUrl);

										if (StringUtil.isEmpty(articleTitleImageUrl)) {
											articleTitleImageUrl = contentImageUrl;
										}
									}
								}
							}
							articleContent3 = FetchArticleDetailService.genTextOrHTMLByCssPath(doc, grabDetailRuleMap.get("conCssPath3"), fetchUrl, false);
							articleContent3 = HtmlUtil.cleanHtml(articleContent3); // 移除文章内容中的超链接
						}
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		Map<String, Object> articleMap = new HashMap<>();
		articleMap.put("articleTitle", articleTitle);
		articleMap.put("articleTitleImage", articleTitleImageUrl);
		articleMap.put("keyword", keyword);
		articleMap.put("author", author);
		articleMap.put("videoHtml", videoHtml);
		articleMap.put("articleDescribe", articleDescribe);
		articleMap.put("articleContent", articleContent);
		articleMap.put("articleContent2", articleContent2);
		articleMap.put("articleContent3", articleContent3);
		articleMap.put("updatedAt", DateUtil.formatFromDate(new Date()));

		Map<String, Object> resultMap = new HashMap<>();
		resultMap.put("success", resultCode);
		resultMap.put("msg", resultMsg);
		resultMap.put("articleJson", articleMap);

		ResponseUtil.printToJson(JSON.toJSONString(resultMap), response);
	}
}
