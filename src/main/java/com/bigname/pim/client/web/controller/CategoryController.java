package com.bigname.pim.client.web.controller;

import com.bigname.common.datatable.model.Pagination;
import com.bigname.common.datatable.model.Request;
import com.bigname.common.datatable.model.Result;
import com.bigname.common.datatable.model.SortOrder;
import com.bigname.common.util.ValidationUtil;
import com.bigname.core.domain.EntityAssociation;
import com.bigname.core.exception.EntityNotFoundException;
import com.bigname.core.util.FindBy;
import com.bigname.core.util.Toggle;
import com.bigname.core.web.controller.BaseController;
import com.bigname.pim.api.domain.Category;
import com.bigname.pim.api.domain.CategoryProduct;
import com.bigname.pim.api.domain.Product;
import com.bigname.pim.api.domain.RelatedCategory;
import com.bigname.pim.api.service.CatalogService;
import com.bigname.pim.api.service.CategoryService;
import com.bigname.pim.api.service.WebsiteService;
import com.bigname.pim.data.exportor.CategoryExporter;
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

import static com.bigname.common.util.ValidationUtil.isEmpty;

/**
 * Created by sruthi on 29-08-2018.
 */
@Controller
@RequestMapping("pim/categories")
public class CategoryController extends BaseController<Category, CategoryService> {

    private CategoryService categoryService;

    public CategoryController(CategoryService categoryService, @Lazy CategoryExporter categoryExporter, CatalogService catalogService, WebsiteService websiteService){
        super(categoryService, Category.class, categoryExporter, websiteService, catalogService);
        this.categoryService = categoryService;
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
        return id == null ? super.details(model) : categoryService.get(id, FindBy.findBy(true), false)
                .map(category -> {
                    if(parameterMap.containsKey("parentId")) {
                        model.put("parentId", parameterMap.get("parentId"));
                    }
                    model.put("category", category);
                    return super.details(id, parameterMap, request, model);
                }).orElseThrow(() -> new EntityNotFoundException("Unable to find Category with Id: " + id));
    }

    @RequestMapping()
    public ModelAndView all(@RequestParam(name = "reload", required = false) boolean reload){
        Map<String, Object> model = new HashMap<>();
        model.put("active", "CATEGORIES");
        model.put("view", "category/categories" + (reload ? "_body" : ""));
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
                        return categoryService.getSubCategories(id, FindBy.EXTERNAL_ID, dataTableRequest.getPageRequest(associationSortPredicate), false);
                    } else {
                        return categoryService.findAllSubCategories(id, FindBy.EXTERNAL_ID, "categoryName", dataTableRequest.getSearch(), dataTableRequest.getPageRequest(associationSortPredicate), false);
                    }
                });
    }

    @RequestMapping(value = "/{id}/subCategories/data", method = RequestMethod.PUT)
    @ResponseBody
    public Map<String, Object> setSubCategoriesSequence(@PathVariable(value = "id") String id, @RequestParam Map<String, String> parameterMap) {
        Map<String, Object> model = new HashMap<>();
        boolean success = categoryService.setSubCategorySequence(id, FindBy.EXTERNAL_ID, parameterMap.get("sourceId"), FindBy.EXTERNAL_ID, parameterMap.get("destinationId"), FindBy.EXTERNAL_ID);
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
                        return categoryService.getCategoryProducts(id, FindBy.EXTERNAL_ID, dataTableRequest.getPageRequest(associationSortPredicate), false);
                    } else {
                        return categoryService.findAllCategoryProducts(id, FindBy.EXTERNAL_ID, "productName", dataTableRequest.getSearch(), dataTableRequest.getPageRequest(associationSortPredicate), false);
                    }
                });
    }

    @RequestMapping(value = "/{id}/products/data", method = RequestMethod.PUT)
    @ResponseBody
    public Map<String, Object> setProductsSequence(@PathVariable(value = "id") String id, @RequestParam Map<String, String> parameterMap) {
        Map<String, Object> model = new HashMap<>();
        boolean success = categoryService.setProductSequence(id, FindBy.EXTERNAL_ID, parameterMap.get("sourceId"), FindBy.EXTERNAL_ID, parameterMap.get("destinationId"), FindBy.EXTERNAL_ID);
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
                        return categoryService.getAvailableSubCategoriesForCategory(id, FindBy.EXTERNAL_ID, pageRequest.getPageNumber(), pageRequest.getPageSize(), pageRequest.getSort(), false);
                    } else {
                        return categoryService.findAvailableSubCategoriesForCategory(id, FindBy.EXTERNAL_ID, "categoryName", dataTableRequest.getSearch(), pageRequest, false);
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
        boolean success = categoryService.addSubCategory(id, FindBy.EXTERNAL_ID, subCategoryId, FindBy.EXTERNAL_ID) != null;
        model.put("success", success);
        return model;
    }

    @RequestMapping(value = "/{id}/subCategories/{subCategoryId}/active/{active}", method = RequestMethod.PUT)
    @ResponseBody
    public Map<String, Object> toggleSubCategory(@PathVariable(value = "id") String categoryId,
                                      @PathVariable(value = "subCategoryId") String subCategoryId,
                                      @PathVariable(value = "active") String active) {
        Map<String, Object> model = new HashMap<>();
        model.put("success", categoryService.toggleSubCategory(categoryId, FindBy.EXTERNAL_ID, subCategoryId, FindBy.EXTERNAL_ID, Toggle.get(active)));
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
                        return categoryService.getAvailableProductsForCategory(id, FindBy.EXTERNAL_ID, pageRequest.getPageNumber(), pageRequest.getPageSize(), pageRequest.getSort(), false);
                    } else {
                        return categoryService.findAvailableProductsForCategory(id, FindBy.EXTERNAL_ID, "productName", dataTableRequest.getSearch(), pageRequest, false);
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
    public Map<String, Object> addProduct(@PathVariable(value = "id") String id, @PathVariable(value = "productId") String productId) {
        Map<String, Object> model = new HashMap<>();
        boolean success = categoryService.addProduct(id, FindBy.EXTERNAL_ID, productId, FindBy.EXTERNAL_ID) != null;
        model.put("success", success);
        return model;
    }

    @RequestMapping(value = "/{id}/products/{productId}/active/{active}", method = RequestMethod.PUT)
    @ResponseBody
    public Map<String, Object> toggleProduct(@PathVariable(value = "id") String categoryId,
                                                 @PathVariable(value = "productId") String productId,
                                                 @PathVariable(value = "active") String active) {
        Map<String, Object> model = new HashMap<>();
        model.put("success", categoryService.toggleProduct(categoryId, FindBy.EXTERNAL_ID, productId, FindBy.EXTERNAL_ID, Toggle.get(active)));
        return model;
    }

}
