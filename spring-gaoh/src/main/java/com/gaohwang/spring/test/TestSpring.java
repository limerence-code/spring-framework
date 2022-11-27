package com.gaohwang.spring.test;

import com.gaohwang.spring.bean.X;
import com.gaohwang.spring.config.Appconfig;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * @author gaoh
 * @version 1.0
 * @date 2020/5/8 9:57
 */
public class TestSpring {

	/**
	 * spring-aspects\src\main\java\org\springframework\cache\aspectj\AspectJJCacheConfiguration.java
	 * spring-context-support\src\main\java\org\springframework\cache\transaction\TransactionAwareCacheDecorator.java
	 * spring-context-support\src\main\java\org\springframework\scheduling\quartz\LocalDataSourceJobStore.java
	 * spring-context-support\src\main\java\org\springframework\scheduling\quartz\SchedulerAccessor.java
	 * spring-context-support\src\main\java\org\springframework\cache\transaction\AbstractTransactionSupportingCacheManager.java
	 * spring-context-support\src\main\java\org\springframework\cache\transaction\TransactionAwareCacheManagerProxy.java
	 */

	@Test
	public void test01() {
		System.out.println(this.getClass().getResource("/"));
		AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(Appconfig.class);
//		applicationContext.scan("com.gaohwang.spring");
//		applicationContext.refresh();
		X bean = applicationContext.getBean(X.class);
		System.out.println(bean);
	}
}
