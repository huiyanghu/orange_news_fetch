package com.it7890.orange.fetch;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVObject;
import com.cki.fetch.util.SpiderUrlUtil;
import com.cki.spider.pro.Spider;
import com.cki.spider.pro.SpiderData;
import com.cki.spider.pro.SpiderUrl;
import com.cki.spider.pro.SpiderUrlListener;
import com.cki.spider.pro.util.NamedThreadFactory;
import com.it7890.orange.config.TpConfig;
import com.it7890.orange.dao.ConArticleContentDao;
import com.it7890.orange.dao.ConArticleDao;
import com.it7890.orange.dao.MediaInfoDao;
import com.it7890.orange.entity.FetchArticle;
import com.it7890.orange.entity.ImageInfo;
import com.it7890.orange.util.Constants;
import com.it7890.orange.util.ImageUtil;
import com.it7890.orange.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

@Component
public class FetchArticleResourceService {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private Spider<SpiderData> spider;
	@Autowired
	private TpConfig tpConfig;

	@Resource
	private ConArticleDao conArticleDao;
	@Resource
	private ConArticleContentDao conArticleContentDao;
	@Resource
	private MediaInfoDao mediaInfoDao;
	private Semaphore limit;

	private ExecutorService articleResourceServiceExecutor;

	public FetchArticleResourceService() {
		this.articleResourceServiceExecutor = Executors.newFixedThreadPool(10, new NamedThreadFactory("articleResourceServiceExecutor"));
	}

	@PostConstruct
	public void init() {
		this.limit = new Semaphore(tpConfig.getSpiderConn());

		this.articleResourceServiceExecutor.execute(new Runnable() {
			@Override
			public void run() {
				while (true) {
					if (Thread.currentThread().isInterrupted()) {
						return;
					}

					FetchArticle fetchArticleInfo = null;
					try {
						fetchArticleInfo = Constants.FETCH_ARTICLE_MEDIA_QUEUE.take();

						logger.info("FETCH_ARTICLE_MEDIA_QUEUE queue url: {}, size: {}", fetchArticleInfo.getSourceUrl(), Constants.FETCH_ARTICLE_MEDIA_QUEUE.size());
					} catch (InterruptedException e) {
						e.printStackTrace();
					}

					fetchArticleInfoResource(fetchArticleInfo);
				}
			}
		});		
	}

	private void fetchArticleInfoResource(FetchArticle fetchArticleInfo) {
		if(null != fetchArticleInfo && null != fetchArticleInfo.getArticleInfo()) {
			List<String> originTitleImageUrls = fetchArticleInfo.getOriginTitleImageUrls();
			List<String> originContentImageUrls = fetchArticleInfo.getOriginContentImageUrls();
			if (null != originTitleImageUrls && originTitleImageUrls.size() > 0) {
				logger.debug("fetch title image>>>: {}", originTitleImageUrls);

				String originTitleImageUrl = originTitleImageUrls.get(0);
				if (StringUtil.isNotEmpty(originTitleImageUrl)) {
					urlSearch(fetchArticleInfo, originTitleImageUrl, 1);
				}
			} else if (null != originContentImageUrls && originContentImageUrls.size() > 0) {
				logger.debug("fetch content image>>>：{}", originContentImageUrls);

				String originContentImageUrl = originContentImageUrls.get(0);
				if (StringUtil.isNotEmpty(originContentImageUrl)) {
					urlSearch(fetchArticleInfo, originContentImageUrl, 2);
				}
			}
		}
	}

