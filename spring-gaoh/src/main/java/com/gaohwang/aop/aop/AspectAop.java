package com.gaohwang.aop.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * @Description:
 * @Author: gaoh
 * @Date: 2020/9/13 9:44
 * @Version: 1.0
 */
@Aspect
@Component
public class AspectAop {
//	@Pointcut("execution(* com.gaohwang.aop.service.*.*(..))")
	@Pointcut("@annotation(AnnotationTest)")
	public void pointCut() {
	}

	@Around("pointCut()")
	public Object around(ProceedingJoinPoint point) {
		MethodSignature signature = (MethodSignature) point.getSignature();

		Method method = signature.getMethod();

		System.out.println("执行的方法名：" + method.getName());
		long time = System.currentTimeMillis();
		Object proceed = null;
		try {
			proceed = point.proceed();
		} catch (Throwable throwable) {
			throwable.printStackTrace();
		}
		System.out.println("执行时间：" + (System.currentTimeMillis() - time)+"\n");
		System.out.println();
		//0001
		//0001 0000
		return proceed;
	}

	@Before("pointCut()")
	public void before() {
		System.out.println("before");
		System.out.println();
	}

	@After("pointCut()")
	public void after() {
		System.out.println("after");
		System.out.println();
	}
}
