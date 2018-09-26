package com.bigname.pim.client.web.controller;

import com.bigname.common.util.ValidationUtil;
import com.bigname.pim.api.domain.Product;
import com.bigname.pim.api.domain.ProductFamily;
import com.bigname.pim.api.domain.ProductVariant;
import com.bigname.pim.api.exception.EntityNotFoundException;
import com.bigname.pim.api.service.ProductFamilyService;
import com.bigname.pim.api.service.ProductVariantService;
import com.bigname.pim.client.model.Breadcrumbs;
import com.bigname.pim.util.FindBy;
import com.bigname.pim.util.Toggle;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Created by sruthi on 20-09-2018.
 */
@Controller
@RequestMapping("pim/productVariants")
public class ProductVariantController extends BaseController<ProductVariant, ProductVariantService> {

    private ProductVariantService productVariantService;
    private ProductFamilyService productFamilyService;

    public ProductVariantController( ProductVariantService productVariantService, ProductFamilyService productFamilyService){
        super(productVariantService);
        this.productVariantService = productVariantService;
        this.productFamilyService = productFamilyService;
    }

    @RequestMapping()
    public ModelAndView all(){
        Map<String, Object> model = new HashMap<>();
        model.put("active", "PRODUCTVARIANTS");
        return new ModelAndView("product/productVariants", model);
    }

    @RequestMapping(method = RequestMethod.POST)
    public ModelAndView create(@ModelAttribute("productVariant") @Valid ProductVariant productVariant, BindingResult result, Model model) {
        if(result.hasErrors()) {
            return new ModelAndView("product/productVariant");
        }
        productVariant.setActive("N");
        productVariantService.create(productVariant);
        return new ModelAndView("redirect:/pim/productVariants");
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.POST)
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
    }

    @RequestMapping(value = {"/{id}", "/create"})
    public ModelAndView details(@PathVariable(value = "id", required = false) String id) {
        Map<String, Object> model = new HashMap<>();
        model.put("active", "PRODUCTVARIANTS");
        if(id == null) {
            model.put("mode", "CREATE");
            model.put("productVariant", new ProductVariant());
            model.put("productFamilies", productFamilyService.getAll(0, 100, Sort.by(new Sort.Order(Sort.Direction.ASC, "productFamilyName"))).getContent());
            model.put("breadcrumbs", new Breadcrumbs("ProductVariants", "ProductVariants", "/pim/productVariants", "Create ProductVariant", ""));
        } else {
            Optional<ProductVariant> _productVariant = productVariantService.get(id, FindBy.findBy(true), false);
            if(_productVariant.isPresent()) {
                ProductVariant productVariant = _productVariant.get();
                if(ValidationUtil.isNotEmpty(productVariant.getProductFamilyId())) {
                    Optional<ProductFamily> productFamily = productFamilyService.get(productVariant.getProductFamilyId(), FindBy.INTERNAL_ID);
                    productFamily.ifPresent(productVariant::setProductFamily);
                }
                model.put("mode", "DETAILS");
                model.put("productVariant", productVariant);
                model.put("productFamily", productFamilyService.get(productVariant.getProductFamilyId(),FindBy.findBy(true), false));
                model.put("breadcrumbs", new Breadcrumbs("ProductVariant", "ProductVariants", "/pim/productVariants", productVariant.getProductVariantName(), ""));
            } else {
                throw new EntityNotFoundException("Unable to find ProductVariant with Id: " + id);
            }
        }
        return new ModelAndView("product/productVariant", model);
    }
}
