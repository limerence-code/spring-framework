package com.gaohwang.async.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * @author gaoh
 * @description
 * @createTime 2024-02-02 17:18
 */
@Component
public class BSyncService {
    @Autowired
//    @Lazy
    private ASyncService aSyncService;

    @Async
    public void fun1() {
        System.out.println("线程名称：" + Thread.currentThread().getName());
        aSyncService.fun1();
    }
}
