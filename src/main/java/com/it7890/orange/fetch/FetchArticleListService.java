package com.it7890.orange.fetch;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import com.avos.avoscloud.AVObject;
import com.cki.filter.UniqueService;
import com.it7890.orange.config.TpConfig;
import com.it7890.orange.dao.ConArticleDao;
import com.it7890.orange.dao.GrabDetailRuleDao;
import com.it7890.orange.dao.GrabListRuleDao;
import com.it7890.orange.entity.GrabDetailRule;
import com.it7890.orange.entity.GrabListRule;
import com.it7890.orange.entity.FetchArticle;
import com.it7890.orange.util.Constants;
import com.it7890.orange.util.HtmlUtil;
import com.it7890.orange.util.StringUtil;
import com.it7890.orange.util.UserAgentUtil;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.cki.fetch.util.SpiderUrlUtil;
import com.cki.spider.pro.Spider;
import com.cki.spider.pro.SpiderData;
import com.cki.spider.pro.SpiderUrl;
import com.cki.spider.pro.SpiderUrlListener;
import com.cki.spider.pro.util.NamedThreadFactory;
import org.springframework.stereotype.Component;

@Component
public class FetchArticleListService {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private Spider<SpiderData> spider;
	@Autowired
	private TpConfig tpConfig;
	@Resource
	private UniqueService uniqueService;
	@Resource
	private ConArticleDao conArticleDao;
	@Resource
	private GrabListRuleDao grabListRuleDao;
	@Resource
	private GrabDetailRuleDao grabDetailRuleDao;

	private ExecutorService startExecutor;
	private ExecutorService searchExecutor;
	private AtomicInteger atomicSearchSum;
	private AtomicInteger checkingSources;

	private Semaphore limit;
	
	public FetchArticleListService() {
		this.startExecutor = Executors.newSingleThreadScheduledExecutor(new NamedThreadFactory("startExecutor"));
		this.searchExecutor = Executors.newSingleThreadScheduledExecutor(new NamedThreadFactory("searchExecutor"));
		atomicSearchSum = new AtomicInteger(0);
		checkingSources = new AtomicInteger(0);
	}

	@PostConstruct
	public void init() {
		buildWaitGrabListRules();

//		this.startExecutor.execute(new Runnable() {
//			@Override
//			public void run() {
//				while (true) {
//					if (Thread.currentThread().isInterrupted()) {
//						return;
//					}
//
//					buildWaitGrabListRules();
//
//					try {
//						logger.info("FetchArticleListService task sleep!!!");
//						Thread.sleep(Constants.SLEEP_MILLIS);
//					} catch (InterruptedException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//				}
//			}
//		});

		this.limit = new Semaphore(tpConfig.getSpiderConn());
		this.searchExecutor.execute(new Runnable() {
			@Override
			public void run() {
				while (true) {
					if (Thread.currentThread().isInterrupted()) {
						return;
					}

					try {
						GrabListRule grabListRulInfo = Constants.FETCH_LIST_RULE_QUEUE.take();
						if (StringUtil.isNotEmpty(grabListRulInfo.getSiteUrl())) {
							logger.debug("prepare fetch source - {}", grabListRulInfo.getSiteUrl());
							urlSearch(grabListRulInfo);
						}
					} catch (InterruptedException e) {
						e.printStackTrace();
						logger.warn("waitFetchGrabListQueue take cause: {}", e);
					}
				}
			}
		});
	}

