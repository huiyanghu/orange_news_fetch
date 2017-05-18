package com.it7890.orange.util;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.it7890.orange.dto.AsNameDTO;
import org.apache.commons.codec.EncoderException;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.qiniu.api.auth.AuthException;
import com.qiniu.api.auth.digest.Mac;
import com.qiniu.api.io.PutRet;
import com.qiniu.api.net.Client;
import com.qiniu.api.net.EncodeUtils;
import com.qiniu.api.resumableio.ResumeableIoApi;
import com.qiniu.api.rs.GetPolicy;
import com.qiniu.api.rs.PutPolicy;
import com.qiniu.api.rs.URLUtils;

public class QiNiuApiUtil {

	private static Logger logger = LoggerFactory.getLogger(QiNiuApiUtil.class);
	
	private static Mac mac = null;
	
	static {
		mac = new Mac(QiNiuConstants.ACCESS_KEY, QiNiuConstants.SECRET_KEY);
	}
	
	/**
	 * 上传图片到七牛云，并预转持久化
	 * @param fileName 上传的文件名，也就是七牛云中文件的key,七牛云不支持新建文件目录，所以如果需要如:"20141224/file.jpg"，只需要在文件名前拼串即可
	 * @param localPath 上传的文件
	 * @param map 需要预转的图片 图片宽度与预转key对应关系（240:1, 440:2, 640:3）
	 * @param type 0 上传到公共空间	1上传到私有空间
	 * @return 上传成功返回七牛云的唯一标示 失败返回null
	 */
	@SuppressWarnings("unchecked")
	public static String uploadPicture(String fileName, File localPath, Map<Integer, AsNameDTO> map, int type) {
		StringBuffer persistentOps = new StringBuffer();
		String scope = getScopeByType(type);
		AsNameDTO asDTO = null;
		for(int i=1; i<=3; i++) {
			if((map.get(i) != null)) {
				asDTO = map.get(i);
				
				if(i > 1) {
					persistentOps.append(";");
				}
				persistentOps.append("imageView/2/w/").append(asDTO.getWidth()).append("|saveas/").append(EncodeUtils.urlsafeEncode(scope + ":" + asDTO.getAsName()));
			}
		}
		
		PutPolicy policy = new PutPolicy(scope);
		policy.persistentOps = persistentOps.toString();
		policy.persistentNotifyUrl = Constants.getStringValue(Constants.PROCESS_NOTIFY);
		
		String upToken = null;
		try {
			upToken = policy.token(mac);
		} catch (Exception e) {
			e.printStackTrace();
			logger.warn("上传图片到七牛云失败:" + e);
		}
		
		PutRet ret = ResumeableIoApi.put(localPath, upToken, fileName);
		if(ret.getStatusCode() != 200) {
			return null;
		} else {
			Map<String, String> resultMap = JsonUtils.jsonToMap(ret.getResponse());
			return resultMap.get("persistentId");
		}
	}
	
	/**
	 * 上传gif图片到七牛云，并预转持久化
	 * @param fileName 上传的文件名，也就是七牛云中文件的key,七牛云不支持新建文件目录，所以如果需要如:"20141224/file.jpg"，只需要在文件名前拼串即可
	 * @param localPath 上传的文件
	 * @param map 需要预转的图片 图片宽度与预转key对应关系（240:1, 440:2, 640:3）
	 * @param type 0 上传到公共空间	1上传到私有空间
	 * @return 上传成功返回七牛云的唯一标示 失败返回null
	 */
	@SuppressWarnings("unchecked")
	public static String uploadGIF(String fileName, File localPath, Map<Integer, AsNameDTO> map, int type) {
		StringBuffer persistentOps = new StringBuffer();
		
		String scope = getScopeByType(type);
		
		AsNameDTO asDTO = null;
		for(int i=1; i<=3; i++) {
			if((map.get(i) != null)) {
				asDTO = map.get(i);
				
				if(i > 1) {
					persistentOps.append(";");
				}
				persistentOps.append("imageView2/0/w/").append(asDTO.getWidth()).append("|saveas/").append(EncodeUtils.urlsafeEncode(scope + ":" + asDTO.getAsName()));
			}	
		}
		
		PutPolicy policy = new PutPolicy(scope);
		policy.persistentOps = persistentOps.toString();
		policy.persistentNotifyUrl = Constants.getStringValue(Constants.PROCESS_NOTIFY);
		
		String upToken = null;
		try {
			upToken = policy.token(mac);
		} catch (Exception e) {
			e.printStackTrace();
			logger.warn("上传图片到七牛云失败:" + e);
		}
		
		PutRet ret = ResumeableIoApi.put(localPath, upToken, fileName);
		if(ret.getStatusCode() != 200) {
			return null;
		} else {
			Map<String, String> resultMap = JsonUtils.jsonToMap(ret.getResponse());
			return resultMap.get("persistentId");
		}
	}
	
