package com.bigname.pim.client.web.controller;

import com.bigname.pim.core.domain.Website;
import com.bigname.pim.core.domain.WebsitePage;
import com.bigname.pim.core.service.WebsitePageService;
import com.bigname.pim.core.service.WebsiteService;
import com.bigname.pim.core.util.BreadcrumbsBuilder;
import com.m7.xtreme.common.datatable.model.Pagination;
import com.m7.xtreme.common.datatable.model.Request;
import com.m7.xtreme.common.datatable.model.Result;
import com.m7.xtreme.common.datatable.model.SortOrder;
import com.m7.xtreme.common.util.CollectionsUtil;
import com.m7.xtreme.xcore.domain.ValidatableEntity;
import com.m7.xtreme.xcore.exception.EntityNotFoundException;
import com.m7.xtreme.xcore.service.BaseService;
import com.m7.xtreme.xcore.util.ID;
import com.m7.xtreme.xcore.web.controller.ControllerSupport;
import com.m7.xtreme.xplatform.model.Breadcrumbs;
import org.javatuples.Pair;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

import static com.m7.xtreme.common.util.ValidationUtil.isEmpty;
import static com.m7.xtreme.common.util.ValidationUtil.isNotEmpty;

@Controller
@RequestMapping("pim/websites")
public class WebsitePageController extends ControllerSupport {

    private WebsitePageService websitePageService;
    private WebsiteService websiteService;

    public WebsitePageController(WebsitePageService websitePageService, WebsiteService websiteService) {
        this.websitePageService = websitePageService;
        this.websiteService = websiteService;
    }

    @RequestMapping(value = "/{websiteId}/pages/data")
    @ResponseBody
    public Result<Map<String, String>> getWebsitePages(@PathVariable(value = "websiteId") String websiteId, HttpServletRequest request) {
        Request dataTableRequest = new Request(request);
        Pagination pagination = dataTableRequest.getPagination();
        Result<Map<String, String>> result = new Result<>();
        result.setDraw(dataTableRequest.getDraw());

        if(isEmpty(dataTableRequest.getSearch())) {
            if (isNotEmpty(websiteId)) {
                websiteService.get(ID.EXTERNAL_ID(websiteId), false).ifPresent(website -> {
                    Sort sort = null;
                    if(pagination.hasSorts() && !dataTableRequest.getOrder().getName().equals("sequenceNum")) {
                        sort = Sort.by(new Sort.Order(Sort.Direction.valueOf(SortOrder.fromValue(dataTableRequest.getOrder().getSortDir()).name()), dataTableRequest.getOrder().getName()));
                    }
                    Page<WebsitePage> paginatedResult = websitePageService.getAll(ID.INTERNAL_ID(website.getId()), pagination.getPageNumber(), pagination.getPageSize(), sort, dataTableRequest.getStatusOptions());
                    List<Map<String, String>> dataObjects = new ArrayList<>();
                    paginatedResult.getContent().forEach(e -> {
                        dataObjects.add(e.toMap());
                    });
                    result.setDataObjects(dataObjects);
                    result.setRecordsTotal(Long.toString(paginatedResult.getTotalElements()));
                    result.setRecordsFiltered(Long.toString(paginatedResult.getTotalElements()));
                });
            }
        }
        return result;
    }

