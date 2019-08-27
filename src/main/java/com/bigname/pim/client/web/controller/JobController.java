package com.bigname.pim.client.web.controller;

import com.bigname.pim.client.util.BreadcrumbsBuilder;
import com.m7.xtreme.xplatform.service.JobInstanceService;
import com.m7.xtreme.xplatform.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("pim/jobs")
public class JobController extends com.m7.xtreme.xplatform.web.controller.JobController {
    public JobController(JobInstanceService jobInstanceService, UserService userService) {
        super(jobInstanceService, userService, new BreadcrumbsBuilder());
    }
}
