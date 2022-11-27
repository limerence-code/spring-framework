package com.gaohwang.beanfactorypostprocessors.processors;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

/**
 * @Description:
 * @Author: gaoh
 * @Date: 2021/3/27 16:24
 * @Version: 1.0
 */
@Component
public class MyBeanDefinitionRegistryPostProcessorOrder implements BeanDefinitionRegistryPostProcessor, Ordered {

	@Override
	public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
		System.out.println("MyBeanDefinitionRegistryPostProcessorOrder#postProcessBeanDefinitionRegistry...");
	}


	@Override
	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
		System.out.println("MyBeanDefinitionRegistryPostProcessorOrder#postProcessBeanFactory...");
	}

	@Override
	public int getOrder() {
		return Integer.MAX_VALUE;
	}
}
