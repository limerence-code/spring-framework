package com.gaohwang.dependency.config;

import com.gaohwang.dependency.bean.Aa;
import com.gaohwang.dependency.bean.Bb;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @Description:
 * @Author: gaoh
 * @Date: 2021/3/4 16:40
 * @Version: 1.0
 */
@Configuration
@ComponentScan("com.gaohwang.dependency")
public class Config {

    @Bean
    public Aa a() {
        return new Aa();
    }

    @Bean
    public Bb b() {
        return new Bb();
    }
}
