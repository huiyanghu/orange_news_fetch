   
  
  
  
  

package com.cki.spider.pro.component;

import java.util.Date;
import java.util.Map;

   
  
  
public interface Cache {

    public boolean set(String key, Object value);

    public boolean set(String key, Object value, Date expiry);

    public boolean delete(String key);

    public Object get(String key);

    public Object[] getMultiArray(String[] keys);

    @SuppressWarnings("unchecked")
    public Map stats();

    public void close();

    public boolean isClosed();
}
