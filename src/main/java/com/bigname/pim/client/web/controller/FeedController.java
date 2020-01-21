package com.bigname.pim.client.web.controller;

import com.bigname.pim.core.data.loader.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

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

    private static final Logger LOGGER = LoggerFactory.getLogger(FeedController.class);

    private CatalogLoader catalogLoader;
    private CategoryLoader categoryLoader;
    private ProductLoader productLoader;
    private ProductLoader1 productLoader1;
    private WebsiteLoader websiteLoader;
    private  CategoryLoader1 categoryLoader1;

    @Value("${loader.catalogFeed.path:/DevStudio/Docs/PIM_ExcelFiles/catalogData.xlsx}")
    private String catalogFeedPath;

    @Value("${loader.productFeed.path:/tmp/10_REGULAR.xlsx}")
    private String productFeedPath;

    // @Value("${loader.websiteFeed.path:/DevStudio/Docs/PIM_ExcelFiles/CategoryData.xlsx}")
    @Value("${loader.categoryFeed.path:/tmp/CATEGORIES.xlsx}")
    private String categoryFeedPath;


    @Value("${loader.websiteFeed.path:/DevStudio/Docs/PIM_ExcelFiles/WebsiteData.xlsx}")
    private String websiteFeedPath;


    @Value("${loader.api.key:80blacwood85}")
    private String apiKey;

    public FeedController(CatalogLoader catalogLoader, CategoryLoader categoryLoader, ProductLoader productLoader, ProductLoader1 productLoader1, WebsiteLoader websiteLoader, CategoryLoader1 categoryLoader1) {
        this.catalogLoader = catalogLoader;
        this.categoryLoader = categoryLoader;
        this.productLoader = productLoader;
        this.productLoader1 = productLoader1;
        this.websiteLoader = websiteLoader;
        this.categoryLoader1 = categoryLoader1;
    }

    @ResponseBody
    @RequestMapping(value = "/load/catalogs", method = RequestMethod.GET)
    public Map<String, Object> loadCatalogData(@RequestParam(value = "apiKey", required = false) String apiKey, HttpServletRequest request) {
        LOGGER.info("LOADING CATALOG DATA");
        Map<String, Object> model = new HashMap<>();
        boolean success = false;
        if(this.apiKey.equals(apiKey)) {
            catalogLoader.load(catalogFeedPath);
            success = true;
        }
        model.put("success", success);
        return model;
    }

    @ResponseBody
    @RequestMapping(value = "/load/products", method = RequestMethod.GET)
    public Map<String, Object> loadProductData(@RequestParam(value = "apiKey", required = false) String apiKey, HttpServletRequest request) {
        LOGGER.info("LOADING PRODUCT DATA ");
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
        LOGGER.info("LOADING PRODUCT1 DATA");
        Map<String, Object> model = new HashMap<>();
        boolean success = false;
        if(this.apiKey.equals(apiKey)) {
//            productLoader1.load(productFeedPath);
            success = true;
        }
        model.put("success", success);
        return model;
    }
//Use this
    @ResponseBody
    @RequestMapping(value = "/load/products2", method = RequestMethod.GET)
    public Map<String, Object> loadProductData12(@RequestParam(value = "apiKey", required = false) String apiKey, HttpServletRequest request) {
        LOGGER.info("LOADING PRODUCT11 DATA");
        Map<String, Object> model = new HashMap<>();
        boolean success = false;
        if(this.apiKey.equals(apiKey)) {
            productLoader1.load1(productFeedPath); //TODO - change after testing
            success = true;
        }
        model.put("success", success);
        return model;
    }
//Use this
    @ResponseBody
    @RequestMapping(value = "/load/categories", method = RequestMethod.GET)
    public Map<String, Object> loadCategoryData(@RequestParam(value = "apiKey", required = false) String apiKey, HttpServletRequest request) {
        LOGGER.info("LOADING CATEGORY DATA");
        Map<String, Object> model = new HashMap<>();
        boolean success = false;
        if(this.apiKey.equals(apiKey)) {
            categoryLoader.load(categoryFeedPath);
            success = true;
        }
        model.put("success", success);
        return model;
    }


    @ResponseBody
    @RequestMapping(value = "/load/categories1", method = RequestMethod.GET)
    public Map<String, Object> loadCategoryData1(@RequestParam(value = "apiKey", required = false) String apiKey, HttpServletRequest request) {
        LOGGER.info("LOADING CATEGORY DATA1");
        Map<String, Object> model = new HashMap<>();
        boolean success = false;
        if(this.apiKey.equals(apiKey)) {
            categoryLoader1.load(categoryFeedPath);
            success = true;
        }
        model.put("success", success);
        return model;
    }

    @ResponseBody
    @RequestMapping(value = "/load/websites", method = RequestMethod.GET)
    public Map<String, Object> loadWebsiteData(@RequestParam(value = "apiKey", required = false) String apiKey, HttpServletRequest request) {
        Map<String, Object> model = new HashMap<>();
        boolean success = false;
        if(this.apiKey.equals(apiKey)) {
            websiteLoader.load(websiteFeedPath);
            success = true;
        }
        model.put("success", success);
        return model;
    }


}
