package com.bigname.pim.client.web.controller;

import com.bigname.common.datatable.model.Pagination;
import com.bigname.common.datatable.model.Request;
import com.bigname.common.datatable.model.Result;
import com.bigname.common.datatable.model.SortOrder;
import com.bigname.common.util.ValidationUtil2;
import com.bigname.core.domain.EntityAssociation;
import com.bigname.core.exception.EntityNotFoundException;
import com.bigname.core.util.FindBy;
import com.bigname.core.util.Toggle;
import com.bigname.core.web.controller.BaseController;
import com.bigname.pim.api.domain.Catalog;
import com.bigname.pim.api.domain.Category;
import com.bigname.pim.api.domain.RootCategory;
import com.bigname.pim.api.service.CatalogService;
import com.bigname.pim.api.service.WebsiteService;
import com.bigname.pim.data.exportor.CatalogExporter;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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

import static com.bigname.common.util.ValidationUtil2.isEmpty;

/**
 *
 * @author Manu V NarayanaPrasad (manu@blacwood.com)
 * @since 1.0
 */
@Controller
@RequestMapping("pim/catalogs")
public class CatalogController extends BaseController<Catalog, CatalogService> {

    private CatalogService catalogService;
    private WebsiteService websiteService;

    public CatalogController(CatalogService catalogService, @Lazy CatalogExporter catalogExporter, WebsiteService websiteService) {
        super(catalogService, Catalog.class, catalogExporter, websiteService);
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
    public Map<String, Object> update(@PathVariable(value = "id") String catalogId, Catalog catalog) {
        return update(catalogId, catalog, "/pim/catalogs/", catalog.getGroup().length == 1 && catalog.getGroup()[0].equals("DETAILS") ? Catalog.DetailsGroup.class : null);
    }


    @RequestMapping(value = {"/{id}", "/create"})
    public ModelAndView details(@PathVariable(value = "id", required = false) String id,
                                @RequestParam(name = "reload", required = false) boolean reload,
                                @RequestParam Map<String, Object> parameterMap,
                                HttpServletRequest request) {

        Map<String, Object> model = new HashMap<>();
        model.put("active", "CATALOGS");
        model.put("mode", id == null ? "CREATE" : "DETAILS");
        model.put("view", "catalog/catalog"  + (reload ? "_body" : ""));
        return id == null ? super.details(model) : catalogService.get(id, FindBy.findBy(true), false)
                .map(catalog -> {
                    model.put("catalog", catalog);
                    return super.details(id, parameterMap, request, model);
                }).orElseThrow(() -> new EntityNotFoundException("Unable to find Catalog with Id: " + id));
    }

    @RequestMapping()
    public ModelAndView all() {
        Map<String, Object> model = new HashMap<>();
        model.put("active", "CATALOGS");
        return new ModelAndView("catalog/catalogs", model);
    }


    @RequestMapping(value =  {"/list", "/data"})
    @ResponseBody
    @SuppressWarnings("unchecked")
    public Result<Map<String, String>> all(HttpServletRequest request, HttpServletResponse response, Model model) {
        Request dataTableRequest = new Request(request);
        if(isEmpty(dataTableRequest.getSearch())) {
            return super.all(request, response, model);
        } else {
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
            Page<Catalog> paginatedResult = catalogService.findAll("catalogName", dataTableRequest.getSearch(), PageRequest.of(pagination.getPageNumber(), pagination.getPageSize(), sort), false);
            paginatedResult.forEach(e -> dataObjects.add(e.toMap()));
            result.setDataObjects(dataObjects);
            result.setRecordsTotal(Long.toString(paginatedResult.getTotalElements()));
            result.setRecordsFiltered(Long.toString(paginatedResult.getTotalElements()));
            return result;
        }
    }

    @RequestMapping("/{id}/rootCategories/data")
    @ResponseBody
    public Result<Map<String, Object>> getRootCategories(@PathVariable(value = "id") String id, HttpServletRequest request) {
       // return getAssociationGridData(catalogService.getRootCategories(id, FindBy.EXTERNAL_ID, getPaginationRequest(request), false), RootCategory.class, request);

        Request dataTableRequest = new Request(request);
        if(isEmpty(dataTableRequest.getSearch())) {
            return getAssociationGridData(catalogService.getRootCategories(id, FindBy.EXTERNAL_ID, getPaginationRequest(request), false), RootCategory.class, request);
        } else {
            Pagination pagination = dataTableRequest.getPagination();
            Result<Map<String, Object>> result = new Result<>();
            result.setDraw(dataTableRequest.getDraw());
            Sort sort = null;
            if(pagination.hasSorts() && !dataTableRequest.getOrder().getName().equals("sequenceNum")) {
                sort = Sort.by(new Sort.Order(Sort.Direction.valueOf(SortOrder.fromValue(dataTableRequest.getOrder().getSortDir()).name()), dataTableRequest.getOrder().getName()));
            }
            List<Map<String, Object>> dataObjects = new ArrayList<>();
            int seq[] = {1};
            Page<Map<String, Object>> paginatedResult = catalogService.findAllRootCategories(id, FindBy.EXTERNAL_ID, "categoryName", dataTableRequest.getSearch(), PageRequest.of(pagination.getPageNumber(), pagination.getPageSize(), sort), false);
            EntityAssociation<Catalog, Category> association = new RootCategory();
            paginatedResult.getContent().forEach(e -> {
                e.put("sequenceNum", Integer.toString(seq[0] ++));
                dataObjects.add(association.toMap(e));
            });
            result.setDataObjects(dataObjects);
            result.setRecordsTotal(Long.toString(paginatedResult.getTotalElements()));
            result.setRecordsFiltered(Long.toString(paginatedResult.getContent().size()));
            return result;
        }
    }

    @RequestMapping(value = "/{id}/rootCategories/data", method = RequestMethod.PUT)
    @ResponseBody
    public Map<String, Object> setRootCategoriesSequence(@PathVariable(value = "id") String id, @RequestParam Map<String, String> parameterMap) {
        Map<String, Object> model = new HashMap<>();
        boolean success = catalogService.setRootCategorySequence(id, FindBy.EXTERNAL_ID, parameterMap.get("sourceId"), FindBy.EXTERNAL_ID, parameterMap.get("destinationId"), FindBy.EXTERNAL_ID);
        model.put("success", success);
        return model;
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
        Page<Category> paginatedResult = ValidationUtil2.isEmpty(dataTableRequest.getSearch()) ? catalogService.getAvailableRootCategoriesForCatalog(id, FindBy.EXTERNAL_ID, pagination.getPageNumber(), pagination.getPageSize(), sort, false)
                : catalogService.findAvailableRootCategoriesForCatalog(id, FindBy.EXTERNAL_ID, "categoryName", dataTableRequest.getSearch(), PageRequest.of(pagination.getPageNumber(), pagination.getPageSize(), sort), false);
        paginatedResult.getContent().forEach(e -> dataObjects.add(e.toMap()));
        result.setDataObjects(dataObjects);
        result.setRecordsTotal(Long.toString(paginatedResult.getTotalElements()));
        result.setRecordsFiltered(Long.toString(paginatedResult.getTotalElements()));
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

    @RequestMapping(value = "/{id}/rootCategories/{rootCategoryId}/active/{active}", method = RequestMethod.PUT)
    @ResponseBody
    public Map<String, Object> toggleRootCategory(@PathVariable(value = "id") String catalogId,
                                             @PathVariable(value = "rootCategoryId") String rootCategoryId,
                                             @PathVariable(value = "active") String active) {
        Map<String, Object> model = new HashMap<>();
        model.put("success", catalogService.toggleRootCategory(catalogId, FindBy.EXTERNAL_ID, rootCategoryId, FindBy.EXTERNAL_ID, Toggle.get(active)));
        return model;
    }

    @RequestMapping("/{id}/hierarchy")
    @ResponseBody
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> getCategoriesHierarchy(@PathVariable(value = "id") String id) {
        return catalogService.getCategoryHierarchy(id);
    }
}