	/**
	 * 下载文章中的资源
	 * @param fetchArticleInfo 抓取的文章对象
	 * @param downloadMediaUrl 下载的资源url
	 * @param mediaType 下载的资源来源类型 1标题资源 2内容资源
	 */
	private void urlSearch(final FetchArticle fetchArticleInfo, final String downloadMediaUrl, final int mediaType) {
		try {
			limit.acquire();
		} catch (InterruptedException e1) {
			logger.error("interrupted.return", e1);
			return;
		}

		logger.debug("wait download title image size: {}, content image size: {}", fetchArticleInfo.getOriginTitleImageUrls().size(), fetchArticleInfo.getOriginContentImageUrls().size());
		logger.debug("downloadMediaUrl: {}", downloadMediaUrl);
		if (downloadMediaUrl.toLowerCase().startsWith("https")) {
			byte[] imageBt = ImageUtil.downloadImageByteByUrl(downloadMediaUrl);
			limit.release();

			if (null != imageBt) {
				analysisArticleResource(downloadMediaUrl, fetchArticleInfo, mediaType, imageBt);
			}
		} else {
			SpiderUrl fetchUrl = SpiderUrlUtil.buildSpiderUrl(downloadMediaUrl);
			fetchUrl.setConnectionTimeoutInMillis(30 * 1000);
			fetchUrl.setMaxExecutionTimeout(60 * 1000);

			spider.fetch(fetchUrl, new SpiderUrlListener() {
				@Override
				public void postFetch(SpiderUrl furl, SpiderData fd) {
					limit.release();

					logger.debug(" fetch-->postFetch, statusCode:{}, furl:{}", fd.getStatusCode(), furl.getUrl());

					if (fd.getCause() != null || fd.getStatusCode() != 200) {
						logger.warn(" fetch-->proxy failed,proxy:{},status:{},cause:{}", new Object[] {furl.getFetchProxy(), fd.getStatusCode(), fd.getCause()});
						return;
					}
					logger.debug("fetch result: {}", new Object[]{furl});

					if (null == fd.getContent()) {
						logger.warn("fetch >>> spiderData content is empty");
						return;
					}

					analysisArticleResource(downloadMediaUrl, fetchArticleInfo, mediaType, fd.getContent());
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
	 * 处理文章中的资源
	 * @param downloadMediaUrl
	 * @param fetchArticleInfo
	 * @param mediaType
	 * @param imageBt
	 */
	private void analysisArticleResource(String downloadMediaUrl, FetchArticle fetchArticleInfo, int mediaType, byte[] imageBt) {
		logger.debug("analysisArticleResource url: {}, mediaType: {}, imageBt:{}", new Object[] {downloadMediaUrl, mediaType, imageBt.length});

		// 文件后缀名
		String fileSuffix = ImageUtil.getSuffixByUrl(downloadMediaUrl);
		if (StringUtil.isEmpty(fileSuffix)) {
			return;
		}

		// 从二进制文件流中读取图片宽高属性
		ImageInfo imageInfo;
		if (fileSuffix.toLowerCase().contains(".gif")) {
			imageInfo = ImageUtil.getGifInfo(imageBt);
		} else {
			imageInfo = ImageUtil.getImageInfo(imageBt);
		}
		if (null == imageInfo) {
			return;
		}

		// 通过AVFile构建文件数据流
		AVFile uploadFile = new AVFile(fetchArticleInfo.getUrlSalt() + fileSuffix, imageBt);
		boolean uploadSuccess = false;
		try {
			uploadFile.save();
			uploadSuccess = true;
		} catch (AVException e) {
			e.printStackTrace();
			logger.warn("upload fail，cause: {}", e);
		}

		// 如果上传失败，则重试一次
		if (!uploadSuccess) {
			try {
				uploadFile.save();
				uploadSuccess = true;
			} catch (AVException e) {
				e.printStackTrace();
			}
		}

		// 上传资料是否完成
		if (uploadSuccess) {
			mediaInfoDao.saveMediaInfo(uploadFile.getObjectId(), imageInfo.getWidth(), imageInfo.getHeight());

			//修改上传完成资源的属性
			switch (mediaType) {
				case 1:
					fetchArticleInfo.getTitleImageUrls().add(uploadFile);

					for (String titleImageUrl : fetchArticleInfo.getOriginTitleImageUrls()) {
						if (downloadMediaUrl.equals(titleImageUrl)) {
							fetchArticleInfo.getOriginTitleImageUrls().remove(titleImageUrl);
							break;
						}
					}
					break;
				case 2:
					fetchArticleInfo.getContentImageUrls().add(uploadFile);

					for (String contentImageUrl : fetchArticleInfo.getOriginContentImageUrls()) {
						if (downloadMediaUrl.equals(contentImageUrl)) {
							fetchArticleInfo.getOriginContentImageUrls().remove(contentImageUrl);
							break;
						}
					}

					String articleContent = fetchArticleInfo.getArticleContent();
					if (StringUtil.isNotEmpty(articleContent)) {
						String decodeDownloadMediaUrl = URLDecoder.decode(downloadMediaUrl); //url解码
						articleContent = articleContent.replace(decodeDownloadMediaUrl, uploadFile.getUrl());
						fetchArticleInfo.setArticleContent(articleContent);
					}
					break;
			}

			// 判断文章是否关联有未上传的资料
			// 1、有则加入队列继续上传
			// 2、没有则进入保存文章流程
			if (fetchArticleInfo.getOriginTitleImageUrls().size() == 0 && fetchArticleInfo.getOriginContentImageUrls().size() == 0) {
				String urlSalt = fetchArticleInfo.getUrlSalt();
				boolean articleExist = conArticleDao.getExistArticleBySalt(urlSalt);
				logger.debug("article is exists >>>>>>: {}", articleExist);
				if (!articleExist) {
					if (fetchArticleInfo.getTitleImageUrls().size() == 0 && fetchArticleInfo.getContentImageUrls().size() > 0) {
						fetchArticleInfo.getTitleImageUrls().add(fetchArticleInfo.getContentImageUrls().get(0));
					}

					AVObject articleInfo = fetchArticleInfo.getArticleInfo();
					articleInfo.put("titlePicObjArr", fetchArticleInfo.getTitleImageUrls());
					articleInfo.put("attr", 1); // 0文字新闻 1图片新闻 2视频新闻 3 连接新闻 4H5游戏新闻 5竞猜新闻 6游戏新闻
					if (fetchArticleInfo.getContentImageUrls().size() > 0) {
						articleInfo.put("contentPicObjArr", fetchArticleInfo.getContentImageUrls());
					}
					logger.info("<============================test new article 2============================>");
					String articleId = conArticleDao.saveConArticle(articleInfo);
					if (StringUtil.isNotEmpty(articleId)) {
						conArticleContentDao.saveArticleContent(articleId, fetchArticleInfo.getArticleContent());
						logger.debug("new article id：{}", articleId);
					}
				}
			} else {
				// 继续下载文章的剩余图片
				fetchArticleInfoResource(fetchArticleInfo);
			}
		}
	}
}