	private void urlSearch(final GrabListRule grabListRuleInfo) {
		try {
			limit.acquire();
		} catch (InterruptedException e1) {
			logger.error("interrupted.return", e1);
			return;
		}

		logger.info(" check new url search");
		final String siteUrl = grabListRuleInfo.getSiteUrl();
		if (siteUrl.toLowerCase().startsWith("https://")) {
			Connection conn = Jsoup.connect(siteUrl);
			conn.userAgent(UserAgentUtil.getUserAgent());
			conn.validateTLSCertificates(false);
			conn.ignoreContentType(true);
			conn.ignoreHttpErrors(true);
			conn.timeout(50 * 1000);
			try {
				Document document = conn.get();
				analysisArticleList(document, grabListRuleInfo, siteUrl);

			} catch (IOException e) {
				e.printStackTrace();
				logger.error("FetchArticleListService urlSearch exception，cause: {}", e);
			}
			finally {
				limit.release();
				atomicSearchSum.incrementAndGet();
			}
		} else {
			SpiderUrl fetchUrl = SpiderUrlUtil.buildSpiderUrl(siteUrl);

			fetchUrl.setConnectionTimeoutInMillis(50 * 1000);
			fetchUrl.setMaxExecutionTimeout(100 * 1000);
			// fetchUrl.setAttachment(pmUrl);
			spider.fetch(fetchUrl, new SpiderUrlListener() {
				@Override
				public void postFetch(SpiderUrl furl, SpiderData fd) {
					logger.debug(" fetch-->postFetch, furl:{}", furl.getUrl());
					limit.release();

					if (fd == null || fd.getCause() != null || fd.getStatusCode() != 200) {
						if (fd == null) {
							logger.warn(" fetch-->fd is null.furl:{},proxy:{}", furl.getUrl(), furl.getFetchProxy());
						} else {
							logger.warn(" fetch-->proxy failed,proxy:{},status:{},cause:{}", new Object[]{furl.getFetchProxy(), fd.getStatusCode(), fd.getCause()});
						}
						atomicSearchSum.incrementAndGet();
						return;
					}
					logger.debug("fetch result: {}, {}", new Object[]{furl, fd});
					String body = fd.getBody();
					Document document = Jsoup.parse(body);
					analysisArticleList(document, grabListRuleInfo, siteUrl);
					atomicSearchSum.incrementAndGet();
				}

				@Override
				public void preFetch(SpiderUrl furl) {

				}

				@Override
				public void refusedByFilter(SpiderUrl furl, SpiderData fd) {

				}
			});
		}
	}

