package com.it7890.orange.util;

import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

/**
 * Created by Astro on 17/5/22.
 */
public class Propertie {

	private static Properties p = new Properties();
	private static Map<String, Object> resultMap = new HashMap<String, Object>();


	/**
	 * 在初始化时，把所有配置文件里的属性加载进map里边
	 */
	static {
		try {
			File propertiesFolder = new ClassPathResource("/").getFile();
			File[] files = propertiesFolder.listFiles();
			Set<Map.Entry<Object,Object>> set = null;
			if(null != files && files.length > 0){
				Map.Entry<Object, Object> entry = null;
				File tempFile = null;
				for(int i=0; i<files.length; i++){
					tempFile = files[i];
					if(tempFile.isFile()){
						String fileName = tempFile.getName();
						if(fileName.lastIndexOf(".properties") > 0){
							p.load(new FileInputStream(tempFile));
							set = p.entrySet();
							if(null != set){
								for(Iterator<Map.Entry<Object, Object>> iter = set.iterator(); iter.hasNext();){
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
}
