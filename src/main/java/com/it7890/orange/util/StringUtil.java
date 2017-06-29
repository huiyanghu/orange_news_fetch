package com.it7890.orange.util;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.bind.ServletRequestUtils;

public class StringUtil extends StringUtils {

	static Log log = LogFactory.getLog(StringUtil.class);

    // 将字符串转移为ASCII码
    public static String getCnASCII(String cnStr) {
        StringBuffer strBuf = new StringBuffer();
        byte[] bGBK = cnStr.getBytes();
        for (int i = 0; i < bGBK.length; i++) {
            // System.out.println(Integer.toHexString(bGBK[i]&0xff));
            strBuf.append(Integer.toHexString(bGBK[i] & 0xff));
        }
        return strBuf.toString();
    }
    
    
    /**
     * 将来源对象的值，赋给新的类，返回该类的多个对象
     * @param sourceList
     * @param clazz
     * @return 任意参数传空的话，会返回null
     */
    public static <T> List<T> beanCopy(List<?> sourceList, Class<T> clazz){
    	if(null == sourceList || null == clazz){
    		return null;
    	}
    	List<T> result = new ArrayList<T>();
    	for(Iterator<?> iter = sourceList.iterator(); iter.hasNext();){
    		result.add(beanCopy(iter.next(), clazz));
    	}
    	return result;
    }
    
