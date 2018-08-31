package com.bigname.pim.client.web.controller;

import com.bigname.pim.api.domain.Catalog;
import com.bigname.pim.api.exception.EntityNotFoundException;
import com.bigname.pim.api.exception.GenericPlatformException;
import com.bigname.pim.api.service.CatalogService;
import com.bigname.pim.api.service.CategoryService;
import com.bigname.pim.util.FindBy;
import com.bigname.pim.client.model.Breadcrumbs;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Created by Manu on 8/8/2018.
 */
@Controller
@RequestMapping("pim/catalogs")
public class CatalogController {

    private CatalogService catalogService;
    private CategoryService categoryService;

    public CatalogController(CatalogService catalogService, CategoryService categoryService) {
        this.catalogService = catalogService;
        this.categoryService = categoryService;
    }

    @RequestMapping()
    public ModelAndView all() {
        Map<String, Object> model = new HashMap<>();
        model.put("active", "CATALOGS");
        model.put("catalogs", catalogService.getAll(0, 25, null, false).getContent());
        return new ModelAndView("catalog/catalogs", model);
    }

    @RequestMapping("/available")
    public ModelAndView availableCatalogs() {
        Map<String, Object> model = new HashMap<>();
//        model.put("catalogs", catalogService.getAll(0, 25, null, false).getContent());
        return new ModelAndView("catalog/availableCatalogs", model);
    }

    @RequestMapping(method = RequestMethod.POST)
    public ModelAndView create(@ModelAttribute("catalog") @Valid Catalog catalog, BindingResult result, Model model) {
        if(result.hasErrors()) {
            return new ModelAndView("catalog/catalog");
        }
        catalog.setActive("N");
        catalogService.create(catalog);
        return new ModelAndView("redirect:/pim/catalogs");
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.POST)
    public ModelAndView update(@PathVariable(value = "id") String id, @ModelAttribute("catalog") @Valid Catalog catalog, BindingResult result, Model model) {
        if (result.hasErrors()) {
            return new ModelAndView("catalog/catalog");
        }
        catalogService.update(id, FindBy.EXTERNAL_ID, catalog);
        return new ModelAndView("redirect:/pim/catalogs");
    }

    @RequestMapping(value = {"/{id}", "/create"})
    public ModelAndView details(@PathVariable(value = "id", required = false) String id) {
        Map<String, Object> model = new HashMap<>();
        model.put("active", "CATALOGS");
        if(id == null) {
            model.put("mode", "CREATE");
            model.put("catalog", new Catalog());
            model.put("breadcrumbs", new Breadcrumbs("Catalogs", "Catalogs", "/pim/catalogs", "Create Catalog", ""));
        } else {
            Optional<Catalog> catalog = catalogService.get(id, FindBy.findBy(true), false);
            if(catalog.isPresent()) {
                catalog.get().setCategories(catalogService.getRootCategories(id, FindBy.EXTERNAL_ID, 0, 25, false));
                model.put("mode", "DETAILS");
                model.put("catalog", catalog.get());
                model.put("breadcrumbs", new Breadcrumbs("Catalogs", "Catalogs", "/pim/catalogs", catalog.get().getCatalogName(), ""));
            } else {
                throw new EntityNotFoundException("Unable to find Catalog with catalog Id: " + id);
            }
        }
        return new ModelAndView("catalog/catalog", model);
    }

    @RequestMapping(value = "/{id}/availableCategories")
    public ModelAndView availableCategories(@PathVariable(value = "id") String id) {
        Map<String, Object> model = new HashMap<>();
        model.put("categories", catalogService.getAvailableRootCategoriesForCatalog(id, FindBy.EXTERNAL_ID));
        return new ModelAndView("category/availableCategories", model);
    }
}
