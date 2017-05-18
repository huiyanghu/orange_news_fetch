package com.it7890.orange.quartz;

import org.quartz.JobDetail;

public class QuartzTest {

	public static void main(String[] args) {
		
		String job_name = "动态任务调度";  
        System.out.println("【系统启动】开始(每1秒输出一次)...");    
        QuartzManager.addJob(job_name, QuartzJob.class, "0/1 * * * * ?");
        
        try {
			Thread.sleep(10 * 1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
        
        System.out.println("【查询任务】");
        JobDetail jobDetail = QuartzManager.getJob(job_name);
        System.out.println("任务名--->" + jobDetail);
        
        System.out.println("【修改时间】开始(每2秒输出一次)...");
        QuartzManager.modifyJobTime(job_name, "0/2 * * * * ?");
        
        try {
			Thread.sleep(20 * 1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
        
        System.out.println("【移除定时】开始...");
        QuartzManager.removeJob(job_name);
        System.out.println("【移除定时】成功"); 
          
//        System.out.println("【再次添加定时任务】开始(每10秒输出一次)...");    
//        QuartzManager.addJob(job_name, QuartzJob.class, "*/10 * * * * ?");    
//        
//        System.out.println("【移除定时】开始...");    
//        QuartzManager.removeJob(job_name);    
//        System.out.println("【移除定时】成功");
	}
}