	/**
	 * 上传音频到七牛云，分片并持久化
	 * @param fileName 上传的文件名，也就是七牛云中文件的key,七牛云不支持新建文件目录，所以如果需要如:"20141224/file.jpg"，只需要在文件名前拼串即可
	 * @param localPath 上传的文件
	 * @param asName 持久化后的文件名
	 * @param type 0 上传到公共空间	1上传到私有空间
	 * @return 上传成功返回七牛云的唯一标示 失败返回null
	 */
	@SuppressWarnings("unchecked")
	public static String uploadAudio(String fileName, File localPath, String asName, int type) {
		String scope = getScopeByType(type);
		
		PutPolicy policy = new PutPolicy(scope);
//		policy.persistentOps = "avthumb/m3u8/segtime/15/preset/audio_64k|saveas/" + EncodeUtils.urlsafeEncode(scope + ":" + asName);
		policy.persistentOps = "avthumb/m3u8/segtime/10/ab/64k|saveas/" + EncodeUtils.urlsafeEncode(scope + ":" + asName);
		policy.persistentNotifyUrl = Constants.getStringValue(Constants.PROCESS_NOTIFY);
		
		String upToken = null;
		try {
			upToken = policy.token(mac);
		} catch (Exception e) {
			e.printStackTrace();
			logger.warn("上传音频到七牛云失败:" + e);
		}
		
		PutRet ret = ResumeableIoApi.put(localPath, upToken, fileName);
		if(ret.getStatusCode() != 200) {
			return null;
		} else {
			Map<String, String> resultMap = JsonUtils.jsonToMap(ret.getResponse());
			return resultMap.get("persistentId");
		}
	}
	
	/**
	 * 上传视频到七牛云，分片并持久化
	 * @param fileName 上传的文件名，也就是七牛云中文件的key,七牛云不支持新建文件目录，所以如果需要如:"20141224/file.jpg"，只需要在文件名前拼串即可
	 * @param localPath 上传的文件
	 * @param asName 持久化后的文件名
	 * @param type 0 上传到公共空间	1上传到私有空间
	 * @return 上传成功返回七牛云的唯一标示 失败返回null
	 */
	@SuppressWarnings("unchecked")
	public static String uploadVideo(String fileName, File localPath, String asName, int type) {		
		String scope = getScopeByType(type);
		
		PutPolicy policy = new PutPolicy(scope);
//		policy.persistentOps = "avthumb/m3u8/segtime/10/preset/video_240k|saveas/" + EncodeUtils.urlsafeEncode(scope + ":" + asName);
		policy.persistentOps = "avthumb/m3u8/segtime/10/ab/64k/vb/5m/|saveas/" + EncodeUtils.urlsafeEncode(scope + ":" + asName);
		policy.persistentNotifyUrl = Constants.getStringValue(Constants.PROCESS_NOTIFY);
		
		String upToken = null;
		try {
			upToken = policy.token(mac);
		} catch (Exception e) {
			e.printStackTrace();
			logger.warn("上传视频到七牛云失败:" + e);
		}
		
		PutRet ret = ResumeableIoApi.put(localPath, upToken, fileName);
		if(ret.getStatusCode() != 200) {
			return null;
		} else {
			Map<String, String> resultMap = JsonUtils.jsonToMap(ret.getResponse());
			return resultMap.get("persistentId");
		}
	}
	
	/**
	 * 裁剪七牛云上的图片宽度并持久化
	 * @param key 七牛云上图片key
	 * @param width 图片宽度，高度根据宽度自适应
	 * @return 七牛云相应结果为:{"persistentId": <persistentId int64>} json串
	 */
	public static String processPicturePixels(String key, int width) {
		Map<String, String> params = new HashMap<String, String>();
		params.put("bucket", QiNiuConstants.SCOPE);
		params.put("key", key);
		params.put("fops", "imageView/2/w/" + width);
		
		String url = QiNiuConstants.PFOP_INTERFACE;
		
		String accessToken = null;
		try {
			accessToken = getAccessToken(url, params);
		} catch (Exception e) {
			e.printStackTrace();
			logger.warn("生成accessToken错误：" + e);
		}

		return HttpClient4Util.post(url, accessToken, params);
	}
	
