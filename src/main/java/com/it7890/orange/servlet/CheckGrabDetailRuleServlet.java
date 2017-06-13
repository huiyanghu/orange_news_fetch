package com.it7890.orange.servlet;

import com.alibaba.fastjson.JSON;
import com.it7890.orange.fetch.FetchArticleDetailService;
import com.it7890.orange.util.ResponseUtil;
import com.it7890.orange.util.StringUtil;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Astro on 17/6/12.
 */
public class CheckGrabDetailRuleServlet extends HttpServlet {

	private static final Logger logger = LoggerFactory.getLogger(SearchServlet.class);


	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setHeader("Access-Control-Allow-Origin", "*");   // 允许跨域请求
		int resultCode = 1;
		String resultMsg = "success";

		String author = "";
		String articleDescribe = "";
		String articleTitle = "";
		String articleContent = "";
		String keyword = "";
		String videoHtml = "";

		String grabDetailRuleStr = request.getParameter("grabDetailRuleJSON");
		logger.info("grabDetailJson: {}", grabDetailRuleStr);
		if (StringUtil.isNotEmpty(grabDetailRuleStr)) {
			Map<String, String> grabDetailRuleMap = JSON.parseObject(grabDetailRuleStr, Map.class);
			if (null != grabDetailRuleMap) {
				Connection con = Jsoup.connect(grabDetailRuleMap.get("testUrl"));
				con.timeout(50 * 1000);
				Document doc = con.post();
				List<String> contentImageUrls = new ArrayList<>();

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
						logger.debug("文章标题： {}", articleTitle);
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

				if (StringUtil.isNotEmpty(grabDetailRuleMap.get("conCssPath"))) {
					Elements contEles = doc.select(grabDetailRuleMap.get("conCssPath"));
					if (null != contEles && contEles.size() > 0) {
						// 移除特定规则的内容
						if (StringUtil.isNotEmpty(grabDetailRuleMap.get("replaceCssPath"))) {
							// 多个规则见用"&&"分隔
							String[] replaceCssList = grabDetailRuleMap.get("replaceCssPath").trim().split("&&");
							for (String replaceItem : replaceCssList) {
								contEles.select(replaceItem).remove();
							}
						}

						// 文章内容中的图片
						Elements imgEles = contEles.select("img");
						logger.debug("文章内容中的图片>>>>>>{}", imgEles);
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
									contentImageUrl = StringUtil.urlEncode(contentImageUrl);
									contentImageUrls.add(contentImageUrl);
								}
							}
							logger.info("文章内容图片>>>：{}", contentImageUrls);
						}
						articleContent = contEles.html();
						articleContent = FetchArticleDetailService.removeAElement(articleContent); // 移除文章内容中的超链接
					}
				}
			}
		}
		Map<String, Object> articleMap = new HashMap<>();
		articleMap.put("articleTitle", articleTitle);
		articleMap.put("keyword", keyword);
		articleMap.put("author", author);
		articleMap.put("videoHtml", videoHtml);
		articleMap.put("articleDescribe", articleDescribe);
		articleMap.put("articleContent", articleContent);

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
