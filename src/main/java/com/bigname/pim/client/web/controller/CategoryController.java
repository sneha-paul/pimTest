package com.bigname.pim.client.web.controller;

import com.bigname.pim.core.data.exportor.CategoryExporter;
import com.bigname.pim.core.domain.*;
import com.bigname.pim.core.service.CatalogService;
import com.bigname.pim.core.service.CategoryService;
import com.bigname.pim.core.service.WebsiteService;
import com.bigname.pim.core.util.BreadcrumbsBuilder;
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
 * Created by sruthi on 29-08-2018.
 */
@Controller
@RequestMapping("pim/categories")
public class CategoryController extends BaseController<Category, CategoryService> {

    private CategoryService categoryService;
    private UserService userService;
    private RestTemplate restTemplate;
    private SyncStatusService syncStatusService;

    public CategoryController(CategoryService categoryService, @Lazy CategoryExporter categoryExporter, JobInstanceService jobInstanceService, CatalogService catalogService, WebsiteService websiteService, UserService userService, RestTemplate restTemplate, SyncStatusService syncStatusService){
        super(categoryService, Category.class, new BreadcrumbsBuilder(), categoryExporter, jobInstanceService, websiteService, catalogService);
        this.categoryService = categoryService;
        this.userService = userService;
        this.restTemplate = restTemplate;
        this.syncStatusService = syncStatusService;
    }

    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> create(Category category) {
        Map<String, Object> model = new HashMap<>();
        if(isValid(category, model, Category.CreateGroup.class)) {
            category.setActive("N");
            category.setDiscontinued("N");
            categoryService.create(category);
            model.put("success", true);
        }
        return model;
    }

    @RequestMapping(value = "/{categoryId}", method = RequestMethod.PUT)
    @ResponseBody
    public Map<String, Object> update(@PathVariable(value = "categoryId") String categoryId, Category category) {
        Category category1 = categoryService.get(ID.EXTERNAL_ID(categoryId), true, true, true).orElse(null);

        categoryService.getAllRootCategoriesWithCategoryId(ID.INTERNAL_ID(category1.getId()))
                .forEach(rootCategory -> {
                    rootCategory.setActive(category.getActive());
                    categoryService.updateRootCategory(rootCategory);
                });
        categoryService.getAllRelatedCategoriesWithSubCategoryId(ID.INTERNAL_ID(category1.getId()))
                .forEach(relatedCategory -> {
                    relatedCategory.setActive(category.getActive());
                    categoryService.updateRelatedCategory(relatedCategory);
                });
        categoryService.getAllProductCategoriesWithCategoryId(ID.EXTERNAL_ID(category1.getId()))
                .forEach(productCategory -> {
                    productCategory.setActive(category.getActive());
                    categoryService.updateProductCategory(productCategory);
                });
        category.setLastExportedTimeStamp(null);
        return update(categoryId, category, "/pim/categories/", category.getGroup().length == 1 && category.getGroup()[0].equals("DETAILS") ? Category.DetailsGroup.class :category.getGroup()[0].equals("SEO") ? Category.SeoGroup.class : null);
    }

    @RequestMapping(value = {"/{id}", "/create"})
    public ModelAndView details(@PathVariable(value = "id", required = false) String id,
                                @RequestParam(name = "reload", required = false) boolean reload,
                                @RequestParam Map<String, Object> parameterMap,
                                HttpServletRequest request) {

        Map<String, Object> model = new HashMap<>();
        model.put("active", "CATEGORIES");
        model.put("mode", id == null ? "CREATE" : "DETAILS");
        model.put("view", "category/category"  + (reload ? "_body" : ""));

        if(id == null) {
            return super.details(model);
        } else {
            Category category = categoryService.get(ID.EXTERNAL_ID(id), true, true, true).orElse(null);
            if(isNotEmpty(category)) {
                if(parameterMap.containsKey("parentId")) {
                    model.put("parentId", parameterMap.get("parentId"));
                }
                model.put("category", category);
            } else if(isEmpty(category)) {
                category = categoryService.get(ID.EXTERNAL_ID(id), false, false, false, true).orElse(null);
                if(parameterMap.containsKey("parentId")) {
                    model.put("parentId", parameterMap.get("parentId"));
                }
                model.put("category", category);
            } else {
                throw new EntityNotFoundException("Unable to find Category with Id: " + id);
            }
            return super.details(id, parameterMap, request, model);
        }
    }

