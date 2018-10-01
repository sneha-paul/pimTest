package com.bigname.pim.client.web.controller;

import com.bigname.common.datatable.model.Pagination;
import com.bigname.common.datatable.model.Request;
import com.bigname.common.datatable.model.Result;
import com.bigname.common.datatable.model.SortOrder;
import com.bigname.common.util.ValidationUtil;
import com.bigname.pim.api.domain.Product;
import com.bigname.pim.api.domain.ProductFamily;
import com.bigname.pim.api.domain.ProductVariant;
import com.bigname.pim.api.exception.EntityNotFoundException;
import com.bigname.pim.api.service.ProductFamilyService;
import com.bigname.pim.api.service.ProductService;
import com.bigname.pim.client.model.Breadcrumbs;
import com.bigname.pim.util.FindBy;
import com.bigname.pim.util.Toggle;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.data.domain.Sort;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.*;

/**
 * Created by Manu on 8/3/2018.
 */
@Controller
@RequestMapping("pim/products")
public class ProductController extends BaseController<Product, ProductService>{

    private ProductService productService;
    private ProductFamilyService productFamilyService;

    public ProductController( ProductService productService,ProductFamilyService productFamilyService){
        super(productService);
        this.productService = productService;
        this.productFamilyService = productFamilyService;
    }

    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> create(Product product) {
        Map<String, Object> model = new HashMap<>();
        if(isValid(product, model, Product.CreateGroup.class)) {
            product.setActive("N");
            productService.create(product);
            model.put("success", true);
        }
        return model;
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    @ResponseBody
    public Map<String, Object> update(@PathVariable(value = "id") String id, Product product) {
        Map<String, Object> model = new HashMap<>();
        if(isValid(product, model, product.getGroup().equals("DETAILS") ? Product.DetailsGroup.class : product.getGroup().equals("SEO") ? Product.SeoGroup.class : null)) {
            productService.update(id, FindBy.EXTERNAL_ID, product);
            model.put("success", true);
        }
        return model;
    }

    @RequestMapping(value = {"/{id}", "/create"})
    public ModelAndView details(@PathVariable(value = "id", required = false) String id) {
        Map<String, Object> model = new HashMap<>();
        model.put("active", "PRODUCTS");
        if(id == null) {
            model.put("mode", "CREATE");
            model.put("product", new Product());
            model.put("productFamilies", productFamilyService.getAll(0, 100, Sort.by(new Sort.Order(Sort.Direction.ASC, "productFamilyName"))).getContent()); //TODO - JIRA BNPIM-6
        } else {
            Optional<Product> _product = productService.get(id, FindBy.EXTERNAL_ID, false);
            if(_product.isPresent()) {
                Product product = _product.get();
                if(ValidationUtil.isNotEmpty(product.getProductFamilyId())) {
                    Optional<ProductFamily> productFamily = productFamilyService.get(product.getProductFamilyId(), FindBy.INTERNAL_ID);
                    productFamily.ifPresent(product::setProductFamily);
                }
                model.put("mode", "DETAILS");
                model.put("product", product);
                model.put("productFamilies", productFamilyService.getAll(0, 100, Sort.by(new Sort.Order(Sort.Direction.ASC, "productFamilyName"))).getContent()); //TODO - JIRA BNPIM-6
                model.put("breadcrumbs", new Breadcrumbs("Product", "Products", "/pim/products", product.getProductName(), ""));
            } else {
                throw new EntityNotFoundException("Unable to find Product with Id: " + id);
            }
        }
        return new ModelAndView("product/product", model);
    }



    @RequestMapping()
    public ModelAndView all(){
        Map<String, Object> model = new HashMap<>();
        model.put("active", "PRODUCTS");
        return new ModelAndView("product/products", model);
    }

    @RequestMapping(value = "/{id}/familyAttributes", method = RequestMethod.PUT)
    @ResponseBody
    public Map<String, Object> update(@PathVariable(value = "id") String id, @RequestParam Map<String, Object> attributes) {
        Map<String, Object> model = new HashMap<>();
        Optional<Product> product = productService.get(id, FindBy.EXTERNAL_ID, false);
        product.ifPresent(product1 -> {
            product1.setGroup((String)attributes.remove("group"));
            product1.setFamilyAttributes(attributes);
            productService.update(id, FindBy.EXTERNAL_ID, product1);
        });
        model.put("success", true);
        return model;
    }

    @RequestMapping("/{id}/variants")
    @ResponseBody
    public Result<Map<String, String>> getProductVariants(@PathVariable(value = "id") String id, HttpServletRequest request) {
        Request dataTableRequest = new Request(request);
        Pagination pagination = dataTableRequest.getPagination();
        Result<Map<String, String>> result = new Result<>();
        result.setDraw(dataTableRequest.getDraw());
        Sort sort = null;
        if(pagination.hasSorts()) {
            sort = Sort.by(new Sort.Order(Sort.Direction.valueOf(SortOrder.fromValue(dataTableRequest.getOrder().getSortDir()).name()), dataTableRequest.getOrder().getName()));
        }
        List<Map<String, String>> dataObjects = new ArrayList<>();
        Page<ProductVariant> paginatedResult = productService.getProductVariants(id, FindBy.EXTERNAL_ID, pagination.getPageNumber(), pagination.getPageSize(), sort, false);
        paginatedResult.getContent().forEach(e -> dataObjects.add(e.toMap()));
        result.setDataObjects(dataObjects);
        result.setRecordsTotal(Long.toString(paginatedResult.getTotalElements()));
        result.setRecordsFiltered(Long.toString(pagination.hasFilters() ? paginatedResult.getContent().size() : paginatedResult.getTotalElements())); //TODO - verify this logic
        return result;
    }



}
