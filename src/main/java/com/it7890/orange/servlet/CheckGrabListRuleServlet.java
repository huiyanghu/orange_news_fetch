package com.it7890.orange.servlet;

import com.alibaba.fastjson.JSON;
import com.cki.fetch.util.SpiderUrlUtil;
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
import sun.misc.BASE64Decoder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Astro on 17/6/20.
 */
public class CheckGrabListRuleServlet extends HttpServlet {

	private static final Logger logger = LoggerFactory.getLogger(CheckGrabListRuleServlet.class);


	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setHeader("Access-Control-Allow-Origin", "*");   // 允许跨域请求

		int resultCode = 1;
		String resultMsg = "success";

		List<String> articleUrls = new ArrayList<>();
		String grabListRuleStr = request.getParameter("grabListRuleJSON");
		logger.info("grabListJson111111: "+ grabListRuleStr);
		if (StringUtil.isNotEmpty(grabListRuleStr)) {
			BASE64Decoder decoder = new BASE64Decoder();
			try {
				// base64解码
				byte[] b = decoder.decodeBuffer(grabListRuleStr);
				grabListRuleStr = new String(b, "utf-8");
				// url解码
				grabListRuleStr = URLDecoder.decode(grabListRuleStr, "utf-8");
				logger.info("grabListJson result: "+ grabListRuleStr);

				Map<String, String> grabListRuleMap = JSON.parseObject(grabListRuleStr, Map.class);
				if (null != grabListRuleMap) {
					Document doc;
					String siteUrl = grabListRuleMap.get("siteUrl").trim();
					Connection conn = Jsoup.connect(siteUrl);
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

					Elements contentElements = doc.select(grabListRuleMap.get("cssPath").trim());
					logger.debug("content element size: {}"+ contentElements.size());

					if (contentElements.size() > 0) {
						if (StringUtil.isNotEmpty(grabListRuleMap.get("findPre").trim())) {
							Elements articleElements = contentElements.select("a[href~=" + grabListRuleMap.get("findPre").trim() + "]");
							for (Element articleElement : articleElements) {
								String articleUrl = articleElement.attr("href");
								articleUrl = HtmlUtil.getRemoteUrl(siteUrl, articleUrl);
								if (StringUtil.isNotEmpty(articleUrl)) {
									articleUrls.add(articleUrl);
								}
							}
						}
					}
				} else {
					logger.info("grabListJson result is empty");
				}
			} catch (IOException e) {
				e.printStackTrace();
				logger.warn("checkGrabListRule exception, cause:" + e);
			}
		}
		Map<String, Object> resultMap = new HashMap<String, Object>();
		resultMap.put("success", resultCode);
		resultMap.put("msg", resultMsg);
		resultMap.put("articleUrlList", articleUrls);

		ResponseUtil.printToJson(JSON.toJSONString(resultMap), response);
	}

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}
}