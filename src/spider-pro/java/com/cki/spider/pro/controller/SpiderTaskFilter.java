   
  
  
  
  
  
  

package com.cki.spider.pro.controller;

import com.cki.spider.pro.SpiderUrl;
import com.cki.spider.pro.SpiderData;

   
  
  
 
  
public interface SpiderTaskFilter {

       
  
  
  
  
  
    boolean accept(SpiderTask fetchTask, SpiderUrl url, SpiderData spiderData);

}
