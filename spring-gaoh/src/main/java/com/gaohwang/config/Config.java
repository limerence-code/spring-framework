package com.gaohwang.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

/**
 * @author:gaoh
 * @create: 2022-07-21 09:44
 * @Description:
 */
@Configuration
//@Configuration(proxyBeanMethods = false)
//@Component
@ComponentScan("com.gaohwang.config")
public class Config {

	@Bean
	public Stu stu() {
		return new Stu();
	}

	@Bean
	public Ten ten() {
		return new Ten();
	}

	public static class Stu {
	}

	public static class Ten {
	}

}
