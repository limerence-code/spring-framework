package com.gaohwang.schedule;

public class CustomizeTask implements Runnable {
    private String name;    // 任务名字
    private String cron;    // 触发条件
    private String data;    // 传输的数据参数
    private String method;  // 需要调用的方法
    
    CustomizeTask(String name, String cron, String data, String method) {
        this.name = name;
        this.cron = cron;
        this.data = data;
        this.method = method;
    }

    public String getCron(){
        return this.cron;
    }

    public String getName(){
        return this.name;
    }

    @Override
    public void run() {
        // 通过反射获取到调用的方法，传入调用的参数 data
		System.out.println("当前任务名称：" + name);
    }
}
