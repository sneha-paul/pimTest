package com.bigname.pim.client.web.controller;

import com.bigname.pim.api.domain.Website;
import com.bigname.pim.api.domain.WebsiteCatalog;
import com.bigname.pim.api.service.WebsiteService;
import com.bigname.pim.client.util.BreadcrumbsBuilder;
import com.m7.xtreme.common.datatable.model.Pagination;
import com.m7.xtreme.common.datatable.model.Request;
import com.m7.xtreme.common.datatable.model.Result;
import com.m7.xtreme.common.datatable.model.SortOrder;
import com.m7.xtreme.common.util.CollectionsUtil;
import com.m7.xtreme.xcore.domain.Entity;
import com.m7.xtreme.xcore.exception.EntityNotFoundException;
import com.m7.xtreme.xcore.util.ID;
import com.m7.xtreme.xcore.web.controller.BaseController;
import com.m7.xtreme.xplatform.domain.User;
import com.m7.xtreme.xplatform.domain.Version;
import com.m7.xtreme.xplatform.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.autoconfigure.metrics.MetricsProperties;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.m7.xtreme.common.util.ValidationUtil.isEmpty;


/**
 * The Controller class for Website
 *
 * @author Manu V NarayanaPrasad (manu@blacwood.com)
 * @since 1.0
 */
@Controller
@RequestMapping("pim/websites")
public class WebsiteController extends BaseController<Website, WebsiteService> {

    private WebsiteService websiteService;
    private UserService userService;


    public WebsiteController(WebsiteService websiteService, UserService userService) {
        super(websiteService, Website.class, new BreadcrumbsBuilder());
        this.websiteService = websiteService;
        this.userService = userService;
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
            websiteService.update(ID.EXTERNAL_ID(id), website);
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

        return id == null ? super.details(model) : websiteService.get(ID.EXTERNAL_ID(id), false)
                .map(website -> {
                    model.put("website", website);
                    return super.details(id, model);
                }).orElseThrow(() -> new EntityNotFoundException("Unable to find Website with Id: " + id));
    }

    @RequestMapping(value = {"/search"})
    public ModelAndView search() {
        Map<String, Object> model = new HashMap<>();
        model.put("active", "WEBSITES");
        model.put("view", "search");
        model.put("title", "Websites");
        return super.details(model);
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
        model.put("view", "website/websites");
        model.put("title", "Websites");
        return all(model);
    }

    @RequestMapping(value = "/data")
    @ResponseBody
    @SuppressWarnings("unchecked")
    public Result<Map<String, String>> all(HttpServletRequest request) {
        return super.all(request, "websiteName");
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
        return getAssociationGridData(request,
                WebsiteCatalog.class,
                dataTableRequest -> {
                    if(isEmpty(dataTableRequest.getSearch())) {
                        return websiteService.getWebsiteCatalogs(ID.EXTERNAL_ID(id), dataTableRequest.getPageRequest(associationSortPredicate), dataTableRequest.getStatusOptions());
                    } else {
                        return websiteService.findAllWebsiteCatalogs(ID.EXTERNAL_ID(id), "catalogName", dataTableRequest.getSearch(), dataTableRequest.getPageRequest(associationSortPredicate), false);
                    }
                });
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
     * @return
     */
    @RequestMapping("/{id}/catalogs/available/list")
    @ResponseBody
    public Result<Map<String, String>> getAvailableCatalogs(@PathVariable(value = "id") String id, HttpServletRequest request) {
        return new Result<Map<String, String>>().buildResult(new Request(request),
                dataTableRequest -> {
                    PageRequest pageRequest = dataTableRequest.getPageRequest(defaultSort);
                    if(isEmpty(dataTableRequest.getSearch())) {
                        return websiteService.getAvailableCatalogsForWebsite(ID.EXTERNAL_ID(id), pageRequest.getPageNumber(), pageRequest.getPageSize(), pageRequest.getSort(), false);
                    } else {
                        return websiteService.findAvailableCatalogsForWebsite(ID.EXTERNAL_ID(id), "catalogName", dataTableRequest.getSearch(), pageRequest, false);
                    }
                },
                paginatedResult -> {
                    List<Map<String, String>> dataObjects = new ArrayList<>();
                    paginatedResult.getContent().forEach(e -> dataObjects.add(e.toMap()));
                    return dataObjects;
                });
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
        boolean success = websiteService.addCatalog(ID.EXTERNAL_ID(id), ID.EXTERNAL_ID(catalogId)) != null;
        model.put("success", success);
        return model;
    }

    @RequestMapping("/{websiteId}/history")
    @ResponseBody
    public Result<Map<String, String>> getHistory(@PathVariable(value = "websiteId") String id, HttpServletRequest request) {
        Request dataTableRequest = new Request(request);
        Pagination pagination = dataTableRequest.getPagination();
        Result<Map<String, String>> result = new Result<>();
        result.setDraw(dataTableRequest.getDraw());
        Sort sort = null;
        if(pagination.hasSorts()) {
            sort = Sort.by(new Sort.Order(Sort.Direction.valueOf(SortOrder.fromValue(dataTableRequest.getOrder().getSortDir()).name()), dataTableRequest.getOrder().getName()));
        }
        Map<String, User> usersLookup = userService.getAll(null, false).stream().collect(Collectors.toMap(Entity::getId, u -> u));
        List<Map<String, String>> dataObjects = new ArrayList<>();
        Website websiteData = websiteService.get(ID.EXTERNAL_ID(id), false).orElse(null);
        final Page<Version> paginatedResult = new PageImpl<>(websiteData.getVersions());
        paginatedResult.getContent().forEach(e -> {
            Website website = (Website) e.getState();
            Map<String, String> data = website.toMap();
            /*String userName = userService.get(ID.INTERNAL_ID(e.getUserId()), false).orElse(null).getUserName();
            data.put("userName", userName);*/
            if(usersLookup.containsKey(e.getUserId())) {
                data.put("userName", usersLookup.get(e.getUserId()).getUserName());
            }
            data.put("timeStamp", String.valueOf(e.getTimeStamp()));
            data.put("userId", String.valueOf(e.getUserId()));
            dataObjects.add(data);
        });
        result.setDataObjects(dataObjects);
        result.setRecordsTotal(Long.toString(paginatedResult.getTotalElements()));
        result.setRecordsFiltered(Long.toString(paginatedResult.getTotalElements()));
        return result;
    }

    @RequestMapping(value = {"/{websiteId}/history/{time}"})
    public ModelAndView details(@PathVariable(value = "websiteId") String websiteId,
                                @PathVariable(name = "time") String time) {
        Map<String, Object> model = new HashMap<>();
        model.put("active", "WEBSITES");
        model.put("mode", "HISTORY");
        model.put("view", "website/website");
        Website website = websiteService.get(ID.EXTERNAL_ID(websiteId), false).orElse(null);
        website.getVersions().forEach(version -> {
            String timeStamp = String.valueOf(version.getTimeStamp());
            if(timeStamp.equalsIgnoreCase(time)) {
                Website website1 = (Website) version.getState();
                model.put("website", website1);
            }
        });
        return super.details(websiteId, model);
    }
}
