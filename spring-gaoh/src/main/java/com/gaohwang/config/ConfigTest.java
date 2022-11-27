package com.gaohwang.config;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * @author:gaoh
 * @create: 2022-07-21 09:46
 * @Description:
 */
public class ConfigTest {
	public static void main(String[] args) {
		AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(Config.class);

		Config config = applicationContext.getBean(Config.class);
		System.out.println("======================================");
		Config.Stu stu = config.stu();
		System.out.println(stu);
		stu = config.stu();
		System.out.println(stu);


		stu = applicationContext.getBean(Config.Stu.class);
		System.out.println(stu);

		stu = applicationContext.getBean(Config.Stu.class);
		System.out.println(stu);

	}
}
