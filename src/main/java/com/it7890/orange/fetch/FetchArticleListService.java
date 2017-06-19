package com.it7890.orange.fetch;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicLong;

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
import com.it7890.orange.util.StringUtil;
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

	private ExecutorService searchExecutor;
	private Semaphore limit;
	private AtomicLong atomicSum;
	
	public FetchArticleListService() {
		this.searchExecutor = Executors.newSingleThreadScheduledExecutor(new NamedThreadFactory("searchExecutor"));
		atomicSum = new AtomicLong(0);
	}

	@PostConstruct
	public void init() {
		logger.info("FetchArticleListService init");
		this.limit = new Semaphore(tpConfig.getSpiderConn());

		this.searchExecutor.execute(new Runnable() {
			@Override
			public void run() {
				while (true) {
					if (Thread.currentThread().isInterrupted()) {
						return;
					}
					buildWaitGrabListRules();
					logger.info("Constants.FETCH_LIST_RULE_QUEUE ================>: {}", Constants.FETCH_LIST_RULE_QUEUE.size());

					while (Constants.FETCH_LIST_RULE_QUEUE.size() > 0) {
						try {
							GrabListRule grabListRulInfo = Constants.FETCH_LIST_RULE_QUEUE.take();
							if (StringUtil.isNotEmpty(grabListRulInfo.getSiteUrl())) {
								logger.debug("准备抓取数据源 - {}", grabListRulInfo.getSiteUrl());
//								urlSearch(grabListRulInfo);
							}
						} catch (InterruptedException e) {
							e.printStackTrace();
							logger.warn("waitFetchGrabListQueue take cause: {}", e);
						}
					}


					// 暂停任务
					try {
						logger.info("暂停任务");
						Thread.sleep(Constants.SLEEP_MILLIS);
					} catch (InterruptedException e) {
						e.printStackTrace();
						logger.warn("暂停任务失败, cause: {}", e);
					}
				}
			}
		});
	}

	private void urlSearch(final GrabListRule grabListRulInfo) {
		logger.debug(" check new url search");
		try {
			limit.acquire();
		} catch (InterruptedException e1) {
			logger.error("interrupted.return", e1);
			return;
		}
		SpiderUrl fetchUrl = SpiderUrlUtil.buildSpiderUrl(grabListRulInfo.getSiteUrl());

		fetchUrl.setConnectionTimeoutInMillis(50 * 1000);
		fetchUrl.setMaxExecutionTimeout(100 * 1000);
		// fetchUrl.setAttachment(pmUrl);

		spider.fetch(fetchUrl, new SpiderUrlListener() {
			@Override
			public void postFetch(SpiderUrl furl, SpiderData fd) {
				limit.release();
				logger.debug(" fetch-->postFetch, furl:{}", furl.getUrl());

				if (fd == null || fd.getCause() != null || fd.getStatusCode() != 200) {
					if (fd == null) {
						logger.warn(" fetch-->fd is null.furl:{},proxy:{}", furl.getUrl(), furl.getFetchProxy());
					} else {
						logger.warn(" fetch-->proxy failed,proxy:{},status:{},cause:{}", new Object[]{furl.getFetchProxy(), fd.getStatusCode(), fd.getCause()});
					}
					atomicSum.incrementAndGet();
					return;
				}
				logger.debug("fetch result: {}, {}", new Object[] {furl, fd});
				String body = fd.getBody();

				if (StringUtil.isNotEmpty(body)) {
					Document doc = Jsoup.parse(body);
					Elements contentElements = doc.select(grabListRulInfo.getCssPath());
					logger.debug("content element size: {}", contentElements.size());

					if (contentElements.size() > 0) {
						if (StringUtil.isNotEmpty(grabListRulInfo.getFindPre())) {
//							Elements articleElements = contentElements.select(grabListRulInfo.getFindPre());
							Elements articleElements = contentElements.select("a[href~=" + grabListRulInfo.getFindPre() + "]");
							for (Element articleElement : articleElements) {
								String articleDetailUrl = articleElement.attr("href");
								logger.debug("抓取到文章详情页url：{}", articleDetailUrl);

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
									logger.debug("抓取到文章标题图片：{}", titleImageUrls);
								}

								String urlSalt = StringUtil.getMD5(articleDetailUrl);
								boolean unique = uniqueService.feed(urlSalt);
								if(unique) {
									try {
										boolean articleExist = conArticleDao.getExistArticleBySalt(urlSalt);
										if (!articleExist) {
											logger.debug("发现新文章>>> {}", articleDetailUrl);

											FetchArticle fetchArticle = new FetchArticle();
											fetchArticle.setGrabDetailRuleInfo(grabListRulInfo.getGrabDetailRule());
											fetchArticle.setGrabListRule(grabListRulInfo);
											fetchArticle.setSourceUrl(articleDetailUrl);
											fetchArticle.setUrlSalt(urlSalt);
											fetchArticle.setOriginTitleImageUrls(titleImageUrls);
											Constants.FETCH_ARTICLE_QUEUE.put(fetchArticle);
										}
									} catch (InterruptedException e) {
										e.printStackTrace();
									}
								}
							}
						}
					}

					if (StringUtil.isNotEmpty(grabListRulInfo.getNextPageCssPath())) {
						Elements nextElements = doc.select(grabListRulInfo.getNextPageCssPath());
						if (nextElements.size() > 0) {
							String nextPageUrl = nextElements.get(0).attr("href");
							logger.debug("获取到下一页url ====================>>>>{}", nextPageUrl);
							try {
								grabListRulInfo.setSiteUrl(nextPageUrl);
								Constants.FETCH_LIST_RULE_QUEUE.put(grabListRulInfo);
								logger.debug("加入到待抓取列表队列中，url: {}", nextPageUrl);
							} catch (InterruptedException e) {
								e.printStackTrace();
								logger.warn("waitFetchGrabListQueue put cause: {}", e);
							}
						} else {
							logger.info("没有检测到有下一页url");
						}
					}
				}
				
				atomicSum.incrementAndGet();
			}

			@Override
			public void preFetch(SpiderUrl furl) {

			}

			@Override
			public void refusedByFilter(SpiderUrl furl, SpiderData fd) {

			}
		});
	}

	/**
	 * 构建待抓取列表规则
	 */
	private void buildWaitGrabListRules() {
		List<AVObject> grabDetailRules = grabDetailRuleDao.findGrabDetailRules();
		logger.info("grabDetailRules >>>>>>>>: {}", grabDetailRules.size());
		List<AVObject> grabListRules = grabListRuleDao.findGrabListRules();
		logger.info("grabListRules >>>>>>>>: {}", grabListRules.size());

		GrabDetailRule grabDetailRule;
		GrabListRule grabListRule;
		for (AVObject grabListRuleObj : grabListRules) {
			grabListRule = new GrabListRule();
			grabListRule.setObjectId(grabListRuleObj.getObjectId());
			grabListRule.setPublicationObj(grabListRuleObj.getAVObject("publicationObj"));
			grabListRule.setNodeObj(grabListRuleObj.getAVObject("nodeObj"));
			grabListRule.setChannelObj(grabListRuleObj.getAVObject("channelObj"));
			grabListRule.setLanguageObj(grabListRuleObj.getAVObject("languageObj"));
			grabListRule.setTopicObj(grabListRuleObj.getAVObject("topicObj"));

			grabListRule.setRuleName(grabListRuleObj.getString("ruleName"));
			grabListRule.setSiteUrl(grabListRuleObj.getString("siteUrl"));
			grabListRule.setCountryCode(grabListRuleObj.getString("countryCode"));
			grabListRule.setFindPre(grabListRuleObj.getString("findPre"));
			grabListRule.setConstant(grabListRuleObj.getString("constant"));
			grabListRule.setKeywords(grabListRuleObj.getString("keywords"));
			grabListRule.setCssPath(grabListRuleObj.getString("cssPath"));
			grabListRule.setNextPageCssPath(grabListRuleObj.getString("nextPageCssPath"));
			grabListRule.setTitlePicCssPath(grabListRuleObj.getString("titlePicCssPath"));
			grabListRule.setGrabTime(grabListRuleObj.getInt("grabTime"));

			grabDetailRule = null;
			AVObject tempGrabListRuleObj;
			for (AVObject grabDetailRuleObj : grabDetailRules) {
				tempGrabListRuleObj = grabDetailRuleObj.getAVObject("grabListRuleObj");
				if (null!=tempGrabListRuleObj && tempGrabListRuleObj.getObjectId().equals(grabListRuleObj.getObjectId())) {
					grabDetailRule = new GrabDetailRule();
					grabDetailRule.setObjectId(grabDetailRuleObj.getObjectId());
					grabDetailRule.setGrabListRuleObj(grabDetailRuleObj.getAVObject("grabListRuleObj"));
					grabDetailRule.setTitleCssPath(grabDetailRuleObj.getString("titleCssPath"));
					grabDetailRule.setDescCssPath(grabDetailRuleObj.getString("descCssPath"));
					grabDetailRule.setConCssPath(grabDetailRuleObj.getString("conCssPath"));
					grabDetailRule.setReplaceCssPath(grabDetailRuleObj.getString("replaceCssPath"));
					grabDetailRule.setSouCssPath(grabDetailRuleObj.getString("souCssPath"));
					grabDetailRule.setImgCssPath(grabDetailRuleObj.getString("imgCssPath"));
					grabDetailRule.setVideoCssPath(grabDetailRuleObj.getString("videoCssPath"));
					grabDetailRule.setAuthorCssPath(grabDetailRuleObj.getString("authorCssPath"));
					grabDetailRule.setKeywordCssPath(grabDetailRuleObj.getString("keywordCssPath"));
					grabDetailRule.setTestUrl(grabDetailRuleObj.getString("testUrl"));
					break;
				}
			}
			if (null != grabDetailRule) {
				grabListRule.setGrabDetailRule(grabDetailRule);
				try {
					Constants.FETCH_LIST_RULE_QUEUE.put(grabListRule);
				} catch (InterruptedException e) {
					e.printStackTrace();
					logger.warn("waitFetchGrabListQueue put cause: {}", e);
				}
			}
		}
	}
}
