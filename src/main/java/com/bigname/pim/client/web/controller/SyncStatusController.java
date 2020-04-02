package com.bigname.pim.client.web.controller;

import com.bigname.pim.core.util.BreadcrumbsBuilder;
import com.m7.xtreme.xplatform.service.SyncStatusService;
import com.m7.xtreme.xplatform.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("pim/syncStatuses")
public class SyncStatusController extends com.m7.xtreme.xplatform.web.controller.SyncStatusController{

    public SyncStatusController(SyncStatusService syncStatusService, UserService userService) {
        super(syncStatusService, userService, new BreadcrumbsBuilder());
    }
}
