package com.gaohwang.dependency.bean;

import org.springframework.beans.factory.annotation.Autowired;

/**
 * @Description:
 * @Author: gaoh
 * @Date: 2021/3/4 16:41
 * @Version: 1.0
 */
public class Bb {
    @Autowired
    private Aa a;
}
