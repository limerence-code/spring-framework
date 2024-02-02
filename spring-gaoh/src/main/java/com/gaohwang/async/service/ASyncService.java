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
public class ASyncService {

    @Autowired
    private BSyncService bSyncService;

    public Object hello(Integer id) {
        System.out.println("线程名称：" + Thread.currentThread().getName());
        bSyncService.fun1(); // 使用接口方式调用，而不是this
        return "service hello";
    }

    @Async
    public void fun1() {
        System.out.println("线程名称：" + Thread.currentThread().getName());
    }

}