    @RequestMapping(value = {"/{websiteId}/pages/{pageId}", "/{websiteId}/pages/create"})
    public ModelAndView pageDetails(@PathVariable(value = "websiteId") String websiteId,
                                    @PathVariable(value = "pageId", required = false) String pageId,
                                    @RequestParam(name = "reload", required = false) boolean reload,
                                    @RequestParam Map<String, Object> parameterMap,
                                    HttpServletRequest request) {
        Map<String, Object> model = new HashMap<>();
        model.put("active", "WEBSITES");
        Optional<Website> _website = websiteService.get(ID.EXTERNAL_ID(websiteId), false);
        if (_website.isPresent()) {
            Website website = _website.get();
            if (pageId == null) {
                model.put("website", website);
                model.put("mode", "CREATE");
                model.put("websitePage", new WebsitePage());
            } else {
                Optional<WebsitePage> websitePage = websitePageService.get(ID.INTERNAL_ID(website.getId()), ID.EXTERNAL_ID(pageId), false);
                if (websitePage.isPresent()) {
                    model.put("mode", "DETAILS");
                    model.put("website", website);
                    model.put("websitePage", websitePage.get());
                    //model.put("breadcrumbs", new BreadcrumbsBuilder().init(pageId, WebsitePage.class, request, parameterMap, new BaseService[]{websiteService, websitePageService}).build());
                    model.put("breadcrumbs", new Breadcrumbs("Pages", "Websites", "/pim/websites/", website.getWebsiteName(), "/pim/websites/" + website.getWebsiteId(),
                            "Pages", "/pim/websites/" + website.getWebsiteId() + "#websitePages", websitePage.get().getPageName(), ""));
                } else {
                    throw new EntityNotFoundException("Unable to find Website with Id: " + pageId);
                }
            }
        }
        return new ModelAndView("website/websitePage" + (reload ? "_body" : ""), model);
    }

