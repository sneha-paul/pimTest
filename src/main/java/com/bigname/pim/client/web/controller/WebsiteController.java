package com.bigname.pim.client.web.controller;

import com.bigname.common.datatable.model.Pagination;
import com.bigname.common.datatable.model.Request;
import com.bigname.common.datatable.model.Result;
import com.bigname.common.datatable.model.SortOrder;
import com.bigname.pim.api.domain.Catalog;
import com.bigname.pim.api.domain.Website;
import com.bigname.pim.api.domain.WebsiteCatalog;
import com.bigname.pim.api.exception.EntityNotFoundException;
import com.bigname.pim.api.service.WebsiteService;
import com.bigname.pim.client.model.Breadcrumbs;
import com.bigname.pim.util.FindBy;
import com.bigname.pim.util.Toggle;
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

/**
 * The Controller class for Website
 *
 * @author Manu V NarayanaPrasad (manu@blacwood.com)
 * @since 1.0
 */
@Controller
@RequestMapping("pim/websites")
public class WebsiteController extends BaseController<Website, WebsiteService>{

    private WebsiteService websiteService;

    public WebsiteController(WebsiteService websiteService) {
        super(websiteService);
        this.websiteService = websiteService;
    }


    /**
     * Handler method to create a new website
     *
     * @param website The website model attribute that needs to be created
     * @param result The BindingResult instance
     * @param model The Model instance
     *
     * @return The ModelAndView instance for the list websites page (if no validation errors),
     *         otherwise the ModelAndView instance for the website details page to show the validation errors
     */
    /*@RequestMapping(method = RequestMethod.POST)
    public ModelAndView create(@ModelAttribute("website") @Valid Website website, BindingResult result, Model model) {
        if(result.hasErrors()) {
            return new ModelAndView("website/website");
        }
        website.setActive("N");
        websiteService.create(website);
        return new ModelAndView("redirect:/pim/websites");
    }*/

    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> create( Website website) {
        Map<String, Object> model = new HashMap<>();
        if(isValid(website, model, Website.CreateGroup.class)) {
            website.setActive("N");
            websiteService.create(website);
            model.put("success", true);
            model.put("path", "/pim/websites");
        }
        return model;
    }

    /**
     * Handler method to update a website instance
     * @param id websiteId of the website instance that needs to be updated
     * @param website The modified website instance corresponding to the given websiteId
     * @param result The BindingResult instance
     * @param model The Model instance
     *
     * @return The ModelAndView instance for the list websites page (if no validation errors),
     *         otherwise the ModelAndView instance for the website details page to show the validation errors
     */
    /*@RequestMapping(value = "/{id}", method = RequestMethod.POST)
    public ModelAndView update(@PathVariable(value = "id") String id, @ModelAttribute("website") @Valid Website website, BindingResult result, Model model) {
        if(result.hasErrors()) {
            return new ModelAndView("website/website");
        }
        websiteService.update(id, FindBy.EXTERNAL_ID, website);
        return new ModelAndView("redirect:/pim/websites");
    }*/

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    @ResponseBody
    public Map<String, Object> update(@PathVariable(value = "id") String id, Website website) {
        Map<String, Object> model = new HashMap<>();
        //if(isValid(website, model, website.getGroup().equals("DETAILS") ? Website.DetailsGroup.class : website.getGroup().equals("SEO") ? Website.SeoGroup.class : null)) {
        websiteService.update(id, FindBy.EXTERNAL_ID, website);
        model.put("success", true);
        //}
        return model;
    }

    @RequestMapping(value = "/{id}/active/{active}", method = RequestMethod.PUT)
    @ResponseBody
    public Map<String, Object> toggle(@PathVariable(value = "id") String id, @PathVariable(value = "active") String active) {
        Map<String, Object> model = new HashMap<>();
        model.put("success", websiteService.toggle(id, FindBy.EXTERNAL_ID, Toggle.get(active)));
        return model;
    }

