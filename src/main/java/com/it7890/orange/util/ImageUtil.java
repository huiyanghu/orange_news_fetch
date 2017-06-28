package com.it7890.orange.util;

import com.it7890.orange.entity.ImageInfo;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import javax.net.ssl.HttpsURLConnection;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Description:
 * @date 2016�?4�?11�? 下午4:47:00
 */
public class ImageUtil {

	private static final Logger log = LoggerFactory.getLogger(ImageUtil.class);
	
	public static boolean isicon(Elements imglinks_set,Elements imglinks_data,Elements imgcontentlinks,Elements imgcontentlinksdata_srcset){
		boolean isicon=false;
		if(imglinks_set!=null&&!imglinks_set.equals("")&&imglinks_set.size()>0){
			isicon=true;
			return isicon;
		}
		if(imglinks_data!=null&&!imglinks_data.equals("")&&imglinks_data.size()>0){
			isicon=true;
			return isicon;
		}
		if(imgcontentlinks!=null&&!imgcontentlinks.equals("")&&imgcontentlinks.size()>0){
			isicon=true;
			return isicon;
		}
		if(imgcontentlinksdata_srcset!=null&&!imgcontentlinksdata_srcset.equals("")&&imgcontentlinksdata_srcset.size()>0){
			isicon=true;
			return isicon;
		}
		return isicon;
	}

	 public static String getrepstr(String content,String rep){
		    content = content.replace(rep,"");
			content=content.replace(rep+"</img>", "");
			content=content.replace(rep.substring(0, rep.length()-1)+"/>","");
			content=content.replace(rep.substring(0, rep.length()-1)+" >","");
			content=content.replace(rep.substring(0, rep.length()-1)+" ></img>","");
			content=content.replaceAll(rep.substring(0, rep.length()-1)+"(.*?)>","");
//	 	   String[] strs=rep.split(" ");
//	 	   String repx="<img";
//	 	   for(int i=1;i<strs.length-1;i++){
//	 		   repx=repx+"(.*?)"+strs[i];
//	 	   }
//	 	   repx=repx+"(.*?)(/>|></img>|>)";
//		   System.out.println(repx);
//		   content=content.replaceAll(repx, "");
//		   System.out.println(content);
		   return content;
	 }
	 
	 public static int appearNumber(String srcText, String findText) {
		    int count = 0;
		    Pattern p = Pattern.compile(findText);
		    Matcher m = p.matcher(srcText);
		    while (m.find()) {
		        count++;
		    }
		    return count;
		}

