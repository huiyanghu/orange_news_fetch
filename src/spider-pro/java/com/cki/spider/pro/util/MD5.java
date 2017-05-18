   
  
  
  
  
  
  

package com.cki.spider.pro.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

   
  
  
 
  
public class MD5 {

       
  
 
  
  
  
    public static String get(InputStream in) {

        MessageDigest messageDigest = null;

        try {
            messageDigest = MessageDigest.getInstance("MD5");

            byte[] buffer = new byte[1024];
            int i = -1;

            while ((i = in.read(buffer)) != -1) {
                messageDigest.update(buffer, 0, i);
            }
        } catch (NoSuchAlgorithmException e) {
            return null;
        } catch (IOException e) {
            return null;
        }

        byte b[] = messageDigest.digest();

        return hashToString(b);

    }

    public static String get(String str) {
        return get(str.getBytes());
    }

    public static String get(byte[] b) {
        return get(new ByteArrayInputStream(b));
    }

    public static String get(byte[] b, int offset, int len) {
        return get(new ByteArrayInputStream(b, offset, len));
    }

       
  
 
  
  
  
    public static String hashToString(byte[] b) {

        int i;
        StringBuffer buf = new StringBuffer("");

        for (int offset = 0; offset < b.length; offset++) {
            i = b[offset];

            if (i < 0) {
                i += 256;
            }

            if (i < 16) {
                buf.append("0");
            }

            buf.append(Integer.toHexString(i));
        }

        return buf.toString();

    }

}