    @RequestMapping()
    public ModelAndView all(@RequestParam(name = "reload", required = false) boolean reload){
        Map<String, Object> model = new HashMap<>();
        model.put("active", "CATEGORIES");
        model.put("view", "category/categories" + (reload ? "_body" : ""));
        model.put("title", "Categories");
        return all(model);
    }

    @RequestMapping("/search")
    public ModelAndView search(@RequestParam(name = "reload", required = false) boolean reload){
        Map<String, Object> model = new HashMap<>();
        model.put("active", "CATEGORIES");
        model.put("view", "search");
        model.put("title", "Categories");
        return all(model);
    }

    @RequestMapping(value =  {"/list", "/data"})
    @ResponseBody
    public Result<Map<String, String>> all(HttpServletRequest request) {
        return all(request, "categoryName");
    }

    @RequestMapping("/hierarchy")
    @ResponseBody
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> getAllAsHierarchy() {
        return categoryService.getCategoryHierarchy(false);
    }

    @RequestMapping("/{id}/subCategories/data")
    @ResponseBody
    public Result<Map<String, Object>> getSubCategories(@PathVariable(value = "id") String id, HttpServletRequest request) {
        return getAssociationGridData(request,
                RelatedCategory.class,
                dataTableRequest -> {
                    if(isEmpty(dataTableRequest.getSearch())) {
                        return categoryService.getSubCategories(ID.EXTERNAL_ID(id), dataTableRequest.getPageRequest(associationSortPredicate), dataTableRequest.getStatusOptions());
                    } else {
                        return categoryService.findAllSubCategories(ID.EXTERNAL_ID(id), "categoryName", dataTableRequest.getSearch(), dataTableRequest.getPageRequest(associationSortPredicate), false);
                    }
                });
    }

    @RequestMapping(value = "/{id}/subCategories/data", method = RequestMethod.PUT)
    @ResponseBody
    public Map<String, Object> setSubCategoriesSequence(@PathVariable(value = "id") String id, @RequestParam Map<String, String> parameterMap) {
        Map<String, Object> model = new HashMap<>();
        boolean success = categoryService.setSubCategorySequence(ID.EXTERNAL_ID(id), ID.EXTERNAL_ID(parameterMap.get("sourceId")), ID.EXTERNAL_ID(parameterMap.get("destinationId")));
        model.put("success", success);
        return model;
    }

    @RequestMapping("/{id}/products/data")
    @ResponseBody
    public Result<Map<String, Object>> getCategoryProducts(@PathVariable(value = "id") String id, HttpServletRequest request) {
        return getAssociationGridData(request,
                CategoryProduct.class,
                dataTableRequest -> {
                    if(isEmpty(dataTableRequest.getSearch())) {
                        return categoryService.getCategoryProducts(ID.EXTERNAL_ID(id), dataTableRequest.getPageRequest(associationSortPredicate), dataTableRequest.getStatusOptions());
                    } else {
                        return categoryService.findAllCategoryProducts(ID.EXTERNAL_ID(id), "productName", dataTableRequest.getSearch(), dataTableRequest.getPageRequest(associationSortPredicate), false);
                    }
                });
    }

    @RequestMapping(value = "/{id}/products/data", method = RequestMethod.PUT)
    @ResponseBody
    public Map<String, Object> setProductsSequence(@PathVariable(value = "id") String id, @RequestParam Map<String, String> parameterMap) {
        Map<String, Object> model = new HashMap<>();
        boolean success = categoryService.setProductSequence(ID.EXTERNAL_ID(id), ID.EXTERNAL_ID(parameterMap.get("sourceId")), ID.EXTERNAL_ID(parameterMap.get("destinationId")));
        model.put("success", success);
        return model;
    }

