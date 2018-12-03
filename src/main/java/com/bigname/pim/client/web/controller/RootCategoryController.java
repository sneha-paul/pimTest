package com.bigname.pim.client.web.controller;

import com.bigname.pim.api.exception.EntityNotFoundException;
import com.bigname.pim.api.service.CatalogService;
import com.bigname.pim.api.service.CategoryService;
import com.bigname.pim.api.service.WebsiteService;
import com.bigname.pim.client.model.Breadcrumbs;
import com.bigname.pim.util.FindBy;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Manu V NarayanaPrasad (manu@blacwood.com)
 * @since 1.0
 */
@Controller
@RequestMapping("pim/websites/{websiteId}/catalogs/{catalogId}")
public class RootCategoryController {
    private WebsiteService websiteService;
    private CatalogService catalogService;
    private CategoryService categoryService;

    public RootCategoryController(WebsiteService websiteService, CatalogService catalogService, CategoryService categoryService){
        this.websiteService = websiteService;
        this.catalogService = catalogService;
        this.categoryService = categoryService;
    }

    @RequestMapping("/categories/{categoryId}")
    public ModelAndView details(@PathVariable(value = "catalogId", required = false) String catalogId,
                                @PathVariable(value = "websiteId", required = false) String websiteId,
                                @PathVariable(value = "categoryId", required = false) String categoryId) {
        Map<String, Object> model = new HashMap<>();
        model.put("active", "CATEGORIES");
        model.put("mode", "DETAILS");
        return websiteService.get(websiteId, FindBy.EXTERNAL_ID, false)
                .map(website -> catalogService.get(catalogId, FindBy.EXTERNAL_ID, false)
                        .map(catalog ->
                            categoryService.get(categoryId, FindBy.EXTERNAL_ID, false)
                                    .map(category -> {
                                        model.put("website", website);
                                        model.put("catalog", catalog);
                                        model.put("category", category);
                                        model.put("breadcrumbs", new Breadcrumbs("Catalogs",
                                                "Websites", "/pim/websites",
                                                website.getWebsiteName(), "/pim/websites/" + websiteId,
                                                "Catalogs", "/pim/websites/" + websiteId + "/catalogs",
                                                catalog.getCatalogName(), "/pim/websites/" + websiteId + "/catalogs/" + catalogId,
                                                "RootCategories", "/pim/websites/" + websiteId + "/catalogs/" + catalogId + "#rootCategories",
                                                category.getCategoryName(), ""));
                                        return new ModelAndView("category/category", model);
                                    }).orElseThrow(() -> new EntityNotFoundException("Unable to find Category with Id: " + categoryId))

                        ).orElseThrow(() -> new EntityNotFoundException("Unable to find Catalog with Id: " + catalogId)))
                .orElseThrow(() -> new EntityNotFoundException("Unable to find Website with Id: " + websiteId));
    }
}
