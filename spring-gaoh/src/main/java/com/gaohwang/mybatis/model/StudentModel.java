package com.gaohwang.mybatis.model;

import lombok.Data;

/**
 * @Description:
 * @Author: gaoh
 * @Date: 2021/3/30 14:09
 * @Version: 1.0
 */
@Data
public class StudentModel {
	private String name;
	private Integer age;
	private Integer id;
	private String remark;


	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getAge() {
		return age;
	}

	public void setAge(Integer age) {
		this.age = age;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}
}
