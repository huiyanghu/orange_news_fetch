   
  
  
  
  
  
  

package com.cki.spider.pro;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.handler.codec.http.HttpHeaders;
import org.jboss.netty.handler.codec.http.HttpResponse;

   
  
  
 
  
public class ResponseBodyCollector {

    private ChannelBuffer buffer;
    private volatile boolean finished;

    public boolean willProcessResponse(HttpResponse response) {

                                                              
        if ((response.getContent() != null) && (response.getContent().readableBytes() > 0)) {
            this.buffer = response.getContent();
            this.finished = true;
            return true;
        }

                                       
        long length = HttpHeaders.getContentLength(response, -1);

        if (length > Integer.MAX_VALUE) {
            this.finished = true;
            return false;
        }

        if (length == 0) {

                         
        	this.buffer=ChannelBuffers.EMPTY_BUFFER;
            this.finished = true;
            return true;
        }

                                                                                  
        if (response.isChunked()) {
            if (length == -1) {

                                                                                                                       
                this.buffer = ChannelBuffers.dynamicBuffer(2048);
            } else {

                                                                                                                    
                                                                                                         
                                                 
                this.buffer = ChannelBuffers.dynamicBuffer((int) length);
            }

            return true;
        }

        this.finished = true;
        return false;
    }

    public void addData(ChannelBuffer content) throws Exception {

        if (!this.finished) {
            this.buffer.writeBytes(content);
        }
    }

    public void addLastData(ChannelBuffer content) throws Exception {

        if (!this.finished) {
            this.buffer.writeBytes(content);

            this.finished = true;
        }

    }

    public ChannelBuffer getBody() {
        return this.buffer;
    }
}
