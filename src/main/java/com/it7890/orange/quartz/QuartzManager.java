package com.it7890.orange.quartz;

import java.io.Serializable;

import org.quartz.CronTrigger;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerFactory;
import org.quartz.impl.StdSchedulerFactory;

/**
 * 定时任务管理类
 */
public class QuartzManager {

	private static SchedulerFactory schedulerFactory = new StdSchedulerFactory();
	
    private static String JOB_GROUP_NAME = "TOUPAI_JOBGROUP_NAME";
    private static String TRIGGER_GROUP_NAME = "TOUPAI_TRIGGERGROUP_NAME";
    
    /**
     * 根据任务名查询一个任务
     * @param jobName 任务名
     * @return
     */
    public static JobDetail getJob(String jobName) {
    	JobDetail jobDetail = null;
    	try {
    		Scheduler sched = schedulerFactory.getScheduler();
    		jobDetail = sched.getJobDetail(jobName, JOB_GROUP_NAME);
    	} catch (Exception e) {
    		jobDetail = null;
    		throw new RuntimeException(e);
    	}
    	
    	return jobDetail;
    }
    
    /**
     * 添加一个定时任务，使用默认的任务组名，触发器名，触发器组名
     * @param jobName 任务名
     * @param cls 任务
     * @param time 时间设置，参考quartz说明文档
     */
    @SuppressWarnings("rawtypes")
	public static void addJob(String jobName, Class cls, String time) {
        try {
            Scheduler sched = schedulerFactory.getScheduler();
            JobDetail jobDetail = new JobDetail(jobName, JOB_GROUP_NAME, cls);// 任务名，任务组，任务执行类
            // 触发器
            CronTrigger trigger = new CronTrigger(jobName, TRIGGER_GROUP_NAME);// 触发器名,触发器组
            trigger.setCronExpression(time);// 触发器时间设定
            sched.scheduleJob(jobDetail, trigger);
            // 启动
            if (!sched.isShutdown()) {
                sched.start();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }  
    }
    
    /**
     * 添加一个定时任务，使用默认的任务组名，触发器名，触发器组名
     * @param jobName 任务名
     * @param cls 任务
     * @param time 时间设置，参考quartz说明文档
     * @param param 参数
     */
    @SuppressWarnings("rawtypes")
    public static void addJob(String jobName, Class cls, String time, Serializable param) {
    	try {
    		Scheduler sched = schedulerFactory.getScheduler();
    		JobDetail jobDetail = new JobDetail(jobName, JOB_GROUP_NAME, cls);// 任务名，任务组，任务执行类
    		jobDetail.getJobDataMap().put("param", param);	//设置参数
    		// 触发器
    		CronTrigger trigger = new CronTrigger(jobName, TRIGGER_GROUP_NAME);// 触发器名,触发器组
    		trigger.setCronExpression(time);// 触发器时间设定
    		sched.scheduleJob(jobDetail, trigger);
    		// 启动
    		if (!sched.isShutdown()) {
    			sched.start();
    		}
    	} catch (Exception e) {
    		throw new RuntimeException(e);
    	}  
    }
    
    /**
     * 添加一个定时任务
     * @param jobName 任务名
     * @param jobGroupName 任务分组名
     * @param triggerName 触发器名
     * @param triggerGroupName 触发器分组名
     * @param jobClass 任务
     * @param time 时间设置，参考quartz说明文档
     */
    @SuppressWarnings("rawtypes")
	public static void addJob(String jobName, String jobGroupName, String triggerName, String triggerGroupName, Class jobClass, String time) {
        try {
            Scheduler sched = schedulerFactory.getScheduler();
            JobDetail jobDetail = new JobDetail(jobName, jobGroupName, jobClass);// 任务名，任务组，任务执行类
            // 触发器
            CronTrigger trigger = new CronTrigger(triggerName, triggerGroupName);// 触发器名,触发器组
            trigger.setCronExpression(time);// 触发器时间设定
            sched.scheduleJob(jobDetail, trigger);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    /**
     * 修改一个任务的触发时间(使用默认的任务组名，触发器名，触发器组名)
     * @param jobName 任务名
     * @param time 时间设置，参考quartz说明文档
     */
    @SuppressWarnings("rawtypes")
	public static void modifyJobTime(String jobName, String time) {
        try {
            Scheduler sched = schedulerFactory.getScheduler();
            CronTrigger trigger = (CronTrigger) sched.getTrigger(jobName,TRIGGER_GROUP_NAME);
            if (trigger == null) {
                return;
            }
            String oldTime = trigger.getCronExpression();
            if (!oldTime.equalsIgnoreCase(time)) {
                JobDetail jobDetail = sched.getJobDetail(jobName,JOB_GROUP_NAME);
                Class objJobClass = jobDetail.getJobClass();
                removeJob(jobName);
                addJob(jobName, objJobClass, time);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    /**
     * 修改一个任务的触发时间
     * @param triggerName 触发器名
     * @param triggerGroupName 触发器分组名名
     * @param time 时间设置，参考quartz说明文档
     */
    public static void modifyJobTime(String triggerName, String triggerGroupName, String time) {
        try {
            Scheduler sched = schedulerFactory.getScheduler();
            CronTrigger trigger = (CronTrigger) sched.getTrigger(triggerName,triggerGroupName);
            if (trigger == null) {
                return;
            }
            String oldTime = trigger.getCronExpression();
            if (!oldTime.equalsIgnoreCase(time)) {
                CronTrigger ct = (CronTrigger) trigger;
                // 修改时间
                ct.setCronExpression(time);
                // 重启触发器
                sched.resumeTrigger(triggerName, triggerGroupName);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }  
    }
    
    /**
     * 移除一个任务(使用默认的任务组名，触发器名，触发器组名)
     * @param jobName 任务名
     */
    public static void removeJob(String jobName) {
        try {
            Scheduler sched = schedulerFactory.getScheduler();
            sched.pauseTrigger(jobName, TRIGGER_GROUP_NAME);// 停止触发器
            sched.unscheduleJob(jobName, TRIGGER_GROUP_NAME);// 移除触发器
            sched.deleteJob(jobName, JOB_GROUP_NAME);// 删除任务
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    /**
     * 移除一个任务
     * @param jobName 任务名
     * @param jobGroupName 分组名
     * @param triggerName 触发器名
     * @param triggerGroupName 触发器分组名
     */
    public static void removeJob(String jobName, String jobGroupName, 
    		String triggerName, String triggerGroupName) {
        try {
            Scheduler sched = schedulerFactory.getScheduler();
            sched.pauseTrigger(triggerName, triggerGroupName);// 停止触发器
            sched.unscheduleJob(triggerName, triggerGroupName);// 移除触发器
            sched.deleteJob(jobName, jobGroupName);// 删除任务
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    /**
     * 启动所有定时任务
     */
    public static void startJobs() {
        try {
            Scheduler sched = schedulerFactory.getScheduler();
            sched.start();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    /**
     * 关闭所有定时任务
     */
    public static void shutdownJobs() {
        try {
            Scheduler sched = schedulerFactory.getScheduler();
            if (!sched.isShutdown()) {
                sched.shutdown();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
