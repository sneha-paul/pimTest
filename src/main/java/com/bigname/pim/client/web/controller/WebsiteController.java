package com.bigname.pim.client.web.controller;

import com.bigname.common.datatable.model.Pagination;
import com.bigname.common.datatable.model.Request;
import com.bigname.common.datatable.model.Result;
import com.bigname.common.datatable.model.SortOrder;
import com.bigname.pim.api.domain.Catalog;
import com.bigname.pim.api.domain.Website;
import com.bigname.pim.api.domain.WebsiteCatalog;
import com.bigname.pim.api.exception.EntityNotFoundException;
import com.bigname.pim.api.exception.GenericPlatformException;
import com.bigname.pim.api.service.CatalogService;
import com.bigname.pim.api.service.WebsiteService;
import com.bigname.pim.util.FindBy;
import com.bigname.pim.client.model.Breadcrumbs;
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
 * Created by Manu on 8/8/2018.
 */
@Controller
@RequestMapping("pim/websites")
public class WebsiteController extends BaseController<Website, WebsiteService>{

    private WebsiteService websiteService;
    private CatalogService catalogService;

    public WebsiteController(WebsiteService websiteService, CatalogService catalogService) {
        super(websiteService);
        this.websiteService = websiteService;
        this.catalogService = catalogService;
    }

    @RequestMapping()
    public ModelAndView all() {
        Map<String, Object> model = new HashMap<>();
        model.put("active", "WEBSITES");
//        model.put("websites", websiteService.getAll(0, 25, null, false).getContent());
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
//                website.get().setCatalogs(websiteService.getWebsiteCatalogs(id, FindBy.EXTERNAL_ID, 0, 25, false));
                model.put("mode", "DETAILS");
                model.put("website", website.get());
                model.put("breadcrumbs", new Breadcrumbs("Websites", "Websites", "/pim/websites", website.get().getWebsiteName(), ""));
            } else {
                throw new EntityNotFoundException("Unable to find Website with website Id: " + id);
            }
        }
        return new ModelAndView("website/website", model);
    }

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

    @RequestMapping(value = "/{id}/catalogs/available")
    public ModelAndView availableCatalogs(@PathVariable(value = "id") String id) {
        Map<String, Object> model = new HashMap<>();
//        model.put("catalogs", websiteService.getAvailableCatalogsForWebsite(id, FindBy.EXTERNAL_ID));
        return new ModelAndView("catalog/availableCatalogs", model);
    }

    @RequestMapping("/{id}/catalogs/available/list")
    @ResponseBody
    public Result<Map<String, String>> getAvailableCatalogs(@PathVariable(value = "id") String id, HttpServletRequest request, HttpServletResponse response, Model model) {
        Request dataTableRequest = new Request(request);
        Pagination pagination = dataTableRequest.getPagination();
        Result<Map<String, String>> result = new Result<>();
        result.setDraw(dataTableRequest.getDraw());
        Sort sort = null;
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

    @ResponseBody
    @RequestMapping(value = "/{id}/catalogs/{catalogId}", method = RequestMethod.POST)
    public Map<String, Object> addCatalogs(@PathVariable(value = "id") String id, @PathVariable(value = "catalogId") String catalogId) {
        Map<String, Object> model = new HashMap<>();
        boolean success = websiteService.addCatalog(id, FindBy.EXTERNAL_ID, catalogId, FindBy.EXTERNAL_ID) != null;
        model.put("success", success);
        return model;
    }
}
