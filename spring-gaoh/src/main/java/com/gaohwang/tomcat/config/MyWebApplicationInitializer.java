package com.gaohwang.tomcat.config;

import com.gaohwang.mybatis.config.MybatisConfig;
import com.gaohwang.util.LoggerUtils;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;

/**
 * @Author: GH
 * @Date: 2019/12/10 23:50
 * @Version 1.0
 */
public class MyWebApplicationInitializer implements WebApplicationInitializer {
	/**
	 * {@link javax.servlet.ServletContainerInitializer}
	 * {@link javax.servlet.annotation.HandlesTypes}
	 * {@link org.springframework.web.SpringServletContainerInitializer}
	 * <p>
	 * springMvc中META-INF/services/javax.servlet.ServletContainerInitializer
	 * <p>
	 * Servlet 3.0 spi 会获取ServletContainerInitializer接口实现类上面@HandlesTypes所有接口所有实现类
	 *
	 * @param servletContext the {@code ServletContext} to initialize
	 * @throws ServletException
	 */

	@Override
	public void onStartup(ServletContext servletContext) throws ServletException {
		LoggerUtils.log.info("======================WebApplicationInitializer======================");
		AnnotationConfigWebApplicationContext context = new AnnotationConfigWebApplicationContext();
		context.register(Config.class);
		context.register(MybatisConfig.class);
		context.refresh();

		// Create and register the DispatcherServlet
		DispatcherServlet servlet = new DispatcherServlet(context);
		ServletRegistration.Dynamic registration = servletContext.addServlet("springMvc", servlet);
		registration.setLoadOnStartup(1);
		registration.addMapping("/app/*");
	}
}
