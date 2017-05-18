package com.it7890.orange.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.httpclient.HttpStatus;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 下载资源工具类
 *
 */
public class FileUtil {

	private final static Logger logger = LoggerFactory.getLogger(FileUtil.class);
	
	/**
	 * 写入文件
	 * @param b 字节数据
	 * @param file 写入的文件
	 * @throws IOException
	 */
	public static void outputFile(byte[] b, File file) {
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(file);
			fos.write(b);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			logger.warn("文件目录不存在:" + e);
		} catch (IOException e) {
			e.printStackTrace();
			logger.warn("写入文件失败:" + e);
		} finally {
			try {
				fos.flush();
				fos.close();
			} catch (IOException e) {
				e.printStackTrace();
				logger.warn("写入文件-->关闭输出流失败:" + e);
			}
		}
	}
	
	/**
	 * 删除文件，只删除文件，不删除目录
	 * @param file 要删除的文件
	 * @return
	 */
	public static boolean deleteFeil(File file) {
		boolean result = false;
		if(file.exists()) {
			result = file.delete();
		}
		return result;
	}
	
	/**
	 * 下载资源方法
	 * @param url 下载资源的url
	 * @param filePathName 下载保存地址
	 */
	public static void download(String url, String filePathName) {
		HttpClient httpclient = new DefaultHttpClient();
		try {
			HttpGet httpget = new HttpGet(url);

			httpget.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/31.0.1650.57 Safari/537.36");
			
			HttpResponse response = httpclient.execute(httpget);

			int statusCode = response.getStatusLine().getStatusCode();
			if(statusCode != HttpStatus.SC_OK) {
				logger.warn("下载资源异常，资源url：" + url);
				return;
			}
			
			File storeFile = new File(filePathName);
			//保存临时图片文件
			File tempFile = new File(filePathName + "tmp");
			FileOutputStream output = new FileOutputStream(tempFile);

			// 得到网络资源的字节数组,并写入文件
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				InputStream instream = entity.getContent();
				try {
					byte b[] = new byte[1024];
					int j = 0;
					while ((j = instream.read(b)) != -1) {
						output.write(b, 0, j);
					}
					b = null;
					if(output != null) {
						output.flush();
						output.close();
					}
					// 临时文件重命名，防止图片下载不全的问题 
	                tempFile.renameTo(storeFile); 
				} catch (IOException ex) {
					// In case of an IOException the connection will be released
					// back to the connection manager automatically
					throw ex;
				} catch (RuntimeException ex) {
					// In case of an unexpected exception you may want to abort
					// the HTTP request in order to shut down the underlying
					// connection immediately.
					httpget.abort();
					throw ex;
				} finally {
					// Closing the input stream will trigger connection release
					try {
						if(instream != null) {
							instream.close();
						}
					} catch (Exception ignore) {
					}
				}
			}

		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		} finally {
			httpclient.getConnectionManager().shutdown();
		}
	}
	
	public static void main(String[] args) {
		download("http://img13.360buyimg.com/da/jfs/t601/63/946011381/61145/38cd8f3b/549a84c3N7c44c630.jpg", "d:/abc.jpg");
	}
}