	/**
	 * 解析文章列表
	 * @param doc
	 * @param grabListRulInfo
	 */
	private void analysisArticleList(Document doc, GrabListRule grabListRulInfo, String siteUrl) {
		if (null != doc && null != grabListRulInfo && StringUtil.isNotEmpty(siteUrl)) {
			if (StringUtil.isNotEmpty(grabListRulInfo.getTargetCssPath()) && (StringUtil.isNotEmpty(grabListRulInfo.getItemCssPath()) || StringUtil.isNotEmpty(grabListRulInfo.getItemLinkCssPath()))) {

				Pattern targetPattern = Pattern.compile(grabListRulInfo.getTargetCssPath().trim(), Pattern.DOTALL | Pattern.CASE_INSENSITIVE);
				Matcher targetMatcher = targetPattern.matcher(doc.html());

				if (targetMatcher.find()) {
					String targetHtml = targetMatcher.group(1);
					List<String> titleImageUrls;
					Matcher itemTitlePicMatcher;
					Matcher itemLinkMatcher;
					if (StringUtil.isNotEmpty(grabListRulInfo.getItemCssPath())) {
						Pattern itemPattern = Pattern.compile(grabListRulInfo.getItemCssPath().trim(), Pattern.DOTALL | Pattern.CASE_INSENSITIVE);
						Pattern itemLinkPattern = Pattern.compile(grabListRulInfo.getItemLinkCssPath().trim(), Pattern.DOTALL | Pattern.CASE_INSENSITIVE);
						Matcher itemMatcher = itemPattern.matcher(targetHtml);
						while (itemMatcher.find()) {
							String itemHtml = itemMatcher.group(0);

							if (StringUtil.isNotEmpty(itemHtml)) {
								itemLinkMatcher = itemLinkPattern.matcher(itemHtml);
								if (itemLinkMatcher.find()) {
									String articleUrl = itemLinkMatcher.group(1);
									logger.info("article url: {}", articleUrl);
									if (StringUtil.isNotEmpty(articleUrl)) {

										titleImageUrls = new ArrayList<>();
										if (StringUtil.isNotEmpty(grabListRulInfo.getItemTitlePicCssPath())) {
											String titleImageUrl;
											Pattern titlePicPattern = Pattern.compile(grabListRulInfo.getItemTitlePicCssPath().trim(), Pattern.DOTALL | Pattern.CASE_INSENSITIVE);
											itemTitlePicMatcher = titlePicPattern.matcher(itemHtml);
											while (itemTitlePicMatcher.find()) {
												titleImageUrl = itemTitlePicMatcher.group(1);
												if (StringUtil.isNotEmpty(titleImageUrl)) {
													titleImageUrls.add(titleImageUrl);
												}
											}
										}

										putFetchArticleQueue(grabListRulInfo, articleUrl, titleImageUrls);
									}
								}
							}
						}
					} else if (StringUtil.isNotEmpty(grabListRulInfo.getItemLinkCssPath())) {
						Pattern itemLinkPattern = Pattern.compile(grabListRulInfo.getItemLinkCssPath().trim(), Pattern.DOTALL | Pattern.CASE_INSENSITIVE);
						itemLinkMatcher = itemLinkPattern.matcher(targetHtml);
						while (itemLinkMatcher.find()) {
							String articleUrl = itemLinkMatcher.group(1);
							if (StringUtil.isNotEmpty(articleUrl)) {
								logger.info("article url: {}", articleUrl);
								titleImageUrls = new ArrayList<>();
								if (StringUtil.isNotEmpty(grabListRulInfo.getItemTitlePicCssPath())) {
									String titleImageUrl;
									Pattern titlePicPattern = Pattern.compile(grabListRulInfo.getItemTitlePicCssPath().trim(), Pattern.DOTALL | Pattern.CASE_INSENSITIVE);
									itemTitlePicMatcher = titlePicPattern.matcher(targetHtml);
									while (itemTitlePicMatcher.find()) {
										titleImageUrl = itemTitlePicMatcher.group(1);
										if (StringUtil.isNotEmpty(titleImageUrl)) {
											titleImageUrls.add(titleImageUrl);
										}
									}
								}

								putFetchArticleQueue(grabListRulInfo, articleUrl, titleImageUrls);
							}
						}
					}
				}
			} else if (StringUtil.isNotEmpty(grabListRulInfo.getFindPre())) {
				Elements contentElements = doc.select(grabListRulInfo.getCssPath());
				logger.debug("content element size: {}", contentElements.size());

				Elements articleElements = contentElements.select("a[href~=" + grabListRulInfo.getFindPre() + "]");
				for (Element articleElement : articleElements) {
					String articleDetailUrl = articleElement.attr("href");
					articleDetailUrl = HtmlUtil.getRemoteUrl(siteUrl, articleDetailUrl);

					logger.debug("article list url：{}", siteUrl);
					logger.debug("article url：{}", articleDetailUrl);

					List<String> titleImageUrls = new ArrayList<>();
					if (StringUtil.isNotEmpty(grabListRulInfo.getTitlePicCssPath())) {
						Elements imgEles = articleElement.select(grabListRulInfo.getTitlePicCssPath());
						for (Element imgEle : imgEles) {
							String titleImageUrl = imgEle.attr("src");
							if (StringUtil.isNotEmpty(titleImageUrl)) {
								titleImageUrl = StringUtil.urlEncode(titleImageUrl);
								titleImageUrls.add(titleImageUrl);
							}
						}
					}
					putFetchArticleQueue(grabListRulInfo, articleDetailUrl, titleImageUrls);
				}
			}
		}

		if (grabListRulInfo.getFetchNextPage() == 1 && StringUtil.isNotEmpty(grabListRulInfo.getNextPageCssPath())) {
			String nextUrl = "";
			Pattern pattern = Pattern.compile(grabListRulInfo.getNextPageCssPath(), Pattern.DOTALL | Pattern.CASE_INSENSITIVE);
			Matcher matcher = pattern.matcher(doc.html());
			if(matcher.find()) {
				nextUrl = matcher.group(1);
			}
			if (StringUtil.isNotEmpty(nextUrl)) {
				logger.debug("next page url ====================>>>>{}", nextUrl);
				try {
					grabListRulInfo.setSiteUrl(nextUrl);
					checkingSources.incrementAndGet();
					Constants.FETCH_LIST_RULE_QUEUE.put(grabListRulInfo);
					logger.debug("join in fetch query，url: {}", nextUrl);
				} catch (InterruptedException e) {
					e.printStackTrace();
					logger.warn("waitFetchGrabListQueue put cause: {}", e);
				}
			}
		}
	}

