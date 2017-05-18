    
  
  
  
  
  
  
package org.springside.modules.orm.hibernate;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;

    
   
  
  
  
  
  
public class OpenSessionInViewFilter extends org.springframework.orm.hibernate3.support.OpenSessionInViewFilter {

	public static final String EXCLUDE_SUFFIXS_NAME = "excludeSuffixs";

	public static final String INCLUDE_SUFFIXS_NAME = "includeSuffixs";

	private static final String[] DEFAULT_EXCLUDE_SUFFIXS = { ".js", ".css", ".jpg", ".gif" };

	private static final String[] DEFAULT_INCLUDE_SUFFIXS = { ".action", ".htm" };

	private String[] excludeSuffixs = DEFAULT_EXCLUDE_SUFFIXS;

	private String[] includeSuffixs = DEFAULT_INCLUDE_SUFFIXS;

	    
  
  
	@Override
	protected boolean shouldNotFilter(final HttpServletRequest request) throws ServletException {
		String fullPath = request.getServletPath();
		String path = StringUtils.substringBefore(fullPath, "?");

		for (String suffix : includeSuffixs) {
			if (path.endsWith(suffix))
				return false;
		}

		for (String suffix : excludeSuffixs) {
			if (path.endsWith(suffix))
				return true;
		}

		return false;
	}

	    
  
  
	@Override
	protected void initFilterBean() throws ServletException {

		String includeSuffixStr = getFilterConfig().getInitParameter(INCLUDE_SUFFIXS_NAME);

		if (StringUtils.isNotBlank(includeSuffixStr)) {
			includeSuffixs = includeSuffixStr.split(",");
			                          
			for (int i = 0; i < includeSuffixs.length; i++) {
				includeSuffixs[i] = "." + includeSuffixs[i];
			}
		}

		String excludeSuffixStr = getFilterConfig().getInitParameter(EXCLUDE_SUFFIXS_NAME);

		if (StringUtils.isNotBlank(excludeSuffixStr)) {
			excludeSuffixs = excludeSuffixStr.split(",");
			for (int i = 0; i < excludeSuffixs.length; i++) {
				excludeSuffixs[i] = "." + excludeSuffixs[i];
			}
		}
	}
}
