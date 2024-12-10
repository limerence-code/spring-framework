package com.gaohwang.async;

import com.gaohwang.async.service.ASyncService;
import com.gaohwang.async.service.BSyncService;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * @author:gaoh
 * @create: 2022-07-18 17:08
 * @Description: org.springframework.beans.factory.support.AbstractBeanFactory#doGetBean(java.lang.String, java.lang.Class, java.lang.Object[], boolean)
 */
public class AsyncTest {
	public static void main(String[] args) {
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(AsyncConfig.class);
		ASyncService aSyncService = context.getBean(ASyncService.class);
		aSyncService.hello(1);
		context.getBean(BSyncService.class);
	}
}
