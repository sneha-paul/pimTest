package com.bigname.pim.client.web.controller;

import com.bigname.pim.core.domain.Catalog;
import com.bigname.pim.core.domain.RootCategory;
import com.bigname.pim.core.domain.Website;
import com.bigname.pim.core.domain.WebsiteCatalog;
import com.bigname.pim.core.service.ConfigService;
import com.bigname.pim.core.service.WebsiteService;
import com.bigname.pim.core.util.BreadcrumbsBuilder;
import com.m7.xtreme.common.datatable.model.Pagination;
import com.m7.xtreme.common.datatable.model.Request;
import com.m7.xtreme.common.datatable.model.Result;
import com.m7.xtreme.common.datatable.model.SortOrder;
import com.m7.xtreme.common.util.CollectionsUtil;
import com.m7.xtreme.common.util.ValidationUtil;
import com.m7.xtreme.xcore.domain.Entity;
import com.m7.xtreme.xcore.exception.EntityNotFoundException;
import com.m7.xtreme.xcore.util.ID;
import com.m7.xtreme.xcore.web.controller.BaseController;
import com.m7.xtreme.xplatform.domain.SyncStatus;
import com.m7.xtreme.xplatform.domain.User;
import com.m7.xtreme.xplatform.domain.Version;
import com.m7.xtreme.xplatform.model.Breadcrumbs;
import com.m7.xtreme.xplatform.service.SyncStatusService;
import com.m7.xtreme.xplatform.service.UserService;
import org.javatuples.Pair;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.m7.xtreme.common.util.ValidationUtil.isEmpty;
import static com.m7.xtreme.common.util.ValidationUtil.isNotEmpty;


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
    private RestTemplate restTemplate;
    private ConfigService configService;
    private SyncStatusService syncStatusService;

    public WebsiteController(WebsiteService websiteService, UserService userService, RestTemplate restTemplate, ConfigService configService, SyncStatusService syncStatusService) {
        super(websiteService, Website.class, new BreadcrumbsBuilder());
        this.websiteService = websiteService;
        this.userService = userService;
        this.restTemplate = restTemplate;
        this.configService = configService;
        this.syncStatusService = syncStatusService;
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

    @RequestMapping(value ="/websiteLoad")
    @ResponseBody
    public String loadCatalogToBOS() {
        boolean status;
        List<Website> websiteList = websiteService.getAll(null, false);
        ResponseEntity<String> response =  restTemplate.postForEntity("http://localhost:8084/website/loadWebsite", websiteList, String.class, new HashMap<>());
        return response.getBody();
    }

    @RequestMapping(value ="/websiteCatalogLoad")
    @ResponseBody
    public Map<String, Object> loadWebsiteCatalogsToBOS() {
        Map<String, Object> model = new HashMap<>();
        boolean success = false;
        List<WebsiteCatalog> websiteCatalogList = websiteService.loadWebsiteCatalogsToBOS();
        ResponseEntity<String> response =  restTemplate.postForEntity("http://localhost:8084/admin//websites/loadWebsiteCatalog", websiteCatalogList, String.class, new HashMap<>());
        if (response != null && response.getStatusCode() == HttpStatus.OK) {
            success = true;
        }
        model.put("success", success);
        return model;
    }

    @RequestMapping("/{websiteId}/configParam/data")
    @ResponseBody
    public Result<Map<String, String>> getConfigsData(@PathVariable(value = "websiteId") String websiteId, HttpServletRequest request) {
        Request dataTableRequest = new Request(request);
        Pagination pagination = dataTableRequest.getPagination();
        Result<Map<String, String>> result = new Result<>();
        result.setDraw(dataTableRequest.getDraw());
        Sort sort = null;
        if(pagination.hasSorts()) {
            sort = Sort.by(new Sort.Order(Sort.Direction.valueOf(SortOrder.fromValue(dataTableRequest.getOrder().getSortDir()).name()), dataTableRequest.getOrder().getName()));
        }
        List<Map<String, String>> dataObjects = new ArrayList<>();
        Website website = websiteService.get(ID.EXTERNAL_ID(websiteId), false).orElse(null);
        configService.getAll(null, false).forEach(config -> {
            List<Map<String, String>> paramList = configService.getCasePreservedConfigParams(ID.EXTERNAL_ID(config.getConfigId()), website.getId());
            paramList.forEach(param -> param.forEach((k,v)-> {
                Map<String, String> newMap = new HashMap<>();
                newMap.put("paramName", k);
                newMap.put("paramValue", String.valueOf(v));
                newMap.put("configId", config.getConfigId());
                dataObjects.add(newMap);
            }));
        });
        Page<Map<String, String>> paginatedResult = new PageImpl<>(dataObjects);
        result.setDataObjects(dataObjects);
        result.setRecordsTotal(Long.toString(paginatedResult.getTotalElements()));
        result.setRecordsFiltered(Long.toString(paginatedResult.getTotalElements()));
        return result;
    }

    @RequestMapping(value = {"/{websiteId}/websiteConfig/{configId}/param/{paramName}", "/{websiteId}/param/create"})
    public ModelAndView parameterCreateView(@PathVariable(value = "websiteId") String websiteId, @PathVariable(value = "configId", required = false) String configId, @PathVariable(value = "paramName", required = false) String paramName) {
        Map<String, Object> model = new HashMap<>();
        websiteService.get(ID.EXTERNAL_ID(websiteId), false).ifPresent(website -> {
            model.put("website", website);
            if(configId == null) {
                List<String> configIds = new ArrayList<>();
                configService.getAll(null, false).forEach(config -> {
                    configIds.add(config.getConfigId());
                });
                model.put("mode", "CREATE");
                model.put("configs", configIds);

            } else {
                configService.get(ID.EXTERNAL_ID(configId), false).ifPresent(config -> {
                    Map<String, String> param = new HashMap<>();
                    model.put("configId", configId);
                    model.put("caseName", paramName);
                    model.put("name", paramName.toUpperCase());
                    String[] paramValue = config.getParameter(paramName.toUpperCase(), String.class, website.getId()).split("\\|");
                    model.put("value", paramValue[1]);
                    model.put("mode", "DETAILS");
                    model.put("breadcrumbs", new Breadcrumbs("Websites",
                            "Websites", "/pim/websites/", website.getWebsiteName(), "/pim/websites/" + website.getWebsiteId(),
                            "Params", "/pim/websites/" + website.getWebsiteId() + "#websiteConfigParam", paramName, ""));
                });
            }
        });
        return new ModelAndView("website/websiteParam", model);
    }

    @RequestMapping(value = "/{websiteId}/params", method = RequestMethod.PUT)
    @ResponseBody
    public Map<String, Object> createParameter(@PathVariable(value = "websiteId") String websiteId, @RequestParam Map<String, String> parameters) {
        Map<String, Object> model = new HashMap<>();
        boolean[] success = {false};
        if (isNotEmpty(parameters.get("paramName")) && isNotEmpty(parameters.get("paramValue"))) {
            websiteService.get(ID.EXTERNAL_ID(websiteId), false).ifPresent(website -> {
                configService.get(ID.EXTERNAL_ID(parameters.get("configId")), false).ifPresent(config -> {
                    String param = config.getParameter(parameters.get("paramName").toUpperCase(), String.class, website.getId());
                    if(ValidationUtil.isEmpty(param) && param == null) {
                        config.setParameter(parameters.get("paramName"), parameters.get("paramName") + "|" + parameters.get("paramValue"), website.getId());
                        config.setGroup("WEB-PARAMS");
                        configService.update(ID.EXTERNAL_ID(config.getConfigId()), config);
                        success[0] = true;
                    } else {
                        Map<String, Pair<String, Object>> _fieldErrors = new HashMap<>();
                        _fieldErrors.put("paramName", Pair.with("Parameter name already exists", parameters.get("paramName")));
                        model.put("fieldErrors", _fieldErrors);
                    }
                });
            });
        } else {
            Map<String, Pair<String, Object>> _fieldErrors = new HashMap<>();
            if(isEmpty(parameters.get("paramName"))) {
                _fieldErrors.put("paramName", Pair.with("Parameter Name cannot be blank", null));
            }
            if(isEmpty(parameters.get("paramValue"))) {
                _fieldErrors.put("paramValue", Pair.with("Parameter Value cannot be blank", null));
            }
            model.put("fieldErrors", _fieldErrors);
        }
        model.put("success", success[0]);
        return model;
    }

    @RequestMapping(value = "/{websiteId}/config/{configId}/param/{paramName}", method = RequestMethod.PUT)
    @ResponseBody
    public Map<String, Object> updateParameter(@PathVariable(value = "websiteId") String websiteId, @PathVariable(value = "configId") String configId, @PathVariable(value = "paramName") String paramName, @RequestParam Map<String, String> parameters) {
        Map<String, Object> model = new HashMap<>();
        boolean[] success = {false};
        if(isNotEmpty(parameters.get("paramValue"))) {
            websiteService.get(ID.EXTERNAL_ID(websiteId), false).ifPresent(website -> {
                configService.get(ID.EXTERNAL_ID(parameters.get("configId")), false).ifPresent(config -> {
                    config.setParameter(parameters.get("paramName"), parameters.get("paramName") + "|" + parameters.get("paramValue"), website.getId());
                    config.setGroup("WEB-PARAMS");
                    configService.update(ID.EXTERNAL_ID(configId), config);
                    success[0] = true;
                });
            });
        } else {
            Map<String, Pair<String, Object>> _fieldErrors = new HashMap<>();
            if(isEmpty(parameters.get("paramValue"))) {
                _fieldErrors.put("paramValue", Pair.with("Parameter Value cannot be blank", null));
            }
            model.put("fieldErrors", _fieldErrors);
        }
        model.put("success", success[0]);
        return model;
    }

    @RequestMapping(value = "/{websiteId}/websiteConfig/{configId}/param/{paramName}/delete", method = RequestMethod.PUT)
    @ResponseBody
    public Map<String, Object> deleteParams(@PathVariable(value = "websiteId") String websiteId, @PathVariable(value = "configId") String configId, @PathVariable(value = "paramName") String paramName) {
        Map<String, Object> model = new HashMap<>();
        websiteService.get(ID.EXTERNAL_ID(websiteId), false).ifPresent(website -> {
            configService.get(ID.EXTERNAL_ID(configId), false).ifPresent(config -> {
                Map<String, Map<String, String>> paramDb = config.getParams();
                Map<String, String> param = paramDb.get(website.getId().toUpperCase());
                Map<String, Map<String, String>> caseParamDb = config.getCasePreservedParams();
                Map<String, String> caseParam = caseParamDb.get(website.getId().toUpperCase());
                Set<String> keySet = param.keySet().stream().filter(k -> k.equals(paramName.toUpperCase())).collect(Collectors.toSet());
                keySet.forEach(key -> {
                    String[] value = config.getParameter(key, String.class, website.getId().toUpperCase()).split("\\|");
                    caseParam.keySet().removeIf(k -> k.equals(value[0]));
                });
                param.keySet().removeIf(k -> k.equals(paramName.toUpperCase()));
                paramDb.put(website.getId().toUpperCase(), param);
                caseParamDb.put(website.getId().toUpperCase(), caseParam);
                config.setParams(paramDb);
                config.setCasePreservedParams(caseParamDb);
                config.setGroup("WEB-PARAMS");
                configService.update(ID.EXTERNAL_ID(configId), config);
            });
        });
        model.put("success", true);
        return model;
    }

    @RequestMapping(value = "/{websiteId}/redirects/data")
    @ResponseBody
    public Result<Map<String, String>> getRedirectUrls(@PathVariable(value = "websiteId") String websiteId, HttpServletRequest request) {
        Request dataTableRequest = new Request(request);
        Pagination pagination = dataTableRequest.getPagination();
        Result<Map<String, String>> result = new Result<>();
        result.setDraw(dataTableRequest.getDraw());
        Sort sort = null;
        if(pagination.hasSorts()) {
            sort = Sort.by(new Sort.Order(Sort.Direction.valueOf(SortOrder.fromValue(dataTableRequest.getOrder().getSortDir()).name()), dataTableRequest.getOrder().getName()));
        }
        List<Map<String, String>> dataObjects = new ArrayList<>();
        websiteService.get(ID.EXTERNAL_ID(websiteId), false).ifPresent(website -> {
            website.getUrlRedirects().forEach((k,v) -> {
                Map<String, String> newMap = new HashMap<>();
                newMap.put("fromUrl", k);
                newMap.put("toUrl", v);
                dataObjects.add(newMap);
            });
        });
        Page<Map<String, String>> paginatedResult = new PageImpl<>(dataObjects);
        result.setDataObjects(dataObjects);
        result.setRecordsTotal(Long.toString(paginatedResult.getTotalElements()));
        result.setRecordsFiltered(Long.toString(paginatedResult.getTotalElements()));
        return result;

    }

    @RequestMapping(value = {"/{websiteId}/redirects/{fromUrl}", "/{websiteId}/redirects/create"})
    public ModelAndView redirectUrlView(@PathVariable(value = "websiteId") String websiteId, @PathVariable(value = "fromUrl", required = false) String fromUrl, HttpServletRequest request) {
        Map<String, Object> model = new HashMap<>();
        websiteService.get(ID.EXTERNAL_ID(websiteId), false).ifPresent(website -> {
            model.put("website", website);
            if(fromUrl == null) {
                model.put("mode", "CREATE");

            } else {
                model.put("fromUrl", fromUrl);
                model.put("toUrl", website.getUrlRedirects().get(fromUrl));
                model.put("mode", "DETAILS");
                model.put("breadcrumbs", new Breadcrumbs("Websites",
                            "Websites", "/pim/websites/", website.getWebsiteName(), "/pim/websites/" + website.getWebsiteId(),
                            "Redirects", "/pim/websites/" + website.getWebsiteId() + "#redirects", fromUrl, ""));

            }
        });
        return new ModelAndView("website/redirect", model);
    }

    @RequestMapping(value = "/{websiteId}/redirects", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> createRedirectUrl(@PathVariable(value = "websiteId") String websiteId, @RequestParam Map<String, String> urlParameters) {
        Map<String, Object> model = new HashMap<>();
        boolean[] success = {false};
        if (isNotEmpty(urlParameters.get("fromUrl")) && isNotEmpty(urlParameters.get("toUrl"))) {
            websiteService.get(ID.EXTERNAL_ID(websiteId), false).ifPresent(website -> {
                Map<String, String> urlMap = website.getUrlRedirects();
                Object url = urlMap.get(urlParameters.get("fromUrl"));
                if (url == null && isEmpty(url)) {
                    urlMap.put(urlParameters.get("fromUrl"), urlParameters.get("toUrl"));
                    website.setUrlRedirects(urlMap);
                    website.setGroup("URL");
                    websiteService.update(ID.EXTERNAL_ID(website.getWebsiteId()), website);
                    success[0] = true;
                } else {
                    Map<String, Pair<String, Object>> _fieldErrors = new HashMap<>();
                    if(url.equals(urlParameters.get("toUrl"))) {
                        _fieldErrors.put("toUrl", Pair.with("To Url already exists", urlParameters.get("toUrl")));
                    }

                    _fieldErrors.put("fromUrl", Pair.with("From Url already exists", urlParameters.get("fromUrl")));
                    model.put("fieldErrors", _fieldErrors);
                }
            });
        } else {
            Map<String, Pair<String, Object>> _fieldErrors = new HashMap<>();
            if(isEmpty(urlParameters.get("fromUrl"))) {
                _fieldErrors.put("fromUrl", Pair.with("Redirect From Url cannot be blank", null));
            }
            if(isEmpty(urlParameters.get("toUrl"))) {
                _fieldErrors.put("toUrl", Pair.with("Redirect To Url cannot be blank", null));
            }
            model.put("fieldErrors", _fieldErrors);
        }
        model.put("success", success[0]);
        return model;
    }

    @RequestMapping(value = "/{websiteId}/redirects/{fromUrl}", method = RequestMethod.PUT)
    @ResponseBody
    public Map<String, Object> updateRedirectUrl(@PathVariable(value = "websiteId") String websiteId, @PathVariable(value = "fromUrl") String fromUrl, @RequestParam Map<String, String> urlParameters) {
        Map<String, Object> model = new HashMap<>();
        boolean[] success = {false};
        if (isNotEmpty(urlParameters.get("fromUrl")) && isNotEmpty(urlParameters.get("toUrl"))) {
            websiteService.get(ID.EXTERNAL_ID(websiteId), false).ifPresent(website -> {
                Map<String, String> urlRedirects = website.getUrlRedirects();
                if (fromUrl.equals(String.valueOf(urlParameters.get("fromUrl")))) {
                    urlRedirects.put(urlParameters.get("fromUrl"), urlParameters.get("toUrl"));
                    website.setUrlRedirects(urlRedirects);
                    website.setGroup("URL");
                    websiteService.update(ID.EXTERNAL_ID(website.getWebsiteId()), website);
                    success[0] = true;
                } else {
                    Set<String> keySet = urlRedirects.keySet().stream().filter(k -> k.equals(String.valueOf(urlParameters.get("fromUrl")))).collect(Collectors.toSet());
                    if (isEmpty(keySet)) {
                        urlRedirects.keySet().remove(fromUrl);
                        urlRedirects.put(urlParameters.get("fromUrl"), urlParameters.get("toUrl"));
                        website.setUrlRedirects(urlRedirects);
                        website.setGroup("URL");
                        websiteService.update(ID.EXTERNAL_ID(website.getWebsiteId()), website);
                        success[0] = true;
                    } else {
                        Map<String, Pair<String, Object>> _fieldErrors = new HashMap<>();
                        _fieldErrors.put("fromUrl", Pair.with("Url already exists", urlParameters.get("fromUrl")));
                        model.put("fieldErrors", _fieldErrors);
                    }
                }
            });
        } else {
            Map<String, Pair<String, Object>> _fieldErrors = new HashMap<>();
            if(isEmpty(urlParameters.get("fromUrl"))) {
                _fieldErrors.put("fromUrl", Pair.with("Redirect From Url cannot be blank", null));
            }
            if(isEmpty(urlParameters.get("toUrl"))) {
                _fieldErrors.put("toUrl", Pair.with("Redirect To Url cannot be blank", null));
            }
            model.put("fieldErrors", _fieldErrors);
        }
        model.put("success", success[0]);
        return model;
    }

    @RequestMapping(value ="/syncUpdatedWebsites", method = RequestMethod.PUT)
    @ResponseBody
    public Map<String, Object> syncUpdatedWebsites() {
        Map<String, Object> model = new HashMap<>();
        List<Website> websites = websiteService.syncUpdatedRecord();
        Map<String, String> map = new HashMap<String, String>();
        ResponseEntity<String> response =  restTemplate.postForEntity("http://localhost:8084/admin/websites/syncUpdatedWebsites", websites, String.class, map);
        if(Objects.equals(response.getBody(), "true")) {
            websites.forEach(website -> {
                website.setLastExportedTimeStamp(LocalDateTime.now());
                List<SyncStatus> syncStatusList = syncStatusService.getPendingSynStatus(website.getId(), "pending");
                syncStatusList.forEach(syncStatus -> {
                    syncStatus.setStatus("updated");
                    syncStatus.setExportedTimeStamp(website.getLastExportedTimeStamp());
                });
                syncStatusService.update(syncStatusList);
            });
            websiteService.update(websites);
            model.put("success", true);
        } else {
            model.put("success", false);
        }
        return model;
    }

    @RequestMapping(value ="/{websiteId}/syncWebsiteCatalog", method = RequestMethod.PUT)
    @ResponseBody
    public Map<String, Object> syncWebsiteCatalog(@PathVariable(value = "websiteId") String websiteId) {
        Map<String, Object> model = new HashMap<>();
        Website website = websiteService.get(ID.EXTERNAL_ID(websiteId), false).orElse(null);
        List<WebsiteCatalog> websiteCatalogList = websiteService.getAllWebsiteCatalogs(website.getId());
        List<WebsiteCatalog> finalWebsiteCatalog = new ArrayList<>();
        websiteCatalogList.forEach(websiteCatalog -> {
            if(ValidationUtil.isEmpty(websiteCatalog.getLastExportedTimeStamp())) {
                finalWebsiteCatalog.add(websiteCatalog);
            }
        });
        ResponseEntity<String> response =  restTemplate.postForEntity("http://localhost:8084/admin/websites/syncWebsiteCatalogs", finalWebsiteCatalog, String.class, new HashMap<>());
        if(Objects.equals(response.getBody(), "true")) {
            finalWebsiteCatalog.forEach(websiteCatalog -> {
                websiteCatalog.setLastExportedTimeStamp(LocalDateTime.now());
                List<SyncStatus> syncStatusList = syncStatusService.getPendingSynStatus(websiteCatalog.getId(), "pending");
                syncStatusList.forEach(syncStatus -> {
                    syncStatus.setStatus("updated");
                    syncStatus.setExportedTimeStamp(websiteCatalog.getLastExportedTimeStamp());
                });
                syncStatusService.update(syncStatusList);
            });
            websiteService.syncWebsiteCatalog(finalWebsiteCatalog);
            model.put("success", true);
        } else {
            model.put("success", null);
        }
        return model;
    }
}
