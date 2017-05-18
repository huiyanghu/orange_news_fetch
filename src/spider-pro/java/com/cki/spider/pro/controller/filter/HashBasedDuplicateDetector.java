   
  
  
  
  
  
  

package com.cki.spider.pro.controller.filter;

import java.util.concurrent.ConcurrentHashMap;

import com.cki.spider.pro.util.MD5;

   
  
  
 
  
public class HashBasedDuplicateDetector implements SimilarUrlDetector {

    private ConcurrentHashMap<String, Object> map;

    private Object stub;

    public HashBasedDuplicateDetector() {

        this.map = new ConcurrentHashMap<String, Object>();

        this.stub = new Object();
    }

    @Override
    public boolean feed(String url) {

        Object obj = this.map.putIfAbsent(MD5.get(url), stub);

        if (obj == null) {
            return true;
        }

        return false;
    }

}
