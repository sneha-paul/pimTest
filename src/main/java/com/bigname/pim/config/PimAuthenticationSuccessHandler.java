package com.bigname.pim.config;

import com.m7.xtreme.xplatform.domain.Event;
import com.m7.xtreme.xplatform.domain.User;
import com.m7.xtreme.xplatform.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.LocalDateTime;

/**
 * Created by dona on 08-02-2019.
 */

@Component
public class PimAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Autowired
    private EventService eventService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        if ("pending".equals(((User) authentication.getPrincipal()).getStatus())) {
            response.sendRedirect("/login");
        } else {
            Event event = new Event();
            String remoteAddr = "";
            response.setStatus(HttpServletResponse.SC_OK);
            remoteAddr = request.getRemoteAddr();
            if (remoteAddr.equalsIgnoreCase("0:0:0:0:0:0:0:1")) {
                InetAddress inetAddress = null;
                try {
                    inetAddress = InetAddress.getLocalHost();
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                }
                remoteAddr = inetAddress != null ? inetAddress.getHostAddress() : "0.0.0.0";
            }
            event.setEntity("Login");
            event.setTimeStamp(LocalDateTime.now());
            event.setUser(((User) authentication.getPrincipal()).getId());
            event.setEventType(Event.Type.LOGIN);
            event.setDetails((((User) authentication.getPrincipal()).getUserName()) + " logged in from " + remoteAddr);
            eventService.create(event);
            response.sendRedirect("/");
        }
    }

}
