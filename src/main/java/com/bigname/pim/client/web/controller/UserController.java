package com.bigname.pim.client.web.controller;

import com.m7.xtreme.xplatform.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("pim/users")
public class UserController extends com.m7.xtreme.xplatform.web.controller.UserController {

    public UserController(UserService userService) {
        super(userService);
    }
}
