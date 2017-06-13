package com.it7890.orange.test;

import com.it7890.orange.servlet.CheckGrabDetailRuleServlet;
import com.it7890.orange.servlet.CheckGrabListRuleServlet;
import com.it7890.orange.servlet.SearchServlet;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 利用Jetty实现简单的嵌入式Httpserver
 */
public class JettyServletExampleServer {

    private static final Logger LOGGER = LoggerFactory.getLogger(JettyServletExampleServer.class);


//    public static void main(String[] args) {
//        try {
//            Server server = new Server(8080);
//
//            ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
//            context.setContextPath("/");
//            server.setHandler(context);
//
//            context.addServlet(new ServletHolder(new SearchServlet()),"/search");
//            context.addServlet(new ServletHolder(new CheckGrabListRuleServlet()),"/checkGrabListRule");
//            context.addServlet(new ServletHolder(new CheckGrabDetailRuleServlet()),"/checkGrabDetailRule");
//            LOGGER.info("server start.");
//            server.start();
//            server.join();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
}
