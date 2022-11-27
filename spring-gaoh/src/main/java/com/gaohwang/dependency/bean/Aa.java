package com.gaohwang.dependency.bean;

import org.springframework.beans.factory.annotation.Autowired;

/**
 * @Description:
 * @Author: gaoh
 * @Date: 2021/3/4 16:41
 * @Version: 1.0
 */
//@Component("a")
public class Aa {
    @Autowired
    private Bb b;

    /*public Aa(Bb b){
        this.b = b;
    }*/
}
