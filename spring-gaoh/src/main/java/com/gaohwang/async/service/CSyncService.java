package com.gaohwang.async.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * @author gaoh
 * @description
 * @createTime 2024-12-06 17:18
 */
@Component
public class CSyncService {
    @Autowired
	@Lazy
    private CSyncService cSyncService;

    public void fun1() {
        System.out.println("线程名称：" + Thread.currentThread().getName());
		cSyncService.fun2();
    }

	@Async
	public void fun2() {
		System.out.println("线程名称：" + Thread.currentThread().getName());
	}
}
