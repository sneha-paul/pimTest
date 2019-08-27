package com.bigname.pim.client.web.controller;

import com.bigname.pim.api.persistence.dao.mongo.SimpleJob;
import com.bigname.pim.api.service.CatalogService;
import com.bigname.pim.client.util.BreadcrumbsBuilder;
import com.m7.xtreme.common.util.JobUtil;
import com.m7.xtreme.xcore.web.controller.BaseController;
import com.m7.xtreme.xplatform.domain.CronDetails;
import com.m7.xtreme.xplatform.domain.JobInstance;
import com.m7.xtreme.xplatform.service.CronJobService;
import com.m7.xtreme.xplatform.service.EventService;
import com.m7.xtreme.xplatform.service.JobInstanceService;
import org.quartz.JobDataMap;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("pim/scheduler")
public class ScheduleController extends BaseController<JobInstance, JobInstanceService> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ScheduleController.class);

    private JobInstanceService jobInstanceService;

    private CronJobService cronJobService;

    private CatalogService catalogService;
    private EventService eventService;

    public ScheduleController(JobInstanceService jobInstanceService, CronJobService cronJobService, CatalogService catalogService, EventService eventService) {
        super(jobInstanceService, JobInstance.class, new BreadcrumbsBuilder());
        this.jobInstanceService = jobInstanceService;
        this.cronJobService = cronJobService;
        this.catalogService = catalogService;
        this.eventService = eventService;

    }

    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> create( JobInstance jobInstance) throws SchedulerException {
        Map<String, Object> model = new HashMap<>();
        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put("catalogService", catalogService);
        jobDataMap.put("eventService", eventService);
        jobDataMap.put("jobInstanceService", jobInstanceService);
        // Date date1 = new SimpleDateFormat("MM-dd-yyyy'T'HH:mm:ss").parse(String.valueOf(jobInstance.getScheduledStartTime()));
        LOGGER.info("scheduleTime : "+ jobInstance.getJobName());
        if(isValid(jobInstance, model, JobInstance.CreateGroup.class)) {
            Class<?> classType = null;
            String className = "com.bigname.pim.data.exportor." + jobInstance.getJobName();
            try {
                classType = Class.forName(className);
                Package.getPackage(jobInstance.getJobName());
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            JobUtil.scheduleSimpleJob(classType, jobInstance, jobInstanceService, jobDataMap);
            model.put("success", true);
        }
        return model;
    }

    @RequestMapping(value="/cronJob", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> createCronJob( CronDetails cronDetails) throws SchedulerException {
        Map<String, Object> model = new HashMap<>();
        if(isValid(cronDetails, model, CronDetails.CreateGroup.class)) {
            JobUtil.scheduleCronJob(SimpleJob.class, jobInstanceService, cronDetails, cronJobService);
            model.put("success", true);
        }
        return model;
    }

    @RequestMapping(value = {"/simpleJob/create"})
    public ModelAndView createSimpleJob() {

        Map<String, Object> model = new HashMap<>();
        model.put("active", "SimpleJobScheduler");
        model.put("mode", "CREATE");
        model.put("view", "scheduler/simpleJob");

        return super.details(model);

    }

    @RequestMapping(value = {"/cronJob/create"})
    public ModelAndView createCronJob() {

        Map<String, Object> model = new HashMap<>();
        model.put("active", "CronJobScheduler");
        model.put("mode", "CREATE");
        model.put("view", "scheduler/cronJob");

        return super.details(model);

    }

    @RequestMapping()
    public ModelAndView all() {
        Map<String, Object> model = new HashMap<>();
        model.put("active", "SCHEDULER");
        model.put("view", "scheduler/scheduler");
        model.put("title", "Scheduler");
        return all(model);
    }

    @RequestMapping(value = "/schedule/{scheduleTime}", method = RequestMethod.POST)
    @ResponseBody
    public void schedule(@PathVariable(value = "scheduleTime") String scheduleTime) throws InterruptedException {
        LOGGER.info("scheduleTime : "+ scheduleTime);
    }
}
