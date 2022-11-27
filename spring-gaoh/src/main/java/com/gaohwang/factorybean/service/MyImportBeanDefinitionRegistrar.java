package com.gaohwang.factorybean.service;

import com.gaohwang.factorybean.interfaces.UserMapper;
import com.gaohwang.factorybean.proxy.MapperFactoryBean;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;

/**
 * @Description:
 * @Author: gaoh
 * @Date: 2021/1/18 16:19
 * @Version: 1.0
 */
public class MyImportBeanDefinitionRegistrar implements ImportBeanDefinitionRegistrar {
	@Override
	public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
		// 执行包扫描，获取所需要的Mapper 这一步省略掉
		BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(MapperFactoryBean.class);
		GenericBeanDefinition bd = (GenericBeanDefinition) beanDefinitionBuilder.getBeanDefinition();
		bd.getConstructorArgumentValues().addGenericArgumentValue(UserMapper.class);
		// 改变自动装配的模式
		/*bd.setAutowireMode(AbstractBeanDefinition.AUTOWIRE_BY_TYPE);*/
		registry.registerBeanDefinition("userMapper", bd);
	}

}
