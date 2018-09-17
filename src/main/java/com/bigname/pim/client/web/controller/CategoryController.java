package com.bigname.pim.client.web.controller;

import com.bigname.pim.api.domain.Category;
import com.bigname.pim.api.exception.EntityNotFoundException;
import com.bigname.pim.api.service.CategoryService;
import com.bigname.pim.client.model.Breadcrumbs;
import com.bigname.pim.util.FindBy;
import com.fasterxml.jackson.databind.ser.Serializers;
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
 * Created by sruthi on 29-08-2018.
 */
@Controller
@RequestMapping("pim/categories")
public class CategoryController extends BaseController<Category, CategoryService>{

    private CategoryService categoryService;

    public CategoryController( CategoryService categoryService){
        super(categoryService);
        this.categoryService = categoryService;
    }

    @RequestMapping()
    public ModelAndView all(){
        Map<String, Object> model = new HashMap<>();
        model.put("active", "CATEGORIES");
       // model.put("categories", categoryService.getAll(0, 25, null, false).getContent());
        return new ModelAndView("category/categories", model);
    }

    @RequestMapping("/available")
    public ModelAndView availableCategories() {
        Map<String, Object> model = new HashMap<>();
        return new ModelAndView("category/availableCategories", model);
    }

    @RequestMapping(method = RequestMethod.POST)
    public ModelAndView create(@ModelAttribute("category") @Valid Category category, BindingResult result, Model model) {
        if(result.hasErrors()) {
            return new ModelAndView("category/category");
        }
        category.setActive("N");
        categoryService.create(category);
        return new ModelAndView("redirect:/pim/categories");
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.POST)
    public ModelAndView update(@PathVariable(value = "id") String id, @ModelAttribute("category") @Valid Category category, BindingResult result, Model model) {
        if (result.hasErrors()) {
            return new ModelAndView("category/category");
        }
        categoryService.update(id, FindBy.EXTERNAL_ID, category);
        return new ModelAndView("redirect:/pim/categories");
    }

    @RequestMapping(value = {"/{id}", "/create"})
    public ModelAndView details(@PathVariable(value = "id", required = false) String id) {
        Map<String, Object> model = new HashMap<>();
        model.put("active", "CATEGORIES");
        if(id == null) {
            model.put("mode", "CREATE");
            model.put("category", new Category());
            model.put("breadcrumbs", new Breadcrumbs("Categories", "Categories", "/pim/categories", "Create Category", ""));
        } else {
            Optional<Category> category = categoryService.get(id, FindBy.findBy(true), false);
            if(category.isPresent()) {
                category.get().setSubCategories(categoryService.getSubCategories(id, FindBy.EXTERNAL_ID, 0, 25, false));
                model.put("mode", "DETAILS");
                model.put("category", category.get());
                model.put("breadcrumbs", new Breadcrumbs("Category", "Categories", "/pim/categories", category.get().getCategoryName(), ""));
            } else {
                throw new EntityNotFoundException("Unable to find Category with category Id: " + id);
            }
        }
        return new ModelAndView("category/category", model);
    }

    @RequestMapping(value = "/{id}/availableSubCategories")
    public ModelAndView availableCategories(@PathVariable(value = "id") String id) {
        Map<String, Object> model = new HashMap<>();
        model.put("categories", categoryService.getAvailableSubCategoriesForCategory(id, FindBy.EXTERNAL_ID));
        return new ModelAndView("category/availableSubCategories", model);
    }

    @ResponseBody
    @RequestMapping(value = "/{id}/subCategories/{subCategoryId}", method = RequestMethod.POST)
    public Map<String, Object> addCategory(@PathVariable(value = "id") String id, @PathVariable(value = "subCategoryId") String subCategoryId) {
        Map<String, Object> model = new HashMap<>();
        boolean success = categoryService.addSubCategory(id, FindBy.EXTERNAL_ID, subCategoryId, FindBy.EXTERNAL_ID) != null;
        model.put("success", success);
        return model;
    }
}
