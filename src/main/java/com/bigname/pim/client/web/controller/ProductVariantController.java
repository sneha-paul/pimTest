package com.bigname.pim.client.web.controller;

import com.bigname.common.datatable.model.Pagination;
import com.bigname.common.datatable.model.Request;
import com.bigname.common.datatable.model.Result;
import com.bigname.common.datatable.model.SortOrder;
import com.bigname.common.util.ConversionUtil;
import com.bigname.common.util.ValidationUtil;
import com.bigname.pim.api.domain.Channel;
import com.bigname.pim.api.domain.Family;
import com.bigname.pim.api.domain.Product;
import com.bigname.pim.api.domain.ProductVariant;
import com.bigname.pim.api.exception.EntityNotFoundException;
import com.bigname.pim.api.service.ChannelService;
import com.bigname.pim.api.service.ProductService;
import com.bigname.pim.api.service.ProductVariantService;
import com.bigname.pim.client.model.Breadcrumbs;
import com.bigname.pim.util.FindBy;
import com.bigname.pim.util.PIMConstants;
import com.bigname.pim.util.Toggle;
import org.javatuples.Pair;
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
import java.util.stream.Collectors;

/**
 * Created by sruthi on 20-09-2018.
 */
@Controller
@RequestMapping("pim/products")
public class ProductVariantController extends BaseController<ProductVariant, ProductVariantService> {

    private ProductVariantService productVariantService;

    private ProductService productService;

    private ChannelService channelService;

    public ProductVariantController( ProductVariantService productVariantService, ProductService productService, ChannelService channelService){
        super(productVariantService);
        this.productVariantService = productVariantService;
        this.productService = productService;
        this.channelService = channelService;
    }

    @RequestMapping("/{productId}/channel/{channelId}/variants/available/list")
    @ResponseBody
    public Result<Map<String, String>> getAvailableVariants(@PathVariable(value = "productId") String productId, @PathVariable(value = "channelId") String channelId, HttpServletRequest request) {

        Request dataTableRequest = new Request(request);
        Pagination pagination = dataTableRequest.getPagination();
        Result<Map<String, String>> result = new Result<>();
        result.setDraw(dataTableRequest.getDraw());
        /*Sort sort = null;
        if(pagination.hasSorts()) {
            sort = Sort.by(new Sort.Order(Sort.Direction.valueOf(SortOrder.fromValue(dataTableRequest.getOrder().getSortDir()).name()), dataTableRequest.getOrder().getName()));
        }*/
        List<Map<String, String>> dataObjects = new ArrayList<>();
        Page<Map<String, String>> paginatedResult = productService.getAvailableVariants(productId, FindBy.EXTERNAL_ID, channelId, pagination.getPageNumber(), pagination.getPageSize(), null);
        dataObjects.addAll(paginatedResult.getContent());
        result.setDataObjects(dataObjects);
        result.setRecordsTotal(Long.toString(paginatedResult.getTotalElements()));
        result.setRecordsFiltered(Long.toString(paginatedResult.getTotalElements()));
        return result;
    }



