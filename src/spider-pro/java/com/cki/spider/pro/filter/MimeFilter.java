   
  
  
  
  
  
  

package com.cki.spider.pro.filter;

import com.cki.spider.pro.SpiderUrl;
import com.cki.spider.pro.SpiderData;
import com.cki.spider.pro.MimeType;
import com.cki.spider.pro.util.HttpHeaderUtil;

   
  
  
 
  
public class MimeFilter implements SpiderFilter {

                                                                                                       
    private MimeType[] acceptMimeTypes;

                                                                                                        

    public MimeFilter() {
    }

    public MimeFilter(MimeType... acceptMimeTypes) {
        this.acceptMimeTypes = acceptMimeTypes;
    }

    @Override
    public boolean accept(SpiderUrl url, SpiderData spiderData) {

        if (this.acceptMimeTypes == null || this.acceptMimeTypes.length == 0) {
            return true;
        }

        String mime = HttpHeaderUtil.getMimeType(spiderData.getResponseHeaders());

        if (mime == null) {
            return false;
        }

        MimeType mimeType = new MimeType(mime);

        for (MimeType filter : acceptMimeTypes) {

            if (HttpHeaderUtil.acceptMime(mimeType, filter)) {
                return true;
            }

        }

        return false;
    }

                                                                                                       
    public MimeType[] getAcceptMimeTypes() {
        return acceptMimeTypes;
    }

    public void setAcceptMimeTypes(MimeType[] acceptMimeTypes) {
        this.acceptMimeTypes = acceptMimeTypes;
    }

}