    /**
     * Handler method to load the website details page or the create new website page
     *
     * @param id websiteId of the website instance that needs to be loaded
     *
     * @return The ModelAndView instance for the details page or create page depending on the presence of the 'id' pathVariable
     */
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
                model.put("mode", "DETAILS");
                model.put("website", website.get());
                model.put("breadcrumbs", new Breadcrumbs("Websites", "Websites", "/pim/websites", website.get().getWebsiteName(), ""));
            } else {
                throw new EntityNotFoundException("Unable to find Website with Id: " + id);
            }
        }
        return new ModelAndView("website/website", model);
    }

    /**
     * Handler method to load the list websites page
     *
     * @return The ModelAndView instance for the list websites page
     */
    @RequestMapping()
    public ModelAndView all() {
        Map<String, Object> model = new HashMap<>();
        model.put("active", "WEBSITES");
        return new ModelAndView("website/websites", model);
    }

    /**
     * Handler method to load a list of all the catalogs associated with the given websiteId.
     * This is a JSON data endpoint required for the dataTable
     *
     * @param id
     * @param request
     * @param response
     * @param model
     * @return
     */
    @RequestMapping("/{id}/catalogs")
    @ResponseBody
    public Result<Map<String, String>> getWebsiteCatalogs(@PathVariable(value = "id") String id, HttpServletRequest request, HttpServletResponse response, Model model) {
        Request dataTableRequest = new Request(request);
        Pagination pagination = dataTableRequest.getPagination();
        Result<Map<String, String>> result = new Result<>();
        result.setDraw(dataTableRequest.getDraw());
        Sort sort = null;
        if(pagination.hasSorts()) {
            sort = Sort.by(new Sort.Order(Sort.Direction.valueOf(SortOrder.fromValue(dataTableRequest.getOrder().getSortDir()).name()), dataTableRequest.getOrder().getName()));
        }
        List<Map<String, String>> dataObjects = new ArrayList<>();
        Page<WebsiteCatalog> paginatedResult = websiteService.getWebsiteCatalogs(id, FindBy.EXTERNAL_ID, pagination.getPageNumber(), pagination.getPageSize(), sort, false);
        paginatedResult.getContent().forEach(e -> dataObjects.add(e.toMap()));
        result.setDataObjects(dataObjects);
        result.setRecordsTotal(Long.toString(paginatedResult.getTotalElements()));
        result.setRecordsFiltered(Long.toString(pagination.hasFilters() ? paginatedResult.getContent().size() : paginatedResult.getTotalElements())); //TODO - verify this logic
        return result;
    }

    /**
     * Handler method for the availableCatalogs page.
     *
     * @param id
     * @return
     */
    @RequestMapping(value = "/{id}/catalogs/available")
    public ModelAndView availableCatalogs(@PathVariable(value = "id") String id) {
        Map<String, Object> model = new HashMap<>();
        return new ModelAndView("catalog/availableCatalogs", model);
    }

    /**
     * Handler method to load a list of all the available catalogs that can be associated to the given websiteId.
     * This is a JSON data endpoint required for the dataTable
     *
     * @param id
     * @param request
     * @param response
     * @param model
     * @return
     */
    @RequestMapping("/{id}/catalogs/available/list")
    @ResponseBody
    public Result<Map<String, String>> getAvailableCatalogs(@PathVariable(value = "id") String id, HttpServletRequest request, HttpServletResponse response, Model model) {
        Request dataTableRequest = new Request(request);
        Pagination pagination = dataTableRequest.getPagination();
        Result<Map<String, String>> result = new Result<>();
        result.setDraw(dataTableRequest.getDraw());
        Sort sort;
        if(pagination.hasSorts()) {
            sort = Sort.by(new Sort.Order(Sort.Direction.valueOf(SortOrder.fromValue(dataTableRequest.getOrder().getSortDir()).name()), dataTableRequest.getOrder().getName()));
        } else {
            sort = Sort.by(new Sort.Order(Sort.Direction.ASC, "externalId"));
        }
        List<Map<String, String>> dataObjects = new ArrayList<>();
        Page<Catalog> paginatedResult = websiteService.getAvailableCatalogsForWebsite(id, FindBy.EXTERNAL_ID, pagination.getPageNumber(), pagination.getPageSize(), sort);
        paginatedResult.getContent().forEach(e -> dataObjects.add(e.toMap()));
        result.setDataObjects(dataObjects);
        result.setRecordsTotal(Long.toString(paginatedResult.getTotalElements()));
        result.setRecordsFiltered(Long.toString(pagination.hasFilters() ? paginatedResult.getContent().size() : paginatedResult.getTotalElements())); //TODO - verify this logic
        return result;
    }

    /**
     * Handler method to associate a catalog to a website
     *
     * @param id
     * @param catalogId
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/{id}/catalogs/{catalogId}", method = RequestMethod.POST)
    public Map<String, Object> addCatalog(@PathVariable(value = "id") String id, @PathVariable(value = "catalogId") String catalogId) {
        Map<String, Object> model = new HashMap<>();
        boolean success = websiteService.addCatalog(id, FindBy.EXTERNAL_ID, catalogId, FindBy.EXTERNAL_ID) != null;
        model.put("success", success);
        return model;
    }
}