	 public static byte[] readInputStream2(InputStream inStream) throws Exception{    
	       ByteArrayOutputStream outStream = new ByteArrayOutputStream();    
	       byte[] buffer = new byte[2048];    
	       int len = 0;    
	       while( (len=inStream.read(buffer)) != -1 ){    
	           outStream.write(buffer, 0, len);    
	       }    
	       inStream.close();    
	       return outStream.toByteArray();    
	}    
	public static byte[] downloadImageByteByUrl(String imageUrl) {
		byte[] data = null;
		if (StringUtil.isNotEmpty(imageUrl)) {
			InputStream inStream = null;
			try {
				URL url = new URL(imageUrl);
				HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//				if (imageUrl.toLowerCase().startsWith("https")) {
//					HttpSSL.trustAllHttpsCertificates();
//					HttpsURLConnection.setDefaultHostnameVerifier(HttpSSL.getHostnameVerifier());
//				}
				conn.setRequestMethod("GET");
				conn.setConnectTimeout(30 * 1000);
				conn.setReadTimeout(40 * 1000);
				//欺骗请求
				conn.setRequestProperty("User-Agent", Constants.USER_AGENT);
				// 通过输入流获取图片数�?
				long contentLength = conn.getContentLength();
				inStream = conn.getInputStream();
				// 得到图片的二进制数据，以二进制封装得到数据，具有通用�?
				data = readInputStream(inStream);
				if (contentLength != data.length) {
					data = new byte[0];
				}
			} catch (Exception e) {
				data = new byte[0];
				log.warn("downloadImageByteByUrl exception, cause: {}", e);
			} finally {
				if (null != inStream) {
					try {
						inStream.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
		return data;
	}

	public static byte[] readInputStream(InputStream inStream) throws Exception {
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		// 创建�?个Buffer字符�?
		byte[] buffer = new byte[1024];
		// 每次读取的字符串长度，如果为-1，代表全部读取完�?
		int len = 0;
		// 使用�?个输入流从buffer里把数据读取出来
		while ((len = inStream.read(buffer)) != -1) {
			// 用输出流�?buffer里写入数据，中间参数代表从哪个位置开始读，len代表读取的长�?
			outStream.write(buffer, 0, len);
		}
		// 关闭输入�?
		inStream.close();
		// 把outStream里的数据写入内存
		return outStream.toByteArray();
	}

	public static String getSuffixByUrl(String url) {
		String suffix = "";
		if (StringUtil.isNotEmpty(url) && url.contains(".")) {
			int dianIndex = url.lastIndexOf(".");
			int wenIndex = (url.indexOf("?", dianIndex));
			int qieIndex = url.indexOf("&", dianIndex);

			if (wenIndex != -1) {
				suffix = url.substring(dianIndex, wenIndex);
			} else if(qieIndex != -1) {
				suffix = url.substring(dianIndex, qieIndex);
			} else {
				suffix = url.substring(url.lastIndexOf("."));
			}
		}
		return suffix;
	}
	
	public static ImageInfo getIsdown(String url) {
		ImageInfo imageInfo = null;
		if (StringUtil.isNotEmpty(url)) {
			InputStream inStream = null;
			try {
				if (url.toLowerCase().startsWith("https")) {
					HttpSSL.trustAllHttpsCertificates();
					HttpsURLConnection.setDefaultHostnameVerifier(HttpSSL.getHostnameVerifier());
				}
				URL urls = new URL(url);
				HttpURLConnection conn = (HttpURLConnection) urls.openConnection();
				conn.setRequestProperty("User-Agent", Constants.USER_AGENT);
				conn.setRequestMethod("GET");
				conn.setConnectTimeout(30 * 1000);
				inStream = conn.getInputStream();//通过输入流获取图片数据
				BufferedImage sourceImg = ImageIO.read(inStream);
				if (null != sourceImg) {
					imageInfo = new ImageInfo();
					imageInfo.setWidth(sourceImg.getWidth());
					imageInfo.setHeight(sourceImg.getHeight());
				}
			} catch (Exception e) {
				log.error("读取失败，url=" + url);
			} finally {
				if (null != inStream) {
					try {
						inStream.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
		return imageInfo;
	}

	public static ImageInfo getImageInfo(byte[] bt) {
	 	ImageInfo imageInfo = null;
	 	if (bt != null) {
		    InputStream inputs = new ByteArrayInputStream(bt);
		    try {
		    	BufferedImage bufferedImage = ImageIO.read(inputs);
		    	if (null != bufferedImage) {
				    imageInfo = new ImageInfo();
				    imageInfo.setWidth(bufferedImage.getWidth());
				    imageInfo.setHeight(bufferedImage.getHeight());
			    }
		    } catch (Exception e) {
			    log.warn("getImageInfo exception, cause: {}", e.getMessage());
		    } finally {
			    inputs = null;
		    }
	    }
	    return imageInfo;
	}

	public static ImageInfo getGifInfo(byte[] data) {
		ImageInfo imageInfo = null;
		if (data != null) {
			try {
				GifDecoder.GifImage gif = GifDecoder.read(data);
				if (null != gif) {
					imageInfo = new ImageInfo();
					imageInfo.setWidth(gif.getWidth());
					imageInfo.setHeight(gif.getHeight());
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return imageInfo;
	}
	 
	 public static void main(String[] args) throws Exception {
//		String url = "https://images2.gamme.com.tw/news2/2017/73/82/qZqanqCVkJ_Zr6Sb.jpg";
//		byte[] bt = downloadImageByteByUrl(url);
//		 InputStream inputs = new ByteArrayInputStream(bt);
//		 try {
//			 BufferedImage bufferedImage = ImageIO.read(inputs);
//			 System.out.println(bufferedImage.getWidth());
//			 System.out.println(bufferedImage.getHeight());
//		 } catch (IOException e) {
//			 e.printStackTrace();
//		 } finally {
//			 try {
//				 inputs.close();
//			 } catch (IOException e) {
//				 e.printStackTrace();
//			 }
//		 }



		 String url = "https://pgw.udn.com.tw/gw/photo.php?u=https://uc.udn.com.tw/photo/2017/05/03/98/3472129.jpg&x=0&y=0&sw=0&sh=0&sl=W&fw=1050";
		 System.out.println(getSuffixByUrl(url));

		 String url2 = "https://images2.gamme.com.tw/news2/2017/73/82/qZqanqCVkJ_Zr6Sb.jpg";
		 System.out.println(getSuffixByUrl(url2));

		 String url3 = "https://images2.gamme.com.tw/news2/2017/73/82/qZqanqCVkJ_Zr6Sb.jpg?w=1";
		 System.out.println(getSuffixByUrl(url3));
	}
}