    @RequestMapping(value = "/{productId}/variants", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> create(@PathVariable(value = "productId") String productId, ProductVariant productVariant) {
        Map<String, Object> model = new HashMap<>();
        productVariant.setProductId(productId);
        if(isValid(productVariant, model, ProductVariant.CreateGroup.class)) {
            productVariant.setActive("N");
            productVariantService.create(productVariant);
            model.put("success", true);
        }
        return model;
    }

    @RequestMapping(value = "/{productId}/variants/{variantId}", method = RequestMethod.PUT)
    @ResponseBody
    public Map<String, Object> update(@PathVariable(value = "productId") String productId, @PathVariable(value = "variantId") String variantId, ProductVariant productVariant) {
        Map<String, Object> model = new HashMap<>();
        productVariant.setProductId(productId);
        if(isValid(productVariant, model, productVariant.getGroup().equals("DETAILS") ? ProductVariant.DetailsGroup.class : productVariant.getGroup().equals("SEO") ? ProductVariant.SeoGroup.class : null)) {
            productVariantService.update(variantId, FindBy.EXTERNAL_ID, productVariant);
            model.put("success", true);
        }
        return model;
    }

    @RequestMapping("/{productId}/variants/list")
    @ResponseBody
    @Override
    public Result<Map<String, String>> all(HttpServletRequest request, HttpServletResponse response, Model model) {
        return super.all(request, response, model);
    }

    @RequestMapping(value = {"/{productId}/variants/{variantId}", "/{productId}/variants/create"})
    public ModelAndView variantDetails(@PathVariable(value = "productId") String productId,
                                       @PathVariable(value = "variantId", required = false) String variantId,
                                       @RequestParam(name = "channelId", defaultValue = PIMConstants.DEFAULT_CHANNEL_ID) String channelId) {
        Map<String, Object> model = new HashMap<>();
        model.put("active", "PRODUCTS");
        Optional<Product> _product = productService.get(productId, FindBy.findBy(true), false);
        if(_product.isPresent()) {
            Product product = _product.get();
            if(variantId == null) {
                ProductVariant productVariant = new ProductVariant(product);
                Family family = product.getProductFamily();
                String variantGroupId = family.getChannelVariantGroups().get(channelId);
                model.put("mode", "CREATE");
                model.put("productVariant", productVariant);
                model.put("axisAttributes", ConversionUtil.toJSONString(family.getVariantGroups().get(variantGroupId).getVariantAxis().get(1).stream().collect(Collectors.toMap(id -> id, id -> product.getProductFamily().getAllAttributesMap().get(id).getName()))));
            } else {
                Optional<ProductVariant> _productVariant = productVariantService.get(variantId, FindBy.EXTERNAL_ID, false);
                if(_productVariant.isPresent()) {
                    ProductVariant productVariant = _productVariant.get();
                    productVariant.setProduct(product);
                    model.put("mode", "DETAILS");
                    model.put("channels", channelService.getAll(0, 100, null).stream().collect(Collectors.toMap(Channel::getChannelId, Channel::getChannelName))); //TODO - JIRA BNPIM-6
                    model.put("productVariant", productVariant);
                    model.put("productFamily", productVariant.getProduct().getProductFamily());
                    model.put("breadcrumbs", new Breadcrumbs("Product",
                            "Products", "/pim/products",
                            product.getProductName(), "/pim/products/" + productId,
                            "Product Variants", "/pim/products/" + productId + "#productVariants",
                            productVariant.getProductVariantName(), ""));
                } else {
                    throw new EntityNotFoundException("Unable to find ProductVariant with Id: " + variantId);
                }
            }
        } else {
            throw new EntityNotFoundException("Unable to find Product with Id: " + productId);
        }

        return new ModelAndView("product/productVariant", model);
    }

    @RequestMapping(value = "/{productId}/variants/{variantId}/active/{active}", method = RequestMethod.PUT)
    @Override
    public Map<String, Object> toggle(@PathVariable(value = "variantId") String id, @PathVariable(value = "active") String active) {
        return super.toggle(id, active);
    }

    @RequestMapping(value = "/{productId}/variants/{variantId}/clone/{cloneType}", method = RequestMethod.PUT)
    @Override
    public Map<String, Object> clone(String id, String type) {
        return super.clone(id, type);
    }

    /*@RequestMapping(value = "/{id}", method = RequestMethod.POST)
    public ModelAndView update(@PathVariable(value = "id") String id, @ModelAttribute("productVariant") @Valid ProductVariant productVariant, BindingResult result, Model model) {
        if (result.hasErrors()) {
            return new ModelAndView("product/productVariant");
        }
        productVariantService.update(id, FindBy.EXTERNAL_ID, productVariant);
        return new ModelAndView("redirect:/pim/productVariants");
    }

    @RequestMapping(value = "/{id}/active/{active}", method = RequestMethod.PUT)
    @ResponseBody
    public Map<String, Object> toggle(@PathVariable(value = "id") String id, @PathVariable(value = "active") String active) {
        Map<String, Object> model = new HashMap<>();
        model.put("success", productVariantService.toggle(id, FindBy.EXTERNAL_ID, Toggle.get(active)));
        return model;
    }*/


}
