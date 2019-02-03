package com.bigname.pim.client.web.controller;

import com.bigname.core.util.FindBy;
import com.bigname.core.web.controller.BaseController;
import com.bigname.pim.api.domain.User;
import com.bigname.pim.api.domain.VerificationToken;
import com.bigname.pim.api.persistence.dao.VerificationTokenDAO;
import com.bigname.pim.api.service.RegistrationService;
import com.bigname.pim.api.service.UserService;
import com.bigname.pim.client.model.Breadcrumbs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


/**
 * Created by sruthi on 05-11-2018.
 */
@Controller
@RequestMapping("pim/user")
public class RegistrationController extends BaseController<User,UserService> {

    private static final Logger LOGGER = LoggerFactory.getLogger(RegistrationController.class);

    private UserService userService;
    private RegistrationService registrationService;

    private VerificationTokenDAO verificationTokenDAO;

    @Autowired
    public PasswordEncoder passwordEncoder;

    public RegistrationController(UserService userService, RegistrationService registrationService, VerificationTokenDAO verificationTokenDAO) {
        super(userService);
        this.userService = userService;
        this.verificationTokenDAO = verificationTokenDAO;
        this.registrationService = registrationService;
    }

    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    public ModelAndView create(User user, final HttpServletRequest request) {
        Map<String, Object> model = new HashMap<>();
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        if (isValid(user, model, User.CreateGroup.class)) {
            user.setStatus("pending");
            user.setActive("N");
            userService.create(user);

            final String token = UUID.randomUUID().toString();
            final VerificationToken myToken  = new VerificationToken(token,user);
            verificationTokenDAO.save(myToken);

            final String appUrl = "http://" + request.getServerName() + ":" + request.getServerPort() + request.getServletPath() ;

            String confirmationUrl = appUrl + "/registrationConfirm?token=" + token;

            LOGGER.info("Confirmation Url : " + confirmationUrl);

            String recipientAddress = user.getEmail();
            String subject = "Registration Confirmation";
            String message = "Hi "+  user.getUserName() + ",  Please login to the following link to confirm your registration : " + confirmationUrl;

            registrationService.sendVerificationEmail(subject, recipientAddress, message);

            model.put("success", true);
        }
        return new ModelAndView("redirect:/login");
    }


    @RequestMapping(value = {"/create"})
    public ModelAndView details(@PathVariable(value = "id", required = false) String id) {
        Map<String, Object> model = new HashMap<>();
        model.put("active", "USER");
        if (id == null ) {
            model.put("mode", "CREATE");
            model.put("user", new User());
            model.put("breadcrumbs", new Breadcrumbs("User", "User", "/pim/login", "Create User", ""));

        }
        return new ModelAndView("/register", model);

    }
    @RequestMapping(value = {"/registrationConfirm"}, method = RequestMethod.GET)
    public ModelAndView confirmRegistration(final Model model, @RequestParam("token") final String token, final RedirectAttributes redirectAttributes){
        final VerificationToken verificationToken = verificationTokenDAO.findByToken(token);
        final User user = verificationToken.getUser();
        user.setActive("Y");
        user.setGroup("DETAILS");
        user.setStatus("Active");
        userService.update(user.getEmail(), FindBy.EXTERNAL_ID,user);
        redirectAttributes.addFlashAttribute("message" , "Your Account Verified Successfully");
        return new ModelAndView("redirect:/pim/dashboard");
    }
    
}
