package com.bigname.pim.client.web.controller;

import com.bigname.pim.api.domain.Event;
import com.bigname.pim.api.domain.User;
import com.bigname.pim.api.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by dona on 07-02-2019.
 */
@Controller
public class LoginController {

    @Autowired
    private EventService eventService;

    @RequestMapping(value = "/home")
    @ResponseBody
    public ModelAndView checkUserStatus(HttpServletRequest request) {
        Event event = new Event();
        String remoteAddr = "";
        Map<String, Object> model = new HashMap<>();
        String userStatus;
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof User) {
            userStatus = ((User)principal).getStatus();
        } else {
            userStatus = principal.toString();
        }
        if("pending".equals(userStatus)){
            return new ModelAndView("redirect:/login");
        }else{
            return new ModelAndView("redirect:/pim/dashboard");
        }

    }


 /*   @RequestMapping(value = "/login?logout")
    @ResponseBody
    public void checkLogOffUserStatus(HttpServletRequest request) {
        Event event = new Event();
        String remoteAddr = "";
        Map<String, Object> model = new HashMap<>();
        String userStatus;
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            remoteAddr = request.getRemoteAddr();
            if (remoteAddr.equalsIgnoreCase("0:0:0:0:0:0:0:1")) {
                InetAddress inetAddress = null;
                try {
                    inetAddress = InetAddress.getLocalHost();
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                }
                String ipAddress = inetAddress.getHostAddress();
                remoteAddr = ipAddress;
            }

            event.setEntity("Logout");
            event.setTimeStamp(LocalDateTime.now());
            event.setUser(((User)principal).getId());
            event.setEventType(Event.Type.LOGIN);
            event.setDetails(((User)principal).getUserName() + " logged out from " + remoteAddr);
            *//*Map<String, Object> dataObj = ConversionUtil.toJSONMap(_t);
            dataObj.put("lastModifiedDateTime", _t.getLastModifiedDateTime());
            dataObj.put("createdDateTime", _t.getCreatedDateTime());
            event.setData(dataObj);*//*
            eventService.create(event);

            //return new ModelAndView("redirect:/pim/dashboard");

    }*/
}
