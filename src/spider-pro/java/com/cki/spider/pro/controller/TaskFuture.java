   
  
  
  
  
  
  

package com.cki.spider.pro.controller;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

   
  
  
 
  
public class TaskFuture<V> implements Future<V> {

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {

                                          
        return false;
    }

    @Override
    public V get() throws InterruptedException, ExecutionException {

                                          
        return null;
    }

    @Override
    public V get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {

                                          
        return null;
    }

    @Override
    public boolean isCancelled() {

                                          
        return false;
    }

    @Override
    public boolean isDone() {

                                          
        return false;
    }

}
