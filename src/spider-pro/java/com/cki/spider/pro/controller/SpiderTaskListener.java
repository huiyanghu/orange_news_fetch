   
  
  
  
  

package com.cki.spider.pro.controller;

import com.cki.spider.pro.SpiderUrl;
import com.cki.spider.pro.SpiderData;

   
  
 
  
  
public interface SpiderTaskListener {

       
  
 
  
  
  
    void preFetch(SpiderTask task, SpiderUrl url);

       
  
  
  
  
  
  
  
    void postFetch(SpiderTask task, SpiderUrl url, SpiderData data);

       
  
  
  
  
    void refusedByFilter(SpiderTask task, SpiderUrl fetchUrl, SpiderData data);

       
  
  
  
  
  
    void onException(SpiderTask task, SpiderUrl fetchUrl, SpiderData data);

       
  
 
  
  
    public void onTaskComplete(SpiderTask task);
}
