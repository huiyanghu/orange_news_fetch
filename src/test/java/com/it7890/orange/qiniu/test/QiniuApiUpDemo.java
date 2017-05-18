package com.it7890.orange.qiniu.test;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.crypto.spec.SecretKeySpec;

import com.it7890.orange.util.DateUtil;
import org.apache.commons.codec.EncoderException;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;

import com.it7890.orange.util.HttpClient4Util;
import com.it7890.orange.util.JsonUtils;
import com.it7890.orange.util.QiNiuConstants;
import com.qiniu.api.auth.AuthException;
import com.qiniu.api.auth.digest.Mac;
import com.qiniu.api.fop.ImageView;
import com.qiniu.api.io.IoApi;
import com.qiniu.api.io.PutExtra;
import com.qiniu.api.io.PutRet;
import com.qiniu.api.net.CallRet;
import com.qiniu.api.net.Client;
import com.qiniu.api.net.EncodeUtils;
import com.qiniu.api.resumableio.ResumeableIoApi;
import com.qiniu.api.rs.GetPolicy;
import com.qiniu.api.rs.PutPolicy;
import com.qiniu.api.rs.URLUtils;

public class QiniuApiUpDemo {

	/**
	 * 上传token
	 * @return
	 */
	public String getUpToken() {
		String accessKey = QiNiuConstants.ACCESS_KEY;
		String secretKey = QiNiuConstants.SECRET_KEY;
		Mac mac = new Mac(accessKey, secretKey);
		PutPolicy policy = new PutPolicy("nian30");
		
		String s = EncodeUtils.urlsafeEncode("nian30:0003.png");
		policy.persistentOps = "imageView/2/w/50|saveas/"+s;
		String upToken = null;
		try {
			upToken = policy.token(mac);
		} catch (AuthException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return upToken;
	}
	
	/**
	 * 简单上传文件
	 */
	public void uploadFile() {
		String uptoken = getUpToken();
		PutExtra extra = new PutExtra();
		String key = "20141224/1.png";
		String localFile = "D:\\demo2.png";
		PutRet ret = IoApi.putFile(uptoken, key, localFile, extra);
		System.out.println(ret.statusCode);
	}
	
	/**
	 * 上传图片并持久化处理结果
	 * @throws AuthException
	 * @throws JSONException
	 */
	public void uploadFileProceed() throws AuthException, JSONException {
		File localPath = new File("D:\\01.jpg");
		String fileName = "xxxx2.jpg";
		
		PutPolicy policy = new PutPolicy("toupaituijian");
		String s1 = EncodeUtils.urlsafeEncode("toupaituijian:xxxx_2.jpg");
		
		policy.persistentOps = "imageView/2/w/240|saveas/" + s1;
		policy.persistentNotifyUrl = "http://www.baidu.com";
		
		String upToken = null;
		try {
			Mac mac = new Mac(QiNiuConstants.ACCESS_KEY, QiNiuConstants.SECRET_KEY);
			upToken = policy.token(mac);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		PutRet ret = ResumeableIoApi.put(localPath, upToken, fileName);
		System.out.println(ret.statusCode);
		Map<String, String> resultMap = JsonUtils.jsonToMap(ret.getResponse());
		System.out.println(resultMap.get("persistentId"));
	}
	
	public String signRequest(HttpPost post) throws AuthException {
		URI uri = post.getURI();
		String path = uri.getRawPath();
		String query = uri.getRawQuery();
		HttpEntity entity = post.getEntity();

		byte[] secretKey = QiNiuConstants.SECRET_KEY.getBytes();
		javax.crypto.Mac mac = null;
		try {
			mac = javax.crypto.Mac.getInstance("HmacSHA1");
		} catch (NoSuchAlgorithmException e) {
			throw new AuthException("No algorithm called HmacSHA1!", e);
		}

		SecretKeySpec keySpec = new SecretKeySpec(secretKey, "HmacSHA1");
		try {
			mac.init(keySpec);
			mac.update(path.getBytes());
		} catch (InvalidKeyException e) {
			throw new AuthException("You've passed an invalid secret key!", e);
		} catch (IllegalStateException e) {
			throw new AuthException(e);
		}

		if (query != null && query.length() != 0) {
			mac.update((byte) ('?'));
			mac.update(query.getBytes());
		}
		mac.update((byte) '\n');
		if (entity != null) {
			org.apache.http.Header ct = entity.getContentType();
			if (ct != null
					&& "application/x-www-form-urlencoded".equals(ct.getValue())) {
				ByteArrayOutputStream w = new ByteArrayOutputStream();
				try {
					entity.writeTo(w);
				} catch (IOException e) {
					throw new AuthException(e);
				}
				mac.update(w.toByteArray());
			}
		}

		byte[] digest = mac.doFinal();
		byte[] digestBase64 = EncodeUtils.urlsafeEncodeBytes(digest);

		StringBuffer b = new StringBuffer();
		b.append(QiNiuConstants.ACCESS_KEY);
		b.append(':');
		b.append(new String(digestBase64));
		
		return b.toString();
	}
	
	public void uploadDemo() {
		String url = "http://nian30.qiniudn.com/upload_demo3.png";
		
		ImageView imageView = new ImageView();
		imageView.mode = 2;
		imageView.quality = 100;
		imageView.width = 260;
		imageView.height = 260;
		imageView.format = "jpg";

		CallRet callRet = imageView.call(url);
		System.out.println(callRet);
	}
	
	public void uploadDemo2() {
		
	}
	
	/**
	 * 对已存在的资源做持久化处理
	 * @throws UnsupportedEncodingException
	 * @throws AuthException
	 */
	public void storedFileDemo() throws UnsupportedEncodingException, AuthException {
		String url = "http://api.qiniu.com/pfop/";
		Map<String, String> params = new HashMap<String, String>();
		params.put("bucket", QiNiuConstants.SCOPE);
		params.put("key", "01.jpg");
		params.put("fops", "imageView/2/w/50");
		params.put("notifyURL", "");
		
		//生成accessToKen
		String accessToken = getAccessToken(url, params);
		
		HttpClient4Util httpClient = new HttpClient4Util();
		String result = httpClient.post(url, accessToken, params);
		System.out.println(result);
	}
	
	/**
	 * 对已存在的视频做分片持久化处理
	 * @throws UnsupportedEncodingException
	 * @throws AuthException
	 */
	public void storeVideoDemo() throws UnsupportedEncodingException, AuthException {
		String url = "http://api.qiniu.com/pfop/";
		Map<String, String> params = new HashMap<String, String>();
		params.put("bucket", QiNiuConstants.SCOPE);
		params.put("key", "demo.mp4");
		params.put("fops", "avthumb/m3u8/segtime/15/preset/video_240k");
		params.put("notifyURL", "");
		
		//生成accessToKen
		String accessToken = getAccessToken(url, params);
		
		HttpClient4Util httpClient = new HttpClient4Util();
		String result = httpClient.post(url, accessToken, params);
		System.out.println(result);
	}
	
	/**
	 * 视频截图
	 */
	public void screenVideoDemo() throws UnsupportedEncodingException, AuthException {
		String url = "http://api.qiniu.com/pfop/";
		Map<String, String> params = new HashMap<String, String>();
		params.put("bucket", QiNiuConstants.SCOPE);
		params.put("key", "demo.mp4");
		params.put("fops", "vframe/png/offset/1/w/240/h/320");	//截视频第一秒的帧
		params.put("notifyURL", "");
		
		//生成accessToKen
		String accessToken = getAccessToken(url, params);
		
		HttpClient4Util httpClient = new HttpClient4Util();
		String result = httpClient.post(url, accessToken, params);
		System.out.println(result);
	}
	
	/**
	 * 对已存在的音频做分片持久处理
	 * @throws UnsupportedEncodingException
	 * @throws AuthException
	 */
	public void storeAudioDemo() throws UnsupportedEncodingException, AuthException {
		String url = "http://api.qiniu.com/pfop/";
		Map<String, String> params = new HashMap<String, String>();
		params.put("bucket", QiNiuConstants.SCOPE);
		params.put("key", "wKiFWlR-aEmAJBY4ATM3Hayuvg8818.mp3");
		params.put("fops", "avthumb/m3u8/segtime/15/preset/audio_64k");
		params.put("notifyURL", "");
		
		//生成accessToKen
		String accessToken = getAccessToken(url, params);
		
		HttpClient4Util httpClient = new HttpClient4Util();
		String result = httpClient.post(url, accessToken, params);
		System.out.println(result);
	}
	
	/**
	 * 私有资源下载
	 * @throws EncoderException
	 * @throws AuthException
	 */
	public void secureDownload() throws EncoderException, AuthException {
		String url = "http://7tszrx.com1.z0.glb.clouddn.com/20150121/d187512d0c414f5a94fc8e66fcb84308.gif?e=" + DateUtil.DatePlusMonth(new Date(), 1) / 1000;
		
		Mac mac = new Mac(QiNiuConstants.ACCESS_KEY, QiNiuConstants.SECRET_KEY);
		String baseUrl = URLUtils.makeBaseUrl("7tszrx.com1.z0.glb.clouddn.com", "20150121/d187512d0c414f5a94fc8e66fcb84308.gif");
		GetPolicy getPolicy = new GetPolicy();
		String downloadUrl = getPolicy.makeRequest(baseUrl, mac);
		
		System.out.println(downloadUrl);
	}
	
	private String getAccessToken(String url, Map<String, String> params) throws UnsupportedEncodingException, AuthException {
		Mac mac = new Mac(QiNiuConstants.ACCESS_KEY, QiNiuConstants.SECRET_KEY);
		
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		if (null != params) {
			for (String key : params.keySet()) {
				NameValuePair nvp = new BasicNameValuePair(key, params.get(key));
				nvps.add(nvp);
			}
		}
		HttpPost postMethod = Client.newPost(url);
		StringEntity entity = new UrlEncodedFormEntity(nvps, "UTF-8");
		entity.setContentType("application/x-www-form-urlencoded");
		postMethod.setEntity(entity);
		
		return mac.signRequest(postMethod);
	}
	
	public void getVideoHeightWidth() {
		String key = "demo.mp4";
		HttpClient4Util httpClient = new HttpClient4Util();
		String resultJson = httpClient.get(QiNiuConstants.DOMAIN + key + "?avinfo");
		JsonUtils jsonUtils = new JsonUtils();
		Map mapJson = jsonUtils.jsonToMap(resultJson);
		Map<String, String> map = (Map<String, String>) ((List)mapJson.get("streams")).get(0);
		int heightWidth[] = new int[2];
		System.out.println(map.get("height"));
		heightWidth[0] = Integer.parseInt(map.get("height"));
		heightWidth[1] = Integer.parseInt(map.get("width"));
		System.out.println(heightWidth);
	}
	
}
