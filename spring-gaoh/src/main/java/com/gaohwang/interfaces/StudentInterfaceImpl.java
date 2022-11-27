package com.gaohwang.interfaces;

/**
 * @author gaoh
 * @Description
 * @create 2021-12-09 下午6:09
 */
public class StudentInterfaceImpl implements StudentInterface/*<StudentInterfaceImpl>*/ {
	public static void main(String[] args) {
		StudentInterfaceImpl studentInterface = new StudentInterfaceImpl();
		studentInterface.query();

	}

	@Override
	public void find() {
		System.out.println("StudentInterfaceImpl find");
	}


}
