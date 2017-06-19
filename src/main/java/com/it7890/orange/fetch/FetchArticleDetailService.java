package com.it7890.orange.fetch;

import com.avos.avoscloud.AVObject;
import com.cki.fetch.util.SpiderUrlUtil;
import com.cki.spider.pro.Spider;
import com.cki.spider.pro.SpiderData;
import com.cki.spider.pro.SpiderUrl;
import com.cki.spider.pro.SpiderUrlListener;
import com.cki.spider.pro.util.NamedThreadFactory;
import com.it7890.orange.config.TpConfig;
import com.it7890.orange.dao.ConArticleDao;
import com.it7890.orange.entity.GrabDetailRule;
import com.it7890.orange.entity.FetchArticle;
import com.it7890.orange.entity.GrabListRule;
import com.it7890.orange.service.ArticleService;
import com.it7890.orange.util.Constants;
import com.it7890.orange.util.HtmlUtil;
import com.it7890.orange.util.StringUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicLong;

@Component
public class FetchArticleDetailService {

	private static final Logger logger = LoggerFactory.getLogger(FetchArticleDetailService.class);

	@Autowired
	private Spider<SpiderData> spider;
	@Autowired
	private TpConfig tpConfig;

	@Resource
	private ConArticleDao conArticleDao;
	@Resource
	private ArticleService articleService;

	private ExecutorService articleDetailServiceExecutor;

	private AtomicLong atomicSum;
	private Semaphore limit;

	public FetchArticleDetailService() {
		this.articleDetailServiceExecutor = Executors.newSingleThreadScheduledExecutor(new NamedThreadFactory("articleDetailServiceExecutor"));
		atomicSum = new AtomicLong(0);
	}

	@PostConstruct
	public void init() {
		this.limit = new Semaphore(tpConfig.getSpiderConn());
		this.articleDetailServiceExecutor.execute(new Runnable() {
			@Override
			public void run() {
				while (true) {
					if (Thread.currentThread().isInterrupted()) {
						return;
					}

					FetchArticle fetchArticleInfo = null;
					try {
						fetchArticleInfo = Constants.FETCH_ARTICLE_QUEUE.take();
						logger.debug("fetchArticleDetailService fetchArticleInfo>>> {}", fetchArticleInfo);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}

					if(null != fetchArticleInfo) {
						urlSearch(fetchArticleInfo);
					}
				}
			}
		});		
	}

