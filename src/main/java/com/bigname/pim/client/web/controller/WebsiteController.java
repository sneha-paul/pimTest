package com.bigname.pim.client.web.controller;

import com.bigname.pim.api.domain.Website;
import com.bigname.pim.api.exception.EntityNotFoundException;
import com.bigname.pim.api.exception.GenericPlatformException;
import com.bigname.pim.api.service.CatalogService;
import com.bigname.pim.api.service.WebsiteService;
import com.bigname.pim.util.FindBy;
import com.bigname.pim.client.model.Breadcrumbs;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
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
@RequestMapping("pim/websites")
public class WebsiteController {

    private WebsiteService websiteService;
    private CatalogService catalogService;

    public WebsiteController(WebsiteService websiteService, CatalogService catalogService) {
        this.websiteService = websiteService;
        this.catalogService = catalogService;
    }

    @RequestMapping()
    public ModelAndView all() {
        Map<String, Object> model = new HashMap<>();
        model.put("active", "WEBSITES");
        model.put("websites", websiteService.getAll(0, 25, null, false).getContent());
        return new ModelAndView("website/websites", model);
    }

    @RequestMapping(method = RequestMethod.POST)
    public ModelAndView create(@ModelAttribute("website") @Valid Website website, BindingResult result, Model model) {
        if(result.hasErrors()) {
            return new ModelAndView("website/website");
        }
        website.setActive("N");
        websiteService.create(website);
        return new ModelAndView("redirect:/pim/websites");
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.POST)
    public ModelAndView update(@PathVariable(value = "id") String id, @ModelAttribute("website") @Valid Website website, BindingResult result, Model model) {
        if(result.hasErrors()) {
            return new ModelAndView("website/website");
        }
        websiteService.update(id, FindBy.EXTERNAL_ID, website);
        return new ModelAndView("redirect:/pim/websites");
    }

    @RequestMapping(value = {"/{id}", "/create"})
    public ModelAndView details(@PathVariable(value = "id", required = false) String id) {
        Map<String, Object> model = new HashMap<>();
        model.put("active", "WEBSITES");
        if(id == null) {
            model.put("mode", "CREATE");
            model.put("website", new Website());
            model.put("breadcrumbs", new Breadcrumbs("Websites", "Websites", "/pim/websites", "Create Website", ""));
        } else {
            Optional<Website> website = websiteService.get(id, FindBy.EXTERNAL_ID, false);
            if(website.isPresent()) {
                website.get().setCatalogs(websiteService.getWebsiteCatalogs(id, FindBy.EXTERNAL_ID, 0, 25, false));
                model.put("mode", "DETAILS");
                model.put("website", website.get());
                model.put("breadcrumbs", new Breadcrumbs("Websites", "Websites", "/pim/websites", website.get().getWebsiteName(), ""));
            } else {
                throw new EntityNotFoundException("Unable to find Website with website Id: " + id);
            }
        }
        return new ModelAndView("website/website", model);
    }

    @RequestMapping(value = "/{id}/availableCatalogs")
    public ModelAndView availableCatalogs(@PathVariable(value = "id") String id) {
        Map<String, Object> model = new HashMap<>();
        model.put("catalogs", websiteService.getAvailableCatalogsForWebsite(id, FindBy.EXTERNAL_ID));
        return new ModelAndView("catalog/availableCatalogs", model);
    }

    @ResponseBody
    @RequestMapping(value = "/{id}/catalogs/{catalogId}", method = RequestMethod.POST)
    public Map<String, Object> addCatalogs(@PathVariable(value = "id") String id, @PathVariable(value = "catalogId") String catalogId) {
        Map<String, Object> model = new HashMap<>();
        boolean success = websiteService.addCatalog(id, FindBy.EXTERNAL_ID, catalogId, FindBy.EXTERNAL_ID) != null;
        model.put("success", success);
        return model;
    }
}
