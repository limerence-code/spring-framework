package com.gaohwang.beanfactorypostprocessors.test;

import com.gaohwang.beanfactorypostprocessors.bean.Demo1;
import com.gaohwang.beanfactorypostprocessors.config.Config;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * @Description:
 * @Author: gaoh
 * @Date: 2021/3/27 16:25
 * @Version: 1.0
 */
public class BeanFactoryPostProcessorTest {
	public static void main(String[] args) {
		AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();
		//向容器中添加BeanFactoryPostProcessor
		applicationContext.addBeanFactoryPostProcessor((beanFactory) -> {
			System.out.println("Lambda#postProcessBeanFactory...");
		});
		//向容器中添加BeanDefinitionRegistryPostProcessor
		applicationContext.addBeanFactoryPostProcessor(new BeanDefinitionRegistryPostProcessor() {
			@Override
			public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
				System.out.println("匿名#postProcessBeanDefinitionRegistry...");
			}
			@Override
			public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
				System.out.println("匿名#postProcessBeanDefinitionRegistry...");
			}
		});
		applicationContext.register(Config.class);
		applicationContext.refresh();
		Demo1 bean = applicationContext.getBean(Demo1.class);
		System.out.println(bean.getDemo2());
		System.out.println(bean.getContext());

//		applicationContext.getBeanFactory().registerSingleton();

	}
}
