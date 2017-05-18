   
  
  
  
  
  
  

package com.cki.spider.pro.filter;

import com.cki.spider.pro.MimeType;

   
  
  
 
  
public class SpiderFilters {

    public static final MimeFilter ACCEPT_ALL_FILTER = new MimeFilter();

    public static final MimeFilter HTML_FILTER = new MimeFilter(new MimeType("text/html"), new MimeType("text/xhtml"),
                                                     new MimeType("text/plain"), new MimeType("text/plain"));

}
