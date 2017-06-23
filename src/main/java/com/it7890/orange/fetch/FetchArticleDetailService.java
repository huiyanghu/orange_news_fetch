package com.it7890.orange.fetch;

import com.avos.avoscloud.AVObject;
import com.cki.fetch.util.SpiderUrlUtil;
import com.cki.spider.pro.Spider;
import com.cki.spider.pro.SpiderData;
import com.cki.spider.pro.SpiderUrl;
import com.cki.spider.pro.SpiderUrlListener;
import com.cki.spider.pro.util.NamedThreadFactory;
import com.it7890.orange.config.TpConfig;
import com.it7890.orange.entity.GrabDetailRule;
import com.it7890.orange.entity.FetchArticle;
import com.it7890.orange.entity.GrabListRule;
import com.it7890.orange.service.ArticleService;
import com.it7890.orange.util.Constants;
import com.it7890.orange.util.HtmlUtil;
import com.it7890.orange.util.StringUtil;
import com.it7890.orange.util.UserAgentUtil;
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
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

@Component
public class FetchArticleDetailService {

	private static final Logger logger = LoggerFactory.getLogger(FetchArticleDetailService.class);

	@Autowired
	private Spider<SpiderData> spider;
	@Autowired
	private TpConfig tpConfig;

	@Resource
	private ArticleService articleService;

	private ExecutorService articleDetailServiceExecutor;
	private Semaphore limit;

