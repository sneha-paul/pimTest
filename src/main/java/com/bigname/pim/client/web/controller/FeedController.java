package com.bigname.pim.client.web.controller;

import com.bigname.pim.api.domain.AssetCollection;
import com.bigname.pim.api.domain.Attribute;
import com.bigname.pim.api.domain.AttributeCollection;
import com.bigname.pim.api.domain.VirtualFile;
import com.bigname.pim.data.loader.AssetLoader;
import com.bigname.pim.data.loader.CategoryLoader;
import com.bigname.pim.data.loader.ProductLoader;
import com.bigname.pim.data.loader.ProductLoader1;
import com.bigname.pim.util.FindBy;
import org.apache.commons.collections.map.AbstractHashedMap;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * @author Manu V NarayanaPrasad (manu@blacwood.com)
 * @since 1.0
 */
@Controller
@RequestMapping("pim/feeds")
public class FeedController {

    private CategoryLoader categoryLoader;
    private ProductLoader productLoader;
    private ProductLoader1 productLoader1;

    @Value("${loader.productFeed.path:/tmp/10_REGULAR.xlsx}")
    private String productFeedPath;

    @Value("${loader.categoryFeed.path:/tmp/CATEGORIES.xlsx}")
    private String categoryFeedPath;

    @Value("${loader.api.key:80blacwood85}")
    private String apiKey;

    public FeedController(CategoryLoader categoryLoader, ProductLoader productLoader, ProductLoader1 productLoader1) {
        this.categoryLoader = categoryLoader;
        this.productLoader = productLoader;
        this.productLoader1 = productLoader1;
    }

    @ResponseBody
    @RequestMapping(value = "/load/products", method = RequestMethod.GET)
    public Map<String, Object> loadProductData(@RequestParam(value = "apiKey", required = false) String apiKey, HttpServletRequest request) {
        Map<String, Object> model = new HashMap<>();
        boolean success = false;
        if(this.apiKey.equals(apiKey)) {
            productLoader.load(productFeedPath);
            success = true;
        }
        model.put("success", success);
        return model;
    }

    @ResponseBody
    @RequestMapping(value = "/load/products1", method = RequestMethod.GET)
    public Map<String, Object> loadProductData1(@RequestParam(value = "apiKey", required = false) String apiKey, HttpServletRequest request) {
        Map<String, Object> model = new HashMap<>();
        boolean success = false;
        if(this.apiKey.equals(apiKey)) {
            productLoader1.load(productFeedPath);
            success = true;
        }
        model.put("success", success);
        return model;
    }

    @ResponseBody
    @RequestMapping(value = "/load/categories", method = RequestMethod.GET)
    public Map<String, Object> loadCategoryData(@RequestParam(value = "apiKey", required = false) String apiKey, HttpServletRequest request) {
        Map<String, Object> model = new HashMap<>();
        boolean success = false;
        if(this.apiKey.equals(apiKey)) {
            categoryLoader.load(categoryFeedPath);
            success = true;
        }
        model.put("success", success);
        return model;
    }
}