    @RequestMapping(value = "/{id}/subCategories/available")
    public ModelAndView availableCategories(@PathVariable(value = "id") String id) {
        Map<String, Object> model = new HashMap<>();
        return new ModelAndView("category/availableSubCategories", model);
    }

    @RequestMapping("/{id}/subCategories/available/list")
    @ResponseBody
    public Result<Map<String, String>> getAvailableSubCategories(@PathVariable(value = "id") String id, HttpServletRequest request) {
        return new Result<Map<String, String>>().buildResult(new Request(request),
                dataTableRequest -> {
                    PageRequest pageRequest = dataTableRequest.getPageRequest(defaultSort);
                    if(isEmpty(dataTableRequest.getSearch())) {
                        return categoryService.getAvailableSubCategoriesForCategory(ID.EXTERNAL_ID(id), pageRequest.getPageNumber(), pageRequest.getPageSize(), pageRequest.getSort(), false);
                    } else {
                        return categoryService.findAvailableSubCategoriesForCategory(ID.EXTERNAL_ID(id), "categoryName", dataTableRequest.getSearch(), pageRequest, false);
                    }
                },
                paginatedResult -> {
                    List<Map<String, String>> dataObjects = new ArrayList<>();
                    paginatedResult.getContent().forEach(e -> dataObjects.add(e.toMap()));
                    return dataObjects;
                });
    }

    @ResponseBody
    @RequestMapping(value = "/{id}/subCategories/{subCategoryId}", method = RequestMethod.POST)
    public Map<String, Object> addSubCategory(@PathVariable(value = "id") String id, @PathVariable(value = "subCategoryId") String subCategoryId) {
        Map<String, Object> model = new HashMap<>();
        boolean success = categoryService.addSubCategory(ID.EXTERNAL_ID(id), ID.EXTERNAL_ID(subCategoryId)) != null;
        model.put("success", success);
        return model;
    }

    @RequestMapping(value = "/{id}/subCategories/{subCategoryId}/active/{active}", method = RequestMethod.PUT)
    @ResponseBody
    public Map<String, Object> toggleSubCategory(@PathVariable(value = "id") String categoryId,
                                                 @PathVariable(value = "subCategoryId") String subCategoryId,
                                                 @PathVariable(value = "active") String active) {
        Map<String, Object> model = new HashMap<>();
        model.put("success", categoryService.toggleSubCategory(ID.EXTERNAL_ID(categoryId), ID.EXTERNAL_ID(subCategoryId), Toggle.get(active)));
        return model;
    }


    @RequestMapping(value = "/{id}/products/available")
    public ModelAndView availableProducts(@PathVariable(value = "id") String id) {
        Map<String, Object> model = new HashMap<>();
        return new ModelAndView("product/availableProducts", model);
    }

    @RequestMapping("/{id}/products/available/list")
    @ResponseBody
    public Result<Map<String, String>> getAvailableProducts(@PathVariable(value = "id") String id, HttpServletRequest request) {
        return new Result<Map<String, String>>().buildResult(new Request(request),
                dataTableRequest -> {
                    PageRequest pageRequest = dataTableRequest.getPageRequest(defaultSort);
                    if(isEmpty(dataTableRequest.getSearch())) {
                        return categoryService.getAvailableProductsForCategory(ID.EXTERNAL_ID(id), pageRequest.getPageNumber(), pageRequest.getPageSize(), pageRequest.getSort(), false);
                    } else {
                        return categoryService.findAvailableProductsForCategory(ID.EXTERNAL_ID(id), "productName", dataTableRequest.getSearch(), pageRequest, false);
                    }
                },
                paginatedResult -> {
                    List<Map<String, String>> dataObjects = new ArrayList<>();
                    paginatedResult.getContent().forEach(e -> dataObjects.add(e.toMap()));
                    return dataObjects;
                });
    }

