package com.it7890.orange.util;

import com.it7890.orange.fetch.BaseAnalysis;
import com.it7890.orange.fetch.DefaultAnalysis;

/**
 * 反射获取分析类
 *
 */
public class GetAnalysisClass {
	
	public static BaseAnalysis get(String className) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
		if (className != null && className.length() != 0) {
			return (BaseAnalysis) Class.forName(className).newInstance();
		}
		return (new DefaultAnalysis());
	}
}
