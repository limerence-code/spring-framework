package com.gaohwang.test.config;

import com.gaohwang.test.model.PaperModel;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

/**
 * @Author: GH
 * @Date: 2020/1/12 15:31
 * @Version 1.0
 */
@Configurable
@ComponentScan("com.gaohwang.test")
public class ConfigTest {
	@Bean("paper")
	public PaperModel getPaper() {
		return new PaperModel();
	}
}
