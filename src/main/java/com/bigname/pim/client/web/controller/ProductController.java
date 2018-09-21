package com.bigname.pim.client.web.controller;

import com.bigname.pim.api.domain.Product;
import com.bigname.pim.api.exception.EntityNotFoundException;
import com.bigname.pim.api.service.ProductFamilyService;
import com.bigname.pim.api.service.ProductService;
import com.bigname.pim.client.model.Breadcrumbs;
import com.bigname.pim.util.FindBy;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
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
    public ModelAndView create(@ModelAttribute("product") @Valid Product product, BindingResult result, Model model) {
        if(result.hasErrors()) {
            return new ModelAndView("product/product");
        }
        product.setActive("N");
        productService.create(product);
        return new ModelAndView("redirect:/pim/products");
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.POST)
    public ModelAndView update(@PathVariable(value = "id") String id, @ModelAttribute("product") @Valid Product product, BindingResult result, Model model) {
        if (result.hasErrors()) {
            return new ModelAndView("product/product");
        }
        productService.update(id, FindBy.EXTERNAL_ID, product);
        return new ModelAndView("redirect:/pim/products");
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
            Optional<Product> product = productService.get(id, FindBy.findBy(true), false);
            if(product.isPresent()) {
                model.put("mode", "DETAILS");
                model.put("product", product.get());
                model.put("productFamilies", productFamilyService.getAll(0, 100, Sort.by(new Sort.Order(Sort.Direction.ASC, "productFamilyName"))).getContent());
                model.put("breadcrumbs", new Breadcrumbs("Product", "Products", "/pim/products", product.get().getProductName(), ""));
            } else {
                throw new EntityNotFoundException("Unable to find Product with Id: " + id);
            }
        }
        return new ModelAndView("product/product", model);
    }

}