	private void urlSearch(final FetchArticle fetchArticleInfo) {
		try {
			limit.acquire();
		} catch (InterruptedException e1) {
			logger.error("interrupted.return", e1);
			return;
		}
		final String fetchUrl = fetchArticleInfo.getSourceUrl();
		final SpiderUrl spiderUrl = SpiderUrlUtil.buildSpiderUrl(fetchUrl);

		spiderUrl.setConnectionTimeoutInMillis(50 * 1000);
		spiderUrl.setMaxExecutionTimeout(100 * 1000);

		spider.fetch(spiderUrl, new SpiderUrlListener() {
			@Override
			public void postFetch(SpiderUrl furl, SpiderData fd) {
				limit.release();
				logger.debug(" fetch-->postFetch,furl:{}", furl.getUrl());

				if (fd == null || fd.getCause() != null || fd.getStatusCode() != 200) {
					if (fd == null) {
						logger.warn(" fetch-->fd is null.furl:{},proxy:{}", furl.getUrl(), furl.getFetchProxy());
					} else {
						logger.warn(" fetch-->proxy failed,proxy:{},status:{},cause:{}", furl.getFetchProxy(), fd.getStatusCode(), fd.getCause());
					}
					atomicSum.incrementAndGet();
					return;
				}
				logger.debug("fetch result: {}, {}", new Object[] {furl, fd});

				// 解析文章
				analysisArticle(fd.getBody(), fetchArticleInfo, articleService);

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
	 * 解析并保存文章
	 * @param body
	 * @param fetchArticleInfo
	 * @param articleService
	 */
	private static void analysisArticle(String body, FetchArticle fetchArticleInfo, ArticleService articleService) {
		if (StringUtil.isNotEmpty(body) && null!=fetchArticleInfo && null!=articleService) {
			String fetchUrl = fetchArticleInfo.getSourceUrl();

			Document doc = Jsoup.parse(body);
			FetchArticleDetailService.cleanElement(doc, "script");
			FetchArticleDetailService.cleanElement(doc, "style");
			FetchArticleDetailService.cleanElement(doc, "ol");
			FetchArticleDetailService.cleanElement(doc, "noscript");
			FetchArticleDetailService.cleanElement(doc, "meta");

			GrabDetailRule grabDetailRuleInfo = fetchArticleInfo.getGrabDetailRuleInfo();
			logger.debug("fetchArticleDetailService grabDetailRuleInfo>>> {}", grabDetailRuleInfo);
			if (null != grabDetailRuleInfo) {
				int imageCount = 0;
				String author = "";
				String articleDescribe = "";
				String articleTitle = "";
				String articleContent = "";
				String articleSourceUrl = fetchArticleInfo.getSourceUrl();
				if (StringUtil.isNotEmpty(grabDetailRuleInfo.getAuthorCssPath())) {
					Elements authorEles = doc.select(grabDetailRuleInfo.getAuthorCssPath());
					if (null != authorEles && authorEles.size() > 0) {
						author = authorEles.get(0).text();
					}
				}
				if (StringUtil.isNotEmpty(grabDetailRuleInfo.getTitleCssPath())) {
					Elements titleEles = doc.select(grabDetailRuleInfo.getTitleCssPath());
					if (null != titleEles && titleEles.size() > 0) {
						articleTitle = titleEles.get(0).text();
						logger.debug("文章标题： {}", articleTitle);
					}
				}
				if (StringUtil.isNotEmpty(grabDetailRuleInfo.getDescCssPath())) {
					Elements descEles = doc.select(grabDetailRuleInfo.getDescCssPath());
					if (null != descEles && descEles.size() > 0) {
						articleDescribe = descEles.get(0).text();
					}
				}
				if (StringUtil.isNotEmpty(grabDetailRuleInfo.getConCssPath())) {
					Elements contEles = doc.select(grabDetailRuleInfo.getConCssPath());
					if (null != contEles && contEles.size() > 0) {
						// 移除特定规则的内容
						if (StringUtil.isNotEmpty(grabDetailRuleInfo.getReplaceCssPath())) {
							// 多个规则见用"&&"分隔
							String[] replaceCssList = grabDetailRuleInfo.getReplaceCssPath().trim().split("&&");
							for (String replaceItem : replaceCssList) {
								contEles.select(replaceItem).remove();
							}
						}

						// 文章内容中的图片
						Elements imgEles = contEles.select("img");
						logger.debug("文章内容中的图片>>>>>>{}", imgEles);
						if (null != imgEles && imgEles.size() > 0) {
							List<String> contentImageUrls = new ArrayList<>();
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
							fetchArticleInfo.setOriginContentImageUrls(contentImageUrls);
							imageCount = contentImageUrls.size();
						}
						articleContent = genTextOrHTMLByCssPath(doc, grabDetailRuleInfo.getConCssPath(), fetchUrl, false);
						articleContent = HtmlUtil.cleanHtml(articleContent); // 移除文章内容中的超链接
					}
				}

				if (StringUtil.isNotEmpty(grabDetailRuleInfo.getConCssPath2())) {
					Elements contEles = doc.select(grabDetailRuleInfo.getConCssPath2());
					if (null != contEles && contEles.size() > 0) {
						// 移除特定规则的内容
						if (StringUtil.isNotEmpty(grabDetailRuleInfo.getReplaceCssPath())) {
							// 多个规则见用"&&"分隔
							String[] replaceCssList = grabDetailRuleInfo.getReplaceCssPath().trim().split("&&");
							for (String replaceItem : replaceCssList) {
								contEles.select(replaceItem).remove();
							}
						}

						// 文章内容中的图片
						Elements imgEles = contEles.select("img");
						logger.debug("文章内容中的图片>>>>>>{}", imgEles);
						if (null != imgEles && imgEles.size() > 0) {
							List<String> contentImageUrls = new ArrayList<>();
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
							fetchArticleInfo.setOriginContentImageUrls(contentImageUrls);
							imageCount = contentImageUrls.size();
						}
						String articleContent2 = genTextOrHTMLByCssPath(doc, grabDetailRuleInfo.getConCssPath2(), fetchUrl, false);
						articleContent += HtmlUtil.cleanHtml(articleContent2); // 移除文章内容中的超链接
					}
				}

				if (StringUtil.isNotEmpty(grabDetailRuleInfo.getConCssPath3())) {
					Elements contEles = doc.select(grabDetailRuleInfo.getConCssPath3());
					if (null != contEles && contEles.size() > 0) {
						// 移除特定规则的内容
						if (StringUtil.isNotEmpty(grabDetailRuleInfo.getReplaceCssPath())) {
							// 多个规则见用"&&"分隔
							String[] replaceCssList = grabDetailRuleInfo.getReplaceCssPath().trim().split("&&");
							for (String replaceItem : replaceCssList) {
								contEles.select(replaceItem).remove();
							}
						}

						// 文章内容中的图片
						Elements imgEles = contEles.select("img");
						logger.debug("文章内容中的图片>>>>>>{}", imgEles);
						if (null != imgEles && imgEles.size() > 0) {
							List<String> contentImageUrls = new ArrayList<>();
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
							fetchArticleInfo.setOriginContentImageUrls(contentImageUrls);
							imageCount = contentImageUrls.size();
						}
						String articleContent3 = genTextOrHTMLByCssPath(doc, grabDetailRuleInfo.getConCssPath3(), fetchUrl, false);
						articleContent += HtmlUtil.cleanHtml(articleContent3); // 移除文章内容中的超链接
					}
				}

				if (StringUtil.isNotEmpty(articleTitle) && StringUtil.isNotEmpty(articleContent) && null != fetchArticleInfo.getGrabListRule() && null != fetchArticleInfo.getGrabDetailRuleInfo()) {
					fetchArticleInfo.setArticleContent(articleContent);

					GrabDetailRule grabDetailRule = fetchArticleInfo.getGrabDetailRuleInfo();
					GrabListRule grabListRule = fetchArticleInfo.getGrabListRule();
					if (null == grabListRule.getNodeObj() || StringUtil.isEmpty(grabListRule.getNodeObj().getObjectId())) {
						return;
					}
					if (null == grabListRule.getLanguageObj() && StringUtil.isNotEmpty(grabListRule.getLanguageObj().getObjectId())) {
						return;
					}
					if (null == grabListRule.getPublicationObj() && StringUtil.isNotEmpty(grabListRule.getPublicationObj().getObjectId())) {
						return;
					}
					if (null == grabListRule.getChannelObj() && StringUtil.isNotEmpty(grabListRule.getChannelObj().getObjectId())) {
						return;
					}
					if (null != grabListRule.getTopicObj() && StringUtil.isNotEmpty(grabListRule.getTopicObj().getObjectId())) {
						return;
					}

					AVObject articleInfo = new AVObject("conarticle");
					articleInfo.put("grabListRuleObj", AVObject.createWithoutData("con_grab_lrule", grabListRule.getObjectId()));
					articleInfo.put("grabDetailRuleObj", AVObject.createWithoutData("con_grab_crule", grabDetailRule.getObjectId()));
					articleInfo.put("countrycode", grabListRule.getCountryCode());
					articleInfo.put("keywords", grabListRule.getKeywords());
					articleInfo.put("salt", fetchArticleInfo.getUrlSalt());
					articleInfo.put("imgcount", imageCount);
					articleInfo.put("keywords", grabListRule.getKeywords());
					articleInfo.put("latitude", 0);
					articleInfo.put("longitude", 0);
					articleInfo.put("sourceurl", articleSourceUrl);
					articleInfo.put("writer", author);
					articleInfo.put("abstracts", articleDescribe);
					articleInfo.put("title", articleTitle);
					articleInfo.put("subtime", new Date());
					articleInfo.put("ctype", 0);
					articleInfo.put("status", 0);

					if (null != grabListRule.getNodeObj() && StringUtil.isNotEmpty(grabListRule.getNodeObj().getObjectId())) {
						articleInfo.put("nodeObj", AVObject.createWithoutData("GlobalNode", grabListRule.getNodeObj().getObjectId())); //节点
					}
					if (null != grabListRule.getLanguageObj() && StringUtil.isNotEmpty(grabListRule.getLanguageObj().getObjectId())) {
						articleInfo.put("languageObj", AVObject.createWithoutData("hb_languages", grabListRule.getLanguageObj().getObjectId())); //语言
					}
					if (null != grabListRule.getPublicationObj() && StringUtil.isNotEmpty(grabListRule.getPublicationObj().getObjectId())) {
						articleInfo.put("publicationObj", AVObject.createWithoutData("con_publications", grabListRule.getPublicationObj().getObjectId())); //媒体
					}
					if (null != grabListRule.getChannelObj() && StringUtil.isNotEmpty(grabListRule.getChannelObj().getObjectId())) {
						articleInfo.put("channelObj", AVObject.createWithoutData("con_channel", grabListRule.getChannelObj().getObjectId())); //渠道
					}
					if (null != grabListRule.getTopicObj() && StringUtil.isNotEmpty(grabListRule.getTopicObj().getObjectId())) {
						articleInfo.put("topicObj", AVObject.createWithoutData("AppTopics", grabListRule.getTopicObj().getObjectId())); //话题
					}

					// 判断文章中是否有图片(标题、内容)
					// 1、有图片则走上传文章资源流程
					// 2、没有图片则直接走保存文章流程
					logger.debug("=========================>>>>>> 文章内容的图片数：{}", imageCount);
					if (imageCount > 0) {
						fetchArticleInfo.setArticleInfo(articleInfo);
						try {
							Constants.FETCH_ARTICLE_MEDIA_QUEUE.put(fetchArticleInfo);
						} catch (InterruptedException e) {
							e.printStackTrace();
							logger.warn("加入待处理文章资源队列失败，cause: {}", e);
						}
					} else {
						boolean articleExist = articleService.getExistArticleBySalt(fetchArticleInfo.getUrlSalt());
						if (!articleExist) {
							articleInfo.put("attr", 0); // 0文字新闻 1图片新闻 2视频新闻 3 连接新闻 4H5游戏新闻 5竞猜新闻 6游戏新闻
							String articleId = articleService.saveConArticle(articleInfo, articleContent);
							logger.debug("新文章id: {}", articleId);
						}
					}
				} else {
					logger.debug("文章已存在，文章id： {}", articleSourceUrl);
				}
			}
		}
	}

	public static void cleanElement(Document doc, String elementname){
		try {
			Elements element = doc.select(elementname);
			if (element != null) {
				element.remove();
			}
		} catch (Exception e) {
			logger.info("清除失败 elementname="+elementname);
		}
	}

	public static String genTextOrHTMLByCssPath(Document doc, String cssPath, String fetchUrl, boolean isText) {
		if(cssPath.contains("|")) {
			return genAttrByCssPath(doc, cssPath);
		} else {
			Elements elements = null;
			try {
				elements = doc.select(cssPath);
			} catch (Exception var7) {
				var7.printStackTrace();
			}

			if(elements != null && elements.size() != 0) {
				Elements imgEles = elements.select("img");
				if (null!=imgEles && imgEles.size()>0) {
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
							imgEle.attr("src", contentImageUrl);
						}
					}
				}

				Element e = elements.get(0);
				String text = e.text();
				String html = e.toString();
				return isText ? text : html;
			} else {
				return "";
			}
		}
	}

	private static String genAttrByCssPath(Document doc, String cssPath) {
		String[] strs = cssPath.split("\\|");
		if(strs.length == 2) {
			Elements es = doc.select(strs[0]);
			if(es.size() == 0) {
				return "";
			} else {
				Element e = (Element)es.get(0);
				return e.attr(strs[1].trim());
			}
		} else {
			return "";
		}
	}
}
