package com.gaohwang.factorybean.service;

import com.gaohwang.factorybean.proxy.ServiceFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternUtils;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.ClassUtils;

import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @Description:
 * @Author: gaoh
 * @Date: 2020/9/8 17:46
 * @Version: 1.0
 */
@Component
public class ServiceBeanDefinitionRegistry implements BeanDefinitionRegistryPostProcessor, ResourceLoaderAware, ApplicationContextAware {

	@Override
	public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
		//这里一般我们是通过反射获取需要代理的接口的clazz列表
		//比如判断包下面的类，或者通过某注解标注的类等等
		Set<Class<?>> beanClazzs = scannerPackages("com.gaohwang.factorybean.interfaces");
		for (Class<?> beanClazz : beanClazzs) {
			BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition();
			GenericBeanDefinition definition = (GenericBeanDefinition) builder.getRawBeanDefinition();

			//注意，这里的BeanClass是生成Bean实例的工厂，不是Bean本身。
			// FactoryBean是一种特殊的Bean，其返回的对象不是指定类的一个实例，
			// 其返回的是该工厂Bean的getObject方法所返回的对象。
			definition.setBeanClass(ServiceFactory.class);

			definition.getConstructorArgumentValues().addGenericArgumentValue(beanClazz);

			//这里采用的是byType方式注入，类似的还有byName等
			definition.setAutowireMode(GenericBeanDefinition.AUTOWIRE_BY_TYPE);
			registry.registerBeanDefinition(beanClazz.getSimpleName(), definition);
		}
	}

	private static final String DEFAULT_RESOURCE_PATTERN = "**/*.class";

	private MetadataReaderFactory metadataReaderFactory;

	/**
	 * 根据包路径获取包及子包下的所有类
	 *
	 * @param basePackage basePackage
	 * @return Set<Class < ?>> Set<Class<?>>
	 */
	private Set<Class<?>> scannerPackages(String basePackage) {
		Set<Class<?>> set = new LinkedHashSet<>();
		String packageSearchPath = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX +
				resolveBasePackage(basePackage) + '/' + DEFAULT_RESOURCE_PATTERN;
		try {
			Resource[] resources = this.resourcePatternResolver.getResources(packageSearchPath);
			for (Resource resource : resources) {
				if (resource.isReadable()) {
					MetadataReader metadataReader = this.metadataReaderFactory.getMetadataReader(resource);
					String className = metadataReader.getClassMetadata().getClassName();
					Class<?> clazz;
					try {
						clazz = Class.forName(className);
						set.add(clazz);
					} catch (ClassNotFoundException e) {
						e.printStackTrace();
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return set;
	}

	protected String resolveBasePackage(String basePackage) {
		return ClassUtils.convertClassNameToResourcePath(this.applicationContext.getEnvironment().resolveRequiredPlaceholders(basePackage));
	}

	@Override
	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {

	}

	private ResourcePatternResolver resourcePatternResolver;

	private ApplicationContext applicationContext;

	@Override
	public void setResourceLoader(ResourceLoader resourceLoader) {
		this.resourcePatternResolver = ResourcePatternUtils.getResourcePatternResolver(resourceLoader);
		this.metadataReaderFactory = new CachingMetadataReaderFactory(resourceLoader);
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

}