	private void putFetchArticleQueue(GrabListRule grabListRulInfo, String articleDetailUrl, List<String> titleImageUrls) {
		String urlSalt = StringUtil.getMD5(articleDetailUrl);
		boolean unique = uniqueService.feed(urlSalt);
		if (unique) {
			try {
				boolean articleExist = conArticleDao.getExistArticleBySalt(urlSalt);
				if (!articleExist) {
					logger.debug("find new article>>> {}", articleDetailUrl);

					FetchArticle fetchArticle = new FetchArticle();
					fetchArticle.setGrabDetailRuleInfo(grabListRulInfo.getGrabDetailRule());
					fetchArticle.setGrabListRule(grabListRulInfo);
					fetchArticle.setSourceUrl(articleDetailUrl);
					fetchArticle.setUrlSalt(urlSalt);
					fetchArticle.setOriginTitleImageUrls(titleImageUrls);
					Constants.FETCH_ARTICLE_QUEUE.put(fetchArticle);
					logger.info("Constants.FETCH_ARTICLE_QUEUE:{}, url: {}", Constants.FETCH_ARTICLE_QUEUE.size(), articleDetailUrl);
				} else {
					logger.info("article is exist, salt:{}", urlSalt);
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		} else {
			logger.info("article is exist, salt:{}", urlSalt);
		}
	}

	/**
	 * 构建待抓取列表规则
	 */
	private void buildWaitGrabListRules() {
		checkingSources.set(0);
		atomicSearchSum.set(0);
		List<AVObject> grabDetailRules = grabDetailRuleDao.findGrabDetailRules();
		List<AVObject> grabListRules = grabListRuleDao.findGrabListRules();
		logger.info("grabListRules: {},   grabDetailRules: {}", grabListRules.size(), grabDetailRules.size());

		GrabDetailRule grabDetailRule;
		GrabListRule grabListRule;

		AVObject tempGrabListRuleObj;
		for (AVObject grabDetailRuleObj : grabDetailRules) {
			tempGrabListRuleObj = grabDetailRuleObj.getAVObject("grabListRuleObj");

			grabDetailRule = new GrabDetailRule();
			grabDetailRule.setObjectId(grabDetailRuleObj.getObjectId());
			grabDetailRule.setTitleCssPath(StringUtil.isNotEmpty(grabDetailRuleObj.getString("titleCssPath")) ? grabDetailRuleObj.getString("titleCssPath").trim() : "");
			grabDetailRule.setDescCssPath(StringUtil.isNotEmpty(grabDetailRuleObj.getString("descCssPath")) ? grabDetailRuleObj.getString("descCssPath").trim() : "");
			grabDetailRule.setConCssPath(StringUtil.isNotEmpty(grabDetailRuleObj.getString("conCssPath")) ? grabDetailRuleObj.getString("conCssPath").trim() : "");
			grabDetailRule.setConCssPath2(StringUtil.isNotEmpty(grabDetailRuleObj.getString("conCssPath1")) ? grabDetailRuleObj.getString("conCssPath1").trim() : "");
			grabDetailRule.setConCssPath3(StringUtil.isNotEmpty(grabDetailRuleObj.getString("conCssPath2")) ? grabDetailRuleObj.getString("conCssPath2").trim() : "");
			grabDetailRule.setReplaceCssPath(StringUtil.isNotEmpty(grabDetailRuleObj.getString("replaceCssPath")) ? grabDetailRuleObj.getString("replaceCssPath").trim() : "");
			grabDetailRule.setSouCssPath(StringUtil.isNotEmpty(grabDetailRuleObj.getString("souCssPath")) ? grabDetailRuleObj.getString("souCssPath").trim() : "");
			grabDetailRule.setImgCssPath(StringUtil.isNotEmpty(grabDetailRuleObj.getString("imgCssPath")) ? grabDetailRuleObj.getString("imgCssPath").trim() : "");
			grabDetailRule.setVideoCssPath(StringUtil.isNotEmpty(grabDetailRuleObj.getString("videoCssPath")) ? grabDetailRuleObj.getString("videoCssPath").trim() : "");
			grabDetailRule.setAuthorCssPath(StringUtil.isNotEmpty(grabDetailRuleObj.getString("authorCssPath")) ? grabDetailRuleObj.getString("authorCssPath").trim() : "");
			grabDetailRule.setKeywordCssPath(StringUtil.isNotEmpty(grabDetailRuleObj.getString("keywordCssPath")) ? grabDetailRuleObj.getString("keywordCssPath").trim() : "");
			grabDetailRule.setTestUrl(StringUtil.isNotEmpty(grabDetailRuleObj.getString("testUrl")) ? grabDetailRuleObj.getString("testUrl").trim() : "");

			grabListRule = null;
			for (AVObject grabListRuleObj : grabListRules) {
				if (tempGrabListRuleObj.getObjectId().equals(grabListRuleObj.getObjectId())) {
					AVObject publicationObj = grabListRuleObj.getAVObject("publicationObj");
					AVObject nodeObj = grabListRuleObj.getAVObject("nodeObj");
					AVObject channelObj = grabListRuleObj.getAVObject("channelObj");
					AVObject languageObj = grabListRuleObj.getAVObject("languageObj");
					AVObject topicObj = grabListRuleObj.getAVObject("topicObj");
					AVObject countryObj = grabListRuleObj.getAVObject("countryObj");
					String siteUrl = grabListRuleObj.getString("siteUrl");

					if (StringUtil.isEmpty(siteUrl) || null == publicationObj || null == nodeObj || null == channelObj || null == languageObj || null == topicObj || null == countryObj || StringUtil.isEmpty(countryObj.getString("countryCode"))) {
						continue;
					}

					siteUrl = siteUrl.trim().toLowerCase();
					if (!(siteUrl.startsWith("https://") || siteUrl.startsWith("http://") || siteUrl.startsWith("www."))) {
						continue;
					}

					grabListRule = new GrabListRule();
					grabListRule.setSiteUrl(siteUrl);
					grabListRule.setObjectId(grabListRuleObj.getObjectId());
					grabListRule.setPublicationObj(publicationObj);
					grabListRule.setNodeObj(nodeObj);
					grabListRule.setChannelObj(channelObj);
					grabListRule.setLanguageObj(languageObj);
					grabListRule.setTopicObj(topicObj);
					grabListRule.setCountryCode(countryObj.getString("countryCode").trim());

					grabListRule.setRuleName(StringUtil.isNotEmpty(grabListRuleObj.getString("ruleName")) ? grabListRuleObj.getString("ruleName").trim() : "");
					grabListRule.setFindPre(StringUtil.isNotEmpty(grabListRuleObj.getString("findPre")) ? grabListRuleObj.getString("findPre").trim() : "");
					grabListRule.setConstant(StringUtil.isNotEmpty(grabListRuleObj.getString("constant")) ? grabListRuleObj.getString("constant").trim() : "");
					grabListRule.setKeywords(StringUtil.isNotEmpty(grabListRuleObj.getString("keywords")) ? grabListRuleObj.getString("keywords").trim() : "");
					grabListRule.setCssPath(StringUtil.isNotEmpty(grabListRuleObj.getString("cssPath")) ? grabListRuleObj.getString("cssPath").trim() : "");
					grabListRule.setNextPageCssPath(StringUtil.isNotEmpty(grabListRuleObj.getString("nextPageCssPath")) ? grabListRuleObj.getString("nextPageCssPath").trim() : "");
					grabListRule.setTitlePicCssPath(StringUtil.isNotEmpty(grabListRuleObj.getString("titlePicCssPath")) ? grabListRuleObj.getString("titlePicCssPath").trim() : "");
					grabListRule.setFetchNextPage(grabListRuleObj.getInt("fetchNextPage"));
					grabListRule.setItemCssPath(StringUtil.isNotEmpty(grabListRuleObj.getString("itemCssPath")) ? grabListRuleObj.getString("itemCssPath").trim() : "");
					grabListRule.setItemLinkCssPath(StringUtil.isNotEmpty(grabListRuleObj.getString("itemLinkCssPath")) ? grabListRuleObj.getString("itemLinkCssPath").trim() : "");
					grabListRule.setItemTitlePicCssPath(StringUtil.isNotEmpty(grabListRuleObj.getString("itemTitlePicCssPath")) ? grabListRuleObj.getString("itemTitlePicCssPath").trim() : "");
					grabListRule.setTargetCssPath(StringUtil.isNotEmpty(grabListRuleObj.getString("targetCssPath")) ? grabListRuleObj.getString("targetCssPath").trim() : "");
					break;
				}
			}

			if (null != grabListRule) {
				grabListRule.setGrabDetailRule(grabDetailRule);
				try {
					Constants.FETCH_LIST_RULE_QUEUE.put(grabListRule);
					checkingSources.incrementAndGet();
				} catch (InterruptedException e) {
					e.printStackTrace();
					logger.warn("waitFetchGrabListQueue put cause: {}", e);
				}
			}
		}


//		while (true) {
//			logger.info("atomicSearchSum value: {}, checkingSources value: {}", atomicSearchSum.intValue(), checkingSources.intValue());
//			if (atomicSearchSum.intValue() == checkingSources.intValue()) {
//				logger.info("search sleep!!");
//				break;
//			}
//
//
//			try {
//				Thread.sleep(1000 * 10);
//			} catch (InterruptedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
	}
}
