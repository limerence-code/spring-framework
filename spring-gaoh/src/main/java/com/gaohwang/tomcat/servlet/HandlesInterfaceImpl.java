package com.gaohwang.tomcat.servlet;

import com.gaohwang.util.LoggerUtils;
import org.slf4j.Logger;

import jakarta.servlet.ServletContext;

/**
 * @author gaoh
 * @since 2022/11/24
 */
public class HandlesInterfaceImpl implements HandlesInterface {
	Logger log = LoggerUtils.getLog(HandlesInterfaceImpl.class);


	@Override
	public void Handles(ServletContext ctx) {
		log.info("HandlesInterfaceImpl...");
		log.info("contextPath:{}", ctx.getContextPath());
	}
}
