package com.bigname.pim.client.web.controller;

import com.bigname.pim.job.JobConfigurer;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/load1")
public class LoadController {
    @Autowired
    JobLauncher jobLauncher;
    @Autowired
    private JobConfigurer jobConfigurer;

    @GetMapping
    public BatchStatus load() throws Exception {
        Map<String, JobParameter> maps = new HashMap<>();
        maps.put("time", new JobParameter(System.currentTimeMillis()));
        JobParameters jobParameters = new JobParameters(maps);
        JobExecution jobExecution = jobLauncher.run(jobConfigurer.synchroniseJob(), jobParameters);
        System.out.println("Status" + jobExecution.getStatus());
        return jobExecution.getStatus();
    }


}
