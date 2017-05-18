   
  
  
  
  
  
  

package com.cki.spider.pro.util;

   
  
  
 
  
public class UrlUtil {

                                                                           
                                                   
    public static String getPathAndQueryString(String url) {

        int fromIndex = 0;

        if (url.toLowerCase().startsWith("http://")) {

            fromIndex = 7;

        } else if (url.toLowerCase().startsWith("https://")) {

            fromIndex = 8;

        }

        int index = url.indexOf('/', fromIndex);

        if (index == -1) {
            return "/";
        }

        int index2 = url.indexOf('#', fromIndex);

        if (index2 == -1) {
            index2 = url.length();
        }

        return url.substring(index, index2);

    }

}
