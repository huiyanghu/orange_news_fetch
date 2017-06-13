/**
 * <pre>
 * Copyright baidu.com CDC [2000-2014]
 * </pre>
 */
package com.it7890.orange.servlet;

import com.it7890.orange.util.ResponseUtil;
import com.it7890.orange.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


public class SearchServlet extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(SearchServlet.class);


    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String query = request.getParameter("query");
        logger.info(String.format("receive query: %s", query));

        String result = "welcome to my server. It's a POST request.";
        if (StringUtil.isNotEmpty(query)) {
            result = query + ", " + result;
        }
        logger.info(String.format("response is: %s", result));

        ResponseUtil.printToJson(result, response);
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String query = request.getParameter("query");
        logger.info(String.format("receive query: %s", query));

        String result = "welcome to my server. It's a GET request.";
        if (StringUtil.isNotEmpty(query)) {
            result = query + ", " + result;
        }
        logger.info(String.format("response is: %s", result));

        ResponseUtil.printToJson(result, response);
    }
}
