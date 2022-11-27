package com.gaohwang.aop.test;

import com.gaohwang.aop.config.Config;
import com.gaohwang.aop.dao.StudentDao;
import com.gaohwang.aop.service.TestService;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * @Description:
 * @Author: gaoh
 * @Date: 2020/9/13 9:57
 * @Version: 1.0
 */
public class TestAop {
	public static void main(String[] args) {
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(Config.class);
		StudentDao bean = context.getBean(StudentDao.class);
//		TestService testService = context.getBean(TestService.class);
//		testService.test();
		bean.student();
	}

/*	@Test
	public void test() {
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(Config.class);
		StudentDao bean = context.getBean(StudentDao.class);
		bean.student();
	}*/
}