	public FetchArticleDetailService() {
		this.articleDetailServiceExecutor = Executors.newFixedThreadPool(10, new NamedThreadFactory("articleDetailServiceExecutor"));
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

						logger.info("detail queue  url: {}", fetchArticleInfo.getSourceUrl());
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

		String fetchUrl = fetchArticleInfo.getSourceUrl();
		if (fetchUrl.toLowerCase().startsWith("https://")) {
			Connection conn = Jsoup.connect(fetchUrl);
			conn.userAgent(UserAgentUtil.getUserAgent());
			conn.validateTLSCertificates(false);
			conn.ignoreContentType(true);
			conn.ignoreHttpErrors(true);
			conn.timeout(30 * 1000);
			try {
				Document document = conn.get();
				logger.debug("fetch result: {}, {}, {}", new Object[]{fetchUrl, fetchArticleInfo.getGrabListRule().getObjectId(), fetchArticleInfo.getGrabDetailRuleInfo().getObjectId()});
				analysisArticle(document, fetchArticleInfo, articleService);
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				limit.release();
			}
		}else {
			final SpiderUrl spiderUrl = SpiderUrlUtil.buildSpiderUrl(fetchUrl);

			spiderUrl.setConnectionTimeoutInMillis(30 * 1000);
			spiderUrl.setMaxExecutionTimeout(60 * 1000);

			spider.fetch(spiderUrl, new SpiderUrlListener() {
				@Override
				public void postFetch(SpiderUrl furl, SpiderData fd) {
					limit.release();

					if (fd == null || fd.getCause() != null || fd.getStatusCode() != 200) {
						if (fd == null) {
							logger.warn(" fetch-->fd is null.furl:{},proxy:{}", furl.getUrl(), furl.getFetchProxy());
						} else {
							logger.warn(" fetch-->proxy failed,proxy:{},status:{},cause:{}", new Object[]{furl.getFetchProxy(), fd.getStatusCode(), fd.getCause()});
						}
						return;
					}
					logger.debug("fetch result: {}, {}, {}, {}", new Object[]{furl, fd, fetchArticleInfo.getGrabListRule().getObjectId(), fetchArticleInfo.getGrabDetailRuleInfo().getObjectId()});

					// 解析文章
					Document document = Jsoup.parse(fd.getBody());
					analysisArticle(document, fetchArticleInfo, articleService);
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
	 * 解析并保存文章
	 * @param doc
	 * @param fetchArticleInfo
	 * @param articleService
	 */
	private static void analysisArticle(Document doc, FetchArticle fetchArticleInfo, ArticleService articleService) {
		if (null != doc && null!=fetchArticleInfo && null!=articleService) {
			String fetchUrl = fetchArticleInfo.getSourceUrl();
			FetchArticleDetailService.cleanElement(doc, "script");
			FetchArticleDetailService.cleanElement(doc, "style");
			FetchArticleDetailService.cleanElement(doc, "ol");
			FetchArticleDetailService.cleanElement(doc, "noscript");
			FetchArticleDetailService.cleanElement(doc, "meta");

			GrabDetailRule grabDetailRuleInfo = fetchArticleInfo.getGrabDetailRuleInfo();
			GrabListRule grabListRuleInfo = fetchArticleInfo.getGrabListRule();
			if (null != grabDetailRuleInfo && null != grabListRuleInfo) {
				String author = "";
				String articleDescribe = "";
				String articleTitle = "";

				StringBuffer sbArticleContent = new StringBuffer();
				String content;
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
						logger.debug("article title： {}", articleTitle);
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
								if (StringUtil.isNotEmpty(replaceItem) && StringUtil.isNotEmpty(replaceItem.trim())) {
									contEles.select(replaceItem.trim()).remove();
								}
							}
						}

						// 文章内容中的图片
						Elements imgEles = contEles.select("img");
						if (null != imgEles && imgEles.size() > 0) {
							List<String> contentImageUrls = new ArrayList<>();
							for (Element imgEle : imgEles) {
								if (null != imgEle && null != imgEle.attributes()) {
									String contentImageUrl = imgEle.attr("data-original");
									if (StringUtil.isNotEmpty(contentImageUrl)) {
										contentImageUrl = StringUtil.urlEncode(contentImageUrl);
										contentImageUrls.add(contentImageUrl);
										continue;
									}
									contentImageUrl = imgEle.attr("data-url");
									if (StringUtil.isNotEmpty(contentImageUrl)) {
										contentImageUrl = StringUtil.urlEncode(contentImageUrl);
										contentImageUrls.add(contentImageUrl);
										continue;
									}
									contentImageUrl = imgEle.attr("src");
									if (StringUtil.isNotEmpty(contentImageUrl)) {
										contentImageUrl = StringUtil.urlEncode(contentImageUrl);
										contentImageUrls.add(contentImageUrl);
									}
								}
							}
							logger.info("article content image urls>>>：{}", contentImageUrls);
							fetchArticleInfo.getOriginContentImageUrls().addAll(contentImageUrls);
						}
						content = genTextOrHTMLByCssPath(doc, grabDetailRuleInfo.getConCssPath(), fetchUrl, false);
						content = HtmlUtil.cleanHtml(content); // 移除文章内容中的超链接
						if (StringUtil.isNotEmpty(content)) {
							sbArticleContent.append(content);
						}
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
								if (StringUtil.isNotEmpty(replaceItem) && StringUtil.isNotEmpty(replaceItem.trim())) {
									contEles.select(replaceItem.trim()).remove();
								}
							}
						}

						// 文章内容中的图片
						Elements imgEles = contEles.select("img");
						if (null != imgEles && imgEles.size() > 0) {
							List<String> contentImageUrls = new ArrayList<>();
							for (Element imgEle : imgEles) {
								String contentImageUrl = imgEle.attr("data-original");
								if (StringUtil.isNotEmpty(contentImageUrl)) {
									contentImageUrl = StringUtil.urlEncode(contentImageUrl);
									contentImageUrls.add(contentImageUrl);
									continue;
								}
								contentImageUrl = imgEle.attr("data-url");
								if (StringUtil.isNotEmpty(contentImageUrl)) {
									contentImageUrl = StringUtil.urlEncode(contentImageUrl);
									contentImageUrls.add(contentImageUrl);
									continue;
								}
								contentImageUrl = imgEle.attr("src");
								if (StringUtil.isNotEmpty(contentImageUrl)) {
									contentImageUrl = StringUtil.urlEncode(contentImageUrl);
									contentImageUrls.add(contentImageUrl);
								}
							}
							logger.info("article content image urls>>>：{}", contentImageUrls);
							fetchArticleInfo.getOriginContentImageUrls().addAll(contentImageUrls);
						}
						content = genTextOrHTMLByCssPath(doc, grabDetailRuleInfo.getConCssPath2(), fetchUrl, false);
						content = HtmlUtil.cleanHtml(content); // 移除文章内容中的超链接

						if (StringUtil.isNotEmpty(content)) {
							sbArticleContent.append(content);
						}
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
								if (StringUtil.isNotEmpty(replaceItem) && StringUtil.isNotEmpty(replaceItem.trim())) {
									contEles.select(replaceItem.trim()).remove();
								}
							}
						}

						// 文章内容中的图片
						Elements imgEles = contEles.select("img");
						if (null != imgEles && imgEles.size() > 0) {
							List<String> contentImageUrls = new ArrayList<>();
							for (Element imgEle : imgEles) {
								String contentImageUrl = imgEle.attr("data-original");
								if (StringUtil.isNotEmpty(contentImageUrl)) {
									contentImageUrl = StringUtil.urlEncode(contentImageUrl);
									contentImageUrls.add(contentImageUrl);
									continue;
								}
								contentImageUrl = imgEle.attr("data-url");
								if (StringUtil.isNotEmpty(contentImageUrl)) {
									contentImageUrl = StringUtil.urlEncode(contentImageUrl);
									contentImageUrls.add(contentImageUrl);
									continue;
								}
								contentImageUrl = imgEle.attr("src");
								if (StringUtil.isNotEmpty(contentImageUrl)) {
									contentImageUrl = StringUtil.urlEncode(contentImageUrl);
									contentImageUrls.add(contentImageUrl);
								}
							}
							logger.info("article image urls>>>：{}", contentImageUrls);
							fetchArticleInfo.getOriginContentImageUrls().addAll(contentImageUrls);
						}
						content = genTextOrHTMLByCssPath(doc, grabDetailRuleInfo.getConCssPath3(), fetchUrl, false);
						content = HtmlUtil.cleanHtml(content); // 移除文章内容中的超链接

						if (StringUtil.isNotEmpty(content)) {
							sbArticleContent.append(content);
						}
					}
				}

