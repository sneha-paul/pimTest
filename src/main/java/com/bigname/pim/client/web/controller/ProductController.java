package com.bigname.pim.client.web.controller;

import com.bigname.common.datatable.model.Pagination;
import com.bigname.common.datatable.model.Request;
import com.bigname.common.datatable.model.Result;
import com.bigname.common.datatable.model.SortOrder;
import com.bigname.common.util.ValidationUtil;
import com.bigname.pim.api.domain.Channel;
import com.bigname.pim.api.domain.Product;
import com.bigname.pim.api.domain.Family;
import com.bigname.pim.api.domain.ProductVariant;
import com.bigname.pim.api.exception.EntityNotFoundException;
import com.bigname.pim.api.service.ChannelService;
import com.bigname.pim.api.service.FamilyService;
import com.bigname.pim.api.service.ProductService;
import com.bigname.pim.client.model.Breadcrumbs;
import com.bigname.pim.util.FindBy;
import com.bigname.pim.util.PIMConstants;
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
import java.util.stream.Collectors;

/**
 * Created by Manu on 8/3/2018.
 */
@Controller
@RequestMapping("pim/products")
public class ProductController extends BaseController<Product, ProductService>{

    private ProductService productService;
    private FamilyService productFamilyService;
    private ChannelService channelService;

    public ProductController( ProductService productService,FamilyService productFamilyService, ChannelService channelService){
        super(productService);
        this.productService = productService;
        this.productFamilyService = productFamilyService;
        this.channelService = channelService;
    }

    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> create(Product product) {
        Map<String, Object> model = new HashMap<>();
        if(isValid(product, model, Product.CreateGroup.class)) {
            product.setActive("N");
            product.setDiscontinued("N");
            productService.create(product);
            model.put("success", true);
        }
        return model;
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    @ResponseBody
    public Map<String, Object> update(@PathVariable(value = "id") String id, Product product, HttpServletRequest request) {
        Map<String, Object> model = new HashMap<>();
        product.setProductId(id);
        product.setAttributeValues(getAttributesMap(request));
        if(isValid(product, model, product.getGroup().equals("DETAILS") ? Product.DetailsGroup.class : null)) {
            productService.update(id, FindBy.EXTERNAL_ID, product);
            model.put("success", true);
        }
        return model;
    }

    @RequestMapping(value = {"/{id}", "/create"})
    public ModelAndView details(@PathVariable(value = "id", required = false) String id, @RequestParam(name = "channelId", defaultValue = PIMConstants.DEFAULT_CHANNEL_ID) String channelId, @RequestParam(name = "reload", required = false) boolean reload) {
        Map<String, Object> model = new HashMap<>();
        model.put("active", "PRODUCTS");
        model.put("channels", channelService.getAll(0, 100, null).stream().collect(Collectors.toMap(Channel::getChannelId, Channel::getChannelName)));
        if(id == null) {
            model.put("mode", "CREATE");
            model.put("product", new Product(channelId));
            model.put("productFamilyVariantGroups", productFamilyService.getFamilyVariantGroups());
        } else {
            Optional<Product> _product = productService.get(id, FindBy.EXTERNAL_ID, false);
            if(_product.isPresent()) {
                Product product = _product.get();
                product.setChannelId(channelId);
                if(ValidationUtil.isNotEmpty(product.getProductFamilyId())) {
                    Optional<Family> productFamily = productFamilyService.get(product.getProductFamilyId(), FindBy.INTERNAL_ID);
                    productFamily.ifPresent(product::setProductFamily);
                }
                model.put("mode", "DETAILS");
                model.put("product", product);
                model.put("productFamilies", productFamilyService.getAll(0, 100, Sort.by(new Sort.Order(Sort.Direction.ASC, "familyName"))).getContent()); //TODO - JIRA BNPIM-6
                model.put("breadcrumbs", new Breadcrumbs("Product", "Products", "/pim/products", product.getProductName(), ""));
            } else {
                throw new EntityNotFoundException("Unable to find Product with Id: " + id);
            }
        }
        return new ModelAndView("product/product" + (reload ? "_body" : ""), model);
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
