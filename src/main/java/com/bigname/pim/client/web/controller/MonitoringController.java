package com.bigname.pim.client.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Manu V NarayanaPrasad (manu@blacwood.com)
 * @since 1.0
 */
@Controller
@RequestMapping("pim")
public class MonitoringController {
    @RequestMapping("/healthCheck")
    @ResponseBody
    public Map<String, Object> healthCheck(HttpServletRequest request) {
        Map<String, Object> model = new HashMap<>();
        model.put("success", true);
        return model;
    }
}
