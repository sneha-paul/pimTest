package com.bigname.pim.client.web.controller;

import com.bigname.pim.api.domain.User;
import com.bigname.pim.api.exception.EntityNotFoundException;
import com.bigname.pim.api.service.UserService;
import com.bigname.pim.client.model.Breadcrumbs;
import com.bigname.pim.util.FindBy;
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
import java.util.Optional;


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

    @RequestMapping(value = "/inside", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> createInside(User user) {
        Map<String, Object> model = new HashMap<>();
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        if(isValid(user, model, User.CreateGroup.class)) {
            user.setActive("Y");  //ToDo : setActive N and do authentication for activating user
            userService.create(user);
            model.put("success", true);
        }
        return model;
    }

    /**
     * Handler method to update a user instance
     *
     * @param id emailId of the user instance that needs to be updated
     * @param user The modified user instance corresponding to the given emailId
     *
     * @return a map of model attributes
     */

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    @ResponseBody
    public Map<String, Object> update(@PathVariable(value = "id") String id, User user) {
        Map<String, Object> model = new HashMap<>();
        if(isValid(user, model, user.getGroup().length == 1 && user.getGroup()[0].equals("DETAILS") ? User.DetailsGroup.class :  null)) {
            userService.update(id, FindBy.EXTERNAL_ID, user);
            model.put("success", true);
        }
        return model;
    }



    @RequestMapping(value = {"/{id}", "/create", "/create/{type}"})
    public ModelAndView details(@PathVariable(value = "id", required = false) String id, @PathVariable(value = "type", required = false) String type) {
        Map<String, Object> model = new HashMap<>();
        model.put("active", "USER");
        if (id == null &&  !"inside".equals(type)) {
            model.put("mode", "CREATE");
            model.put("user", new User());
            model.put("breadcrumbs", new Breadcrumbs("User", "User", "/pim/login", "Create User", ""));
            return new ModelAndView("/register", model);
        }else if (id == null && "inside".equals(type)){
            model.put("mode", "CREATE");
            model.put("user", new User());
            model.put("breadcrumbs", new Breadcrumbs("User", "User", "/pim/users", "Create User", ""));
            return new ModelAndView("user/user", model);
        }
        else{
            Optional<User> user = userService.get(id, FindBy.EXTERNAL_ID, false);
            if (user.isPresent()) {
                model.put("mode", "DETAILS");
                model.put("user", user.get());
                model.put("breadcrumbs", new Breadcrumbs("Users", "Users", "/pim/users", user.get().getUserName(), ""));
            } else {
                throw new EntityNotFoundException("Unable to find User with Id: " + id);
            }
            return new ModelAndView("user/user", model);
        }

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
