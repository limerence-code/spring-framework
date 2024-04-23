package com.gaohwang.schedule;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.io.IOException;

public class TestSchedule {
	public static void main(String[] args) {
		try {
			AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(ScheduleConfig.class);
			DynamicTask task = applicationContext.getBean(DynamicTask.class);
			TestSchedule testSchedule = new TestSchedule();
			testSchedule.startTask(task);
			System.in.read();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

	}

    public void startTask( DynamicTask task) {
        task.startCron();
    }

    // 测试访问： http://localhost:8080/stopById?taskId=任务一
    public void stopById( DynamicTask task,String taskId) {
        task.stop(taskId);
    }

    public void stopAll( DynamicTask task) {
        task.stopAll();
    }
}
