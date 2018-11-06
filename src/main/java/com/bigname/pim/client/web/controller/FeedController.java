package com.bigname.pim.client.web.controller;

import com.bigname.pim.data.loader.ProductLoader;
import org.apache.commons.collections.map.AbstractHashedMap;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Manu V NarayanaPrasad (manu@blacwood.com)
 * @since 1.0
 */
@Controller
@RequestMapping("pim/feeds")
public class FeedController {

    private ProductLoader productLoader;

    public FeedController(ProductLoader productLoader) {
        this.productLoader = productLoader;
    }

    @RequestMapping(value = "load", method = RequestMethod.POST)
    public Map<String, Object> loadData(HttpServletRequest request) {
        Map<String, Object> model = new HashMap<>();
        productLoader.load("C:\\DevStudio\\Projects\\Bigname\\PIM\\src\\main\\resources\\feed\\10_REGULAR.xlsx");
        return model;
    }
}
