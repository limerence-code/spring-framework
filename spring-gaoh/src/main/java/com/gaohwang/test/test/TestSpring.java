package com.gaohwang.test.test;

import com.gaohwang.test.config.ConfigTest;
import org.junit.jupiter.api.Test;
import org.openjdk.jol.info.ClassLayout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * @Author: GH
 * @Date: 2020/1/12 15:28
 * @Version 1.0
 */
public class TestSpring {

	public static Logger log = LoggerFactory.getLogger(TestSpring.class);

	@Test
	public void test() {
		AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();
		applicationContext.register(ConfigTest.class);
		applicationContext.refresh();

		String[] beanDefinitionNames = applicationContext.getBeanDefinitionNames();
		for (String beanName : beanDefinitionNames) {
			log.info(beanName);
		}

		Object o = new Object();
		System.out.println(ClassLayout.parseInstance(o).toPrintable());
	}
}
