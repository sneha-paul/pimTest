package com.bigname.pim.client.web.controller;

import com.bigname.pim.api.domain.Category;
import com.bigname.pim.api.domain.CategoryProduct;
import com.bigname.pim.api.domain.RelatedCategory;
import com.bigname.pim.api.service.CatalogService;
import com.bigname.pim.api.service.CategoryService;
import com.bigname.pim.api.service.WebsiteService;
import com.bigname.pim.data.exportor.CategoryExporter;
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

import static com.m7.xtreme.common.util.ValidationUtil.isEmpty;
import static com.m7.xtreme.common.util.ValidationUtil.isNotEmpty;


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
        Category category1 = categoryService.get(ID.EXTERNAL_ID(categoryId), false).orElse(null);

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
            Category category = categoryService.get(ID.EXTERNAL_ID(id), false).orElse(null);
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
    public Map<String, Object> addProduct(@PathVariable(value = "id") String id, @PathVariable(value = "productId") String productId) {
        Map<String, Object> model = new HashMap<>();
        boolean success = categoryService.addProduct(ID.EXTERNAL_ID(id), ID.EXTERNAL_ID(productId)) != null;
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
        model.put("success", categoryService.archive(ID.EXTERNAL_ID(categoryId), Archive.get(archived)));
        return model;
    }

}
