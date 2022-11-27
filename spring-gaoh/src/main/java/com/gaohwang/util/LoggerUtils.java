package com.gaohwang.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author gaoh
 * @since 2022/11/24
 */
public class LoggerUtils {
	private LoggerUtils() {
	}

	public static Logger log = LoggerFactory.getLogger(LoggerUtils.class);

	public static Logger getLog(Class<?> clazz) {
		return LoggerFactory.getLogger(clazz);
	}
}
