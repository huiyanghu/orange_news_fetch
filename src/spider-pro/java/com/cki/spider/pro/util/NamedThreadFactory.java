  
  
 
  
  
  
 
      
 
  
  
  
  
  
  

package com.cki.spider.pro.util;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

   
          
   
 
      
  
public class NamedThreadFactory implements ThreadFactory {

                                                                                  

    private static final AtomicInteger POOL_NUMBER = new AtomicInteger(1);

                                                                                  

    private final ThreadGroup group;
    private final AtomicInteger threadNumber = new AtomicInteger(1);
    private final String namePrefix;

                                                                                  

    public NamedThreadFactory() {
        this("ThreadPool(" + POOL_NUMBER.getAndIncrement() + "-thread-");
    }

    public NamedThreadFactory(String namePrefix) {
        SecurityManager s = System.getSecurityManager();
        this.group = (s != null) ? s.getThreadGroup() :
                     Thread.currentThread().getThreadGroup();
        this.namePrefix = namePrefix + "(pool" +
                          POOL_NUMBER.getAndIncrement() + "-thread-";
    }

                                                                                  

    public Thread newThread(Runnable r) {
        Thread t = new Thread(this.group, r, this.namePrefix +
                              this.threadNumber.getAndIncrement() + ")", 0L);
        if (t.isDaemon()) {
            t.setDaemon(false);
        }
        if (t.getPriority() != Thread.NORM_PRIORITY) {
            t.setPriority(Thread.NORM_PRIORITY);
        }
        return t;
    }
}