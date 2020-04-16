package com.bigname.pim.client.web.controller;

import com.bigname.pim.core.domain.Catalog;
import com.bigname.pim.core.domain.Category;
import com.bigname.pim.core.domain.RootCategory;
import com.bigname.pim.core.domain.WebsiteCatalog;
import com.bigname.pim.core.service.CatalogService;
import com.bigname.pim.core.service.WebsiteService;
import com.bigname.pim.core.util.BreadcrumbsBuilder;
import com.bigname.pim.core.data.exportor.CatalogExporter;
import com.m7.xtreme.common.datatable.model.Pagination;
import com.m7.xtreme.common.datatable.model.Request;
import com.m7.xtreme.common.datatable.model.Result;
import com.m7.xtreme.common.datatable.model.SortOrder;
import com.m7.xtreme.common.util.ValidationUtil;
import com.m7.xtreme.xcore.domain.Entity;
import com.m7.xtreme.xcore.exception.EntityNotFoundException;
import com.m7.xtreme.xcore.util.Archive;
import com.m7.xtreme.xcore.util.ID;
import com.m7.xtreme.xcore.util.Toggle;
import com.m7.xtreme.xcore.web.controller.BaseController;
import com.m7.xtreme.xplatform.domain.SyncStatus;
import com.m7.xtreme.xplatform.domain.User;
import com.m7.xtreme.xplatform.domain.Version;
import com.m7.xtreme.xplatform.service.JobInstanceService;
import com.m7.xtreme.xplatform.service.SyncStatusService;
import com.m7.xtreme.xplatform.service.UserService;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.m7.xtreme.common.util.ValidationUtil.isEmpty;
import static com.m7.xtreme.common.util.ValidationUtil.isNotEmpty;


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
    private UserService userService;
    private RestTemplate restTemplate;
    private SyncStatusService syncStatusService;

    public CatalogController(CatalogService catalogService, @Lazy CatalogExporter catalogExporter, WebsiteService websiteService, JobInstanceService jobInstanceService, UserService userService, RestTemplate restTemplate, SyncStatusService syncStatusService) {
        super(catalogService, Catalog.class, new BreadcrumbsBuilder(), catalogExporter, jobInstanceService, websiteService);
        this.catalogService = catalogService;
        this.websiteService = websiteService;
        this.userService = userService;
        this.restTemplate = restTemplate;
        this.syncStatusService = syncStatusService;
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
        catalog.setLastExportedTimeStamp(null);
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

    @RequestMapping("/search")
    public ModelAndView search() {
        Map<String, Object> model = new HashMap<>();
        model.put("active", "CATALOGS");
        model.put("view", "search");
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

    @RequestMapping("/{catalogId}/history")
    @ResponseBody
    public Result<Map<String, String>> getHistory(@PathVariable(value = "catalogId") String id, HttpServletRequest request) {
        Request dataTableRequest = new Request(request);
        Pagination pagination = dataTableRequest.getPagination();
        Result<Map<String, String>> result = new Result<>();
        result.setDraw(dataTableRequest.getDraw());
        Sort sort = null;
        if(pagination.hasSorts()) {
            sort = Sort.by(new Sort.Order(Sort.Direction.valueOf(SortOrder.fromValue(dataTableRequest.getOrder().getSortDir()).name()), dataTableRequest.getOrder().getName()));
        }
        Map<String, User> usersLookup = userService.getAll(null, false).stream().collect(Collectors.toMap(Entity::getId, u -> u));
        List<Map<String, String>> dataObjects = new ArrayList<>();
        Catalog catalogData = catalogService.get(ID.EXTERNAL_ID(id), false).orElse(null);
        final Page<Version> paginatedResult = new PageImpl<>(catalogData.getVersions());
        paginatedResult.getContent().forEach(e -> {
            Catalog catalog = (Catalog) e.getState();
            Map<String, String> data = catalog.toMap();
            if(usersLookup.containsKey(e.getUserId())) {
                data.put("userName", usersLookup.get(e.getUserId()).getUserName());
            }
            data.put("timeStamp", String.valueOf(e.getTimeStamp()));
            data.put("userId", String.valueOf(e.getUserId()));
            dataObjects.add(data);
        });
        result.setDataObjects(dataObjects);
        result.setRecordsTotal(Long.toString(paginatedResult.getTotalElements()));
        result.setRecordsFiltered(Long.toString(paginatedResult.getTotalElements()));
        return result;
    }

    @RequestMapping(value = {"/{catalogId}/history/{time}"})
    public ModelAndView details(@PathVariable(value = "catalogId") String catalogId,
                                @PathVariable(name = "time") String time) {
        Map<String, Object> model = new HashMap<>();
        model.put("active", "CATALOGS");
        model.put("mode", "HISTORY");
        model.put("view", "catalog/catalog");
        Catalog catalog = catalogService.get(ID.EXTERNAL_ID(catalogId), false).orElse(null);
        catalog.getVersions().forEach(version -> {
            String timeStamp = String.valueOf(version.getTimeStamp());
            if(timeStamp.equalsIgnoreCase(time)) {
                Catalog catalog1 = (Catalog) version.getState();
                model.put("catalog", catalog1);
            }
        });
        return super.details(catalogId, model);
    }

    @RequestMapping(value ="/catalogLoad")
    @ResponseBody
    public Map<String, Object> loadCatalogToBOS() {
        Map<String, Object> model = new HashMap<>();
        boolean success = false;
        List<Catalog> catalogList = catalogService.getAll(null, true);
        ResponseEntity<String> response =  restTemplate.postForEntity("http://localhost:8084/admin/catalogs/loadCatalog", catalogList, String.class, new HashMap<>());
        if (response != null && response.getStatusCode() == HttpStatus.OK) {
            success = true;
        }
        model.put("success", success);
        return model;
    }

    @RequestMapping(value ="/rootCategoryLoad")
    @ResponseBody
    public Map<String, Object> loadRootCategoryToBOS() {
        Map<String, Object> model = new HashMap<>();
        boolean success = false;
        List<RootCategory> rootCategoryList = catalogService.loadRootCategoryToBOS();
        ResponseEntity<String> response =  restTemplate.postForEntity("http://localhost:8084/admin/catalogs/loadRootCategory", rootCategoryList, String.class, new HashMap<>());
        if (response != null && response.getStatusCode() == HttpStatus.OK) {
            success = true;
        }
        model.put("success", success);
        return model;
    }

    @RequestMapping(value ="/syncUpdatedCatalogs", method = RequestMethod.PUT)
    @ResponseBody
    public Map<String, Object> syncUpdatedCatalog() {
        Map<String, Object> model = new HashMap<>();
        List<Catalog> catalogs = catalogService.syncUpdatedRecord();
        ResponseEntity<String> response =  restTemplate.postForEntity("http://localhost:8084/admin/catalogs/syncUpdatedCatalogs", catalogs, String.class, new HashMap<>());
        if(Objects.equals(response.getBody(), "true")) {
            catalogs.forEach(catalog -> {
                catalog.setLastExportedTimeStamp(LocalDateTime.now());
                List<SyncStatus> syncStatusList = syncStatusService.getPendingSynStatus(catalog.getId(), "pending");
                syncStatusList.forEach(syncStatus -> {
                    syncStatus.setStatus("updated");
                    syncStatus.setExportedTimeStamp(catalog.getLastExportedTimeStamp());
                });
                syncStatusService.update(syncStatusList);
            });
            catalogService.update(catalogs);
            model.put("success", true);
        } else {
            model.put("success", null);
        }
        return model;
    }

    @RequestMapping(value ="/{catalogId}/syncRootCategories", method = RequestMethod.PUT)
    @ResponseBody
    public Map<String, Object> syncRootCategories(@PathVariable(value = "catalogId") String catalogId) {
        Map<String, Object> model = new HashMap<>();
        Catalog catalog = catalogService.get(ID.EXTERNAL_ID(catalogId), false).orElse(null);
        List<RootCategory> rootCategoryList = catalogService.getAllRootCategories(catalog.getId());
        List<RootCategory> finalRootCategory = new ArrayList<>();
        rootCategoryList.forEach(rootCategory -> {
            if(ValidationUtil.isEmpty(rootCategory.getLastExportedTimeStamp())) {
                finalRootCategory.add(rootCategory);
            }
        });
        ResponseEntity<String> response =  restTemplate.postForEntity("http://localhost:8084/admin/catalogs/syncRootCategories", finalRootCategory, String.class, new HashMap<>());
        if(Objects.equals(response.getBody(), "true")) {
            finalRootCategory.forEach(rootCategory -> {
                rootCategory.setLastExportedTimeStamp(LocalDateTime.now());
                List<SyncStatus> syncStatusList = syncStatusService.getPendingSynStatus(rootCategory.getId(), "pending");
                syncStatusList.forEach(syncStatus -> {
                    syncStatus.setStatus("updated");
                    syncStatus.setExportedTimeStamp(catalog.getLastExportedTimeStamp());
                });
                syncStatusService.update(syncStatusList);
            });
            catalogService.syncRootCategories(finalRootCategory);
            model.put("success", true);
        } else {
            model.put("success", null);
        }
        return model;
    }

}
