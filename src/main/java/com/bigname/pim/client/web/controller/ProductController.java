package com.bigname.pim.client.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 * Created by Manu on 8/3/2018.
 */
@Controller
@RequestMapping("pim/products")
public class ProductController {

//    @Value("${welcome.message:test}")
//    private String message = "Hello World";

    @RequestMapping()
    public ModelAndView listAll() {
        return new ModelAndView("product/products");
    }
}
