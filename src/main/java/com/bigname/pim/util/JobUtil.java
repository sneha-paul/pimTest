package com.bigname.pim.util;

import com.bigname.pim.api.persistence.dao.SimpleJob;
import com.m7.xtreme.common.util.StatusUpdateUtil;
import com.m7.xtreme.xcore.domain.JobInstance;
import com.m7.xtreme.xcore.service.JobInstanceService;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class JobUtil {

    public static void schedule(JobInstanceService jobInstanceService){
        try {
            List<JobInstance> jobsList = jobInstanceService.getJobDetails();
            if (jobsList.isEmpty()) {

            } else {
                jobsList.forEach(jobs -> {
                    Date actualStartTime = jobs.getActualStartTime();
                    if (actualStartTime == null) {
                        JobDetail job = JobBuilder.newJob(SimpleJob.class)
                                .withIdentity(jobs.getJobName(), "newJob").build();

                        Date currentDate = new Date(System.currentTimeMillis());
                        Date startDate = null;
                        if (jobs.getScheduledStartTime().compareTo(currentDate) >= 0) {
                            startDate = jobs.getScheduledStartTime();
                        } else {
                            startDate = currentDate;
                        }
                        Trigger trigger = TriggerBuilder
                                .newTrigger()
                                .withIdentity("dummyTriggerName", "newJob")
                                .withSchedule(
                                        SimpleScheduleBuilder.simpleSchedule()
                                                .withIntervalInSeconds(0).withRepeatCount(0))
                                .startAt(startDate)
                                .build();
                        try {
                            Scheduler scheduler = new StdSchedulerFactory().getScheduler();
                            jobs.setStatus("Running");
                            jobs.setActualStartTime(new Date(System.currentTimeMillis()));
                            JobInstance jobNew = jobInstanceService.updateJobsDetails(jobs);

                            scheduler.start();
                            scheduler.scheduleJob(job, trigger);

                            Timer timer = new Timer();
                            TimerTask task = new StatusUpdateUtil(trigger, jobNew, jobInstanceService);
                            timer.schedule(task, 0, 3000);
                        } catch (SchedulerException se) {

                        }

                    }
                });
            }
        } catch (NullPointerException ne){

        }
    }
}
