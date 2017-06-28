package com.it7890.orange.util;

import java.util.ArrayList;
import java.util.List;

public class UserAgentUtil {
   private static final String UserAgent = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.36";

   private static final String UserAgent1= "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/49.0.2623.75 Safari/537.36";
   private static final String UserAgent8= "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/49.0.2623.75 Safari/537.36";
   private static final String UserAgent2= "Mozilla/5.0 (Windows NT 10.0; WOW64; rv:51.0) Gecko/20100101 Firefox/51.0";
   private static final String UserAgent7= "Mozilla/5.0 (Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/55.0.2883.87 Safari/537.36";
   private static final String UserAgent4= "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:51.0) Gecko/20100101 Firefox/51.0";
   private static final String UserAgent5= "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.87 Safari/537.36";
   private static final String UserAgent6= "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/53.0.2785.143 Safari/537.36";

   public static String getUserAgent(){
	   List<String> list=new ArrayList<String>();
	   list.add(UserAgent1);
	   list.add(UserAgent2);
	   list.add(UserAgent4);
	   list.add(UserAgent5);
	   list.add(UserAgent6);
	   list.add(UserAgent7);
	   list.add(UserAgent8);
//	   nt [] arr = {1,2,3,4};
	 //产生0-(arr.length-1)的整数值,也是数组的索引
	 int index=(int)(Math.random()*list.size());
	 String userAgent = list.get(index);
	   return userAgent;
   }
}
