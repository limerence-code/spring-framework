package com.gaohwang.async;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * @author:gaoh
 * @create: 2022-07-18 17:06
 * @Description:
 */
@Configuration
@EnableAsync
@ComponentScan("com.gaohwang.async")
public class AsyncConfig {

}