	/**
	 * 对七牛云上的音频做分片并持久化
	 * @param key 七牛云音频key
	 * @return 七牛云相应结果为:{"persistentId": <persistentId int64>} json串
	 */
	public static String processAudio(String key) {
		Map<String, String> params = new HashMap<String, String>();
		params.put("bucket", QiNiuConstants.SCOPE);
		params.put("key", key);
		params.put("fops", "avthumb/m3u8/segtime/15/preset/audio_64k");
//		params.put("notifyURL", "");
		
		String url = QiNiuConstants.PFOP_INTERFACE;
		
		//生成accessToKen
		String accessToken = null;
		try {
			accessToken = getAccessToken(url, params);
		} catch (Exception e) {
			e.printStackTrace();
			logger.warn("生成accessToken错误：" + e);
		}
		
		return HttpClient4Util.post(url, accessToken, params);
	}
	
	/**
	 * 对七牛元上的视频做分片并持久化
	 * @param key 七牛云视频key
	 * @return 七牛云相应结果为:{"persistentId": <persistentId int64>} json串
	 */
	public static String processVideo(String key) {
		Map<String, String> params = new HashMap<String, String>();
		params.put("bucket", QiNiuConstants.SCOPE);
		params.put("key", key);
		params.put("fops", "avthumb/m3u8/segtime/15/preset/video_240k");
//		params.put("notifyURL", "");
		
		String url = QiNiuConstants.PFOP_INTERFACE;
		
		//生成accessToKen
		String accessToken = null;
		try {
			accessToken = getAccessToken(url, params);
		} catch (Exception e) {
			e.printStackTrace();
			logger.warn("生成accessToken错误：" + e);
		}
		
		return HttpClient4Util.post(url, accessToken, params);
	}
	
	/**
	 * 生成私有云资源访问token
	 * @param key
	 * @param deadline
	 * @return
	 * @throws EncoderException
	 * @throws AuthException
	 */
	public static String secureUrl(String key) {
		String url = "";
		try {
			String baseUrl = URLUtils.makeBaseUrl(QiNiuConstants.SECRET_SIMPLE_DOMAIN, key);
//			String baseUrl = URLUtils.makeBaseUrl(QiNiuConstants.TEST_SECRET_SIMPLE_DOMAIN, key);
			GetPolicy getPolicy = new GetPolicy();
			getPolicy.expires = 604800;		//7天 = 60 * 60 * 24 * 7
			url =  getPolicy.makeRequest(baseUrl, mac);
		} catch (EncoderException e) {
			e.printStackTrace();
		} catch (AuthException e) {
			e.printStackTrace();
		}
		return url;
	}
	
	/**
	 * 生成accessToKen
	 * @param url 七牛云处理接口
	 * @param params 操作参数
	 * @return
	 * @throws UnsupportedEncodingException 
	 * @throws AuthException 
	 */
	private static String getAccessToken(String url, Map<String, String> params) throws UnsupportedEncodingException, AuthException {
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		if (null != params) {
			for (String key : params.keySet()) {
				NameValuePair nvp = new BasicNameValuePair(key, params.get(key));
				nvps.add(nvp);
			}
		}
		HttpPost postMethod = Client.newPost(url);
		StringEntity entity = new UrlEncodedFormEntity(nvps, HTTP.UTF_8);
		entity.setContentType("application/x-www-form-urlencoded");
		postMethod.setEntity(entity);
		
		return mac.signRequest(postMethod);
	}
	
	/**
	 * 返回空间
	 * @param type 0 上传到公共空间	1上传到私有空间
	 * @return
	 */
	private static String getScopeByType(int type) {
		String scope = "";
		switch (type) {
		case 0:
			scope = QiNiuConstants.SCOPE;
			break;
		case 1:
			scope = QiNiuConstants.SECRET_SCOPE;
//			scope = QiNiuConstants.TEST_SECRET_SCOPE;
			break;
		default:
			break;
		}
		return scope;
	}
	
	public static void main(String[] args) {
		String json = processPicturePixels("000.png", 440);
		System.out.println(json);
		Map<String, String> map = JsonUtils.jsonToMap(json);
		System.out.println(map.get("persistentId"));
	}
}
