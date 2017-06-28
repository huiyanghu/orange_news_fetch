package com.it7890.orange.servlet;

import com.alibaba.fastjson.JSON;
import com.it7890.orange.fetch.FetchArticleDetailService;
import com.it7890.orange.util.*;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.misc.BASE64Decoder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLDecoder;
import java.util.*;

/**
 * Created by Astro on 17/6/20.
 */
public class CheckGrabDetailRuleServlet extends HttpServlet {

	private static final Logger logger = LoggerFactory.getLogger(CheckGrabListRuleServlet.class);


	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setHeader("Access-Control-Allow-Origin", "*");   // 允许跨域请求

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

		String grabDetailRuleStr = request.getParameter("grabDetailRuleJSON");
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
					String fetchUrl = grabDetailRuleMap.get("testUrl").trim();
					Connection conn = Jsoup.connect(fetchUrl);
					conn.userAgent(Constants.USER_AGENT);
					conn.validateTLSCertificates(false);
					conn.ignoreContentType(true);
					conn.ignoreHttpErrors(true);
					conn.timeout(50 * 1000);
					doc = conn.get();
					FetchArticleDetailService.cleanElement(doc, "script");
					FetchArticleDetailService.cleanElement(doc, "style");
					FetchArticleDetailService.cleanElement(doc, "ol");
					FetchArticleDetailService.cleanElement(doc, "noscript");
					FetchArticleDetailService.cleanElement(doc, "meta");

					if (StringUtil.isNotEmpty(grabDetailRuleMap.get("authorCssPath").trim())) {
						Elements authorEles = doc.select(grabDetailRuleMap.get("authorCssPath"));
						if (null != authorEles && authorEles.size() > 0) {
							author = authorEles.get(0).text();
						}
					}
					if (StringUtil.isNotEmpty(grabDetailRuleMap.get("titleCssPath").trim())) {
						Elements titleEles = doc.select(grabDetailRuleMap.get("titleCssPath").trim());
						if (null != titleEles && titleEles.size() > 0) {
							articleTitle = titleEles.get(0).text();
							logger.debug("文章标题： {}"+ articleTitle);
						}
					}
					if (StringUtil.isNotEmpty(grabDetailRuleMap.get("descCssPath").trim())) {
						Elements descEles = doc.select(grabDetailRuleMap.get("descCssPath").trim());
						if (null != descEles && descEles.size() > 0) {
							articleDescribe = descEles.get(0).text();
						}
					}
					if (StringUtil.isNotEmpty(grabDetailRuleMap.get("keywordCssPath").trim())) {
						Elements keywordEles = doc.select(grabDetailRuleMap.get("keywordCssPath").trim());
						if (null != keywordEles && keywordEles.size() > 0) {
							keyword = keywordEles.get(0).text();
						}
					}

					if (StringUtil.isNotEmpty(grabDetailRuleMap.get("conCssPath").trim())) {
						Elements contEles = doc.select(grabDetailRuleMap.get("conCssPath").trim());
						if (null != contEles && contEles.size() > 0) {
							// 移除特定规则的内容
							if (StringUtil.isNotEmpty(grabDetailRuleMap.get("replaceCssPath").trim())) {
								// 多个规则见用"&&"分隔
								String[] replaceCssList = grabDetailRuleMap.get("replaceCssPath").trim().split("&&");
								for (String replaceItem : replaceCssList) {
									if (StringUtil.isNotEmpty(replaceItem.trim())) {
										doc.select(replaceItem.trim()).remove();
									}
								}
							}

							// 文章内容中的图片
							Elements imgEles = contEles.select("img");
							if (null != imgEles && imgEles.size() > 0) {
								for (Element imgEle : imgEles) {
									String contentImageUrl = imgEle.attr("src");
									if (StringUtil.isNotEmpty(contentImageUrl)) {
										contentImageUrl = HtmlUtil.getRemoteUrl(fetchUrl, contentImageUrl);

										if (StringUtil.isNotEmpty(articleTitleImageUrl)) {
											articleTitleImageUrl = contentImageUrl;
										}
									}
								}
							}
							articleContent = FetchArticleDetailService.genTextOrHTMLByCssPath(doc, grabDetailRuleMap.get("conCssPath").trim(), fetchUrl, false);
							articleContent = HtmlUtil.cleanHtml(articleContent); // 移除文章内容中的超链接
						}
					}

					if (StringUtil.isNotEmpty(grabDetailRuleMap.get("conCssPath2").trim())) {
						Elements contEles = doc.select(grabDetailRuleMap.get("conCssPath2").trim());
						if (null != contEles && contEles.size() > 0) {
							// 移除特定规则的内容
							if (StringUtil.isNotEmpty(grabDetailRuleMap.get("replaceCssPath").trim())) {
								// 多个规则见用"&&"分隔
								String[] replaceCssList = grabDetailRuleMap.get("replaceCssPath").trim().split("&&");
								for (String replaceItem : replaceCssList) {
									if (StringUtil.isNotEmpty(replaceItem.trim())) {
										doc.select(replaceItem.trim()).remove();
									}
								}
							}

							// 文章内容中的图片
							Elements imgEles = contEles.select("img");
							logger.debug("文章内容中的图片>>>>>>{}"+ imgEles);
							if (null != imgEles && imgEles.size() > 0) {
								for (Element imgEle : imgEles) {
									String contentImageUrl = imgEle.attr("src");
									if (StringUtil.isNotEmpty(contentImageUrl)) {
										contentImageUrl = HtmlUtil.getRemoteUrl(fetchUrl, contentImageUrl);

										if (StringUtil.isNotEmpty(articleTitleImageUrl)) {
											articleTitleImageUrl = contentImageUrl;
										}
									}
								}
							}
							articleContent2 = FetchArticleDetailService.genTextOrHTMLByCssPath(doc, grabDetailRuleMap.get("conCssPath2").trim(), fetchUrl, false);
							articleContent2 = HtmlUtil.cleanHtml(articleContent2); // 移除文章内容中的超链接
						}
					}

					if (StringUtil.isNotEmpty(grabDetailRuleMap.get("conCssPath3").trim())) {
						Elements contEles = doc.select(grabDetailRuleMap.get("conCssPath3").trim());
						if (null != contEles && contEles.size() > 0) {
							// 移除特定规则的内容
							if (StringUtil.isNotEmpty(grabDetailRuleMap.get("replaceCssPath").trim())) {
								// 多个规则见用"&&"分隔
								String[] replaceCssList = grabDetailRuleMap.get("replaceCssPath").trim().split("&&");
								for (String replaceItem : replaceCssList) {
									if (StringUtil.isNotEmpty(replaceItem.trim())) {
										doc.select(replaceItem.trim()).remove();
									}
								}
							}

							// 文章内容中的图片
							Elements imgEles = contEles.select("img");
							if (null != imgEles && imgEles.size() > 0) {
								for (Element imgEle : imgEles) {
									String contentImageUrl = imgEle.attr("src");
									if (StringUtil.isNotEmpty(contentImageUrl)) {
										contentImageUrl = HtmlUtil.getRemoteUrl(fetchUrl, contentImageUrl);

										if (StringUtil.isEmpty(articleTitleImageUrl)) {
											articleTitleImageUrl = contentImageUrl;
										}
									}
								}
							}
							articleContent3 = FetchArticleDetailService.genTextOrHTMLByCssPath(doc, grabDetailRuleMap.get("conCssPath3").trim(), fetchUrl, false);
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

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}
}