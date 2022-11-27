package com.gaohwang.aop.service;

import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * @Description:
 * @Author: gaoh
 * @Date: 2021/3/22 16:56
 * @Version: 1.0
 */
@Component
public class Demo {

	public void test() {
		System.out.println("test");
	}

	public Demo() {
		System.out.println("构造方法-Demo！");
		System.out.println();
	}


	@PostConstruct
	public void postConstruct() {
		System.out.println("postConstruct-Demo！");
		System.out.println();
	}
}