    @RequestMapping(value = "/{websiteId}/pages", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> createPage(@PathVariable(value = "websiteId") String websiteId, WebsitePage websitePage) {
        return websiteService.get(ID.EXTERNAL_ID(websiteId), false).map(website -> {
            Map<String, Object> model = new HashMap<>();
            if(isValid(websitePage, model, WebsitePage.CreateGroup.class)) {
                websitePage.setActive("Y");
                websitePage.setWebsiteId(website.getId());
                websitePageService.create(websitePage);
                model.put("success", true);
            }
            return model;
        }).orElseThrow(() -> new EntityNotFoundException("Unable to find Website with Id: " + websiteId));
    }

    @RequestMapping(value = "/pages/{id}", method = RequestMethod.PUT)
    @ResponseBody
    public Map<String, Object> updatePage(@PathVariable(value = "id") String id, WebsitePage websitePage) {
        Map<String, Object> model = new HashMap<>();
        model.put("context", CollectionsUtil.toMap("id", id));
        if(isValid(websitePage, model, websitePage.getGroup().length == 1 && websitePage.getGroup()[0].equals("DETAILS") ? WebsitePage.DetailsGroup.class : null)) {
            websitePageService.update(ID.EXTERNAL_ID(id), websitePage);
            model.put("success", true);
        }
        return model;
    }

    @Override
    protected <E extends ValidatableEntity> Map<String, Pair<String, Object>> validate(E e, Map<String, Object> context, Class<?>... groups) {
        return websitePageService.validate(e, context, groups);
    }

    @RequestMapping(value = "/{websiteId}/pages/{pageId}/attributes/data")
    @ResponseBody
    public Result<Map<String, String>> getWebsitePagesAttributes(@PathVariable(value = "websiteId") String websiteId, @PathVariable(value = "pageId") String pageId, HttpServletRequest request) {
        Request dataTableRequest = new Request(request);
        Pagination pagination = dataTableRequest.getPagination();
        Result<Map<String, String>> result = new Result<>();
        result.setDraw(dataTableRequest.getDraw());
        Sort sort = null;
        if(pagination.hasSorts()) {
            sort = Sort.by(new Sort.Order(Sort.Direction.valueOf(SortOrder.fromValue(dataTableRequest.getOrder().getSortDir()).name()), dataTableRequest.getOrder().getName()));
        }
        List<Map<String, String>> dataObjects = new ArrayList<>();
        websitePageService.getPageAttributes(websiteId, pageId).forEach(pageAttributes -> {
            pageAttributes.forEach((k,v) -> {
                Map<String, String> newMap = new HashMap<>();
                String[] value = String.valueOf(v).split("\\|");
                newMap.put("attributeName", value[0]);
                newMap.put("attributeValue", value[1]);
                newMap.put("attributeId", k);
                dataObjects.add(newMap);
            });
        });
        Page<Map<String, String>> paginatedResult = new PageImpl<>(dataObjects);
        result.setDataObjects(dataObjects);
        result.setRecordsTotal(Long.toString(paginatedResult.getTotalElements()));
        result.setRecordsFiltered(Long.toString(paginatedResult.getTotalElements()));
        return result;
    }

    @RequestMapping(value= {"/{websiteId}/pages/{pageId}/attributes/{pageAttributeId}", "/{websiteId}/pages/{pageId}/attributes/create"})
    public ModelAndView websitePagesAttributeDetails(@PathVariable(value = "websiteId") String websiteId,
                                                     @PathVariable(value = "pageId") String pageId,
                                                     @PathVariable(value = "pageAttributeId", required = false) String pageAttributeId) {
        return websiteService.get(ID.EXTERNAL_ID(websiteId), false)
                .map(website -> {
                    Map<String, Object> model = new HashMap<>();
                    WebsitePage websitePage = websitePageService.get(ID.EXTERNAL_ID(pageId), false).orElseThrow(() -> new EntityNotFoundException("Unable to find Page with Id: " + pageId));
                    model.put("mode", pageAttributeId == null ? "CREATE" : "DETAILS");
                    if(isNotEmpty(pageAttributeId)) {
                        websitePage.getPageAttributes().forEach((k,v) -> {
                            String[] value = String.valueOf(v.get(pageAttributeId)).split("\\|");
                            model.put("attributeName", value[0]);
                            model.put("attributeValue", value[1]);
                            model.put("attributeId", pageAttributeId);
                            model.put("website", website);
                            model.put("websitePage", websitePage);
                            model.put("breadcrumbs", new Breadcrumbs("Pages", "Websites", "/pim/websites/", website.getWebsiteName(), "/pim/websites/" + website.getWebsiteId(),
                                    "Pages", "/pim/websites/" + website.getWebsiteId() + "#websitePages", websitePage.getPageName(), "/pim/websites/" + website.getWebsiteId() + "/pages/" + websitePage.getPageId(), "Attributes", "/pim/websites/" + website.getWebsiteId() + "/pages/" + websitePage.getPageId() + "#pageAttributes", v.get(pageAttributeId).toString(), ""));
                        });
                    }
                    return new ModelAndView("website/websitePageAttribute", model);
                }).orElseThrow(() -> new EntityNotFoundException("Unable to find Website with Id: " + websiteId));
    }


    @RequestMapping(value = "/{websiteId}/pages/{pageId}/attributes", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> createPageAttribute(@PathVariable(value = "websiteId") String websiteId, @PathVariable(value = "pageId") String pageId, @RequestParam Map<String, String> attribute) {
        Map<String, Object> model = new HashMap<>();
        boolean[] success = {false};
        if (isNotEmpty(attribute.get("attributeId")) && isNotEmpty(attribute.get("attributeName")) && isNotEmpty(attribute.get("attributeValue"))) {
            websiteService.get(ID.EXTERNAL_ID(websiteId), false).ifPresent(website -> {
                Optional<WebsitePage> websitePage = websitePageService.get(ID.EXTERNAL_ID(pageId), false);
                if (websitePage.isPresent()) {
                    Map<String, Map<String, Object>> pageAttributeMap = websitePage.get().getPageAttributes();
                    if (pageAttributeMap.get("DEFAULT_GROUP") != null) {
                        Object attributeName = pageAttributeMap.get("DEFAULT_GROUP").get(attribute.get("attributeId").toUpperCase());
                        if (isEmpty(attributeName) && attributeName == null) {
                            pageAttributeMap.get("DEFAULT_GROUP").put(attribute.get("attributeId").toUpperCase(), attribute.get("attributeName") + "|" + attribute.get("attributeValue"));
                            websitePage.get().setGroup("PAGE-ATTRIBUTES");
                            websitePage.get().setPageAttributes(pageAttributeMap);
                            websitePageService.update(ID.EXTERNAL_ID(pageId), websitePage.get());
                            success[0] = true;
                        } else {
                            Map<String, Pair<String, Object>> _fieldErrors = new HashMap<>();
                            _fieldErrors.put("attributeId", Pair.with("Attribute Id already exists", attribute.get("attributeId")));
                            model.put("fieldErrors", _fieldErrors);
                        }
                    } else {
                        pageAttributeMap.put("DEFAULT_GROUP", Map.of(attribute.get("attributeId").toUpperCase(), attribute.get("attributeName") + "|" + attribute.get("attributeValue")));
                        websitePage.get().setGroup("PAGE-ATTRIBUTES");
                        websitePage.get().setPageAttributes(pageAttributeMap);
                        websitePageService.update(ID.EXTERNAL_ID(pageId), websitePage.get());
                        success[0] = true;
                    }
                }
            });
        }
        model.put("success", success[0]);
        return model;
    }

    @RequestMapping(value = "/{websiteId}/pages/{pageId}/attributes/{attributeId}", method = RequestMethod.PUT)
    @ResponseBody
    public Map<String, Object> updatePageAttribute(@PathVariable(value = "websiteId") String websiteId, @PathVariable(value = "pageId") String pageId, @PathVariable(value = "attributeId") String attributeId, @RequestParam Map<String, String> attribute) {
        Map<String, Object> model = new HashMap<>();
        boolean[] success = {false};
        if (isNotEmpty(attribute.get("attributeId")) && isNotEmpty(attribute.get("attributeName")) && isNotEmpty(attribute.get("attributeValue"))) {
            websiteService.get(ID.EXTERNAL_ID(websiteId), false).ifPresent(website -> {
                Optional<WebsitePage> websitePage = websitePageService.get(ID.EXTERNAL_ID(pageId), false);
                if (websitePage.isPresent()) {
                    Map<String, Map<String, Object>> pageAttributeMap = websitePage.get().getPageAttributes();
                    String attributeName = String.valueOf(pageAttributeMap.get("DEFAULT_GROUP").get(attributeId));
                    if (isNotEmpty(attributeName)) {
                        pageAttributeMap.get("DEFAULT_GROUP").put(attributeId, attribute.get("attributeName") + "|" + attribute.get("attributeValue"));
                        websitePage.get().setGroup("PAGE-ATTRIBUTES");
                        websitePage.get().setPageAttributes(pageAttributeMap);
                        websitePageService.update(ID.EXTERNAL_ID(pageId), websitePage.get());
                        success[0] = true;
                    }
                }
            });
        }
        model.put("success", success[0]);
        return model;
    }

    @RequestMapping(value = "/{websiteId}/pages/{pageId}/attributes/{attributeId}/delete", method = RequestMethod.PUT)
    @ResponseBody
    public Map<String, Object> deletePageAttribute(@PathVariable(value = "websiteId") String websiteId, @PathVariable(value = "pageId") String pageId, @PathVariable(value = "attributeId") String attributeId, @RequestParam Map<String, String> attribute) {
        Map<String, Object> model = new HashMap<>();
        boolean[] success = {false};
        websiteService.get(ID.EXTERNAL_ID(websiteId), false).ifPresent(website -> {
            Optional<WebsitePage> websitePage = websitePageService.get(ID.EXTERNAL_ID(pageId), false);
            if(websitePage.isPresent()) {
                Map<String, Map<String, Object>> pageAttributeMap = websitePage.get().getPageAttributes();
                Map<String, Object> attributeMap = pageAttributeMap.get("DEFAULT_GROUP");
                attributeMap.keySet().removeIf(k -> k.equals(attributeId));
                pageAttributeMap.put("DEFAULT_GROUP", attributeMap);
                websitePage.get().setGroup("PAGE-ATTRIBUTES");
                websitePage.get().setPageAttributes(pageAttributeMap);
                websitePageService.update(ID.EXTERNAL_ID(pageId), websitePage.get());
                success[0] = true;
            }
        });
        model.put("success", success[0]);
        return model;
    }

}
