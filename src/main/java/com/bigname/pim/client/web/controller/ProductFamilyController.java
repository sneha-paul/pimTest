package com.bigname.pim.client.web.controller;

import com.bigname.common.datatable.model.Pagination;
import com.bigname.common.datatable.model.Request;
import com.bigname.common.datatable.model.Result;
import com.bigname.common.datatable.model.SortOrder;
import com.bigname.pim.api.domain.Attribute;
import com.bigname.pim.api.domain.ProductFamily;
import com.bigname.pim.api.exception.EntityNotFoundException;
import com.bigname.pim.api.service.ProductFamilyService;
import com.bigname.pim.client.model.Breadcrumbs;
import com.bigname.pim.util.FindBy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
    public ModelAndView create(@Valid ProductFamily productFamily, BindingResult result, Model model) {
        if(result.hasErrors()) {
            return new ModelAndView("product/productFamily");
        }
        productFamily.setActive("N");
        productFamilyService.create(productFamily);
        return new ModelAndView("redirect:/pim/productFamilies");
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.POST)
    public ModelAndView update(@PathVariable(value = "id") String id, @Valid ProductFamily productFamily, BindingResult result, Model model) {
        if(result.hasErrors()) {
            return new ModelAndView("product/productFamily");
        }
        productFamilyService.update(id, FindBy.EXTERNAL_ID, productFamily);
        return new ModelAndView("redirect:/pim/productFamilies");
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

    @RequestMapping("/{id}/{type}/attributes")
    @ResponseBody
    public Result<Map<String, String>> getFamilyAttributes(@PathVariable(value = "id") String id, @PathVariable(value = "type") String type, HttpServletRequest request, HttpServletResponse response, Model model) {

        Request dataTableRequest = new Request(request);
        Pagination pagination = dataTableRequest.getPagination();
        Result<Map<String, String>> result = new Result<>();
        result.setDraw(dataTableRequest.getDraw());
        Sort sort = null;
        if(pagination.hasSorts()) {
            sort = Sort.by(new Sort.Order(Sort.Direction.valueOf(SortOrder.fromValue(dataTableRequest.getOrder().getSortDir()).name()), dataTableRequest.getOrder().getName()));
        }
        List<Map<String, String>> dataObjects = new ArrayList<>();
        Page<Attribute> paginatedResult = productFamilyService.getFamilyAttributes(id, FindBy.EXTERNAL_ID, type, pagination.getPageNumber(), pagination.getPageSize(), sort);
        paginatedResult.getContent().forEach(e -> dataObjects.add(e.toMap()));
        result.setDataObjects(dataObjects);
        result.setRecordsTotal(Long.toString(paginatedResult.getTotalElements()));
        result.setRecordsFiltered(Long.toString(paginatedResult.getTotalElements()));
        return result;
    }

    @RequestMapping("/{id}/{type}/attribute")
    public ModelAndView attributeDetails(@PathVariable(value = "id") String id, @PathVariable(value = "type") String type) {
        Map<String, Object> model = new HashMap<>();
        model.put("attribute", new Attribute());
        model.put("type", type);
        model.put("productFamilyId", id);
        return new ModelAndView("product/productFamilyAttribute", model);
    }

    @RequestMapping(value = "/{id}/{type}/attribute", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> saveAttribute(@PathVariable(value = "id") String id, @PathVariable(value = "type") String type, @Valid Attribute attribute, BindingResult result) {
        Map<String, Object> model = new HashMap<>();
        Optional<ProductFamily> productFamily = productFamilyService.get(id, FindBy.EXTERNAL_ID, false);
        if(productFamily.isPresent()) {
            if (result.hasErrors()) {

            } else {
                if(productFamily.get().getProductFamilyAttributes().contains(attribute)) {
                    productFamily.get().getProductFamilyAttributes().remove(attribute);
                }
                productFamily.get().getProductFamilyAttributes().add(attribute);
                if(productFamily.get().getProductVariantFamilyAttributes().contains(attribute)) {
                    productFamily.get().getProductVariantFamilyAttributes().remove(attribute);
                }
                productFamily.get().getProductVariantFamilyAttributes().add(attribute);
                productFamilyService.update(id, FindBy.EXTERNAL_ID, productFamily.get());
                model.put("success", true);
                model.put("attribute", attribute);
            }
        }
        return model;
    }
}
