package com.it7890.orange.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.Map.Entry;

import org.springframework.core.io.ClassPathResource;

public class Constants {

	private static Properties p = new Properties();
	private static Map<String, Object> resultMap = new HashMap<String, Object>();
	
	
	/**
	 * 在初始化时，把所有配置文件里的属性加载进map里边
	 */
	static {
		try {
			File propertiesFolder = new ClassPathResource("/").getFile();
			File[] files = propertiesFolder.listFiles();
			Set<Entry<Object,Object>> set = null;
			if(null != files && files.length > 0){
				Entry<Object, Object> entry = null;
				File tempFile = null;
				for(int i=0; i<files.length; i++){
					tempFile = files[i];
					if(tempFile.isFile()){
						String fileName = tempFile.getName();
						if(fileName.lastIndexOf(".properties") > 0){
							p.load(new FileInputStream(tempFile));
							set = p.entrySet();
							if(null != set){
								for(Iterator<Entry<Object, Object>> iter = set.iterator(); iter.hasNext();){
									entry = iter.next();
									resultMap.put(entry.getKey().toString(), entry.getValue());
								}
							}
						}
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 提取*.properties文件里的值，此文件应在项目根路径上</br>
	 * @param key	配置文件里的键
	 * @return 真实值或者Object实例,不会返回null
	 */
	public static Object get(String key) {
		return resultMap.get(key);
	}
	
	
	/**
	 * 提取*.properties文件里的值，此文件应在项目根路径上</br>
	 * @param key	配置文件里的键
	 * @return 真实值或者null
	 */
	public static String getStringValue(String key) {
		return resultMap.get(key) == null ? null:resultMap.get(key).toString();
	}
	
	/**
	 * 提取*.properties文件里的值，此文件应在项目根路径上</br>
	 * @param key	配置文件里的键
	 * @param defaultValue 如果为空的话，返回的默认值
	 * @return 真实值或者提供的默认值
	 */
	public static String getStringValue(String key,String defaultValue) {
		return getStringValue(key) == null ? defaultValue:getStringValue(key);
	}
	
	/**
	 * 提取*.properties文件里的值，此文件应在项目根路径上</br>
	 * 将值转换成int型，如果不存在或不是数值型，返回0
	 * @param key
	 * @return
	 */
	public static int getIntValue(String key){
		return getIntValue(key, 0);
	}
	
	/**
	 * 提取*.properties文件里的值，此文件应在项目根路径上</br>
	 * 将值转换成int型，如果不存在或不是数值型，返回提供的默认值
	 * @param key
	 * @param defaultValue
	 * @return
	 */
	public static int getIntValue(String key, int defaultValue){
		String value = getStringValue(key);
		if(StringUtil.isInteger(value)){
			try{
				return Integer.parseInt(value);
			}catch(Exception e){
				e.printStackTrace();
				return defaultValue;
			}
		}else{
			return defaultValue;
		}
	}
	
	/** ------------------------ 不得姐 接口常量  -------------------------- **/
	/** 不得姐 段子 **/
	public final static String BUDEJIE_DATA = "29";
	/** 不得姐  图片 **/
	public final static String BUDEJIE_DATA_PIC = "10";
	/** 不得姐 音频 **/
	public final static String BUDEJIE_VOICE = "31";
	/** 不得姐 视频 **/
	public final static String BUDEJIE_VIDEO = "41";
	
	/** 不得姐标识 **/
	public final static String BUDEJIE_MARK = "budejie_";
	/** 不得姐标识gif动态图 **/
	public final static String BUDEJIE_IS_GIF = "1";
	/** 不得姐 推荐状态 **/
	public final static String BUDEJIE_HOT_STATE = "4";
	
	/** ------------------------ 乐吧子 常量  -------------------------- **/
	/** 乐吧子 标识 **/
	public final static String LEBAZI_MARK = "lebazi_";
	/** 乐吧子域名 **/
	public final static String LEBAZI_DOMAIN = "http://www.lebazi.com";
	
	/** ------------------------ 搞笑啦 常量  -------------------------- **/
	/** 搞笑啦 标识 **/
	public final static String GAOXIAOLA_MARK = "gaoxiaola_";
	/** 搞笑啦域名 **/
	public final static String GAOXIAOLA_DOMAIN = "http://www.gaoxiaola.com";
	
	/** ------------------------ 内涵村 常量  -------------------------- **/
	/** 内涵村 标识 **/
	public final static String NHCUN_MARK = "nhcun_";
	/** 内涵村 域名 **/
	public final static String NHCUN_DOMAIN = "http://www.nhcun.com";
	/** 内涵村 动态图 url **/
	public final static String NHCUN_GIF_DOMAIN = "http://www.nhcun.com/gif/";
	/** 内涵村 显示页 url **/
	public final static String NHCUN_SHOW_DOMAIN = "http://www.nhcun.com/show/";
	
	/** ------------------------ QQ天空网 常量  -------------------------- **/
	/** QQ天空网 标识 **/
	public final static String QQSKY_MARK = "qqsky_";
	/** QQ天空网 动态 url **/
	public final static String QQSKY_DONGTAI_DOMAIN = "http://www.qqsky.cc/gaoxiao/dongtaitupian/";
	/** QQ天空网 内涵 url **/
	public final static String QQSKY_NEIHAN_DOMAIN = "http://www.qqsky.cc/gaoxiao/neihantu/";
	
	/** ------------------------ 6188美图 常量  -------------------------- **/
	/** 6188 标识 **/
	public final static String MEITU_MARK = "6188_";
	/** 6188美图 美女自拍 url **/
	public final static String MEITU_MEINVZIPAI_DOMAIN = "http://www.6188.net/mm/meinvzipai/";
	/** 6188美图 清纯美女 url **/
	public final static String MEITU_QINGCHUNMEINV_DOMAIN = "http://www.6188.net/mm/qingchunmeinv/";
	/** 6188美图 性感 url **/
	public final static String MEITU_XIURENMOTE_DOMAIN = "http://www.6188.net/mm/xiurenmote/";
	
	/** ------------------------ 糗事百科 常量  -------------------------- **/
	/** 糗事百科 标识 **/
	public final static String QIUSHIBAIKE_MARK = "qiushibaike_";
	/** 糗事百科 域名 **/
	public final static String QIUSHIBAIKE_DOMAIN = "http://www.qiushibaike.cc";
	
	/** ------------------------ 笑酒楼 常量 --------------------------- **/
	/** 笑酒楼 标识 **/
	public final static String XIAOJIALOU_MARK = "xiaojialou_";
	/** 笑酒楼主域名 **/
	public final static String XIAOJIALOU_DOMAIN = "http://www.xiaojiulou.net";
	/** 笑酒楼 内涵漫画 url xe**/
	public final static String XIAOJIALOU_HOT_DOMAIN = "http://www.xiaojiulou.com/hot/";
	/** 笑酒楼 色系军团 url xemh**/
	public final static String XIAOJIALOU_SEXI_DOMAIN = "http://www.xiaojiulou.com/sexi/";
	/** 笑酒楼 色小组邪恶漫画 url XG**/
	public final static String XIAOJIALOU_SEXIAOZU_DOMAIN = "http://www.xiaojiulou.com/sexiaozu/";
	/** 笑酒楼 搞笑图片 url nh**/
	public final static String XIAOJIALOU_GAOXIAOTUPIAN_DOMAIN = "http://www.xiaojiulou.com/gaoxiaotupian/";
	
	/** ------------------------ 帖子类型 常量  -------------------------- **/
	/** 段子 **/
	public final static int LOCAL_DATA = 0;
	/** 普通图片 **/
	public final static int LOCAL_DATA_PIC = 1;
	/** 音频 **/
	public final static int LOCAL_VOICE = 2;
	/** 视频 **/
	public final static int LOCAL_VIDEO = 3;
	/** gif动态图 **/
	public final static int LOCAL_DATA_GIF = 4;
	
	/** ------------------------ 抓取状态 常量  -------------------------- **/
	/** 抓取状态：待抓取 **/
	public final static int SITE_STATUS_READY = 0;
	/** 抓取状态：抓取完成 **/
	public final static int SITE_STATUS_COMPLETE = 1;
	
	/** ------------------------ 抓取数据源项类型 常量  -------------------------- **/
	/** 数据项类型：详情 **/
	public final static int SITE_PAGE_TYPE_DETAIL = 0;
	/** 数据项类型：列表 **/
	public final static int SITE_PAGE_TYPE_LIST = 1;
	/** 数据项类型：分页 **/
	public final static int SITE_PAGE_TYPE_PAGING = 2;
	
	/** ------------------------ 帖子状态 常量  -------------------------- **/
	/** 帖子状态：不显示  (如：处理失败)**/
	public final static int TOPIC_STATUS_BLOCK = 0;
	/** 帖子状态：成功 **/
	public final static int TOPIC_STATUS_ACTIVE = 1;
	/** 帖子状态：待下载资源 **/
	public final static int TOPIC_STATUS_DOWNLOAD = 2;
	/** 帖子状态：待上传资源到七牛云 **/
	public final static int TOPIC_STATUS_UPLOAD = 3;
	/** 帖子状态：上传成功，待处理资源  **/
	public final static int TOPIC_STATUS_PROCESS = 4;
	
	/** 用户类型：抓取的用户 **/
	public final static int USER_STATUS_FETCH = 0;
	/** 允许取空的次数 **/
	public final static int EMPTY_SUM = 50;
	
	/** 标识帖子 **/
	public final static String TOPIC = "topic_";
	/** 标识用户 **/
	public final static String USERINFO = "userInfo_";
	/** 默认图片url **/
	public static final String DEFAULT_PIC_URL = "default_pic_url";
	/** 资源临时保存路径 **/
	public static final String RESOURCE_TMP_PATH = "resourceTmpPath";
	
	/** 七牛处理结果通知的url **/
	public static final String PROCESS_NOTIFY = "process_notify";
	
	/** 下载文件最大限制 **/
	public static final String MAX_CONTENT_LENGTH = "max_content_length";
	
	/** 固定线程池线程数 **/
	public static final String FIXED_THREAD_POOL = "fixed_thread_pool";
	
	
	/** -------------------------------- 分类code -------------------------------- **/
	/** 邪恶 **/
	public static final String XE = "xe";
	/** 内涵 **/
	public static final String NH = "nh";
	/** 偷拍 **/
	public static final String TP = "tp";
	/** 性感 **/
	public static final String XG = "xg";
	/** 清纯 **/
	public static final String QC = "qc";
	/** 自拍 **/
	public static final String ZP = "zp";
	/** 邪恶漫画 **/
	public static final String XEMH = "xemh";
}