    @ResponseBody
    @RequestMapping(value = "/{id}/products/{productId}", method = RequestMethod.POST)
    public Map<String, Object> addProduct(@PathVariable(value = "id") String id, @PathVariable(value = "productId") String productId, HttpServletRequest request) {
        Map<String, Object> model = new HashMap<>();
        boolean success = categoryService.addProduct(ID.EXTERNAL_ID(id), ID.EXTERNAL_ID(productId)) != null;
        categoryService.addParentCategory(ID.EXTERNAL_ID(request.getParameter("parentId")), ID.EXTERNAL_ID(productId));
        model.put("success", success);
        return model;
    }

    @RequestMapping(value = "/{id}/products/{productId}/active/{active}", method = RequestMethod.PUT)
    @ResponseBody
    public Map<String, Object> toggleProduct(@PathVariable(value = "id") String categoryId,
                                             @PathVariable(value = "productId") String productId,
                                             @PathVariable(value = "active") String active) {
        Map<String, Object> model = new HashMap<>();
        model.put("success", categoryService.toggleProduct(ID.EXTERNAL_ID(categoryId), ID.EXTERNAL_ID(productId), Toggle.get(active)));
        return model;
    }

    @RequestMapping(value = "/{categoryId}/categories/active/{active}", method = RequestMethod.PUT)
    @ResponseBody
    public Map<String, Object> toggleCategory(@PathVariable(value = "categoryId") String categoryId,
                                              @PathVariable(value = "active") String active) {
        Map<String, Object> model = new HashMap<>();
        model.put("success", categoryService.toggleCategory(ID.EXTERNAL_ID(categoryId), Toggle.get(active)));
        return model;
    }

    @RequestMapping(value = "/{categoryId}/categories/archive/{archived}", method = RequestMethod.PUT)
    @ResponseBody
    public Map<String, Object> archive(@PathVariable(value = "categoryId") String categoryId, @PathVariable(value = "archived") String archived) {
        Map<String, Object> model = new HashMap<>();
        Category category = categoryService.get(ID.EXTERNAL_ID(categoryId), true, true, true).orElse(null);
        if(isEmpty(category)) {
            category = categoryService.get(ID.EXTERNAL_ID(categoryId), false, false, false, true).orElse(null);
        }
        //categoryService.archiveCategoryAssociations(ID.EXTERNAL_ID(categoryId), Archive.get(archived), category);
        model.put("success", categoryService.archive(ID.EXTERNAL_ID(categoryId), Archive.get(archived)));
        return model;
    }

