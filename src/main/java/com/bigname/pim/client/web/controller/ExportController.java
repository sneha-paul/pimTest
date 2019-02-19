package com.bigname.pim.client.web.controller;

import com.bigname.pim.api.service.ProductVariantService;
import com.bigname.pim.data.exportor.AttributeCollectionExporter;
import com.bigname.pim.data.exportor.CatalogExporter;
import com.bigname.pim.data.exportor.CategoryExporter;
import com.bigname.pim.data.exportor.ProductExporter;
import com.bigname.pim.data.loader.exporter.WebsiteExporter;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

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
    private ProductExporter productExporter;
    private ProductVariantService productVariantService;
    private AttributeCollectionExporter attributeCollectionExporter;

    public ExportController( CatalogExporter catalogExporter, WebsiteExporter websiteExporter,CategoryExporter categoryExporter, ProductExporter productExporter, ProductVariantService productVariantService, AttributeCollectionExporter attributeCollectionExporter) {
        this.catalogExporter = catalogExporter;
        this.websiteExporter = websiteExporter;
        this.categoryExporter = categoryExporter;
        this.productExporter = productExporter;
        this.productVariantService = productVariantService;
        this.attributeCollectionExporter = attributeCollectionExporter;
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

    @RequestMapping(value = "/productsData", method = RequestMethod.GET)
    public Map<String, Object> exportProductData(HttpServletRequest request) {
        Map<String, Object> model = new HashMap<>();
        productExporter.exportData("/DevStudio/Docs/PIM_ExcelFiles/ProductData.xlsx");
        return model;
    }

    @RequestMapping(value = "/parentProductsDataFeed", method = RequestMethod.GET)
    public Map<String, Object> exportProductsData(HttpServletRequest request) {
        Map<String, Object> model = new HashMap<>();
        productExporter.exportProductsData("/DevStudio/Docs/PIM_ExcelFiles/ParentProductsData.xlsx");

        return model;
    }

    @RequestMapping(value = "/childProductsDataFeed", method = RequestMethod.GET)
    public Map<String, Object> productVariantsDataFeed(HttpServletRequest request) {
        Map<String, Object> model = new HashMap<>();
        productExporter.exportChildProductsData("/DevStudio/Docs/PIM_ExcelFiles/ChildProductsData.xlsx");
        return model;
    }

    @ResponseBody
    @RequestMapping(value = "/productData", method = RequestMethod.GET)
    public Map<String, Object> exportProductJsonData(HttpServletRequest request) {
        Map<String, Object> model = new HashMap<>();
        productExporter.exportJsonData("/DevStudio/Docs/PIM_ExcelFiles/ProductData.json");
        return model;
    }

    @ResponseBody
    @RequestMapping(value = "/{id}/attributeCollectionData", method = RequestMethod.GET)
    public Map<String, Object> exportAttributeCollectionData(@PathVariable(value = "id") String id, HttpServletRequest request) {
        Map<String, Object> model = new HashMap<>();
        attributeCollectionExporter.exportAttributeData("/DevStudio/Docs/PIM_ExcelFiles/AttributeCollectionData.xlsx", id);
        return model;
    }

    @ResponseBody
    @RequestMapping(value = "/{id}/attributesOptionData", method = RequestMethod.GET)
    public Map<String, Object> exportAttributeCollectionDataOption(@PathVariable(value = "id") String id,  HttpServletRequest request) {
        Map<String, Object> model = new HashMap<>();
        attributeCollectionExporter.exportAttributesOptionData("/DevStudio/Docs/PIM_ExcelFiles/AttributeCollectionDataOption.xlsx", id);
        return model;
    }
}
