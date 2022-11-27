package com.gaohwang.aop.service;

import com.gaohwang.aop.aop.AnnotationTest;
import org.springframework.aop.framework.AopContext;
import org.springframework.stereotype.Component;

/**
 * @Description:
 * @Author: gaoh
 * @Date: 2021/3/3 20:01
 * @Version: 1.0
 */
@Component
public class TestService {
	@AnnotationTest
	public void test() {
		System.out.println("Test");
		((TestService)AopContext.currentProxy()).test("6666...");
	}




	@AnnotationTest
	public void test(String str) {
		System.out.println("Test:"+str);
	}
}
