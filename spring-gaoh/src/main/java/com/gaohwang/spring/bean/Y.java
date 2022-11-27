package com.gaohwang.spring.bean;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author gaoh
 * @version 1.0
 * @date 2020/5/8 9:56
 */
@Component
public class Y {
    @Autowired
    X x;

    public Y(){
        System.out.println("Y create");
    }
}