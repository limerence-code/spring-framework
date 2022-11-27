package com.gaohwang.async;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * @author:gaoh
 * @create: 2022-07-18 17:07
 * @Description:
 */
@Service
@Slf4j
public class HelloServiceImpl implements HelloService {
	@Autowired
	@Lazy
	private HelloService helloService;

	@Override
	public Object hello(Integer id) {
		System.out.println("线程名称：" + Thread.currentThread().getName());
		helloService.fun1(); // 使用接口方式调用，而不是this
		return "service hello";
	}

	@Async
	@Override
	public void fun1() {
		System.out.println("线程名称：" + Thread.currentThread().getName());
	}
}
