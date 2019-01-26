package com.bigname.pim.client.web.controller;

import com.bigname.common.datatable.model.Pagination;
import com.bigname.common.datatable.model.Request;
import com.bigname.common.datatable.model.Result;
import com.bigname.common.datatable.model.SortOrder;
import com.bigname.common.util.CollectionsUtil;
import com.bigname.common.util.ValidationUtil2;
import com.bigname.pim.api.domain.*;
import com.bigname.pim.api.exception.EntityNotFoundException;
import com.bigname.pim.api.service.WebsiteService;
import com.bigname.pim.util.FindBy;
import com.bigname.pim.util.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.bigname.common.util.ValidationUtil.isEmpty;

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
        super(websiteService, Website.class);
        this.websiteService = websiteService;
    }


    /**
     * Handler method to create a new website
     *
     * @param website The website model attribute that needs to be created
     *
     * @return a map of model attributes
     */

    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> create( Website website) {
        Map<String, Object> model = new HashMap<>();
        if(isValid(website, model, Website.CreateGroup.class)) {
            website.setActive("N");
            websiteService.create(website);
            model.put("success", true);
        }
        return model;
    }

    /**
     * Handler method to update a website instance
     *
     * @param id websiteId of the website instance that needs to be updated
     * @param website The modified website instance corresponding to the given websiteId
     *
     * @return a map of model attributes
     */

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    @ResponseBody
    public Map<String, Object> update(@PathVariable(value = "id") String id, Website website) {
        Map<String, Object> model = new HashMap<>();
        model.put("context", CollectionsUtil.toMap("id", id));
        if(isValid(website, model, website.getGroup().length == 1 && website.getGroup()[0].equals("DETAILS") ? Website.DetailsGroup.class : null)) {
            websiteService.update(id, FindBy.EXTERNAL_ID, website);
            model.put("success", true);
            if(!id.equals(website.getWebsiteId())) {
                model.put("refreshUrl", "/pim/websites/" + website.getWebsiteId());
            }
        }
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
    public ModelAndView details(@PathVariable(value = "id", required = false) String id,
                                @RequestParam(name = "reload", required = false) boolean reload) {

        Map<String, Object> model = new HashMap<>();
        model.put("active", "WEBSITES");
        model.put("mode", id == null ? "CREATE" : "DETAILS");
        model.put("view", "website/website" + (reload ? "_body" : ""));

        return id == null ? super.details(model) : websiteService.get(id, FindBy.EXTERNAL_ID, false)
                .map(website -> {
                    model.put("website", website);
                    return super.details(id, model);
                }).orElseThrow(() -> new EntityNotFoundException("Unable to find Website with Id: " + id));
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

    @RequestMapping(value =  {"/list", "/data"})
    @ResponseBody
    @SuppressWarnings("unchecked")
    public Result<Map<String, String>> all(HttpServletRequest request, HttpServletResponse response, Model model) {
        Request dataTableRequest = new Request(request);
        if(isEmpty(dataTableRequest.getSearch())) {
            return super.all(request, response, model);
        } else {
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
            Page<Website> paginatedResult = websiteService.findAll("websiteName", dataTableRequest.getSearch(), new Pageable(pagination.getPageNumber(), pagination.getPageSize(), sort), false);
            paginatedResult.forEach(e -> dataObjects.add(e.toMap()));
            result.setDataObjects(dataObjects);
            result.setRecordsTotal(Long.toString(paginatedResult.getTotalElements()));
            result.setRecordsFiltered(Long.toString(paginatedResult.getTotalElements()));
            return result;
        }
    }

    /**
     * Handler method to load a list of all the catalogs associated with the given websiteId.
     * This is a JSON data endpoint required for the dataTable
     *
     * @param id
     * @param request
     * @return
     */
    @RequestMapping("/{id}/catalogs/data")
    @ResponseBody
    public Result<Map<String, Object>> getWebsiteCatalogs(@PathVariable(value = "id") String id, HttpServletRequest request) {
        Request dataTableRequest = new Request(request);
        if(ValidationUtil2.isEmpty(dataTableRequest.getSearch())) {
            return getAssociationGridData(websiteService.getWebsiteCatalogs(id, FindBy.EXTERNAL_ID, getPaginationRequest(request), false), WebsiteCatalog.class, request);
        } else {
            Pagination pagination = dataTableRequest.getPagination();
            Result<Map<String, Object>> result = new Result<>();
            result.setDraw(dataTableRequest.getDraw());
            Sort sort = null;
            if(pagination.hasSorts() && !dataTableRequest.getOrder().getName().equals("sequenceNum")) {
                sort = Sort.by(new Sort.Order(Sort.Direction.valueOf(SortOrder.fromValue(dataTableRequest.getOrder().getSortDir()).name()), dataTableRequest.getOrder().getName()));
            }
            List<Map<String, Object>> dataObjects = new ArrayList<>();
            int seq[] = {1};
            Page<Map<String, Object>> paginatedResult = websiteService.findAllWebsiteCatalogs(id, FindBy.EXTERNAL_ID, "catalogName", dataTableRequest.getSearch(), new Pageable(pagination.getPageNumber(), pagination.getPageSize(), sort), false);
            EntityAssociation<Website, Catalog> association = new WebsiteCatalog();
            paginatedResult.getContent().forEach(e -> {
                e.put("sequenceNum", Integer.toString(seq[0] ++));
                dataObjects.add(association.toMap(e));
            });
            result.setDataObjects(dataObjects);
            result.setRecordsTotal(Long.toString(paginatedResult.getTotalElements()));
            result.setRecordsFiltered(Long.toString(paginatedResult.getContent().size()));
            return result;
        }
    }

    /**
     * Handler method for the availableCatalogs page.
     *
     * @return
     */
    @RequestMapping(value = "/{id}/catalogs/available")
    public ModelAndView availableCatalogs() {
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
        Page<Catalog> paginatedResult = ValidationUtil2.isEmpty(dataTableRequest.getSearch()) ? websiteService.getAvailableCatalogsForWebsite(id, FindBy.EXTERNAL_ID, pagination.getPageNumber(), pagination.getPageSize(), sort, false)
                : websiteService.findAvailableCatalogsForWebsite(id, FindBy.EXTERNAL_ID, "catalogName", dataTableRequest.getSearch(), new Pageable(pagination.getPageNumber(), pagination.getPageSize(), sort), false);
        paginatedResult.getContent().forEach(e -> dataObjects.add(e.toMap()));
        result.setDataObjects(dataObjects);
        result.setRecordsTotal(Long.toString(paginatedResult.getTotalElements()));
        result.setRecordsFiltered(Long.toString(paginatedResult.getTotalElements()));
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
