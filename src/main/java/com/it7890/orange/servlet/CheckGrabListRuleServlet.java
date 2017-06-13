package com.it7890.orange.servlet;

import com.alibaba.fastjson.JSON;
import com.it7890.orange.util.ResponseUtil;
import com.it7890.orange.util.StringUtil;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
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
public class CheckGrabListRuleServlet extends HttpServlet {

	private static final Logger logger = LoggerFactory.getLogger(SearchServlet.class);


	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setHeader("Access-Control-Allow-Origin", "*");   // 允许跨域请求
		int resultCode = 1;
		String resultMsg = "success";

		List<String> articleUrls = new ArrayList<>();
		String grabListRuleStr = request.getParameter("grabListRuleJSON");
		logger.info("grabListJson: {}", grabListRuleStr);

		if (StringUtil.isNotEmpty(grabListRuleStr)) {
			Map<String, String> grabListRuleMap = JSON.parseObject(grabListRuleStr, Map.class);
			if (null != grabListRuleMap) {
				Connection con = Jsoup.connect(grabListRuleMap.get("siteUrl"));
				con.timeout(50 * 1000);
				Document doc = con.post();

				Elements contentElements = doc.select(grabListRuleMap.get("cssPath"));
				logger.debug("content element size: {}", contentElements.size());

				if (contentElements.size() > 0) {
					if (StringUtil.isNotEmpty(grabListRuleMap.get("findPre"))) {
						Elements articleElements = contentElements.get(0).select(grabListRuleMap.get("findPre"));
						for (Element articleElement : articleElements) {
							articleUrls.add(articleElement.attr("href"));
						}
					}
				}
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
