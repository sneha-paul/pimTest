package com.bigname.pim.client.web.controller;

import com.bigname.pim.api.domain.User;
import com.bigname.pim.api.service.UserService;
import com.bigname.pim.client.model.Breadcrumbs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.Map;


/**
 * Created by sruthi on 05-11-2018.
 */
@Controller
@RequestMapping("pim/users")
public class RegistrationController extends BaseController<User, UserService> {

    private UserService userService;

    public RegistrationController(UserService userService) {
        super(userService);
        this.userService = userService;
    }

    @Autowired
    public PasswordEncoder passwordEncoder;

    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    public ModelAndView create(User user) {
        Map<String, Object> model = new HashMap<>();
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        if(isValid(user, model, User.CreateGroup.class)) {
            user.setActive("Y");  //ToDo : setActive N and do authentication for activating user
            userService.create(user);
            model.put("success", true);
        }
        return new ModelAndView("redirect:/login");
    }

    @RequestMapping(value = {"/create"})
    public ModelAndView details(@PathVariable(value = "id", required = false) String id) {
        Map<String, Object> model = new HashMap<>();
        model.put("active", "USER");
        if (id == null) {
            model.put("mode", "CREATE");
            model.put("user", new User());
            model.put("breadcrumbs", new Breadcrumbs("User", "User", "/pim/login", "Create Website", ""));
        }
        return new ModelAndView("/register", model);
    }
    /**
     * Handler method to load the list users page
     *
     * @return The ModelAndView instance for the list users page
     */
    @RequestMapping()
    public ModelAndView all() {
        Map<String, Object> model = new HashMap<>();
        model.put("active", "USERS");
        return new ModelAndView("user/users", model);
    }

}
