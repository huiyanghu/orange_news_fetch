package com.it7890.orange.fetch;

import com.avos.avoscloud.AVOSCloud;
import com.it7890.orange.util.Constants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.webapp.WebAppContext;


public class Bootstrap {
	private static final Logger logger = LogManager.getLogger(Bootstrap.class);

	public static void main(String[] args) {
		// 启用北美节点
		AVOSCloud.useAVCloudUS();
		AVOSCloud.initialize(Constants.LEAN_CLOUD_APP_ID, Constants.LEAN_CLOUD_APP_KEY, Constants.LEAN_CLOUD_MASTER_KEY);

		Bootstrap.startJetty(PORT, "");
		logger.info("orange_news_fetch start.");
	}

	public static final int PORT = 8080;
//	public static final int PORT = 80;
	public static final String CONTEXT = "/";

	private static final String DEFAULT_WEBAPP_PATH = "src/main/webapp";

	/**
	 * 创建用于开发运行调试的Jetty Server, 以src/main/webapp为Web应用目录.
	 */
	public static Server createServerInSource(int port, String contextPath) {
		Server server = new Server();
		// 设置在JVM退出时关闭Jetty的钩子。
		server.setStopAtShutdown(true);

		// 这是http的连接器
		ServerConnector connector = new ServerConnector(server);
		connector.setPort(port);
		// 解决Windows下重复启动Jetty居然不报告端口冲突的问题.
		connector.setReuseAddress(true);
		server.setConnectors(new Connector[] { connector });

		WebAppContext webContext = new WebAppContext(DEFAULT_WEBAPP_PATH, contextPath);
		// webContext.setContextPath("/");
		webContext.setDescriptor("src/main/webapp/WEB-INF/web.xml");
		// 设置webapp的位置
		webContext.setResourceBase(DEFAULT_WEBAPP_PATH);
		webContext.setClassLoader(Thread.currentThread().getContextClassLoader());
		server.setHandler(webContext);
		return server;
	}

	/**
	 * 启动jetty服务
	 *
	 * @param port
	 * @param context
	 */
	public static void startJetty(int port, String context) {
		final Server server = Bootstrap.createServerInSource(PORT, CONTEXT);
		try {
			server.stop();
			server.start();
			server.join();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}
}
