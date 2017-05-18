   
  
  
  
  
  
  

package com.cki.spider.pro;


   
  
 
  
public interface SpiderUrlListener {

       
  
 
  
  
  
	void preFetch(SpiderUrl url);

       
  
  
  
  
    void postFetch(SpiderUrl url, SpiderData data);

       
  
  
  
  
    void refusedByFilter(SpiderUrl fetchUrl, SpiderData data);
}
