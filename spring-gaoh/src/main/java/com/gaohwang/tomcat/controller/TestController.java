package com.gaohwang.tomcat.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.gaohwang.mybatis.service.StudentService;
import com.gaohwang.util.LoggerUtils;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @Author: GH
 * @Date: 2019/12/10 23:54
 * @Version 1.0
 */
@Controller
public class TestController {
	Logger log = LoggerUtils.getLog(TestController.class);

	@Autowired
	private StudentService studentService;


	@GetMapping(value = "/test")
	public String test() {
		log.info("test");
		return "test";
	}

	@GetMapping(value = "/test1")
	@ResponseBody
	public String test1() {
		log.info("test1");
		return JSON.toJSONString(studentService.list());
	}

	@GetMapping(value = "/map")
	@ResponseBody
	public Map<String, String> map() {
		log.info("map...");
		return new HashMap<String, String>(2) {
			{
				put("1", "1");
				put("2", "2");
			}
		};
	}
}