    @RequestMapping("/{categoryId}/history")
    @ResponseBody
    public Result<Map<String, String>> getHistory(@PathVariable(value = "categoryId") String id, HttpServletRequest request) {
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
        Category categoryData = categoryService.get(ID.EXTERNAL_ID(id), false).orElse(null);
        final Page<Version> paginatedResult = new PageImpl<>(categoryData.getVersions());
        paginatedResult.getContent().forEach(e -> {
            Category category = (Category) e.getState();
            Map<String, String> data = category.toMap();
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

    @RequestMapping(value = {"/{categoryId}/history/{time}"})
    public ModelAndView details(@PathVariable(value = "categoryId") String categoryId,
                                @PathVariable(name = "time") String time) {
        Map<String, Object> model = new HashMap<>();
        model.put("active", "CATEGORIES");
        model.put("mode", "HISTORY");
        model.put("view", "category/category");
        Category category = categoryService.get(ID.EXTERNAL_ID(categoryId), false).orElse(null);
        category.getVersions().forEach(version -> {
            String timeStamp = String.valueOf(version.getTimeStamp());
            if(timeStamp.equalsIgnoreCase(time)) {
                Category category1 = (Category) version.getState();
                model.put("category", category1);
            }
        });
        return super.details(categoryId, model);
    }

    @RequestMapping("/{id}/allProducts/data")
    @ResponseBody
    public Result<Map<String, Object>> getAllParentCategoryProducts(@PathVariable(value = "id") String id, HttpServletRequest request) {
        return getAssociationGridData(request,
                ParentCategoryProduct.class,
                dataTableRequest -> {
                    if(isEmpty(dataTableRequest.getSearch())) {
                        return categoryService.getAllParentCategoryProducts(ID.EXTERNAL_ID(id), dataTableRequest.getPageRequest(associationSortPredicate), dataTableRequest.getStatusOptions());
                    } else {
                        return categoryService.findAllParentCategoryProducts(ID.EXTERNAL_ID(id), "productName", dataTableRequest.getSearch(), dataTableRequest.getPageRequest(associationSortPredicate), false);
                    }
                });
    }

    @RequestMapping(value = "/{id}/allProducts/data", method = RequestMethod.PUT)
    @ResponseBody
    public Map<String, Object> setAllParentCategoryProductsSequence(@PathVariable(value = "id") String id, @RequestParam Map<String, String> parameterMap) {
        Map<String, Object> model = new HashMap<>();
        boolean success = categoryService.setAllParentCategoryProductsSequence(ID.EXTERNAL_ID(id), ID.EXTERNAL_ID(parameterMap.get("sourceId")), ID.EXTERNAL_ID(parameterMap.get("destinationId")));
        model.put("success", success);
        return model;
    }

    @RequestMapping(value = "/{id}/allProducts/{productId}/active/{active}", method = RequestMethod.PUT)
    @ResponseBody
    public Map<String, Object> toggleParentCategoryProduct(@PathVariable(value = "id") String categoryId,
                                             @PathVariable(value = "productId") String productId,
                                             @PathVariable(value = "active") String active) {
        Map<String, Object> model = new HashMap<>();
        model.put("success", categoryService.toggleParentCategoryProduct(ID.EXTERNAL_ID(categoryId), ID.EXTERNAL_ID(productId), Toggle.get(active)));
        return model;
    }

    @ResponseBody
    @RequestMapping(value = "/{id}/syncProducts", method = RequestMethod.POST)
    public Map<String, Object> syncAllParentCategoryProducts(@PathVariable(value = "id") String categoryId, HttpServletRequest request) {
        Request dataTableRequest = new Request(request);
        Map<String, Object> model = new HashMap<>();
        boolean success = categoryService.syncAllParentCategoryProducts(categoryId, dataTableRequest.getPageRequest(associationSortPredicate));
        model.put("success", success);
        return model;
    }

    @RequestMapping(value ="/categoryLoad")
    public void loadCategoryToBOS() {
        List<Category> categoryList = categoryService.getAll(null, false);
        Map<String, String> map = new HashMap<String, String>();
        ResponseEntity<String> response =  restTemplate.postForEntity("http://localhost:8084/admin/categories/loadCategory", categoryList, String.class, map);
    }

    @RequestMapping(value ="/relatedCategoryLoad")
    @ResponseBody
    public Map<String, Object> loadRelatedCategoryToBOS() {
        Map<String, Object> model = new HashMap<>();
        boolean success = false;
        List<RelatedCategory> relatedCategoryList = categoryService.getAll();
        Map<String, String> map = new HashMap<String, String>();
        ResponseEntity<String> response =  restTemplate.postForEntity("http://envelopes.localhost:8084/category/loadRelatedCategory", relatedCategoryList, String.class, map);
        if (response != null && response.getStatusCode() == HttpStatus.OK) {
            success = true;
        }
        model.put("success", success);
        return model;
    }

    @RequestMapping(value ="/categoryProductLoad")
    public void loadProductCategoryToBOS() {
        List<CategoryProduct> categoryProductList = categoryService.loadCategoryProductToBOS();
        Map<String, String> map = new HashMap<String, String>();
        ResponseEntity<String> response =  restTemplate.postForEntity("http://envelopes.localhost:8084/category/loadCategoryProduct", categoryProductList, String.class, map);
    }

    @RequestMapping(value ="/syncUpdatedCategories", method = RequestMethod.PUT)
    @ResponseBody
    public Map<String, Object> syncUpdatedCategories() {
        Map<String, Object> model = new HashMap<>();
        List<Category> categories = categoryService.syncUpdatedRecord();
        Map<String, String> map = new HashMap<String, String>();
        ResponseEntity<String> response =  restTemplate.postForEntity("http://localhost:8084/admin/categories/syncUpdatedCategories", categories, String.class, map);
        if(Objects.equals(response.getBody(), "true")) {
            categories.forEach(category -> {
                category.setLastExportedTimeStamp(LocalDateTime.now());
                List<SyncStatus> syncStatusList = syncStatusService.getPendingSynStatus(category.getId(), "pending");
                syncStatusList.forEach(syncStatus -> {
                    syncStatus.setStatus("updated");
                    syncStatus.setExportedTimeStamp(category.getLastExportedTimeStamp());
                });
                syncStatusService.update(syncStatusList);
            });
            categoryService.update(categories);
            model.put("success", true);
        } else {
            model.put("success", false);
        }
        return model;
    }

    @RequestMapping(value ="/{id}/syncSubCategories", method = RequestMethod.PUT)
    @ResponseBody
    public Map<String, Object> syncSubCategories(@PathVariable(value = "id") String id) {
        Map<String, Object> model = new HashMap<>();
        Category category = categoryService.get(ID.EXTERNAL_ID(id), false).orElse(null);
        List<RelatedCategory> subCategoriesList = categoryService.getAllRelatedCategoriesWithSubCategoryId(ID.INTERNAL_ID(category.getId()));
        List<RelatedCategory> finalSubCategories = new ArrayList<>();
        subCategoriesList.forEach(subCategory -> {
            if(ValidationUtil.isEmpty(subCategory.getLastExportedTimeStamp())) {
                finalSubCategories.add(subCategory);
            }
        });
        ResponseEntity<String> response =  restTemplate.postForEntity("http://localhost:8084/admin/categories/syncSubCategories", finalSubCategories, String.class, new HashMap<>());
        if(Objects.equals(response.getBody(), "true")) {
            finalSubCategories.forEach(subCategory -> {
                subCategory.setLastExportedTimeStamp(LocalDateTime.now());
                List<SyncStatus> syncStatusList = syncStatusService.getPendingSynStatus(subCategory.getId(), "pending");
                syncStatusList.forEach(syncStatus -> {
                    syncStatus.setStatus("updated");
                    syncStatus.setExportedTimeStamp(subCategory.getLastExportedTimeStamp());
                });
                syncStatusService.update(syncStatusList);
            });
            categoryService.syncSubCategories(finalSubCategories);
            model.put("success", true);
        } else {
            model.put("success", null);
        }
        return model;
    }

    @RequestMapping(value ="/{id}/syncCategoryProducts", method = RequestMethod.PUT)
    @ResponseBody
    public Map<String, Object> syncCategoryProducts(@PathVariable(value = "id") String id) {
        Map<String, Object> model = new HashMap<>();
        Category category = categoryService.get(ID.EXTERNAL_ID(id), false).orElse(null);
        List<CategoryProduct> categoryProductList = categoryService.getAllCategoryProducts(ID.INTERNAL_ID(category.getId()));
        List<CategoryProduct> finalCategoryProducts = new ArrayList<>();
        categoryProductList.forEach(categoryProduct -> {
            if(ValidationUtil.isEmpty(categoryProduct.getLastExportedTimeStamp())) {
                finalCategoryProducts.add(categoryProduct);
            }
        });
        ResponseEntity<String> response =  restTemplate.postForEntity("http://localhost:8084/admin/categories/syncCategoryProducts", finalCategoryProducts, String.class, new HashMap<>());
        if(Objects.equals(response.getBody(), "true")) {
            finalCategoryProducts.forEach(categoryProduct -> {
                categoryProduct.setLastExportedTimeStamp(LocalDateTime.now());
                List<SyncStatus> syncStatusList = syncStatusService.getPendingSynStatus(categoryProduct.getId(), "pending");
                syncStatusList.forEach(syncStatus -> {
                    syncStatus.setStatus("updated");
                    syncStatus.setExportedTimeStamp(categoryProduct.getLastExportedTimeStamp());
                });
                syncStatusService.update(syncStatusList);
            });
            categoryService.syncCategoryProducts(finalCategoryProducts);
            model.put("success", true);
        } else {
            model.put("success", null);
        }
        return model;
    }
}
