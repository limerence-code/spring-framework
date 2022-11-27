package com.gaohwang.aop.service;

import com.gaohwang.aop.aop.AnnotationTest;
import com.gaohwang.aop.dao.StudentDao;
import org.springframework.stereotype.Component;

/**
 * @Description:
 * @Author: gaoh
 * @Date: 2020/9/13 9:56
 * @Version: 1.0
 */
@Component
public class StudentImpl implements StudentDao {
	@AnnotationTest
	@Override
	public void student() {
		System.out.println("Good Good Student !");
	}
}
