package com.bigname.pim.client.web.controller;

import com.bigname.pim.api.domain.Catalog;
import com.bigname.pim.api.domain.RootCategory;
import com.bigname.pim.api.domain.WebsiteCatalog;
import com.bigname.pim.api.service.CatalogService;
import com.bigname.pim.api.service.WebsiteService;
import com.bigname.pim.client.util.BreadcrumbsBuilder;
import com.bigname.pim.data.exportor.CatalogExporter;
import com.m7.xtreme.common.datatable.model.Request;
import com.m7.xtreme.common.datatable.model.Result;
import com.m7.xtreme.xcore.exception.EntityNotFoundException;
import com.m7.xtreme.xcore.util.Archive;
import com.m7.xtreme.xcore.util.ID;
import com.m7.xtreme.xcore.util.Toggle;
import com.m7.xtreme.xcore.web.controller.BaseController;
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

import static com.m7.xtreme.common.util.ValidationUtil.*;


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
        super(catalogService, Catalog.class, new BreadcrumbsBuilder(), catalogExporter, websiteService);
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
        Catalog catalog1 = catalogService.get(ID.EXTERNAL_ID(catalogId), false).orElse(null);
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
        if(id == null) {
            return super.details(model);
        } else {
            Catalog catalog = catalogService.get(ID.EXTERNAL_ID(id), false).orElse(null);
            if(isEmpty(catalog)) {
                catalog = catalogService.get(ID.EXTERNAL_ID(id), false, false, false, true).orElse(null);
                model.put("catalog", catalog);
            } else if(isNotEmpty(catalog)) {
                model.put("catalog", catalog);
            } else {
                throw new EntityNotFoundException("Unable to find Catalog with Id: " + id);
            }

            return super.details(id, parameterMap, request, model);
        }
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
                        return catalogService.getRootCategories(ID.EXTERNAL_ID(id), dataTableRequest.getPageRequest(associationSortPredicate), dataTableRequest.getStatusOptions());
                    } else {
                        return catalogService.findAllRootCategories(ID.EXTERNAL_ID(id), "categoryName", dataTableRequest.getSearch(), dataTableRequest.getPageRequest(associationSortPredicate), false);
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
                        return catalogService.getAvailableRootCategoriesForCatalog(ID.EXTERNAL_ID(id), pageRequest.getPageNumber(), pageRequest.getPageSize(), pageRequest.getSort(), false);
                    } else {
                        return catalogService.findAvailableRootCategoriesForCatalog(ID.EXTERNAL_ID(id), "categoryName", dataTableRequest.getSearch(), pageRequest, false);
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
        boolean success = catalogService.addRootCategory(ID.EXTERNAL_ID(id), ID.EXTERNAL_ID(rootCategoryId)) != null;
        model.put("success", success);
        return model;
    }

    @RequestMapping(value = "/{id}/rootCategories/data", method = RequestMethod.PUT)
    @ResponseBody
    public Map<String, Object> setRootCategoriesSequence(@PathVariable(value = "id") String id, @RequestParam Map<String, String> parameterMap) {
        Map<String, Object> model = new HashMap<>();
        boolean success = catalogService.setRootCategorySequence(ID.EXTERNAL_ID(id), ID.EXTERNAL_ID(parameterMap.get("sourceId")), ID.EXTERNAL_ID(parameterMap.get("destinationId")));
        model.put("success", success);
        return model;
    }

    @RequestMapping(value = "/{id}/rootCategories/{rootCategoryId}/active/{active}", method = RequestMethod.PUT)
    @ResponseBody
    public Map<String, Object> toggleRootCategory(@PathVariable(value = "id") String catalogId,
                                                  @PathVariable(value = "rootCategoryId") String rootCategoryId,
                                                  @PathVariable(value = "active") String active) {
        Map<String, Object> model = new HashMap<>();
        model.put("success", catalogService.toggleRootCategory(ID.EXTERNAL_ID(catalogId), ID.EXTERNAL_ID(rootCategoryId), Toggle.get(active)));
        return model;
    }

    @RequestMapping("/{id}/hierarchy")
    @ResponseBody
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> getCategoriesHierarchy(@PathVariable(value = "id") String id) {
        return catalogService.getCategoryHierarchy(ID.EXTERNAL_ID(id));
    }

    @RequestMapping(value = "/{catalogId}/catalogs/active/{active}", method = RequestMethod.PUT)
    @ResponseBody
    public Map<String, Object> toggleCatalogs(@PathVariable(value = "catalogId") String catalogId,
                                              @PathVariable(value = "active") String active) {
        Map<String, Object> model = new HashMap<>();
        model.put("success", catalogService.toggleCatalog(ID.EXTERNAL_ID(catalogId), Toggle.get(active)));
        return model;
    }

    @RequestMapping(value = "/{catalogId}/catalogs/archive/{archived}", method = RequestMethod.PUT)
    @ResponseBody
    public Map<String, Object> archive(@PathVariable(value = "catalogId") String catalogId, @PathVariable(value = "archived") String archived) {
        Map<String, Object> model = new HashMap<>();
        Catalog catalog = catalogService.get(ID.EXTERNAL_ID(catalogId), false).orElse(null);
        if(isEmpty(catalog)) {
            catalog = catalogService.get(ID.EXTERNAL_ID(catalogId), false, false, false, true).orElse(null);
        }
        //catalogService.archiveCatalogAssociations(ID.EXTERNAL_ID(catalogId), Archive.get(archived), catalog);
        model.put("success", catalogService.archive(ID.EXTERNAL_ID(catalogId), Archive.get(archived)));
        return model;
    }
}
