package com.gaohwang.tomcat;

import com.gaohwang.util.LoggerUtils;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.startup.Tomcat;

/**
 * @author gaoh
 * @since 2022/11/24
 */
public class SpringBootApplication {
	private static int port = 11000;
	private static String contextPath = "/";


	public static void run() {
		Tomcat tomcat = new Tomcat();
		String baseDir = Thread.currentThread().getContextClassLoader().getResource("").getPath();
		//设置tomcat启动后的工作目录
		tomcat.setBaseDir(baseDir);
		//设置端口
		tomcat.setPort(port);
		//获取执行器，并设置io协议
		Connector connector = new Connector("org.apache.coyote.http11.Http11NioProtocol");
		//设置端口
		connector.setPort(port);
		//设置执行器
		tomcat.setConnector(connector);

		tomcat.addWebapp(contextPath, baseDir);
		//启用JNDI命名
		tomcat.enableNaming();
		try {
			tomcat.start();
		} catch (LifecycleException e) {
			LoggerUtils.log.error("tomcat 启动失败");
		}
		//tomcat启动后，让其阻塞，不让当前线程结束，等待处理请求，
		tomcat.getServer().await();
	}

	public static void main(String[] args) {
		run();
	}
}
