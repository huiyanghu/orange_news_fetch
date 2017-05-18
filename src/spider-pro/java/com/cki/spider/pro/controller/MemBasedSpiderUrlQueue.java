   
  
  
  
  
  
  

package com.cki.spider.pro.controller;

import java.util.LinkedList;
import java.util.concurrent.LinkedBlockingQueue;

   
  
  
  
  
  
 
  
public class MemBasedSpiderUrlQueue implements SpiderUrlQueue {

    private LinkedList<InternalSpiderUrl> seedList;

    private LinkedBlockingQueue<InternalSpiderUrl> urls;

    public MemBasedSpiderUrlQueue() {

        this.seedList = new LinkedList<InternalSpiderUrl>();
        this.urls = new LinkedBlockingQueue<InternalSpiderUrl>();
    }

    @Override
    public void put(InternalSpiderUrl fetchUrl) {

        try {
            urls.put(fetchUrl);
        } catch (InterruptedException e) {
            return;
        }
    }

    @Override
    public void putSeed(InternalSpiderUrl seed) {

        if (urls.size() == 0) {
            urls.add(seed);
        } else {
            seedList.addLast(seed);
        }
    }

    @Override
    public InternalSpiderUrl take() throws InterruptedException {

        synchronized (seedList) {

            InternalSpiderUrl seed = seedList.pollFirst();

            if (seed != null) {
                return seed;
            }
        }

        return urls.take();
    }
}
