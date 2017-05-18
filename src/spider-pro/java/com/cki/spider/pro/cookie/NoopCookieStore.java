   
  
  
  
  
  
  

package com.cki.spider.pro.cookie;

import java.util.Collections;
import java.util.List;

   
  
  
 
  
public class NoopCookieStore implements CookieStore {

    @Override
    public void add(Cookie cookie) {

    }

    @Override
    public void close() {

    }

    @Override
    public List<Cookie> find(String url) {

        return Collections.emptyList();
    }

    @Override
    public void flush() {

    }

    @Override
    public void flushExpires() {

    }

    @Override
    public void init() {

    }

}
