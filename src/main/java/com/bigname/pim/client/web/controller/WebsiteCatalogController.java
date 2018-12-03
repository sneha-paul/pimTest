package com.bigname.pim.client.web.controller;

import com.bigname.common.datatable.model.Pagination;
import com.bigname.common.datatable.model.Request;
import com.bigname.common.datatable.model.Result;
import com.bigname.common.datatable.model.SortOrder;
import com.bigname.pim.api.domain.Category;
import com.bigname.pim.api.domain.RootCategory;
import com.bigname.pim.api.exception.EntityNotFoundException;
import com.bigname.pim.api.service.CatalogService;
import com.bigname.pim.api.service.WebsiteService;
import com.bigname.pim.client.model.Breadcrumbs;
import com.bigname.pim.util.FindBy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Manu V NarayanaPrasad (manu@blacwood.com)
 * @since 1.0
 */
@Controller
@RequestMapping("pim/websites/{websiteId}")
public class WebsiteCatalogController {
    private WebsiteService websiteService;
    private CatalogService catalogService;

    public WebsiteCatalogController(WebsiteService websiteService, CatalogService catalogService){
        this.websiteService = websiteService;
        this.catalogService = catalogService;
    }

    @RequestMapping("/catalogs/{catalogId}")
    public ModelAndView details(@PathVariable(value = "catalogId", required = false) String catalogId,
                                @PathVariable(value = "websiteId", required = false) String websiteId) {
        Map<String, Object> model = new HashMap<>();
        model.put("active", "CATALOGS");
        model.put("mode", "DETAILS");
        return websiteService.get(websiteId, FindBy.EXTERNAL_ID, false)
                .map(website -> catalogService.get(catalogId, FindBy.EXTERNAL_ID, false)
                        .map(catalog -> {
                            model.put("website", website);
                            model.put("catalog", catalog);
                            model.put("breadcrumbs", new Breadcrumbs("Catalogs", "Websites", "/pim/websites", website.getWebsiteName(), "/pim/websites/" + websiteId, "Catalogs", "/pim/websites/" + websiteId + "#catalogs", catalog.getCatalogName(), ""));
                            return new ModelAndView("catalog/catalog", model);
                        }).orElseThrow(() -> new EntityNotFoundException("Unable to find Catalog with Id: " + catalogId)))
                .orElseThrow(() -> new EntityNotFoundException("Unable to find Website with Id: " + websiteId));
    }

    @RequestMapping("/catalogs/{catalogId}/rootCategories")
    @ResponseBody
    public Result<Map<String, String>> getRootCategories(@PathVariable(value = "websiteId") String websiteId,
                                                         @PathVariable(value = "catalogId") String catalogId,
                                                         HttpServletRequest request) {
        return websiteService.getWebsiteCatalog(websiteId, FindBy.EXTERNAL_ID, catalogId, FindBy.EXTERNAL_ID)
                .map(websiteCatalog -> {
                    Result<Map<String, String>> result = new Result<>();
                    Request dataTableRequest = new Request(request);
                    Pagination pagination = dataTableRequest.getPagination();
                    result.setDraw(dataTableRequest.getDraw());
                    Sort sort = null;
                    if(pagination.hasSorts()) {
                        sort = Sort.by(new Sort.Order(Sort.Direction.valueOf(SortOrder.fromValue(dataTableRequest.getOrder().getSortDir()).name()), dataTableRequest.getOrder().getName()));
                    }
                    List<Map<String, String>> dataObjects = new ArrayList<>();

                    Page<RootCategory> paginatedResult = catalogService.getRootCategories(websiteCatalog.getId(), pagination.getPageNumber(), pagination.getPageSize(), sort, false);
                    paginatedResult.getContent().forEach(e -> dataObjects.add(e.toMap()));
                    result.setDataObjects(dataObjects);
                    result.setRecordsTotal(Long.toString(paginatedResult.getTotalElements()));
                    result.setRecordsFiltered(Long.toString(pagination.hasFilters() ? paginatedResult.getContent().size() : paginatedResult.getTotalElements())); //TODO - verify this logic
                    return result;
                })
                .orElse(new Result<>());

    }


    @RequestMapping(value = "/catalogs/{catalogId}/rootCategories/available")
    public ModelAndView availableRootCategories() {
        return new ModelAndView("category/availableRootCategories", new HashMap<>());
    }

    @RequestMapping("/catalogs/{catalogId}/rootCategories/available/list")
    @ResponseBody
    public Result<Map<String, String>> getAvailableRootCategories(@PathVariable(value = "websiteId") String websiteId,
                                                                  @PathVariable(value = "catalogId") String catalogId,
                                                                  HttpServletRequest request) {
        return websiteService.getWebsiteCatalog(websiteId, FindBy.EXTERNAL_ID, catalogId, FindBy.EXTERNAL_ID)
                .map(websiteCatalog -> {
                    Request dataTableRequest = new Request(request);
                    Pagination pagination = dataTableRequest.getPagination();
                    Result<Map<String, String>> result = new Result<>();
                    result.setDraw(dataTableRequest.getDraw());
                    Sort sort;
                    if(pagination.hasSorts()) {
                        sort = Sort.by(new Sort.Order(Sort.Direction.valueOf(SortOrder.fromValue(dataTableRequest.getOrder().getSortDir()).name()), dataTableRequest.getOrder().getName()));
                    } else {
                        sort = Sort.by(new Sort.Order(Sort.Direction.ASC, "externalId"));
                    }
                    List<Map<String, String>> dataObjects = new ArrayList<>();
                    Page<Category> paginatedResult = catalogService.getAvailableRootCategoriesForCatalog(websiteCatalog.getId(), pagination.getPageNumber(), pagination.getPageSize(), sort);
                    paginatedResult.getContent().forEach(e -> dataObjects.add(e.toMap()));
                    result.setDataObjects(dataObjects);
                    result.setRecordsTotal(Long.toString(paginatedResult.getTotalElements()));
                    result.setRecordsFiltered(Long.toString(pagination.hasFilters() ? paginatedResult.getContent().size() : paginatedResult.getTotalElements())); //TODO - verify this logic
                    return result;
                })
                .orElse(new Result<>());


    }

    @ResponseBody
    @RequestMapping(value = "/catalogs/{catalogId}/rootCategories/{rootCategoryId}", method = RequestMethod.POST)
    public Map<String, Object> addRootCategory(@PathVariable(value = "websiteId") String websiteId,
                                               @PathVariable(value = "catalogId") String catalogId,
                                               @PathVariable(value = "rootCategoryId") String rootCategoryId) {
        Map<String, Object> model = new HashMap<>();
        boolean success = catalogService.addRootCategory(websiteId, FindBy.EXTERNAL_ID, catalogId, FindBy.EXTERNAL_ID, rootCategoryId, FindBy.EXTERNAL_ID) != null;
        model.put("success", success);
        return model;
    }
}
