package com.gaohwang.factorybean.proxy;

import com.gaohwang.factorybean.interfaces.Select;
import org.springframework.beans.factory.FactoryBean;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * @Description:
 * @Author: gaoh
 * @Date: 2021/1/18 16:13
 * @Version: 1.0
 */
@SuppressWarnings({"unchecked", "rawtypes"})
public class MapperFactoryBean   implements FactoryBean, InvocationHandler {
	private Class clazz;
	public MapperFactoryBean(Class clazz){
		this.clazz=clazz;
	}
	@Override
	public Object getObject() throws Exception {
		Class[] clazzs=new Class[]{clazz};
		return Proxy.newProxyInstance(this.getClass().getClassLoader(),clazzs,this);
	}
	@Override
	public Class<?> getObjectType() {
		return clazz;
	}
	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		Annotation annotation=method.getAnnotation(Select.class);
		System.out.println(((Select) annotation).value());
		return clazz;
	}

}
