   
  
  
  
  
  
  

package com.cki.spider.pro.controller;

import com.cki.spider.pro.SpiderUrl;

   
  
  
 
  
public class InternalSpiderUrl extends SpiderUrl {

    private static final long serialVersionUID = 861409033631355150L;

                                                                                                    
    private transient SpiderTaskContext taskContext;

    private int depth;

                                                                                                    
    public InternalSpiderUrl(SpiderUrl fetchUrl, SpiderTaskContext taskContext) {

        this(fetchUrl, taskContext, 0);
    }

    public InternalSpiderUrl(SpiderUrl fetchUrl, SpiderTaskContext taskContext, int depth) {

        super(fetchUrl);

        this.taskContext = taskContext;
        this.depth = depth;
    }

                                                                                                    
    public SpiderTaskContext getTaskContext() {
        return taskContext;
    }

    public int getDepth() {
        return depth;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

}