    /**
     * 将来源对象的值，赋给新的类，返回该类的对象
     * @param o
     * @param clazz
     * @return 任意参数为空都会返回null
     */
    public static <T> T beanCopy(Object o, Class<T> clazz){
    	if(null == o || null == clazz){
    		return null;
    	}
    	T t = null;
    	try {
			t = clazz.newInstance();
			beanCopy(o, t, false);
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
    	return t;
    }
	
	/**
	 * 计算显示的页数,特殊数据已被处理,不会报错
	 * @param total			总数据个数
	 * @param pageSize		每页显示的个数
	 * @return
	 */
	public static int getPageNum(int total, int pageSize){
		if(total <=0 || pageSize <= 0){
			return 0;
		}
		if(total%pageSize == 0){
			return total/pageSize;
		}else{
			return total/pageSize + 1;
		}
	}
	
	/**
	 * 验证某字符串是否符合邮箱格式
	 * @param str
	 * @return
	 */
	public static boolean isEmail(String str){
		String regex = "\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*";  
	    Pattern   p   =   Pattern.compile(regex);  
	    Matcher   m   =   p.matcher(str);  
	    return m.matches();  
	}
	
	/**
	 * 验证某字符串是否符合手机格式
	 * @param str
	 * @return
	 */
	public static boolean isMobile(String str){
		String regular = "1[3,4,5,8]{1}\\d{9}";
		Pattern pattern = Pattern.compile(regular);
		boolean flag = false;
		if (str != null) {
			Matcher matcher = pattern.matcher(str);
			flag = matcher.matches();
		}
		return flag;
	}
	
	/**
	 * 解决GET方式乱码问题
	 * @param s
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public static String encode(String s) throws UnsupportedEncodingException{
		if(null == s || s.length() == 0){
			return "";
		}
		s = new String(s.getBytes("iso-8859-1"),"UTF-8");
		return s;
	}

	/**
	 * 计算当前的起始页数，特殊数据已被处理,不会报错
	 * @param startPage	当前的起始数
	 * @param pageSize	每页个数
	 * @return
	 */
	public static int getStartPage(int startPage, int pageSize){
		if(startPage <= 0 || pageSize <= 0){
			return 0;
		}
		return startPage/pageSize;
	}
	
	
	
	/**
	 * 根据提供的参数，生成md5值</br>
	 * 会对传过来的值用UTF-8方式编码
	 * @param ss
	 * @return	正常的字符串，出错会返回null
	 */

	public static String getMD5(String ss) {
		byte[] source;
		try {
			source = ss.getBytes("UTF-8");
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
			return null;
		}
		String s = null;
		char hexDigits[] = { // 用来将字节转换成 16 进制表示的字符
		'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd',
				'e', 'f' };
		try {
			java.security.MessageDigest md = java.security.MessageDigest
					.getInstance("MD5");
			md.update(source);
			byte tmp[] = md.digest(); // MD5 的计算结果是一个 128 位的长整数，
			// 用字节表示就是 16 个字节
			char str[] = new char[16 * 2]; // 每个字节用 16 进制表示的话，使用两个字符，
			// 所以表示成 16 进制需要 32 个字符
			int k = 0; // 表示转换结果中对应的字符位置
			for (int i = 0; i < 16; i++) { // 从第一个字节开始，对 MD5 的每一个字节
				// 转换成 16 进制字符的转换
				byte byte0 = tmp[i]; // 取第 i 个字节
				str[k++] = hexDigits[byte0 >>> 4 & 0xf]; // 取字节中高 4 位的数字转换, 
				// >>> 为逻辑右移，将符号位一起右移
				str[k++] = hexDigits[byte0 & 0xf]; // 取字节中低 4 位的数字转换
			}
			s = new String(str); // 换后的结果转换为字符串

		} catch (Exception e) {
			e.printStackTrace();
		}
		return s;
	}

	/**
	 * 本方法封装了往前台设置的header,contentType等信息
	 * @param message			需要传给前台的数据
	 * @param type				指定传给前台的数据格式,如"html","json"等
	 * @param response			HttpServletResponse对象
	 * @throws IOException
	 * @createDate 2010-12-31 17:55:41
	 */
	public static void writeToWeb(String message, String type, HttpServletResponse response) throws IOException{
		response.setHeader("Pragma", "No-cache");
		response.setHeader("Cache-Control", "no-cache");
		response.setContentType("text/" + type +"; charset=utf-8");
		response.getWriter().write(message);
		response.getWriter().close();
	}

	
	
	
	
	/**
	 * 如果传过来个空，则返回""</br>
	 * 否则返回原对象
	 * @param o
	 * @return
	 */
	public static Object nullToSpace(Object o){
		if(null == o){
			return "";
		}
		return o;
	}
	
	/**
	 * 提供字符串是否可转换成数值型的判断</br>
	 * 如果可转成数值，则返回false</br>
	 * 如果不可转成数值，则返回true</br>
	 * isnan == is not a number</br>
	 * @param s	需要测试的字符串
	 * @return	true or false
	 */
	public static boolean isNAN(String s){
		if(null == s || s.length() == 0){
			return true;
		}
		Pattern pattern = Pattern.compile("[-+]?[0-9]*\\.?[0-9]+$");
		Matcher isNum = pattern.matcher(s);
		if(isNum.matches()){
			return false;
		}else{
			return true;
		}
	}
	
	public static boolean isInteger(String s){
		if(null == s || s.length() == 0){
			return false;
		}
		Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");
		return pattern.matcher(s).matches();
	}
	
	
	/**
	 * 取随机的32位uuid
	 * @return
	 */
	public static String getUUID () {
		return UUID.randomUUID().toString().replaceAll("-", "");
	}
	
	/**
	 * 获取通过easyUI传过来的排序参数
	 * @param request
	 * @return
	 */
	public static String getOrderString(HttpServletRequest request){
		String orderString = "";
		
		String sortName = ServletRequestUtils.getStringParameter(request, "sort", "");
		String sortOrder = ServletRequestUtils.getStringParameter(request, "order", "");
		if(sortName.length() > 0){
			orderString = sortName;
			if(sortOrder.length() > 0){
				orderString += " " + sortOrder;
			}
		}
		return orderString;
	}
	
	
	/**
	 * 把Map<String,Object>处理成实体类
	 * @param clazz		想要的实体类
	 * @param list		包含信息的列表
	 * @return	任意参数传null会返回null，否则会返回List<T>对象
	 */
	public static <T> List<T> mapToList(Class<T> clazz, List<Map<String,Object>> list){
		
		if(null == list || null == clazz){
			return null;
		}
		List<T> result = new ArrayList<T>();
		Map<String,Object> map;
		for(Iterator<Map<String,Object>> iter = list.iterator(); iter.hasNext();){
			map = iter.next();
			result.add(mapToObject(clazz, map));
		}
		return result;
	}
	
	/**
	 * 把Map<String,Object>处理成实体类
	 * @param clazz		想要的实体类
	 * @param map		包含信息的Map对象
	 * @return
	 */
	public static <T> T mapToObject(Class<T> clazz, Map<String,Object> map){
		
		if(null == map){
			return null;
		}
		
		Field[] fields = clazz.getDeclaredFields();	//取到所有类下的属性，也就是变量名
		Field field;
		T o = null;
		try {
			o = clazz.newInstance();
		} catch (InstantiationException e1) {
			e1.printStackTrace();
		} catch (IllegalAccessException e1) {
			e1.printStackTrace();
		}
		for(int i=0; i<fields.length; i++){
			field = fields[i];
			String fieldName = field.getName();
			//把属性的第一个字母处理成大写
			String stringLetter=fieldName.substring(0, 1).toUpperCase();    
			//取得set方法名，比如setBbzt
			String setterName="set"+stringLetter+fieldName.substring(1);    
			//真正取得set方法。
			Method setterMethod = null;
			Class<?> fieldClass = field.getType();
			try {
				Object value = map.get(fieldName);
				if (value != null && String.valueOf(value).trim().length() > 0 && isHaveSuchMethod(clazz, setterName)) {
					setterMethod = clazz.getMethod(setterName, fieldClass);
					if (fieldClass == String.class) {
						setterMethod.invoke(o, String.valueOf(value));// 为其赋值
					} else if (fieldClass == Integer.class || fieldClass == int.class) {
						setterMethod.invoke(o,
								Integer.parseInt(String.valueOf(value)));// 为其赋值
					} else if (fieldClass == Boolean.class || fieldClass == boolean.class) {
						setterMethod.invoke(o,
								Boolean.getBoolean(String.valueOf(value)));// 为其赋值
					} else if (fieldClass == Short.class || fieldClass == short.class) {
						setterMethod.invoke(o,
								Short.parseShort(String.valueOf(value)));// 为其赋值
					} else if (fieldClass == Long.class || fieldClass == long.class) {
						setterMethod.invoke(o,
								Long.parseLong(String.valueOf(value)));// 为其赋值
					} else if (fieldClass == Double.class || fieldClass == double.class) {
						setterMethod.invoke(o,
								Double.parseDouble(String.valueOf(value)));// 为其赋值
					} else if (fieldClass == Float.class || fieldClass == float.class) {
						setterMethod.invoke(o,
								Float.parseFloat(String.valueOf(value)));// 为其赋值
					} else if (fieldClass == BigInteger.class) {
						setterMethod.invoke(o, BigInteger.valueOf(Long
								.parseLong(String.valueOf(value))));// 为其赋值
					} else if (fieldClass == BigDecimal.class) {
						setterMethod.invoke(o, BigDecimal.valueOf(Double
								.parseDouble(String.valueOf(value))));// 为其赋值
					} else if (fieldClass == Date.class) {
						if (map.get(fieldName).getClass() == java.sql.Date.class) {
							setterMethod.invoke(o, new Date(
									((java.sql.Date) value).getTime()));// 为其赋值
						} else if (map.get(fieldName).getClass() == java.sql.Time.class) {
							setterMethod.invoke(o, new Date(
									((java.sql.Time) value).getTime()));// 为其赋值
						} else if (map.get(fieldName).getClass() == java.sql.Timestamp.class) {
							setterMethod.invoke(o, new Date(
									((java.sql.Timestamp) value).getTime()));// 为其赋值
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		}
		return o;
	}
	
	/**
	 * 将map中存放的数据转成类</br>
	 * map中的key应该是以下划线分隔的</br>
	 * 目前还没有实现解析注解的方式来确定key，等有时间的时候加上
	 * @param clazz
	 * @param list
	 * @return 任意参数为空时会返回null
	 */
	public static <T> List<T> mapToListWith_(Class<T> clazz, List<Map<String, Object>> list){
		if(null == list || null == clazz){
			return null;
		}
		List<T> result = new ArrayList<T>();
		Map<String,Object> map;
		for(Iterator<Map<String,Object>> iter = list.iterator(); iter.hasNext();){
			map = iter.next();
			result.add(mapToObjectWith_(clazz, map));
		}
		return result;
	}
	/**
	 * 将map中存放的数据转成类</br>
	 * map中的key应该是以下划线分隔的</br>
	 * 目前还没有实现解析注解的方式来确定key，等有时间的时候加上
	 * @param clazz
	 * @param map
	 * @return
	 */
	public static <T> T mapToObjectWith_(Class<T> clazz, Map<String,Object> map){
		
		if(null == map){
			return null;
		}
		
		Field[] fields = clazz.getDeclaredFields();	//取到所有类下的属性，也就是变量名
		Field field;
		T o = null;
		try {
			o = clazz.newInstance();
		} catch (InstantiationException e1) {
			e1.printStackTrace();
		} catch (IllegalAccessException e1) {
			e1.printStackTrace();
		}
		for(int i=0; i<fields.length; i++){
			field = fields[i];
			String fieldName = field.getName();
			//TODO 目前本方法按驼峰规则自动转换属性名，以后应该先判断是否有注解，有注解的话应该用注解规定的名
			StringBuffer sb = new StringBuffer();//map中存放的key字符串
			char[] filedNameArray = fieldName.toCharArray();
			for(int j=0; j<filedNameArray.length; j++){
				if(Character.isUpperCase(filedNameArray[j])){
					sb.append("_");
				}
				sb.append(Character.toLowerCase(filedNameArray[j]));
			}
			String mapKey = sb.toString();//map中真实的key
			//把属性的第一个字母处理成大写
			String stringLetter=fieldName.substring(0, 1).toUpperCase();    
			//取得set方法名，比如setBbzt
			String setterName="set"+stringLetter+fieldName.substring(1);    
			//真正取得set方法。
			Method setterMethod = null;
			Class<?> fieldClass = field.getType();
			try {
				Object value = map.get(mapKey);
				if (value != null && String.valueOf(value).trim().length() > 0 && isHaveSuchMethod(clazz, setterName)) {
					setterMethod = clazz.getMethod(setterName, fieldClass);
					if (fieldClass == String.class) {
						setterMethod.invoke(o, String.valueOf(value));// 为其赋值
					} else if (fieldClass == Integer.class || fieldClass == int.class) {
						setterMethod.invoke(o,
								Integer.parseInt(String.valueOf(value)));// 为其赋值
					} else if (fieldClass == Boolean.class || fieldClass == boolean.class) {
						setterMethod.invoke(o,
								Boolean.getBoolean(String.valueOf(value)));// 为其赋值
					} else if (fieldClass == Short.class || fieldClass == short.class) {
						setterMethod.invoke(o,
								Short.parseShort(String.valueOf(value)));// 为其赋值
					} else if (fieldClass == Long.class || fieldClass == long.class) {
						setterMethod.invoke(o,
								Long.parseLong(String.valueOf(value)));// 为其赋值
					} else if (fieldClass == Double.class || fieldClass == double.class) {
						setterMethod.invoke(o,
								Double.parseDouble(String.valueOf(value)));// 为其赋值
					} else if (fieldClass == Float.class || fieldClass == float.class) {
						setterMethod.invoke(o,
								Float.parseFloat(String.valueOf(value)));// 为其赋值
					} else if (fieldClass == BigInteger.class) {
						setterMethod.invoke(o, BigInteger.valueOf(Long
								.parseLong(String.valueOf(value))));// 为其赋值
					} else if (fieldClass == BigDecimal.class) {
						setterMethod.invoke(o, BigDecimal.valueOf(Double
								.parseDouble(String.valueOf(value))));// 为其赋值
					} else if (fieldClass == Date.class) {
						if (map.get(mapKey).getClass() == java.sql.Date.class) {
							setterMethod.invoke(o, new Date(
									((java.sql.Date) value).getTime()));// 为其赋值
						} else if (map.get(mapKey).getClass() == java.sql.Time.class) {
							setterMethod.invoke(o, new Date(
									((java.sql.Time) value).getTime()));// 为其赋值
						} else if (map.get(mapKey).getClass() == java.sql.Timestamp.class) {
							setterMethod.invoke(o, new Date(
									((java.sql.Timestamp) value).getTime()));// 为其赋值
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		}
		return o;
	}
	
	/**
	 * 将请求的参数放进clazz类里边
	 * @param request
	 * @param clazz
	 * @return 
	 */
	public static <T> T requestToObject(HttpServletRequest request, Class<T> clazz){
		
		if(null == request){
			return null;
		}
		
		Field[] fields = clazz.getDeclaredFields();	//取到所有类下的属性，也就是变量名
		Field field;
		T o = null;
		try {
			o = clazz.newInstance();
		} catch (InstantiationException e1) {
			e1.printStackTrace();
		} catch (IllegalAccessException e1) {
			e1.printStackTrace();
		}
		for(int i=0; i<fields.length; i++){
			field = fields[i];
			String fieldName = field.getName();
			//把属性的第一个字母处理成大写
			String stringLetter=fieldName.substring(0, 1).toUpperCase();    
			//取得set方法名，比如setBbzt
			String setterName="set"+stringLetter+fieldName.substring(1);    
			//真正取得get方法。
			Method setterMethod = null;
			Class<?> fieldClass = field.getType();
			try {
				Object value = request.getParameter(fieldName);
				Object valueArray = request.getParameterValues(fieldName);
				setterMethod = clazz.getMethod(setterName, fieldClass);
				if (value != null && isHaveSuchMethod(clazz, setterName)) {
					if(String.valueOf(value).trim().length() > 0){
						if (fieldClass == String.class) {
							setterMethod.invoke(o, String.valueOf(value));// 为其赋值
						} else if (fieldClass == Integer.class || fieldClass == int.class) {
							setterMethod.invoke(o, Integer.parseInt(String.valueOf(value)));// 为其赋值
						} else if (fieldClass == Boolean.class || fieldClass == boolean.class) {
							setterMethod.invoke(o,
									Boolean.getBoolean(String.valueOf(value)));// 为其赋值
						} else if (fieldClass == Short.class || fieldClass == short.class) {
							setterMethod.invoke(o,
									Short.parseShort(String.valueOf(value)));// 为其赋值
						} else if (fieldClass == Long.class || fieldClass == long.class) {
							setterMethod.invoke(o,
									Long.parseLong(String.valueOf(value)));// 为其赋值
						} else if (fieldClass == Double.class || fieldClass == double.class) {
							setterMethod.invoke(o,
									Double.parseDouble(String.valueOf(value)));// 为其赋值
						} else if (fieldClass == Float.class || fieldClass == float.class) {
							setterMethod.invoke(o,
									Float.parseFloat(String.valueOf(value)));// 为其赋值
						} else if (fieldClass == BigInteger.class) {
							setterMethod.invoke(o, BigInteger.valueOf(Long
									.parseLong(String.valueOf(value))));// 为其赋值
						} else if (fieldClass == BigDecimal.class) {
							setterMethod.invoke(o, BigDecimal.valueOf(Double
									.parseDouble(String.valueOf(value))));// 为其赋值
						} else if (fieldClass == Date.class) {
							String tempValue = value.toString();
							Date tempDate = null;
							// 根据字符串长度确定要用何种形式转换
							if (tempValue.length() > 0 && tempValue.length() < 12) {
								tempDate = DateUtil.StringToDate(value.toString(),
										DateUtil.FORMATER_YYYY_MM_DD);
							} else if (tempValue.length() >= 13
									&& tempValue.length() < 21) {
								tempDate = DateUtil.StringToDate(value.toString(),
										DateUtil.FORMATER_YYYY_MM_DD_HH_MM_SS);
							}
							// 如果转换成功了，就赋值，如果不成功就让它空着吧。
							if (null != tempDate) {
								setterMethod.invoke(o, tempDate);// 为其赋值
							}
						}
					}else{
						Object oo = null;
						setterMethod.invoke(o, oo);// 为其赋值
					}

				}
				if (valueArray != null && isHaveSuchMethod(clazz, setterName)) {
					if (fieldClass == String[].class) {
						setterMethod.invoke(o, value == null ? null : valueArray);// 为其赋值
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		}
		return o;
	}
	/**
	 * 将请求的参数封装成java.util.Map<String, Object>对象
	 * 注意！	如果同一个参数名对应了多个值，只会取第1个
	 * 注意！	本方法所赋的所有值都是java.lang.String类型的
	 * @param request
	 * @return	本方法不会返回null
	 */
	public static Map<String, Object> requestToMap(HttpServletRequest request){
		
		Map<String, Object> map = new HashMap<String, Object>();
		if(null == request){
			return map;
		}
		
		Map<String, String[]> m = request.getParameterMap();
		String key;
		for(Iterator<String> iter = m.keySet().iterator(); iter.hasNext();){
			key = iter.next();
			map.put(key, m.get(key)[0]);
		}
		return map;
	}
	/**
	 * 自动将传过来的参数放到实体，本方法仅适用于修改页面
	 * 本方法会把接到的空字符串也set进实体
	 * @param request
	 * @param entity
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> T requestToObject(HttpServletRequest request, T entity){
		
		if(null == request || null == entity){
			return null;
		}
		Class<T> clazz = (Class<T>) entity.getClass();
		Field[] fields = clazz.getDeclaredFields();	//取到所有类下的属性，也就是变量名
		Field field;
		for(int i=0; i<fields.length; i++){
			field = fields[i];
			String fieldName = field.getName();
			//把属性的第一个字母处理成大写
			String stringLetter=fieldName.substring(0, 1).toUpperCase();    
			//取得set方法名，比如setBbzt
			String setterName="set"+stringLetter+fieldName.substring(1);    
			//真正取得get方法。
			Method setterMethod = null;
			Class<?> fieldClass = field.getType();
			try {
				Object value = request.getParameter(fieldName);
				Object valueArray = request.getParameterValues(fieldName);
				setterMethod = clazz.getMethod(setterName, fieldClass);
				if (value != null && isHaveSuchMethod(clazz, setterName)) {
					if(String.valueOf(value).trim().length() > 0){
						if (fieldClass == String.class) {
							setterMethod.invoke(entity, String.valueOf(value));// 为其赋值
						} else if (fieldClass == Integer.class || fieldClass == int.class) {
							setterMethod.invoke(entity, Integer.parseInt(String.valueOf(value)));// 为其赋值
						} else if (fieldClass == Boolean.class || fieldClass == boolean.class) {
							setterMethod.invoke(entity,
									Boolean.getBoolean(String.valueOf(value)));// 为其赋值
						} else if (fieldClass == Short.class || fieldClass == short.class) {
							setterMethod.invoke(entity,
									Short.parseShort(String.valueOf(value)));// 为其赋值
						} else if (fieldClass == Long.class || fieldClass == long.class) {
							setterMethod.invoke(entity,
									Long.parseLong(String.valueOf(value)));// 为其赋值
						} else if (fieldClass == Double.class || fieldClass == double.class) {
							setterMethod.invoke(entity,
									Double.parseDouble(String.valueOf(value)));// 为其赋值
						} else if (fieldClass == Float.class || fieldClass == float.class) {
							setterMethod.invoke(entity,
									Float.parseFloat(String.valueOf(value)));// 为其赋值
						} else if (fieldClass == BigInteger.class) {
							setterMethod.invoke(entity, BigInteger.valueOf(Long
									.parseLong(String.valueOf(value))));// 为其赋值
						} else if (fieldClass == BigDecimal.class) {
							setterMethod.invoke(entity, BigDecimal.valueOf(Double
									.parseDouble(String.valueOf(value))));// 为其赋值
						} else if (fieldClass == Date.class) {
							String tempValue = value.toString();
							Date tempDate = null;
							// 根据字符串长度确定要用何种形式转换
							if (tempValue.length() > 0 && tempValue.length() < 12) {
								tempDate = DateUtil.StringToDate(value.toString(),
										DateUtil.FORMATER_YYYY_MM_DD);
							} else if (tempValue.length() >= 13
									&& tempValue.length() < 21) {
								tempDate = DateUtil.StringToDate(value.toString(),
										DateUtil.FORMATER_YYYY_MM_DD_HH_MM_SS);
							}
							// 如果转换成功了，就赋值，如果不成功就让它空着吧。
							if (null != tempDate) {
								setterMethod.invoke(entity, tempDate);// 为其赋值
							}
						}
					}else{
						Object oo = null;
						setterMethod.invoke(entity, oo);// 为其赋值
					}
					
				}
				if (valueArray != null && isHaveSuchMethod(clazz, setterName)) {
					if (fieldClass == String[].class) {
						setterMethod.invoke(entity, value == null ? null : valueArray);// 为其赋值
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		}
		return entity;
	}


	/**
	 * 判断某个类里是否有某个方法
	 * @param clazz
	 * @param methodName
	 * @return
	 */
	public static boolean isHaveSuchMethod(Class<?> clazz, String methodName){
		Method[] methodArray = clazz.getMethods();
		boolean result = false;
		if(null != methodArray){
			for(int i=0; i<methodArray.length; i++){
				if(methodArray[i].getName().equals(methodName)){
					result = true;
					break;
				}
			}
		}
		return result;
	}
	
	
	
	/**
	 * 将来源对象的值 ，赋给目标对象</br>
	 * @param source	来源对象
	 * @param target	目标对象
	 * @param isCopyNull	如果source中的值为null时，是否将其赋给target对象
	 */
	public static void beanCopy(Object source, Object target, boolean isCopyNull) {

		if (null == source || null == target) {
			if (log.isWarnEnabled()) {
				log.warn("对象复制警告，不允许对象为null！");
			}
			return ;
		}


		Class<?> sourceClazz = source.getClass();
		Class<?> targetClazz = target.getClass();
		Field[] fields = targetClazz.getDeclaredFields(); // 取到所有类下的属性，也就是变量名
		Field field;

		for (int i = 0; i < fields.length; i++) {
			field = fields[i];
			String fieldName = field.getName();
			// 把属性的第一个字母处理成大写
			String stringLetter = fieldName.substring(0, 1).toUpperCase();
			// 取得setter方法名，比如setBbzt
			String setterName = "set" + stringLetter + fieldName.substring(1);
			// 取得getter方法名
			String getterName = "get" + stringLetter + fieldName.substring(1);
			// 真正取得set方法。
			Method setterMethod = null;
			// 真正取得get方法
			Method sourceGetterMethod = null;

			Class<?> fieldClass = field.getType();
			try {
				if (isHaveSuchMethod(targetClazz, setterName)) {
					setterMethod = targetClazz.getMethod(setterName, fieldClass);
					if (isHaveSuchMethod(sourceClazz, getterName)) {
						sourceGetterMethod = sourceClazz.getMethod(getterName);
						Object sourceValue = sourceGetterMethod.invoke(source);
						if (null != sourceValue) {
							setterMethod.invoke(target, sourceValue);// 为其赋值
						}else{
							if(isCopyNull){
								setterMethod.invoke(target, sourceValue);
							}
						}
					}
				}
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}

		}
		return ;
	}
	
	
	/**
	 * 根据移动端需求，所有对象如果为空时要换成""<br/>
	 * 所有数值类型为空时要换成0<br/>
	 * @param collection
	 */
	public static void treatNull(Collection collection){
		if(null == collection || collection.size() == 0){
			return ;
		}
		for(Iterator iter = collection.iterator(); iter.hasNext();){
			treatNull(iter.next());
		}
	}
	
	/**
	 * 根据移动端需求，所有对象如果为空时要换成""<br/>
	 * 所有数值类型为空时要换成0<br/>
	 * @param source
	 */
	public static void treatNull(Object source){
		
		if(null == source){
			return ;
		}
		Class clazz = source.getClass();
		Field[] fields = clazz.getDeclaredFields();	//取到所有类下的属性，也就是变量名
		Field field;
		for(int i=0; i<fields.length; i++){
			field = fields[i];
			String fieldName = field.getName();
			//把属性的第一个字母处理成大写
			String stringLetter=fieldName.substring(0, 1).toUpperCase();    
			//取得setter方法名，比如setBbzt
			String setterName="set"+stringLetter+fieldName.substring(1);    
			//取得getter方法名，比如getBbzt
			String getterName="get"+stringLetter+fieldName.substring(1);    
			//真正取得get方法。
			Method setterMethod = null;
			Method getterMethod = null;
			Class<?> fieldClass = field.getType();
			Object value = null;
			try {
				getterMethod = clazz.getMethod(getterName);
				if (isHaveSuchMethod(clazz, getterName) && isHaveSuchMethod(clazz, setterName)) {
					value = getterMethod.invoke(source);
					setterMethod = clazz.getMethod(setterName, fieldClass);
					if(value == null){
						if (fieldClass == String.class) {
							setterMethod.invoke(source, "");// 为其赋值
						} else if (fieldClass == Integer.class || fieldClass == int.class) {
							setterMethod.invoke(source, 0);// 为其赋值
						} else if (fieldClass == Boolean.class || fieldClass == boolean.class) {
							setterMethod.invoke(source, false);// 为其赋值
						} else if (fieldClass == Short.class || fieldClass == short.class) {
							setterMethod.invoke(source, 0);// 为其赋值
						} else if (fieldClass == Long.class || fieldClass == long.class) {
							setterMethod.invoke(source, 0l);// 为其赋值
						} else if (fieldClass == Double.class || fieldClass == double.class) {
							setterMethod.invoke(source, 0d);// 为其赋值
						} else if (fieldClass == Float.class || fieldClass == float.class) {
							setterMethod.invoke(source, 0f);// 为其赋值
						} else if (fieldClass == BigInteger.class) {
							setterMethod.invoke(source, 0);// 为其赋值
						} else if (fieldClass == BigDecimal.class) {
							setterMethod.invoke(source, 0);// 为其赋值
						} else if (fieldClass == Date.class) {
							String tempValue = value.toString();
							Date tempDate = null;
							// 根据字符串长度确定要用何种形式转换
							if (tempValue.length() > 0 && tempValue.length() < 12) {
								tempDate = DateUtil.StringToDate(value.toString(),
										DateUtil.FORMATER_YYYY_MM_DD);
							} else if (tempValue.length() >= 13
									&& tempValue.length() < 21) {
								tempDate = DateUtil.StringToDate(value.toString(),
										DateUtil.FORMATER_YYYY_MM_DD_HH_MM_SS);
							}
							// 如果转换成功了，就赋值，如果不成功就让它空着吧。
							if (null != tempDate) {
								setterMethod.invoke(source, tempDate);// 为其赋值
							}
						}
					}
					
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		}
		return;
	}
	/**
	 * 按照给定的分隔标志，将列表封闭成字符串
	 * @param list
	 * @param reg
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public String join(List list, String reg){
		StringBuffer sb = new StringBuffer();
		if(null == list || list.size() == 0){
			return null;
		}
		for(Iterator iter = list.iterator(); iter.hasNext();){
			sb.append(iter.next()).append(reg);
		}
		int length = sb.length();
		if(length > 0){
			sb = sb.delete(length -1, length);
		}
		return sb.toString();
	}
	
	/**
	 * 将形式为unicode的字符串，转换成能显示的字符串
	 * @param unicodeString
	 * @return
	 */
	public static String unicodeEsc2Unicode(String unicodeString) {
		if (unicodeString == null) {
			return null;
		}

		StringBuffer retBuf = new StringBuffer();
		int maxLength = unicodeString.length();
		for (int i = 0; i < maxLength; i++) {
			if (unicodeString.charAt(i) == '\\') {
				if ((i < maxLength - 5) && ((unicodeString.charAt(i + 1) == 'u') || (unicodeString.charAt(i + 1) == 'U'))) {
					try {
						retBuf.append((char) Integer.parseInt(unicodeString.substring(i + 2, i + 6), 16));
						i += 5;
					} catch (NumberFormatException localNumberFormatException) {
						retBuf.append(unicodeString.charAt(i));
						localNumberFormatException.printStackTrace();
					}
				} else {
					retBuf.append(unicodeString.charAt(i));
				}
			} else {
				retBuf.append(unicodeString.charAt(i));
			}
		}

		return retBuf.toString();
	}
	
	/**
	 * 如果为空返回0,否则返回int值
	 * @param param
	 * @return
	 */
	public static int stringToInt(String param) {
		if(StringUtils.isNotEmpty(param)) {
			return Integer.parseInt(param);
		} else {
			return 0;
		}
	}
	
	/**
	 * 如果为空返回defaultValue得值,否则返回int值
	 * @param param
	 * @return
	 */
	public static int stringToInt(String param, int defaultValue) {
		if(StringUtils.isNotEmpty(param)) {
			return Integer.parseInt(param);
		} else {
			return defaultValue;
		}
	}

	public static String urlEncode(String originImageUrl) {
		String newOriginImageUrl = "";
		if (StringUtil.isNotEmpty(originImageUrl)) {
			int startIndex = originImageUrl.lastIndexOf('/')+1;
			int endIndex = originImageUrl.lastIndexOf('.');
			if (startIndex < endIndex) {
				String resourceName = originImageUrl.substring(startIndex, endIndex);
				try {
					if (!isUtf8Url(resourceName)) {
						String newResourceName = URLEncoder.encode(resourceName, "UTF-8");
						newOriginImageUrl = originImageUrl.replace(resourceName, newResourceName);
					}
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
			}
		}
		return newOriginImageUrl;
	}

	/**
	 * 编码是否有效
	 * @param text
	 * @return
	 */
	public static boolean Utf8codeCheck(String text){
		String sign = "";
		if (text.startsWith("%e"))
			for (int i = 0, p = 0; p != -1; i++) {
				p = text.indexOf("%", p);
				if (p != -1)
					p++;
				sign += p;
			}
		return sign.equals("147-1");
	}
	/**
	 * 判断是否Utf8Url编码
	 * @param text
	 * @return
	 */
	public static boolean isUtf8Url(String text) {
		text = text.toLowerCase();
		int p = text.indexOf("%");
		if (p != -1 && text.length() - p > 9) {
			text = text.substring(p, p + 9);
		}
		return Utf8codeCheck(text);
	}
}
