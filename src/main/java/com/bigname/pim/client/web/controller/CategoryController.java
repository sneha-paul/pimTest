package com.bigname.pim.client.web.controller;

import com.bigname.common.datatable.model.Pagination;
import com.bigname.common.datatable.model.Request;
import com.bigname.common.datatable.model.Result;
import com.bigname.common.datatable.model.SortOrder;
import com.bigname.common.util.ValidationUtil;
import com.bigname.pim.api.domain.Category;
import com.bigname.pim.api.domain.RelatedCategory;
import com.bigname.pim.api.domain.CategoryProduct;
import com.bigname.pim.api.exception.EntityNotFoundException;
import com.bigname.pim.api.service.CategoryService;
import com.bigname.pim.client.model.Breadcrumbs;
import com.bigname.pim.util.FindBy;
import com.bigname.pim.util.Toggle;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.*;

/**
 * Created by sruthi on 29-08-2018.
 */
@Controller
@RequestMapping("pim/categories")
public class CategoryController extends BaseController<Category, CategoryService>{

    private CategoryService categoryService;

    public CategoryController(CategoryService categoryService){
        super(categoryService);
        this.categoryService = categoryService;
    }

    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> create(Category category) {
        Map<String, Object> model = new HashMap<>();
        if(isValid(category, model, Category.CreateGroup.class)) {
            category.setActive("N");
            categoryService.create(category);
            model.put("success", true);
            model.put("path", "/pim/categories");
        }
        return model;
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    @ResponseBody
    public Map<String, Object> update(@PathVariable(value = "id") String id, Category category) {
        Map<String, Object> model = new HashMap<>();
        if(isValid(category, model, category.getGroup().equals("DETAILS") ? Category.DetailsGroup.class : category.getGroup().equals("SEO") ? Category.SeoGroup.class : null)) {
            categoryService.update(id, FindBy.EXTERNAL_ID, category);
            model.put("success", true);
        }
        return model;
    }

    @RequestMapping(value = "/{id}/active/{active}", method = RequestMethod.PUT)
    @ResponseBody
    public Map<String, Object> toggle(@PathVariable(value = "id") String id, @PathVariable(value = "active") String active) {
        Map<String, Object> model = new HashMap<>();
        model.put("success", categoryService.toggle(id, FindBy.EXTERNAL_ID, Toggle.get(active)));
        return model;
    }

    @RequestMapping(value = {"/{id}", "/create"})
    public ModelAndView details(@PathVariable(value = "id", required = false) String id) {
        Map<String, Object> model = new HashMap<>();
        model.put("active", "CATEGORIES");
        if(id == null) {
            model.put("mode", "CREATE");
            model.put("category", new Category());
            model.put("breadcrumbs", new Breadcrumbs("Categories", "Categories", "/pim/categories", "Create Category", ""));
        } else {
            Optional<Category> category = categoryService.get(id, FindBy.findBy(true), false);
            if(category.isPresent()) {
                model.put("mode", "DETAILS");
                model.put("category", category.get());
                model.put("breadcrumbs", new Breadcrumbs("Category", "Categories", "/pim/categories", category.get().getCategoryName(), ""));
            } else {
                throw new EntityNotFoundException("Unable to find Category with Id: " + id);
            }
        }
        return new ModelAndView("category/category", model);
    }

    @RequestMapping()
    public ModelAndView all(){
        Map<String, Object> model = new HashMap<>();
        model.put("active", "CATEGORIES");
        return new ModelAndView("category/categories", model);
    }

    @RequestMapping("/{id}/subCategories")
    @ResponseBody
    public Result<Map<String, String>> getSubCategories(@PathVariable(value = "id") String id, HttpServletRequest request, HttpServletResponse response, Model model) {
        Request dataTableRequest = new Request(request);
        Pagination pagination = dataTableRequest.getPagination();
        Result<Map<String, String>> result = new Result<>();
        result.setDraw(dataTableRequest.getDraw());
        Sort sort = null;
        if(pagination.hasSorts()) {
            sort = Sort.by(new Sort.Order(Sort.Direction.valueOf(SortOrder.fromValue(dataTableRequest.getOrder().getSortDir()).name()), dataTableRequest.getOrder().getName()));
        }
        List<Map<String, String>> dataObjects = new ArrayList<>();
        Page<RelatedCategory> paginatedResult = categoryService.getSubCategories(id, FindBy.EXTERNAL_ID, pagination.getPageNumber(), pagination.getPageSize(), sort, false);
        paginatedResult.getContent().forEach(e -> dataObjects.add(e.toMap()));
        result.setDataObjects(dataObjects);
        result.setRecordsTotal(Long.toString(paginatedResult.getTotalElements()));
        result.setRecordsFiltered(Long.toString(pagination.hasFilters() ? paginatedResult.getContent().size() : paginatedResult.getTotalElements())); //TODO - verify this logic
        return result;
    }

    @RequestMapping(value = "/{id}/subCategories/available")
    public ModelAndView availableCategories(@PathVariable(value = "id") String id) {
        Map<String, Object> model = new HashMap<>();
        return new ModelAndView("category/availableSubCategories", model);
    }

    @RequestMapping("/{id}/subCategories/available/list")
    @ResponseBody
    public Result<Map<String, String>> getAvailableSubCategories(@PathVariable(value = "id") String id, HttpServletRequest request, HttpServletResponse response, Model model) {
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
        Page<Category> paginatedResult = categoryService.getAvailableSubCategoriesForCategory(id, FindBy.EXTERNAL_ID, pagination.getPageNumber(), pagination.getPageSize(), sort);
        paginatedResult.getContent().forEach(e -> dataObjects.add(e.toMap()));
        result.setDataObjects(dataObjects);
        result.setRecordsTotal(Long.toString(paginatedResult.getTotalElements()));
        result.setRecordsFiltered(Long.toString(pagination.hasFilters() ? paginatedResult.getContent().size() : paginatedResult.getTotalElements())); //TODO - verify this logic
        return result;
    }

    @ResponseBody
    @RequestMapping(value = "/{id}/subCategories/{subCategoryId}", method = RequestMethod.POST)
    public Map<String, Object> addSubCategory(@PathVariable(value = "id") String id, @PathVariable(value = "subCategoryId") String subCategoryId) {
        Map<String, Object> model = new HashMap<>();
        boolean success = categoryService.addSubCategory(id, FindBy.EXTERNAL_ID, subCategoryId, FindBy.EXTERNAL_ID) != null;
        model.put("success", success);
        return model;
    }

    @RequestMapping("/{id}/products")
    @ResponseBody
    public Result<Map<String, String>> getCategoryProducts(@PathVariable(value = "id") String id, HttpServletRequest request, HttpServletResponse response, Model model) {
        Request dataTableRequest = new Request(request);
        Pagination pagination = dataTableRequest.getPagination();
        Result<Map<String, String>> result = new Result<>();
        result.setDraw(dataTableRequest.getDraw());
        Sort sort = null;
        if(pagination.hasSorts()) {
            sort = Sort.by(new Sort.Order(Sort.Direction.valueOf(SortOrder.fromValue(dataTableRequest.getOrder().getSortDir()).name()), dataTableRequest.getOrder().getName()));
        }
        List<Map<String, String>> dataObjects = new ArrayList<>();
        Page<CategoryProduct> paginatedResult = categoryService.getCategoryProducts(id, FindBy.EXTERNAL_ID, pagination.getPageNumber(), pagination.getPageSize(), sort, false);
        paginatedResult.getContent().forEach(e -> dataObjects.add(e.toMap()));
        result.setDataObjects(dataObjects);
        result.setRecordsTotal(Long.toString(paginatedResult.getTotalElements()));
        result.setRecordsFiltered(Long.toString(pagination.hasFilters() ? paginatedResult.getContent().size() : paginatedResult.getTotalElements())); //TODO - verify this logic
        return result;
    }

}
