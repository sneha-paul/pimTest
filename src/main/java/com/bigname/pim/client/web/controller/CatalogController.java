package com.bigname.pim.client.web.controller;

import com.bigname.pim.api.domain.Catalog;
import com.bigname.pim.api.domain.RootCategory;
import com.bigname.pim.api.domain.WebsiteCatalog;
import com.bigname.pim.api.service.CatalogService;
import com.bigname.pim.api.service.WebsiteService;
import com.bigname.pim.data.exportor.CatalogExporter;
import com.m7.common.datatable.model.Request;
import com.m7.common.datatable.model.Result;
import com.m7.xcore.exception.EntityNotFoundException;
import com.m7.xcore.util.FindBy;
import com.m7.xcore.util.Toggle;
import com.m7.xcore.web.controller.BaseController;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.m7.common.util.ValidationUtil.isEmpty;

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
        //updating websiteCatalog
        Catalog catalog1 = catalogService.get(catalogId, FindBy.EXTERNAL_ID, false).orElse(null);
        List<WebsiteCatalog> websiteCatalogs = catalogService.getAllWebsiteCatalogsWithCatalogId(catalog1.getId());
        websiteCatalogs.forEach(websiteCatalog -> {
            websiteCatalog.setActive(catalog.getActive());
            catalogService.updateWebsiteCatalog(websiteCatalog);
        });
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
        model.put("view", "catalog/catalogs");
        model.put("title", "Catalogs");
        return all(model);
    }


    @RequestMapping(value =  {"/list", "/data"})
    @ResponseBody
    @SuppressWarnings("unchecked")
    public Result<Map<String, String>> all(HttpServletRequest request) {
        return all(request, "catalogName");
    }

    @RequestMapping("/{id}/rootCategories/data")
    @ResponseBody
    public Result<Map<String, Object>> getRootCategories(@PathVariable(value = "id") String id, HttpServletRequest request) {
        return getAssociationGridData(request,
                RootCategory.class,
                dataTableRequest -> {
                    if(isEmpty(dataTableRequest.getSearch())) {
                        return catalogService.getRootCategories(id, FindBy.EXTERNAL_ID, dataTableRequest.getPageRequest(associationSortPredicate), false);
                    } else {
                        return catalogService.findAllRootCategories(id, FindBy.EXTERNAL_ID, "categoryName", dataTableRequest.getSearch(), dataTableRequest.getPageRequest(associationSortPredicate), false);
                    }
                });
    }

    @RequestMapping(value = "/{id}/rootCategories/available")
    public ModelAndView availableCategories(@PathVariable(value = "id") String id) {
        Map<String, Object> model = new HashMap<>();
        return new ModelAndView("category/availableRootCategories", model);
    }

    @RequestMapping("/{id}/rootCategories/available/list")
    @ResponseBody
    public Result<Map<String, String>> getAvailableRootCategories(@PathVariable(value = "id") String id, HttpServletRequest request) {
        return new Result<Map<String, String>>().buildResult(new Request(request),
                dataTableRequest -> {
                    PageRequest pageRequest = dataTableRequest.getPageRequest(defaultSort);
                    if(isEmpty(dataTableRequest.getSearch())) {
                        return catalogService.getAvailableRootCategoriesForCatalog(id, FindBy.EXTERNAL_ID, pageRequest.getPageNumber(), pageRequest.getPageSize(), pageRequest.getSort(), false);
                    } else {
                        return catalogService.findAvailableRootCategoriesForCatalog(id, FindBy.EXTERNAL_ID, "categoryName", dataTableRequest.getSearch(), pageRequest, false);
                    }
                },
                paginatedResult -> {
                    List<Map<String, String>> dataObjects = new ArrayList<>();
                    paginatedResult.getContent().forEach(e -> dataObjects.add(e.toMap()));
                    return dataObjects;
                });
    }

    @ResponseBody
    @RequestMapping(value = "/{id}/rootCategories/{rootCategoryId}", method = RequestMethod.POST)
    public Map<String, Object> addRootCategory(@PathVariable(value = "id") String id, @PathVariable(value = "rootCategoryId") String rootCategoryId) {
        Map<String, Object> model = new HashMap<>();
        boolean success = catalogService.addRootCategory(id, FindBy.EXTERNAL_ID, rootCategoryId, FindBy.EXTERNAL_ID) != null;
        model.put("success", success);
        return model;
    }

    @RequestMapping(value = "/{id}/rootCategories/data", method = RequestMethod.PUT)
    @ResponseBody
    public Map<String, Object> setRootCategoriesSequence(@PathVariable(value = "id") String id, @RequestParam Map<String, String> parameterMap) {
        Map<String, Object> model = new HashMap<>();
        boolean success = catalogService.setRootCategorySequence(id, FindBy.EXTERNAL_ID, parameterMap.get("sourceId"), FindBy.EXTERNAL_ID, parameterMap.get("destinationId"), FindBy.EXTERNAL_ID);
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

    @RequestMapping(value = "/{catalogId}/catalogs/active/{active}", method = RequestMethod.PUT)
    @ResponseBody
    public Map<String, Object> toggleCatalogs(@PathVariable(value = "catalogId") String catalogId,
                                                  @PathVariable(value = "active") String active) {
        Map<String, Object> model = new HashMap<>();
        model.put("success", catalogService.toggleCatalog(catalogId, FindBy.EXTERNAL_ID, Toggle.get(active)));
        return model;
    }
}
