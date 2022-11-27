package com.gaohwang.interfaces;

/**
 * @author gaoh
 * @Description
 * @create 2021-12-09 下午6:08
 */
public interface StudentInterface/*<T>*/ {

	/*default T getObject() {
		return null;
	}*/


	default void test(){
		System.out.println(this);
	}


	default void find() {
		System.out.println("base find");
	}

	default void query() {
		test();
		find();
	}
}
