package com.bigname.pim.client.web.controller;

import com.bigname.pim.api.domain.ProductFamily;
import com.bigname.pim.api.exception.EntityNotFoundException;
import com.bigname.pim.api.service.ProductFamilyService;
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

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

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
        model.put("active", "PRODUCTS");
        return new ModelAndView("product/productFamilies", model);
    }

    @RequestMapping(method = RequestMethod.POST)
    public ModelAndView create(@ModelAttribute("productFamily") @Valid ProductFamily productFamily, BindingResult result, Model model) {
        if(result.hasErrors()) {
            return new ModelAndView("product/productFamily");
        }
        productFamily.setActive("N");
        productFamilyService.create(productFamily);
        return new ModelAndView("redirect:/pim/productFamilies");
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.POST)
    public ModelAndView update(@PathVariable(value = "id") String id, @ModelAttribute("productFamily") @Valid ProductFamily productFamily, BindingResult result, Model model) {
        if(result.hasErrors()) {
            return new ModelAndView("product/productFamily");
        }
        productFamilyService.update(id, FindBy.EXTERNAL_ID, productFamily);
        return new ModelAndView("redirect:/pim/productFamilies");
    }

    @RequestMapping(value = {"/{id}", "/create"})
    public ModelAndView details(@PathVariable(value = "id", required = false) String id) {
        Map<String, Object> model = new HashMap<>();
        model.put("active", "PRODUCTS");
        if(id == null) {
            model.put("mode", "CREATE");
            model.put("productFamily", new ProductFamily());
            model.put("breadcrumbs", new Breadcrumbs("Product Families", "Product Families", "/pim/productFamilies", "Create Product Family", ""));
        } else {
            Optional<ProductFamily> productFamily = productFamilyService.get(id, FindBy.EXTERNAL_ID, false);
            if(productFamily.isPresent()) {
//                productFamily.get().setCatalogs(websiteService.getWebsiteCatalogs(id, FindBy.EXTERNAL_ID, 0, 25, false));
                model.put("mode", "DETAILS");
                model.put("productFamily", productFamily.get());
                model.put("breadcrumbs", new Breadcrumbs("Product Families", "Product Families", "/pim/productFamilies", productFamily.get().getProductFamilyName(), ""));
            } else {
                throw new EntityNotFoundException("Unable to find Product Family with Id: " + id);
            }
        }
        return new ModelAndView("product/productFamily", model);
    }
}
