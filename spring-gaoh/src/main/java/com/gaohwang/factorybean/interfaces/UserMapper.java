package com.gaohwang.factorybean.interfaces;

/**
 * @Description:
 * @Author: gaoh
 * @Date: 2021/1/18 16:19
 * @Version: 1.0
 */
public interface UserMapper {

	@Select("123")
	void selectUserById();
}
