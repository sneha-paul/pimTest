package com.bigname.pim.client.web.controller;

import com.bigname.common.datatable.model.Pagination;
import com.bigname.common.datatable.model.Request;
import com.bigname.common.datatable.model.Result;
import com.bigname.common.datatable.model.SortOrder;
import com.bigname.pim.api.domain.*;
import com.bigname.pim.api.exception.EntityNotFoundException;
import com.bigname.pim.api.service.ProductFamilyService;
import com.bigname.pim.client.model.Breadcrumbs;
import com.bigname.pim.util.FindBy;
import com.bigname.pim.util.Toggle;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.*;

/**
 * @author Manu V NarayanaPrasad (manu@blacwood.com)
 * @since 1.0
 */
@Controller
@RequestMapping("pim/productFamilies")
public class ProductFamilyController extends BaseController<ProductFamily, ProductFamilyService> {

    private ProductFamilyService productFamilyService;

    public ProductFamilyController(ProductFamilyService productFamilyService) {
        super(productFamilyService);
        this.productFamilyService = productFamilyService;
    }

    @RequestMapping()
    public ModelAndView all() {
        Map<String, Object> model = new HashMap<>();
        model.put("active", "PRODUCT_FAMILIES");
        return new ModelAndView("product/productFamilies", model);
    }

    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> create( ProductFamily productFamily) {
        Map<String, Object> model = new HashMap<>();
        if(isValid(productFamily, model, ProductFamily.CreateGroup.class)) {
            productFamily.setActive("N");
            productFamilyService.create(productFamily);
            model.put("success", true);
        }
        return model;
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    @ResponseBody
    public Map<String, Object> update(@PathVariable(value = "id") String id, ProductFamily productFamily) {
        Map<String, Object> model = new HashMap<>();
        if(isValid(productFamily, model, productFamily.getGroup().equals("DETAILS") ? ProductFamily.DetailsGroup.class : null)) {
            productFamilyService.update(id, FindBy.EXTERNAL_ID, productFamily);
            model.put("success", true);
        }
        return model;
    }

    @RequestMapping(value = "/{id}/active/{active}", method = RequestMethod.PUT)
    @ResponseBody
    public Map<String, Object> toggle(@PathVariable(value = "id") String id, @PathVariable(value = "active") String active) {
        Map<String, Object> model = new HashMap<>();
        model.put("success", productFamilyService.toggle(id, FindBy.EXTERNAL_ID, Toggle.get(active)));
        return model;
    }

    @RequestMapping(value = {"/{id}", "/create"})
    public ModelAndView details(@PathVariable(value = "id", required = false) String id) {
        Map<String, Object> model = new HashMap<>();
        model.put("active", "PRODUCT_FAMILIES");
        if(id == null) {
            model.put("mode", "CREATE");
            model.put("productFamily", new ProductFamily());
            model.put("breadcrumbs", new Breadcrumbs("Product Families", "Product Families", "/pim/productFamilies", "Create Product Family", ""));
        } else {
            Optional<ProductFamily> productFamily = productFamilyService.get(id, FindBy.EXTERNAL_ID, false);
            if(productFamily.isPresent()) {
                model.put("mode", "DETAILS");
                model.put("productFamily", productFamily.get());
                model.put("breadcrumbs", new Breadcrumbs("Product Families", "Product Families", "/pim/productFamilies", productFamily.get().getProductFamilyName(), ""));
            } else {
                throw new EntityNotFoundException("Unable to find Product Family with Id: " + id);
            }
        }
        return new ModelAndView("product/productFamily", model);
    }

    @RequestMapping("/{id}/{entityType}/attribute")
    public ModelAndView attributeDetails(@PathVariable(value = "id") String id, @PathVariable(value = "entityType") String type) {
        Map<String, Object> model = new HashMap<>();
        model.put("attribute", new Attribute());
        model.put("attributeGroups", productFamilyService.getAttributeGroupsIdNamePair(id, FindBy.EXTERNAL_ID, type, null));
        model.put("parentAttributeGroups", productFamilyService.getParentAttributeGroupsIdNamePair(id, FindBy.EXTERNAL_ID, type, null));
        return new ModelAndView("product/productFamilyAttribute", model);
    }

    @RequestMapping(value = "/{productFamilyId}/attribute", method = RequestMethod.PUT)
    @ResponseBody
    public Map<String, Object> saveAttribute(@PathVariable(value = "productFamilyId") String id, Attribute attribute) {
        Map<String, Object> model = new HashMap<>();
        Optional<ProductFamily> productFamily = productFamilyService.get(id, FindBy.EXTERNAL_ID, false);
        // TODO - cross field validation to see if one of attributeGroup ID and AttributeGroup name is not empty
        if(productFamily.isPresent() && isValid(attribute, model)) {
            productFamily.get().addAttribute(attribute);
            productFamilyService.update(id, FindBy.EXTERNAL_ID, productFamily.get());
            model.put("success", true);
        }
        return model;
    }

    @RequestMapping("/{id}/{entityType}/attributes")
    @ResponseBody
    public Result<Map<String, String>> getFamilyAttributes(@PathVariable(value = "id") String id, @PathVariable(value = "entityType") String entityType, HttpServletRequest request) {

        Request dataTableRequest = new Request(request);
        Pagination pagination = dataTableRequest.getPagination();
        Result<Map<String, String>> result = new Result<>();
        result.setDraw(dataTableRequest.getDraw());
        Sort sort = null;
        if(pagination.hasSorts()) {
            sort = Sort.by(new Sort.Order(Sort.Direction.valueOf(SortOrder.fromValue(dataTableRequest.getOrder().getSortDir()).name()), dataTableRequest.getOrder().getName()));
        }
        List<Map<String, String>> dataObjects = new ArrayList<>();
        Page<Attribute> paginatedResult = productFamilyService.getFamilyAttributes(id, FindBy.EXTERNAL_ID, entityType, pagination.getPageNumber(), pagination.getPageSize(), sort);
        paginatedResult.getContent().forEach(e -> dataObjects.add(e.toMap()));
        result.setDataObjects(dataObjects);
        result.setRecordsTotal(Long.toString(paginatedResult.getTotalElements()));
        result.setRecordsFiltered(Long.toString(paginatedResult.getTotalElements()));
        return result;
    }

    @RequestMapping("/{productFamilyId}/{entityType}/attributes/{attributeId}/options/list")
    @ResponseBody
    public Result<Map<String, String>> getFamilyAttributeOptions(@PathVariable(value = "productFamilyId") String productFamilyId, @PathVariable(value = "entityType") String entityType, @PathVariable(value = "attributeId") String attributeId, HttpServletRequest request) {

        Request dataTableRequest = new Request(request);
        Pagination pagination = dataTableRequest.getPagination();
        Result<Map<String, String>> result = new Result<>();
        result.setDraw(dataTableRequest.getDraw());
        Sort sort = null;
        if(pagination.hasSorts()) {
            sort = Sort.by(new Sort.Order(Sort.Direction.valueOf(SortOrder.fromValue(dataTableRequest.getOrder().getSortDir()).name()), dataTableRequest.getOrder().getName()));
        }
        List<Map<String, String>> dataObjects = new ArrayList<>();
        Page<AttributeOption> paginatedResult = productFamilyService.getFamilyAttributeOptions(productFamilyId, FindBy.EXTERNAL_ID, entityType, attributeId, pagination.getPageNumber(), pagination.getPageSize(), sort);
        paginatedResult.getContent().forEach(e -> dataObjects.add(e.toMap()));
        result.setDataObjects(dataObjects);
        result.setRecordsTotal(Long.toString(paginatedResult.getTotalElements()));
        result.setRecordsFiltered(Long.toString(paginatedResult.getTotalElements()));
        return result;
    }

    @RequestMapping("/{productFamilyId}/{entityType}/attributes/{attributeId}/options")
    public ModelAndView attributeOptions(@PathVariable(value = "productFamilyId") String productFamilyId,
                                         @PathVariable(value = "entityType") String entityType,
                                         @PathVariable(value = "attributeId") String attributeId) {
        Map<String, Object> model = new HashMap<>();
//        model.put("productFamilyId", productFamilyId);
        model.put("attributeId", attributeId);
        model.put("type", entityType);
        return new ModelAndView("product/productFamilyAttributeOptions", model);
    }

    @RequestMapping(value = "/{productFamilyId}/{entityType}/attributes/{attributeId}/options", method = RequestMethod.PUT)
    @ResponseBody
    public Map<String, Object> saveAttributeOptions(
            @PathVariable(value = "productFamilyId") String productFamilyId,
            @PathVariable(value = "entityType") String entityType,
            AttributeOption attributeOption) {
        Map<String, Object> model = new HashMap<>();
        Optional<ProductFamily> productFamily = productFamilyService.get(productFamilyId, FindBy.EXTERNAL_ID, false);
        if(productFamily.isPresent() && isValid(attributeOption, model)) {
            productFamily.get().addAttributeOption(attributeOption, entityType);
            productFamilyService.update(productFamilyId, FindBy.EXTERNAL_ID, productFamily.get());
            model.put("success", true);
        }
        return model;
    }
}