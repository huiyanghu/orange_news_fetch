   
  
  
  
  
  
  

package com.cki.spider.pro.filter;

import com.cki.spider.pro.SpiderUrl;
import com.cki.spider.pro.SpiderData;


   
  
  
 
  
public interface SpiderFilter {

       
  
  
  
  
  
    boolean accept(SpiderUrl url, SpiderData spiderData);

}