				if (StringUtil.isNotEmpty(articleTitle) && sbArticleContent.length() > 20) {
					String articleContent = sbArticleContent.toString();
					fetchArticleInfo.setArticleContent(articleContent);

					if (null == grabListRuleInfo.getNodeObj() || StringUtil.isEmpty(grabListRuleInfo.getNodeObj().getObjectId())) {
						return;
					}
					if (null == grabListRuleInfo.getLanguageObj() || StringUtil.isEmpty(grabListRuleInfo.getLanguageObj().getObjectId())) {
						return;
					}
					if (null == grabListRuleInfo.getPublicationObj() || StringUtil.isEmpty(grabListRuleInfo.getPublicationObj().getObjectId())) {
						return;
					}
					if (null == grabListRuleInfo.getChannelObj() || StringUtil.isEmpty(grabListRuleInfo.getChannelObj().getObjectId())) {
						return;
					}
					if (null == grabListRuleInfo.getTopicObj() || StringUtil.isEmpty(grabListRuleInfo.getTopicObj().getObjectId())) {
						return;
					}

					int contentImageCount = fetchArticleInfo.getOriginContentImageUrls().size();
					AVObject articleInfo = new AVObject("conarticle");
					articleInfo.put("grabListRuleObj", AVObject.createWithoutData("GrabListRule", grabListRuleInfo.getObjectId()));
					articleInfo.put("grabDetailRuleObj", AVObject.createWithoutData("GrabDetailRule", grabDetailRuleInfo.getObjectId()));
					articleInfo.put("countrycode", grabListRuleInfo.getCountryCode());
					articleInfo.put("keywords", grabListRuleInfo.getKeywords());
					articleInfo.put("salt", fetchArticleInfo.getUrlSalt());
					articleInfo.put("imgcount", contentImageCount);
					articleInfo.put("keywords", grabListRuleInfo.getKeywords());
					articleInfo.put("latitude", 0);
					articleInfo.put("longitude", 0);
					articleInfo.put("sourceurl", articleSourceUrl);
					articleInfo.put("writer", author);
					articleInfo.put("abstracts", articleDescribe);
					articleInfo.put("title", articleTitle);
					articleInfo.put("subtime", new Date());
					articleInfo.put("ctype", 0);
					articleInfo.put("status", 0);

					articleInfo.put("nodeObj", AVObject.createWithoutData("GlobalNode", grabListRuleInfo.getNodeObj().getObjectId())); //节点
					articleInfo.put("languageObj", AVObject.createWithoutData("hb_languages", grabListRuleInfo.getLanguageObj().getObjectId())); //语言
					articleInfo.put("publicationObj", AVObject.createWithoutData("con_publications", grabListRuleInfo.getPublicationObj().getObjectId())); //媒体
					articleInfo.put("channelObj", AVObject.createWithoutData("ConChannel", grabListRuleInfo.getChannelObj().getObjectId())); //渠道
					articleInfo.put("topicObj", AVObject.createWithoutData("AppTopics", grabListRuleInfo.getTopicObj().getObjectId())); //话题

					// 判断文章中是否有图片(标题、内容)
					// 1、有图片则走上传文章资源流程
					// 2、没有图片则直接走保存文章流程
					logger.info("article image size：{}", contentImageCount);
					if (contentImageCount > 0) {
						fetchArticleInfo.setArticleInfo(articleInfo);
						try {
							Constants.FETCH_ARTICLE_MEDIA_QUEUE.put(fetchArticleInfo);
						} catch (InterruptedException e) {
							e.printStackTrace();
							logger.warn("re add article resource queue，cause: {}", e);
						}
					} else {
						logger.debug("new article  url: {}, grabListRuleId:{}, grabDetailRuleId:{}", new Object[]{articleSourceUrl, grabListRuleInfo.getObjectId(), grabDetailRuleInfo.getObjectId()});
						boolean articleExist = articleService.getExistArticleBySalt(fetchArticleInfo.getUrlSalt());
						if (!articleExist) {
							articleInfo.put("attr", 0); // 0文字新闻 1图片新闻 2视频新闻 3 连接新闻 4H5游戏新闻 5竞猜新闻 6游戏新闻

							logger.info("test new articleId");
							String articleId = articleService.saveConArticle(articleInfo, articleContent);
							logger.info("new article id: {}", articleId);
						} else {
							logger.info("article is exists： {}", articleSourceUrl);
						}
					}
				} else {
					logger.info("article title is empty, url:{}, grabListRuleId:{}, grabDetailRuleId:{}", new Object[]{articleSourceUrl, grabListRuleInfo.getObjectId(), grabDetailRuleInfo.getObjectId()});
				}
			}
		}
	}

	public static void cleanElement(Document doc, String elementName) {
		try {
			Elements element = doc.select(elementName);
			if (element != null) {
				element.remove();
			}
		} catch (Exception e) {
			logger.info("清除失败 elementname="+elementName);
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
						String contentImageDataOriginalUrl = imgEle.attr("data-original");
						String contentImageDataUrl = imgEle.attr("data-url");
						String contentImageUrl = imgEle.attr("src");
						if(StringUtil.isNotEmpty(contentImageDataOriginalUrl)) {
							contentImageUrl = HtmlUtil.getRemoteUrl(fetchUrl, contentImageDataOriginalUrl);
							imgEle.attr("src", contentImageUrl);
						} else if(StringUtil.isNotEmpty(contentImageDataUrl)) {
							contentImageUrl = HtmlUtil.getRemoteUrl(fetchUrl, contentImageDataUrl);
							imgEle.attr("src", contentImageUrl);
						} else if (StringUtil.isNotEmpty(contentImageUrl)) {
							contentImageUrl = HtmlUtil.getRemoteUrl(fetchUrl, contentImageUrl);
							imgEle.attr("src", contentImageUrl);
						}

						Attributes imgAttrs = imgEle.attributes();
						// 删除标签中多余属性
						List<String> removeAttrs = new ArrayList<>();
						for (Attribute imgAttr : imgAttrs) {
							String attrKey = imgAttr.getKey();
							if (!"src".equals(attrKey.toLowerCase())) {
								removeAttrs.add(attrKey);
							}
						}
						for (String attrKey : removeAttrs) {
							imgAttrs.remove(attrKey);
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
				Element e = es.get(0);
				return e.attr(strs[1].trim());
			}
		} else {
			return "";
		}
	}
}
