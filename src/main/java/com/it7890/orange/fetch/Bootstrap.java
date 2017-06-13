package com.it7890.orange.fetch;

import com.avos.avoscloud.AVOSCloud;
import com.it7890.orange.servlet.CheckGrabDetailRuleServlet;
import com.it7890.orange.servlet.CheckGrabListRuleServlet;
import com.it7890.orange.servlet.SearchServlet;
import com.it7890.orange.util.Constants;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.springframework.context.support.ClassPathXmlApplicationContext;


public class Bootstrap {
	private static final Logger logger = LogManager.getLogger(Bootstrap.class);

	public static void main(String[] args) {
		// 启用北美节点
		AVOSCloud.useAVCloudUS();
		AVOSCloud.initialize(Constants.LEAN_CLOUD_APP_ID, Constants.LEAN_CLOUD_APP_KEY, Constants.LEAN_CLOUD_MASTER_KEY);

		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("classpath:spring/*.xml");
		logger.info("orange_news_fetch start.");

		startJettyServlet();
	}

	private static void startJettyServlet() {
		try {
			Server server = new Server(80);

			ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
			context.setContextPath("/");
			server.setHandler(context);

			context.addServlet(new ServletHolder(new SearchServlet()),"/search");
			context.addServlet(new ServletHolder(new CheckGrabListRuleServlet()),"/checkGrabListRule");
			context.addServlet(new ServletHolder(new CheckGrabDetailRuleServlet()),"/checkGrabDetailRule");
			logger.info("server start.");
			server.start();
			server.join();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
