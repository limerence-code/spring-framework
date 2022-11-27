package com.gaohwang.factorybean.config;

import com.gaohwang.factorybean.service.MyImportBeanDefinitionRegistrar;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * @Description:
 * @Author: gaoh
 * @Date: 2020/9/8 17:48
 * @Version: 1.0
 */
@Configuration
@ComponentScan("com.gaohwang.factorybean")
@Import(MyImportBeanDefinitionRegistrar.class)
public class Config {
}
