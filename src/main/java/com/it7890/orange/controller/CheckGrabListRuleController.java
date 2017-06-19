package com.it7890.orange.controller;

import com.alibaba.fastjson.JSON;
import com.cki.fetch.util.SpiderUrlUtil;
import com.cki.spider.pro.Spider;
import com.cki.spider.pro.SpiderData;
import com.cki.spider.pro.SpiderUrl;
import com.it7890.orange.fetch.FetchArticleDetailService;
import com.it7890.orange.util.HtmlUtil;
import com.it7890.orange.util.ResponseUtil;
import com.it7890.orange.util.StringUtil;
import com.it7890.orange.util.UserAgentUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import sun.misc.BASE64Decoder;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Astro on 17/6/14.
 */
@Controller
public class CheckGrabListRuleController {

	private final static Logger logger = LogManager.getLogger(CheckGrabListRuleController.class);

	@Autowired
	private Spider<SpiderData> spider;

	@RequestMapping("/checkGrabListRule")
	@ResponseBody
	public void checkGrabListRule(@RequestParam(value = "grabListRuleJSON", defaultValue="") String grabListRuleStr, HttpServletResponse response) {
		int resultCode = 1;
		String resultMsg = "success";

		List<String> articleUrls = new ArrayList<>();
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
					String siteUrl = grabListRuleMap.get("siteUrl");
					if (siteUrl.toLowerCase().startsWith("https://")) {
						Connection conn = Jsoup.connect(siteUrl);
						conn.userAgent(UserAgentUtil.getUserAgent());
						conn.timeout(50 * 1000);
						doc = conn.post();
					}else {
						SpiderUrl fetchUrl = SpiderUrlUtil.buildSpiderUrl(siteUrl);
						fetchUrl.setConnectionTimeoutInMillis(100 * 1000);
						fetchUrl.setMaxExecutionTimeout(100 * 1000);
						String html = spider.fetchHtml(fetchUrl);
						doc = Jsoup.parse(html);
					}
					FetchArticleDetailService.cleanElement(doc, "script");
					FetchArticleDetailService.cleanElement(doc, "style");
					FetchArticleDetailService.cleanElement(doc, "ol");
					FetchArticleDetailService.cleanElement(doc, "noscript");
					FetchArticleDetailService.cleanElement(doc, "meta");

					Elements contentElements = doc.select(grabListRuleMap.get("cssPath"));
					logger.debug("content element size: {}"+ contentElements.size());

					if (contentElements.size() > 0) {
						if (StringUtil.isNotEmpty(grabListRuleMap.get("findPre"))) {
							Elements articleElements = contentElements.select("a[href~=" + grabListRuleMap.get("findPre") + "]");
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
}
