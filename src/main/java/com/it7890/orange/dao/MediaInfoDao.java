package com.it7890.orange.dao;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.it7890.orange.util.StringUtil;
import org.springframework.stereotype.Repository;

@Repository
public class MediaInfoDao {

	/**
	 * 保存媒体文件信息
	 * @param fileId 文件id
	 * @param width  宽
	 * @param height 高
	 * @return
	 */
	public void saveMediaInfo(String fileId, int width, int height) {
		if (StringUtil.isNotEmpty(fileId) && width > 0 && height > 0) {
			AVObject mediaInfo = new AVObject("MediaInfo");
			mediaInfo.put("fileObj", AVObject.createWithoutData("_File", fileId));
			mediaInfo.put("width", width);
			mediaInfo.put("height", height);
			try {
				mediaInfo.save();
			} catch (AVException e) {
				e.printStackTrace();
			}
		}
	}
}
