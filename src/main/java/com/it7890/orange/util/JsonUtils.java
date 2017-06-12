package com.it7890.orange.util;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JsonUtils {

	public final static Logger log = LoggerFactory.getLogger(JsonUtils.class); // 日志

	public static void mapToXml() {
	}

	/** 获取根元素 */
	public static Element getRootElement(String srcXml) throws DocumentException {
		Document srcdoc = DocumentHelper.parseText(srcXml);
		Element elem = srcdoc.getRootElement();
		return elem;
	}

	public static HashMap xmlToMap(Element element) {
		List elementlists = element.elements();
		String keyName = element.getName();
		HashMap retHas = new HashMap();
		if (elementlists.size() == 0) {
			// 没有子元素
			retHas.put(element.getQualifiedName(), element.getTextTrim());
			return retHas;
		} else {
			HashMap hs = new HashMap();
			Vector vector = new Vector();
			for (Iterator it = elementlists.iterator(); it.hasNext();) {
				Element elem = (Element) it.next();
				String key = elem.getQualifiedName();
				HashMap tempMap = xmlToMap(elem);
				if (hs.containsKey(key)) {
					HashMap hsclone = (HashMap) hs.clone();
					if (hsclone.get(key) instanceof HashMap) {
						vector.add(hsclone.get(key));
						vector.add(tempMap.get(key));
					} else if (hsclone.get(key) instanceof Vector) {
						vector = (Vector) hsclone.get(key);
						vector.add(tempMap.get(key));
					} else {
						vector.add(hsclone.get(key));
						vector.add(tempMap.get(key));
					}
					Vector vectorclone = (Vector) vector.clone();
					vector.clear();
					hs.put(key, vectorclone);

				} else {
					hs.put(elem.getQualifiedName(), tempMap.get(key));
				}

			}
			retHas.put(keyName, hs);
			return retHas;
		}
	}

	public static <T> T jsonToEntity(Class<T> entityClass, String json) throws Exception {
		ObjectMapper objectMapper = new ObjectMapper();
		return (T) objectMapper.readValue(json, entityClass);
	}

	/** 返回List<Map>对象 */
	public static List jsonToList(String json) throws Exception {
		ObjectMapper objectMapper = new ObjectMapper();
		List<LinkedHashMap<String, Object>> list = objectMapper.readValue(json, List.class);

		// for (int i = 0; i < list.size(); i++)
		// {
		// Map<String, Object> map = list.get(i);
		// Set<String> set = map.keySet();
		// for (Iterator<String> it = set.iterator();it.hasNext();)
		// {
		// String key = it.next(); System.out.println(key + ":" + map.get(key));
		// }
		// }
		return list;
	}

//	/** 返回对象数组 */
//	public static Object[] jsonToArray(Class entityClass, String json) throws Exception {
//		ObjectMapper objectMapper = new ObjectMapper();
//		Object[] arr = objectMapper.readValue(json, entityClass);
//		// for (int i = 0; i < arr.length; i++)
//		// {
//		// TestVO vo = (TestVO) arr[i];
//		// System.out.println(vo.getImg());
//		// // System.out.println(arr[i].getClass());
//		// }
//		return arr;
//	}

	/** 返回Map对象 */
	public static Map jsonToMap(String json) {
		try {
			// System.out.println("访问服务器json：" + json);
			ObjectMapper objectMapper = new ObjectMapper();
			Map<String, Map<String, Object>> maps = objectMapper.readValue(json, Map.class);
			// Set<String> key = maps.keySet();
			// Iterator<String> iter = key.iterator();
			// while (iter.hasNext())
			// {
			// String field = iter.next();
			// System.out.println(field + ":" + maps.get(field));
			// }
			return maps;
		} catch (Exception e) {
			// TODO: handle exception
			return null;
		}

	}
	
	public static Map jsonToMapList(String json) {
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			Map<String, ArrayList<String>> maps = objectMapper.readValue(json, Map.class);
			return maps;
		} catch (Exception e) {
			return null;
		}
	}

	public static String stringToJson(String s) {
		if (s == null) {
			return nullToJson();
		}
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < s.length(); i++) {
			char ch = s.charAt(i);
			switch (ch) {
			case '\"':
				// case '"':
				sb.append("\\\"");
				break;
			case '\\':
				sb.append("\\\\");
				break;
			case '\b':
				sb.append("\\b");
				break;
			case '\f':
				sb.append("\\f");
				break;
			case '\n':
				sb.append("\\n");
				break;
			case '\r':
				sb.append("\\r");
				break;
			case '\t':
				sb.append("\\t");
				break;
			case '/':
				sb.append("/");
				// sb.append("\\/");
				break;
			default:
				if (ch >= '\u0000' && ch <= '\u001F') {
					String ss = Integer.toHexString(ch);
					sb.append("\\u");
					for (int k = 0; k < 4 - ss.length(); k++) {
						sb.append('0');
					}
					sb.append(ss.toUpperCase());
				} else {
					sb.append(ch);
				}
			}
		}
		return sb.toString();
	}

	public static String nullToJson() {
		return "";
	}

	public static String objectToJson(Object obj) {
		// System.out.println(obj.toString()+"=======rrrrrrrrrrrr");
		StringBuilder json = new StringBuilder();
		if (obj == null) {
			json.append("\"\"");

		} else if (obj instanceof Number) {
			json.append(numberToJson((Number) obj));
		} else if (obj instanceof Boolean) {
			json.append(booleanToJson((Boolean) obj));
		} else if (obj instanceof String) {
			json.append("\"").append(stringToJson(obj.toString())).append("\"");
		} else if (obj instanceof Object[]) {
			json.append(arrayToJson((Object[]) obj));
		} else if (obj instanceof List) {
			json.append(listToJson((List<?>) obj));
		} else if (obj instanceof Map) {
			json.append(mapToJson((Map<?, ?>) obj));
		} else if (obj instanceof Set) {
			json.append(setToJson((Set<?>) obj));
		} else {
			json.append(beanToJson(obj));
		}
		return json.toString();
	}

	public static String numberToJson(Number number) {
		return "\"" + number.toString() + "\"";
	}

	public static String booleanToJson(Boolean bool) {
		return "\"" + bool.toString() + "\"";
	}

	/** */
	/**
	 * @param bean
	 *            bean对象
	 * @return String
	 */
	public static String beanToJson(Object bean) {
		StringBuilder json = new StringBuilder();
		json.append("{");
		PropertyDescriptor[] props = null;
		try {
			props = Introspector.getBeanInfo(bean.getClass(), Object.class).getPropertyDescriptors();
		} catch (IntrospectionException e) {
			e.printStackTrace();
		}
		if (props != null) {
			for (int i = 0; i < props.length; i++) {
				try {
					String name = objectToJson(props[i].getName());
					String value = objectToJson(props[i].getReadMethod().invoke(bean));
					/**
					 * 如果空值也要显示出来 就用这个 去掉空值判断 json.append(name);
					 * json.append(":"); json.append(value); json.append(",");
					 */
//					if (props[i].getReadMethod().invoke(bean) != null) {
						json.append(name);
						json.append(":");
						json.append(value);
						json.append(",");
//					}

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			json.setCharAt(json.length() - 1, '}');
		} else {
			json.append("}");
		}
		return json.toString();
	}

	/** */
	/**
	 * @param list
	 *            list对象
	 * @return String
	 */
	public static String listToJson(List<?> list) {
		StringBuilder json = new StringBuilder();
		json.append("[");
		if (list != null && list.size() > 0) {
			for (Object obj : list) {

				json.append(objectToJson(obj));
				json.append(",");
			}
			json.setCharAt(json.length() - 1, ']');
		} else {
			json.append("]");
		}
		return json.toString();
	}

	/** */
	/**
	 * @param array
	 *            对象数组
	 * @return String
	 */
	public static String arrayToJson(Object[] array) {
		StringBuilder json = new StringBuilder();
		json.append("[");
		if (array != null && array.length > 0) {
			for (Object obj : array) {
				json.append(objectToJson(obj));
				json.append(",");
			}
			json.setCharAt(json.length() - 1, ']');
		} else {
			json.append("]");
		}
		return json.toString();
	}

	/** */
	/**
	 * @param map
	 *            map对象
	 * @return String
	 */
	public static String mapToJson(Map map) {
		StringBuilder json = new StringBuilder();
		json.append("{");
		if (map != null && map.size() > 0) {
			for (Object key : map.keySet()) {
				/**
				 * 如果空值也要显示出来 就用这个 去掉空值判断 json.append(name); json.append(":");
				 * 
				 */

				// if (map.get(key) != null && !"".equals(map.get(key)))
				// {
				json.append(objectToJson(key));
				json.append(":");
				json.append(objectToJson(map.get(key)));
				json.append(",");
				// }

			}
			json.setCharAt(json.length() - 1, '}');
		} else {
			json.append("}");
		}
		return json.toString();
	}

	/** 集合对象 */
	public static String setToJson(Set<?> set) {
		StringBuilder json = new StringBuilder();
		json.append("[");
		if (set != null && set.size() > 0) {
			for (Object obj : set) {
				json.append(objectToJson(obj));
				json.append(",");
			}
			json.setCharAt(json.length() - 1, ']');
		} else {
			json.append("]");
		}
		return json.toString();
	}

	/** 将map转换为实体, 返回值为object需要做强转 */
	public static Object maptoEntity(Map map, Object vo) {
		Method[] methods = vo.getClass().getDeclaredMethods();
		for (int i = 0; i < methods.length; i++) {
			String name = methods[i].getName();
			if (!name.startsWith("set")) // 过滤掉不是以"set"开头的函数
				continue;
			name = name.substring(3); // 去掉函数名（例如：setIP(...)）前的前三个字符“set”，结果为"ip"
			name = name.toLowerCase();
			if (map.get(name) == null)
				continue;
			Object v = null;
			if (methods[i].getParameterTypes()[0] == String.class)
				v = (String) map.get(name);
			else if (methods[i].getParameterTypes()[0] == int.class)
				v = new Integer((String) map.get(name));
			else if (methods[i].getParameterTypes()[0] == Integer.class)
				v = Integer.parseInt(map.get(name).toString());
			else if (methods[i].getParameterTypes()[0] == Boolean.class)
				v = new Boolean("1".equals((String) map.get(name)));
			else if (methods[i].getParameterTypes()[0] == Double.class)
				v = new Double((String) map.get(name));
			try {
				if (map.get(name) != null)
					methods[i].invoke(vo, new Object[] { v });
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return vo;
	}

}
