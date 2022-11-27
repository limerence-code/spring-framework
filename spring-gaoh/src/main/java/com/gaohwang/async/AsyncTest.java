package com.gaohwang.async;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * @author:gaoh
 * @create: 2022-07-18 17:08
 * @Description:
 */
public class AsyncTest {
	public static void main(String[] args) {
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(AsyncConfig.class);
		HelloService helloService = context.getBean(HelloService.class);
		helloService.fun1();
	}
}
