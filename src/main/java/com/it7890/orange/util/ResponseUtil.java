package com.it7890.orange.util;

import org.eclipse.jetty.server.Request;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * <pre>
 * ResponseUtil.java
 * @author kanpiaoxue<br>
 * @version 1.0
 * Create Time 2014年9月2日 下午4:57:06<br>
 * Description : 工具类
 * </pre>
 */
public class ResponseUtil {

    private ResponseUtil() {
        super();
    }

    /**
     * 相应结果
     * @param request
     * @param response
     * @param result
     * @throws IOException
     */
    public static void print(Request request, HttpServletResponse response, String result) throws IOException {
        response.setContentType("text/json;charset=utf-8");
        response.setStatus(HttpServletResponse.SC_OK);
        request.setHandled(true);
        response.getWriter().println(result);
    }


    /**
     * 直接输出 json 字符串
     * @param jsonStr
     * @param response
     */
    public static void printToJson(String jsonStr, HttpServletResponse response) {
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/json");
        response.setDateHeader("Expires", 0);

        PrintWriter out = null;
        try {
            out = response.getWriter();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != out) {
                out.print(jsonStr);
                out.flush();
                out.close();
            }
        }
    }

}
