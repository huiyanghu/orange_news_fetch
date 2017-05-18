   
  
  
  
  
  
  

package com.cki.spider.pro.util;

import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cki.spider.pro.HttpHeaders;
import com.cki.spider.pro.MimeType;

   
  
  
 
  
public class HttpHeaderUtil {

    private static final Logger logger = LoggerFactory.getLogger(HttpHeaderUtil.class);

    public static String getCharset(HttpHeaders headers) {

        String contentType = headers.getHeader(HttpHeaders.Names.CONTENT_TYPE);

        if (contentType == null) {
            return null;
        }

        String tag = "charset=";

        int index = contentType.toLowerCase().lastIndexOf(tag);

        if (index == -1) {
            return null;
        }

        return contentType.substring(index + tag.length());

    }

    public static Charset getAndVerifyCharset(HttpHeaders headers) {

        String charset = getCharset(headers);

        if (charset == null) {
            return null;
        } else if (charset.toLowerCase().equals("gb2312")) {
            charset = "GB18030";
        }

        try {
            return Charset.forName(charset);
        } catch (UnsupportedCharsetException e) {
            logger.error("UnsupportedCharset:{}", charset, e);
            return null;
        } catch (Exception e) {
            logger.error("error", e);
            return null;
        }

    }

    public static String getMimeType(HttpHeaders headers) {

        String contentType = headers.getHeader(HttpHeaders.Names.CONTENT_TYPE);

        if (contentType == null) {
            return null;
        }

        String tag1 = "Content-Type: ";

        int index1 = contentType.indexOf(tag1);

        if (index1 == -1) {
            index1 = 0;
        } else {
            index1 = tag1.length();
        }

        String tag = "; charset=";

        int index = contentType.indexOf(tag);

        if (index == -1) {
            index = contentType.length();
        }

        return contentType.substring(index1, index);

    }

    public static boolean acceptMime(MimeType mime, MimeType filter) {

                                    
        if (filter.getMajor() == null) {
            return true;
        }

        if (!filter.getMajor().equals(mime.getMajor())) {
            return false;
        }

        if (filter.getMinor() == null) {
            return true;
        }

        if (filter.getMinor().equals(mime.getMinor())) {
            return true;
        }

        return false;

    }

}
