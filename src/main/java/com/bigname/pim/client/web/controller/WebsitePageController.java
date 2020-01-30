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
import org.javatuples.Pair;
import org.springframework.data.domain.Page;
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

    @RequestMapping(value = "{websiteId}/pages/data")
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
                    int seq[] = {1};
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

    @RequestMapping(value = {"/{websiteId}/pages/{pageId}", "{websiteId}/pages/create"})
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
                    model.put("breadcrumbs", new BreadcrumbsBuilder().init(pageId, WebsitePage.class, request, parameterMap, new BaseService[]{websiteService, websitePageService}).build());
                } else {
                    throw new EntityNotFoundException("Unable to find Website with Id: " + pageId);
                }
            }
        }
        return new ModelAndView("website/websitePage" + (reload ? "_body" : ""), model);
    }

    @RequestMapping(value = "/pages", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> createPage(WebsitePage websitePage) {
        Map<String, Object> model = new HashMap<>();
        if(isValid(websitePage, model, WebsitePage.CreateGroup.class)) {
            websitePage.setActive("N");
            websitePageService.create(websitePage);
            model.put("success", true);
        }
        return model;
    }

    @RequestMapping(value = "/pages/{id}", method = RequestMethod.PUT)
    @ResponseBody
    public Map<String, Object> updatePage(@PathVariable(value = "id") String id, WebsitePage websitePage) {
        Map<String, Object> model = new HashMap<>();
        model.put("context", CollectionsUtil.toMap("id", id));
        if(isValid(websitePage, model, websitePage.getGroup().length == 1 && websitePage.getGroup()[0].equals("DETAILS") ? WebsitePage.DetailsGroup.class : null)) {
            websitePageService.update(ID.EXTERNAL_ID(id), websitePage);
            model.put("success", true);
            if(!id.equals(websitePage.getPageId())) {
                model.put("refreshUrl", "/pim/websites/" + websitePage.getPageId());
            }
        }
        return model;
    }

    @Override
    protected <E extends ValidatableEntity> Map<String, Pair<String, Object>> validate(E e, Map<String, Object> context, Class<?>... groups) {
        return websitePageService.validate(e, context, groups);
    }
}
