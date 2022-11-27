package com.gaohwang.factorybean.test;

import com.gaohwang.factorybean.config.Config;
import com.gaohwang.factorybean.interfaces.CalculateService;
import com.gaohwang.factorybean.interfaces.TestService;
import com.gaohwang.factorybean.interfaces.UserMapper;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * @Description:
 * @Author: gaoh
 * @Date: 2020/9/8 17:53
 * @Version: 1.0
 */
public class Test001 {

    @Test
    public void test() {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(Config.class);
        TestService testService = context.getBean(TestService.class);

        CalculateService calculateService = context.getBean(CalculateService.class);


        String testList = testService.getList("code123","name456");
        String calculateResult = calculateService.getResult("测试");

        System.out.println(testList + "," + calculateResult);


		UserMapper userMapper = (UserMapper) context.getBean("userMapper");
//		UserMapper userMapper = context.getBean(UserMapper.class);
		userMapper.selectUserById();
    }
}
