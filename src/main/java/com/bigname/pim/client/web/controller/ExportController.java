package com.bigname.pim.client.web.controller;

import com.bigname.pim.data.loader.exporter.CatalogExporter;
import com.bigname.pim.data.loader.exporter.CategoryExporter;
import com.bigname.pim.data.loader.exporter.WebsiteExporter;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by sruthi on 25-01-2019.
 */
@Controller
@RequestMapping("pim/export")
public class ExportController {
    private CatalogExporter catalogExporter;
    private WebsiteExporter websiteExporter;
    private CategoryExporter categoryExporter;

    public ExportController( CatalogExporter catalogExporter, WebsiteExporter websiteExporter,CategoryExporter categoryExporter) {
        this.catalogExporter = catalogExporter;
        this.websiteExporter = websiteExporter;
        this.categoryExporter=categoryExporter;
    }

    @RequestMapping(value = "/catalogs" , method = RequestMethod.GET)
    public Map<String, Object> exportCatalogData(HttpServletRequest request) {
        Map<String, Object> model =new HashMap<>();
        catalogExporter.exportData(  "/DevStudio/Docs/PIM_ExcelFiles/catalogData.xlsx");

        return model;
    }

    @RequestMapping(value = "/websites", method = RequestMethod.GET)
    public Map<String, Object> exportWebsiteData(HttpServletRequest request) {
        Map<String, Object> model = new HashMap<>();
        websiteExporter.exportData("/DevStudio/Docs/PIM_ExcelFiles/WebsiteData.xlsx");
        return model;
    }
    @RequestMapping(value = "/categories", method = RequestMethod.GET)
    public Map<String, Object> exportCategoryData(HttpServletRequest request) {
        Map<String, Object> model = new HashMap<>();
        categoryExporter.exportData("/DevStudio/Docs/PIM_ExcelFiles/CategoryData.xlsx");
        return model;
    }
}
