package com.bigname.pim.api.persistence.dao;

import com.m7.xtreme.xcore.domain.JobInstance;
import com.m7.xtreme.xcore.service.JobInstanceService;
import org.quartz.Job;
import org.quartz.JobExecutionContext;

import java.util.Date;
import java.util.UUID;

public class SimpleJob implements Job {

    private JobInstanceService jobInstanceService;

    public JobInstanceService getJobInstanceService() {
        return jobInstanceService;
    }

    public void setJobInstanceService(JobInstanceService jobInstanceService) {
        this.jobInstanceService = jobInstanceService;
    }

    @Override
    public void execute(JobExecutionContext jobExecutionContext) {

        System.out.println("Job Executed");
        String jobName = jobExecutionContext.getJobDetail().getKey().getName();
        String jobType = jobExecutionContext.getJobDetail().getKey().getGroup();
        System.out.println("JobType : " + jobType);
        System.out.println("========"+jobExecutionContext.getFireInstanceId());
        System.out.println(jobExecutionContext.getTrigger().getStartTime());
        Date endTime = jobExecutionContext.getScheduledFireTime();
        System.out.println("End Time : "+endTime);

        if(jobType.equals("CronJob")){
            Date scheduleTime = new Date(System.currentTimeMillis());
            //String cronExpression = "0 * * ? * *	";  //"*/10 * * * * ? *";

            JobInstance jobs = new JobInstance();
            jobs.setJobId(UUID.randomUUID().toString());
            jobs.setJobName(jobName);
            jobs.setJobType(jobType);
            jobs.setStatus("Completed");
            jobs.setScheduledStartTime(scheduleTime);
            jobs.setActualStartTime(jobExecutionContext.getTrigger().getStartTime());
            jobs.setCompletedTime(endTime);
            JobInstance jobNew = jobInstanceService.createJobsDetails(jobs);
        }
    }
}
