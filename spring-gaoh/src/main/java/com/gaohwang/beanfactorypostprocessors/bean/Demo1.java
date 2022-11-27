package com.gaohwang.beanfactorypostprocessors.bean;

import org.apache.catalina.core.ApplicationContext;
import org.springframework.stereotype.Component;

/**
 * @Description:
 * @Author: gaoh
 * @Date: 2021/3/27 16:28
 * @Version: 1.0
 */
@Component
public class Demo1 {

	private Demo2 demo2;

	public void setDemo2(Demo2 demo2) {
		this.demo2 = demo2;
	}

	public Demo2 getDemo2() {
		return demo2;
	}

	private ApplicationContext context;

	public void setContext(ApplicationContext context) {
		this.context = context;
	}

	public ApplicationContext getContext() {
		return context;
	}
}
