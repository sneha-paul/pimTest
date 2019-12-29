package com.bigname.pim.core.persistence.dao.mongo;

import com.m7.xtreme.xplatform.domain.JobInstance;
import com.m7.xtreme.xplatform.service.JobInstanceService;
import org.quartz.Job;
import org.quartz.JobExecutionContext;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

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

        System.out.println("============== : "+jobExecutionContext.getJobDetail().getJobDataMap().getString("jobService"));

        System.out.println("Job Executed");
        String jobName = jobExecutionContext.getJobDetail().getKey().getName();
        String jobType = jobExecutionContext.getJobDetail().getKey().getGroup();
        System.out.println("JobType : " + jobType);
        System.out.println("========" + jobExecutionContext.getFireInstanceId());
        System.out.println(jobExecutionContext.getTrigger().getStartTime());
        Instant endDateInstant = Instant.ofEpochMilli(jobExecutionContext.getScheduledFireTime().getTime());
        LocalDateTime endTime = LocalDateTime.ofInstant(endDateInstant, ZoneId.systemDefault());
        System.out.println("End Time : " + endTime);
        Instant startDateInstant = Instant.ofEpochMilli(jobExecutionContext.getTrigger().getStartTime().getTime());
        LocalDateTime startDateTime = LocalDateTime.ofInstant(startDateInstant, ZoneId.systemDefault());

        if (jobType.equals("CronJob")) {
            //String cronExpression = "0 * * ? * *	";  //"*/10 * * * * ? *";

            JobInstance jobInstance = new JobInstance();
            jobInstance.setExternalId(jobName);
            jobInstance.setJobName(jobName);
            jobInstance.setJobType(jobType);
            jobInstance.setStatus("Completed");
            jobInstance.setScheduledStartTime(startDateTime);
            jobInstance.setActualStartTime(LocalDateTime.now());
            jobInstance.setCompletedTime(endTime);
            jobInstanceService.createJobsDetails(jobInstance);
        }
    }
}
