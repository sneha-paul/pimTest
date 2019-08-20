package com.bigname.pim.client.web.controller;

import com.bigname.pim.client.util.BreadcrumbsBuilder;
import com.m7.xtreme.xplatform.service.EventService;
import com.m7.xtreme.xplatform.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("pim/events")
public class EventController extends com.m7.xtreme.xplatform.web.controller.EventController {
    public EventController(EventService eventService, UserService userService) {
        super(eventService, userService, new BreadcrumbsBuilder());
    }
}
