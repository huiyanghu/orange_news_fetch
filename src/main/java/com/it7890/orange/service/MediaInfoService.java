package com.it7890.orange.service;

import javax.annotation.Resource;

/**
 * Created by Astro on 17/6/1.
 */
@Resource(name="mediaInfoService")
public interface MediaInfoService {

	void saveMediaInfo(String fileId, int width, int height);
}
