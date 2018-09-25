package com.bigname.pim.client.web.controller;

import com.bigname.common.util.ValidationUtil;
import com.bigname.pim.api.domain.Product;
import com.bigname.pim.api.domain.ProductFamily;
import com.bigname.pim.api.exception.EntityNotFoundException;
import com.bigname.pim.api.service.ProductFamilyService;
import com.bigname.pim.api.service.ProductService;
import com.bigname.pim.client.model.Breadcrumbs;
import com.bigname.pim.util.FindBy;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.data.domain.Sort;


import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

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

    @RequestMapping()
    public ModelAndView all(){
        Map<String, Object> model = new HashMap<>();
        model.put("active", "PRODUCTS");
        return new ModelAndView("product/products", model);
    }

    @RequestMapping(method = RequestMethod.POST)
    public ModelAndView create(Product product, BindingResult result, Model model) {
        /*if(result.hasErrors()) {
            return new ModelAndView("product/product");
        }*/
        product.setActive("N");

        productService.create(product);
        return new ModelAndView("redirect:/pim/products");
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.POST)
    public ModelAndView update(@PathVariable(value = "id") String id, @ModelAttribute("product") @Valid Product product, BindingResult result, Model model) {
        /*if (result.hasErrors()) {
            return new ModelAndView("product/product");
        }*/
        productService.update(id, FindBy.EXTERNAL_ID, product);
        return new ModelAndView("redirect:/pim/products");
    }

    @RequestMapping(value = "/{id}/familyAttributes", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> update(@PathVariable(value = "id") String id, @RequestParam Map<String, Object> attributes) {
        Map<String, Object> model = new HashMap<>();
        Optional<Product> product = productService.get(id, FindBy.EXTERNAL_ID, false);
        product.ifPresent(product1 -> {
            product1.setFamilyAttributes(attributes);
            productService.update(id, FindBy.EXTERNAL_ID, product1);
        });
        model.put("success", true);
        return model;
    }

    @RequestMapping(value = {"/{id}", "/create"})
    public ModelAndView details(@PathVariable(value = "id", required = false) String id) {
        Map<String, Object> model = new HashMap<>();
        model.put("active", "PRODUCTS");
        if(id == null) {
            model.put("mode", "CREATE");
            model.put("product", new Product());
            model.put("productFamilies", productFamilyService.getAll(0, 100, Sort.by(new Sort.Order(Sort.Direction.ASC, "productFamilyName"))).getContent());
            model.put("breadcrumbs", new Breadcrumbs("Products", "Products", "/pim/products", "Create Product", ""));
        } else {
            Optional<Product> _product = productService.get(id, FindBy.findBy(true), false);
            if(_product.isPresent()) {
                Product product = _product.get();
                if(ValidationUtil.isNotEmpty(product.getProductFamilyId())) {
                    Optional<ProductFamily> productFamily = productFamilyService.get(product.getProductFamilyId(), FindBy.INTERNAL_ID);
                    productFamily.ifPresent(product::setProductFamily);
                }
                model.put("mode", "DETAILS");
                model.put("product", product);
                model.put("productFamilies", productFamilyService.getAll(0, 100, Sort.by(new Sort.Order(Sort.Direction.ASC, "productFamilyName"))).getContent());
                model.put("breadcrumbs", new Breadcrumbs("Product", "Products", "/pim/products", product.getProductName(), ""));
            } else {
                throw new EntityNotFoundException("Unable to find Product with Id: " + id);
            }
        }
        return new ModelAndView("product/product", model);
    }

}
