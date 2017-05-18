   
  
  
  
  
  
  

package com.cki.spider.pro.host.event;

import com.cki.spier.pro.host.HostContext;

   
  
  
 
  
public class HostContextEvent implements ContextEvent {

    private HostContext hostContext;

    public HostContextEvent(HostContext context) {
        this.hostContext = context;
    }

    public HostContext getHostContext() {
        return hostContext;
    }

    public void setHostContext(HostContext hostContext) {
        this.hostContext = hostContext;
    }

}
