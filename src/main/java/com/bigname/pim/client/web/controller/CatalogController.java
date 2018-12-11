package com.bigname.pim.client.web.controller;

import com.bigname.common.datatable.model.Pagination;
import com.bigname.common.datatable.model.Request;
import com.bigname.common.datatable.model.Result;
import com.bigname.common.datatable.model.SortOrder;
import com.bigname.pim.api.domain.Catalog;
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
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Manu V NarayanaPrasad (manu@blacwood.com)
 * @since 1.0
 */
@Controller
@RequestMapping("pim/catalogs")
public class CatalogController extends BaseController<Catalog, CatalogService>{

    private CatalogService catalogService;
    private WebsiteService websiteService;

    public CatalogController(CatalogService catalogService, WebsiteService websiteService) {
        super(catalogService);
        this.catalogService = catalogService;
        this.websiteService = websiteService;
    }


    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> create( Catalog catalog) {
        Map<String, Object> model = new HashMap<>();
        if(isValid(catalog, model, Catalog.CreateGroup.class)) {
            catalog.setActive("N");
            catalog.setDiscontinued("N");
            catalogService.create(catalog);
            model.put("success", true);
        }
        return model;
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    @ResponseBody
    public Map<String, Object> update(@PathVariable(value = "id") String id, Catalog catalog) {
        Map<String, Object> model = new HashMap<>();
        if(isValid(catalog, model, catalog.getGroup().length == 1 && catalog.getGroup()[0].equals("DETAILS") ? Catalog.DetailsGroup.class : null)) {
            catalogService.update(id, FindBy.EXTERNAL_ID, catalog);
            model.put("success", true);
        }
        return model;
    }

    @RequestMapping(value = {"/{id}", "/create"})
    public ModelAndView details(@PathVariable(value = "id", required = false) String id,
                                @RequestParam Map<String, Object> parameterMap,
                                HttpServletRequest request) {
        Map<String, Object> model = new HashMap<>();
        String websiteId = parameterMap.containsKey("websiteId") ? (String) parameterMap.get("websiteId") : "";
        String hash = parameterMap.containsKey("hash") ? (String) parameterMap.get("hash") : "";
        String defaultURL = "/pim/catalogs";
        if(!websiteId.isEmpty()) {
            defaultURL = "/pim/websites/" + websiteId + "#" + hash;
        }
        String referrer = getReferrerURL(request, defaultURL, "");
        model.put("active", "CATALOGS");
        if(id == null) {
            model.put("mode", "CREATE");
            model.put("catalog", new Catalog());
            model.put("breadcrumbs", new Breadcrumbs("Catalogs", "Catalogs", referrer, "Create Catalog", ""));
            return new ModelAndView("catalog/catalog", model);
        } else {
            return catalogService.get(id, FindBy.findBy(true), false)
                 .map(catalog -> {

                     model.put("mode", "DETAILS");
                     model.put("catalog", catalog);
                     model.put("backURL", referrer);

                     Breadcrumbs breadcrumbs = new Breadcrumbs("Catalogs");
                     if(!websiteId.isEmpty()) {
                         breadcrumbs.addCrumbs("Websites", "/pim/websites");
                         websiteService.get(websiteId, FindBy.EXTERNAL_ID, false)
                                 .ifPresent(website -> breadcrumbs.addCrumbs(website.getWebsiteName(), "/pim/websites/" + website.getWebsiteId()));
                     }
                     breadcrumbs.addCrumbs("Catalogs", referrer);
                     breadcrumbs.addCrumbs(catalog.getCatalogName(), "");
                     model.put("breadcrumbs", breadcrumbs);

                     return new ModelAndView("catalog/catalog", model);
                 }).orElseThrow(() -> new EntityNotFoundException("Unable to find Catalog with Id: " + id));
        }
    }

    @RequestMapping()
    public ModelAndView all() {
        Map<String, Object> model = new HashMap<>();
        model.put("active", "CATALOGS");
        return new ModelAndView("catalog/catalogs", model);
    }

    @RequestMapping("/{id}/rootCategories")
    @ResponseBody
    public Result<Map<String, String>> getRootCategories(@PathVariable(value = "id") String id, HttpServletRequest request, HttpServletResponse response, Model model) {
        Request dataTableRequest = new Request(request);
        Pagination pagination = dataTableRequest.getPagination();
        Result<Map<String, String>> result = new Result<>();
        result.setDraw(dataTableRequest.getDraw());
        Sort sort = null;
        if(pagination.hasSorts()) {
            sort = Sort.by(new Sort.Order(Sort.Direction.valueOf(SortOrder.fromValue(dataTableRequest.getOrder().getSortDir()).name()), dataTableRequest.getOrder().getName()));
        }
        List<Map<String, String>> dataObjects = new ArrayList<>();
        Page<RootCategory> paginatedResult = catalogService.getRootCategories(id, FindBy.EXTERNAL_ID, pagination.getPageNumber(), pagination.getPageSize(), sort, false);
        paginatedResult.getContent().forEach(e -> dataObjects.add(e.toMap()));
        result.setDataObjects(dataObjects);
        result.setRecordsTotal(Long.toString(paginatedResult.getTotalElements()));
        result.setRecordsFiltered(Long.toString(pagination.hasFilters() ? paginatedResult.getContent().size() : paginatedResult.getTotalElements())); //TODO - verify this logic
        return result;
    }


    @RequestMapping(value = "/{id}/rootCategories/available")
    public ModelAndView availableCategories(@PathVariable(value = "id") String id) {
        Map<String, Object> model = new HashMap<>();
        return new ModelAndView("category/availableRootCategories", model);
    }

    @RequestMapping("/{id}/rootCategories/available/list")
    @ResponseBody
    public Result<Map<String, String>> getAvailableRootCategories(@PathVariable(value = "id") String id, HttpServletRequest request, HttpServletResponse response, Model model) {
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
        Page<Category> paginatedResult = catalogService.getAvailableRootCategoriesForCatalog(id, FindBy.EXTERNAL_ID, pagination.getPageNumber(), pagination.getPageSize(), sort);
        paginatedResult.getContent().forEach(e -> dataObjects.add(e.toMap()));
        result.setDataObjects(dataObjects);
        result.setRecordsTotal(Long.toString(paginatedResult.getTotalElements()));
        result.setRecordsFiltered(Long.toString(pagination.hasFilters() ? paginatedResult.getContent().size() : paginatedResult.getTotalElements())); //TODO - verify this logic
        return result;
    }

    @ResponseBody
    @RequestMapping(value = "/{id}/rootCategories/{rootCategoryId}", method = RequestMethod.POST)
    public Map<String, Object> addRootCategory(@PathVariable(value = "id") String id, @PathVariable(value = "rootCategoryId") String rootCategoryId) {
        Map<String, Object> model = new HashMap<>();
        boolean success = catalogService.addRootCategory(id, FindBy.EXTERNAL_ID, rootCategoryId, FindBy.EXTERNAL_ID) != null;
        model.put("success", success);
        return model;
    }
}
