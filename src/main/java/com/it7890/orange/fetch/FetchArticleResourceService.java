package com.it7890.orange.fetch;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVObject;
import com.cki.fetch.util.SpiderUrlUtil;
import com.cki.filter.UniqueService;
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
import com.it7890.orange.util.Constants;
import com.it7890.orange.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
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

	private ExecutorService articleResourceServiceExecutor;

	private Semaphore limit;

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
					} catch (InterruptedException e) {
						e.printStackTrace();
					}

					if(null != fetchArticleInfo && null != fetchArticleInfo.getArticleInfo()) {
						List<String> originTitleImageUrls = fetchArticleInfo.getOriginTitleImageUrls();
						List<String> originContentImageUrls = fetchArticleInfo.getOriginContentImageUrls();
						if (null != originTitleImageUrls && originTitleImageUrls.size() > 0) {
							logger.debug("准备抓取文章标题图片>>>: {}", originTitleImageUrls);

							String originTitleImageUrl = originTitleImageUrls.remove(0);
							if (StringUtil.isNotEmpty(originTitleImageUrl)) {
								urlSearch(fetchArticleInfo, originTitleImageUrl, 1);
							}
						} else if (null != originContentImageUrls && originContentImageUrls.size() > 0) {
							logger.debug("准备抓取文章内容图片>>>：{}", originContentImageUrls);

							String originContentImageUrl = originContentImageUrls.remove(0);
							if (StringUtil.isNotEmpty(originContentImageUrl)) {
								urlSearch(fetchArticleInfo, originContentImageUrl, 2);
							}
						}
					}
				}
			}
		});		
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

		SpiderUrl fetchUrl = SpiderUrlUtil.buildSpiderUrl(downloadMediaUrl);
		fetchUrl.setConnectionTimeoutInMillis(120 * 1000);
		fetchUrl.setMaxExecutionTimeout(120 * 1000);
		// fetchUrl.setAttachment(pmUrl);

		spider.fetch(fetchUrl, new SpiderUrlListener() {
			@Override
			public void postFetch(SpiderUrl furl, SpiderData fd) {
				limit.release();
				logger.debug(" fetch-->postFetch, statusCode:{}, furl:{}", fd.getStatusCode(), furl.getUrl());

				if (fd.getCause() != null || fd.getStatusCode() != 200) {
					logger.warn(" fetch-->proxy failed,proxy:{},status:{},cause:{}", furl.getFetchProxy(), fd.getStatusCode(), fd.getCause());
					return;
				}
				logger.debug("fetch result: {}", new Object[]{furl});

				if (null == fd.getContent()) {
					logger.warn("fetch >>> spiderData content is empty");
					return;
				}

				// 流文件中获取图片宽高
				int imageWidth = 0;
				int imageHeight = 0;
				InputStream inputs = new ByteArrayInputStream(fd.getContent());
				try {
					BufferedImage bufferedImage = ImageIO.read(inputs);
					imageWidth = bufferedImage.getWidth();
					imageHeight = bufferedImage.getHeight();
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					try {
						inputs.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}

				// 通过AVFile构建文件数据流
				String fileSuffix = downloadMediaUrl.substring(downloadMediaUrl.lastIndexOf("."));    //后缀名
				AVFile uploadFile = new AVFile(fetchArticleInfo.getUrlSalt() + fileSuffix, fd.getContent());
				boolean uploadSuccess = false;
				try {
					uploadFile.save();
					uploadSuccess = true;
				} catch (AVException e) {
					e.printStackTrace();
					logger.warn("上传文件失败，cause: {}", e);
				}

				// 上传资料是否完成
				if (!uploadSuccess) {
					logger.debug("加入到文章资源队列，等待重新上传, url:{}", downloadMediaUrl);
					switch (mediaType) {
						case 1:
							List<String> originTitleImageUrls = fetchArticleInfo.getOriginTitleImageUrls();
							if (null == originTitleImageUrls) {
								originTitleImageUrls = new ArrayList<>();
							}
							originTitleImageUrls.add(downloadMediaUrl);
							fetchArticleInfo.setOriginTitleImageUrls(originTitleImageUrls);
							break;
						case 2:
							List<String> originContentImageUrls = fetchArticleInfo.getOriginContentImageUrls();
							if (null == originContentImageUrls) {
								originContentImageUrls = new ArrayList<>();
							}
							originContentImageUrls.add(downloadMediaUrl);
							fetchArticleInfo.setOriginContentImageUrls(originContentImageUrls);
							break;
					}
					try {
						Constants.FETCH_ARTICLE_MEDIA_QUEUE.put(fetchArticleInfo);
					} catch (InterruptedException e) {
						e.printStackTrace();
						logger.warn("重新加入文章资源队列失败，cause: {}", e);
					}
				} else {
					mediaInfoDao.saveMediaInfo(uploadFile.getObjectId(), imageWidth, imageHeight);

					//修改上传完成资源的属性
					switch (mediaType) {
						case 1:
							List<AVFile> titleImageUrls = fetchArticleInfo.getTitleImageUrls();
							titleImageUrls.add(uploadFile);
							fetchArticleInfo.setTitleImageUrls(titleImageUrls);
							break;
						case 2:
							List<AVFile> contentImageUrls = fetchArticleInfo.getContentImageUrls();
							contentImageUrls.add(uploadFile);
							fetchArticleInfo.setContentImageUrls(contentImageUrls);

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
						logger.debug("文章是否存在 >>>>>>: {}", articleExist);
						if (!articleExist) {
							AVObject articleInfo = fetchArticleInfo.getArticleInfo();
							logger.debug("文章中的资源>{}, 资源数：{}", fetchArticleInfo.getTitleImageUrls(), fetchArticleInfo.getTitleImageUrls().size());

							if (fetchArticleInfo.getTitleImageUrls().size() > 0) {
								articleInfo.put("titlePicObjArr", fetchArticleInfo.getTitleImageUrls());
							}
							if (fetchArticleInfo.getContentImageUrls().size() > 0) {
								articleInfo.put("contentPicObjArr", fetchArticleInfo.getTitleImageUrls());
							}
							articleInfo.put("attr", 1); // 0文字新闻 1图片新闻 2视频新闻 3 连接新闻 4H5游戏新闻 5竞猜新闻 6游戏新闻
							String articleId = conArticleDao.saveConArticle(articleInfo);
							if (StringUtil.isNotEmpty(articleId)) {
								conArticleContentDao.saveArticleContent(articleId, fetchArticleInfo.getArticleContent());
								logger.debug("新文章id：{}", articleId);
							}
						}
					} else {
						// 继续加入文章"待上传"队列
						try {
							Constants.FETCH_ARTICLE_MEDIA_QUEUE.put(fetchArticleInfo);
						} catch (InterruptedException e) {
							e.printStackTrace();
							logger.warn("继续加入文章资源队列失败，cause: {}", e);
						}
					}
				}
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
