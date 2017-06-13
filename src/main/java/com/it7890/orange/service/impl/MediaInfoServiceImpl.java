package com.it7890.orange.service.impl;

import com.it7890.orange.dao.MediaInfoDao;
import com.it7890.orange.service.MediaInfoService;

import javax.annotation.Resource;

/**
 * Created by Astro on 17/6/1.
 */
public class MediaInfoServiceImpl implements MediaInfoService {

	@Resource
	MediaInfoDao mediaInfoDao;

	@Override
	public void saveMediaInfo(String fileId, int width, int height) {
		mediaInfoDao.saveMediaInfo(fileId, width, height);
	}
}
