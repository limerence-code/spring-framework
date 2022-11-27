package com.gaohwang.factorybean.interfaces;

import java.lang.annotation.*;

/**
 * @Description:
 * @Author: gaoh
 * @Date: 2021/1/18 16:18
 * @Version: 1.0
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Select {
	String value();
}
