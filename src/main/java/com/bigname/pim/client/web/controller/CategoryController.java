package com.bigname.pim.client.web.controller;

import com.bigname.common.datatable.model.Pagination;
import com.bigname.common.datatable.model.Request;
import com.bigname.common.datatable.model.Result;
import com.bigname.common.datatable.model.SortOrder;
import com.bigname.common.util.StringUtil;
import com.bigname.common.util.ValidationUtil;
import com.bigname.pim.api.domain.*;
import com.bigname.pim.api.exception.EntityNotFoundException;
import com.bigname.pim.api.service.CatalogService;
import com.bigname.pim.api.service.CategoryService;
import com.bigname.pim.api.service.WebsiteService;
import com.bigname.pim.client.model.Breadcrumbs;
import com.bigname.pim.util.FindBy;
import com.bigname.pim.util.PIMConstants;
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
import java.util.stream.Collectors;

/**
 * Created by sruthi on 29-08-2018.
 */
@Controller
@RequestMapping("pim/categories")
public class CategoryController extends BaseController<Category, CategoryService>{

    private CatalogService catalogService;
    private CategoryService categoryService;

    public CategoryController(CategoryService categoryService, CatalogService catalogService){
        super(categoryService);
        this.categoryService = categoryService;
        this.catalogService = catalogService;
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

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    @ResponseBody
    public Map<String, Object> update(@PathVariable(value = "id") String id, Category category) {
        Map<String, Object> model = new HashMap<>();
        if(isValid(category, model, category.getGroup().length == 1 && category.getGroup()[0].equals("DETAILS") ? Category.DetailsGroup.class :category.getGroup()[0].equals("SEO") ? Category.SeoGroup.class : null)) {
            categoryService.update(id, FindBy.EXTERNAL_ID, category);
            model.put("success", true);
        }
        return model;
    }

    @RequestMapping(value = {"/{id}", "/create"})
    public ModelAndView details(@PathVariable(value = "id", required = false) String id,
                                @RequestParam Map<String, Object> parameterMap,
                                HttpServletRequest request) {


        Map<String, Object> model = new HashMap<>();
        String referrer = getReferrerURL(request, "/pim/categories", "");
        model.put("active", "CATEGORIES");
        if(id == null) {
            model.put("mode", "CREATE");
            model.put("category", new Category());
            model.put("breadcrumbs", new Breadcrumbs("Categories", "Categories", referrer, "Create Category", ""));
        } else {
            Optional<Category> category = categoryService.get(id, FindBy.findBy(true), false);
            if(category.isPresent()) {
                model.put("mode", "DETAILS");
                model.put("category", category.get());
                model.put("backURL", referrer);
                String parentId = parameterMap.containsKey("parentId") ? (String) parameterMap.get("parentId") : "";
                String catalogId = parameterMap.containsKey("catalogId") ? (String) parameterMap.get("catalogId") : "";
                String hash = parameterMap.containsKey("hash") ? (String) parameterMap.get("hash") : "";
                Breadcrumbs breadcrumbs = new Breadcrumbs("Category");
                if(!catalogId.isEmpty()) {
                    breadcrumbs.addCrumbs("Catalogs", "/pim/catalogs");
                    catalogService.get(catalogId, FindBy.EXTERNAL_ID, false)
                            .ifPresent(catalog -> breadcrumbs.addCrumbs(catalog.getCatalogName(), "/pim/catalogs/" + catalog.getCatalogId()));
                    referrer = getReferrerURL(request, "/pim/catalogs/" + catalogId, "/pim/categories/");
                }

                breadcrumbs.addCrumbs("Categories", referrer);
                if(!parentId.isEmpty()) {
                    model.put("parentId", parameterMap.get("parentId"));
                    String[] parentIds = StringUtil.splitPipeDelimited(parentId);
                    Map<String, Category> parentsMap = categoryService.getAll(parentIds, FindBy.EXTERNAL_ID, null, false).stream().collect(Collectors.toMap(Entity::getExternalId, c -> c));
                    String _parentId = "";
                    for (int i = 0; i < parentIds.length; i++) {
                        if(i > 0) {
                            _parentId = (_parentId.isEmpty() ? "" : "|") + parentIds[i - 1];
                        }
                        Category parentCategory = parentsMap.get(parentIds[i]);
                        StringBuilder url = new StringBuilder("/pim/categories/" + parentCategory.getCategoryId());
                        if(!_parentId.isEmpty() || !hash.isEmpty()) {
                            url.append("?");
                            if(!catalogId.isEmpty()) {
                                url.append("catalogId=").append(catalogId);
                                if(!_parentId.isEmpty() || !hash.isEmpty()) {
                                    url.append("&");
                                }
                            }
                            if(!_parentId.isEmpty()) {
                                url.append("parentId=").append(_parentId);
                                if(!hash.isEmpty()) {
                                    url.append("&");
                                }
                            }
                            if(!hash.isEmpty()) {
                                url.append("hash=").append(hash);
                            }
                        }
                        url.append("#subCategories");
                        breadcrumbs.addCrumbs(parentCategory.getCategoryName(), url.toString());
                        if(i == parentIds.length - 1) {
                            model.put("backURL", url.toString());
                        }
                    }
                }
                breadcrumbs.addCrumbs(category.get().getCategoryName(), "");
                model.put("breadcrumbs", breadcrumbs);
            } else {
                throw new EntityNotFoundException("Unable to find Category with Id: " + id);
            }
        }
        return new ModelAndView("category/category", model);
    }

    @RequestMapping()
    public ModelAndView all(@RequestParam(name = "reload", required = false) boolean reload){
        Map<String, Object> model = new HashMap<>();
        model.put("active", "CATEGORIES");
        return new ModelAndView("category/categories" + (reload ? "_body" : ""), model);
    }

    @RequestMapping("/hierarchy")
    @ResponseBody
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> getAllAsHierarchy() {
        return categoryService.getCategoryHierarchy(false);
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
        Page<Category> paginatedResult = categoryService.getAvailableSubCategoriesForCategory(id, FindBy.EXTERNAL_ID, pagination.getPageNumber(), pagination.getPageSize(), sort, false);
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

    @RequestMapping(value = "/{id}/products/available")
    public ModelAndView availableProducts(@PathVariable(value = "id") String id) {
        Map<String, Object> model = new HashMap<>();
        return new ModelAndView("product/availableProducts", model);
    }

    @RequestMapping("/{id}/products/available/list")
    @ResponseBody
    public Result<Map<String, String>> getAvailableProducts(@PathVariable(value = "id") String id, HttpServletRequest request, HttpServletResponse response, Model model) {
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
        Page<Product> paginatedResult = categoryService.getAvailableProductsForCategory(id, FindBy.EXTERNAL_ID, pagination.getPageNumber(), pagination.getPageSize(), sort);
        paginatedResult.getContent().forEach(e -> dataObjects.add(e.toMap()));
        result.setDataObjects(dataObjects);
        result.setRecordsTotal(Long.toString(paginatedResult.getTotalElements()));
        result.setRecordsFiltered(Long.toString(pagination.hasFilters() ? paginatedResult.getContent().size() : paginatedResult.getTotalElements())); //TODO - verify this logic
        return result;
    }

    @ResponseBody
    @RequestMapping(value = "/{id}/products/{productId}", method = RequestMethod.POST)
    public Map<String, Object> addCatalog(@PathVariable(value = "id") String id, @PathVariable(value = "productId") String productId) {
        Map<String, Object> model = new HashMap<>();
        boolean success = categoryService.addProduct(id, FindBy.EXTERNAL_ID, productId, FindBy.EXTERNAL_ID) != null;
        model.put("success", success);
        return model;
    }

}
