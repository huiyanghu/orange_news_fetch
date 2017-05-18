   
  
  
  
  
  
  

package com.cki.spider.pro.controller;

   
  
 
  
  
  
  
  
 
 
  
 
  
public interface SpiderUrlQueue {

    void put(InternalSpiderUrl fetchUrl);

       
  
  
  
    void putSeed(InternalSpiderUrl seed);

       
  
  
  
  
    InternalSpiderUrl take() throws InterruptedException;
}
