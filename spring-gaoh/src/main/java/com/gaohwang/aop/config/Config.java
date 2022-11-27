package com.gaohwang.aop.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * @Description:
 * @Author: gaoh
 * @Date: 2020/9/13 9:43
 * @Version: 1.0
 */
@Configuration
@EnableAspectJAutoProxy(exposeProxy = true)
@ComponentScan("com.gaohwang.aop")
public class Config {
}
