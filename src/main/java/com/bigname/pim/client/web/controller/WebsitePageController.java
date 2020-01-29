package com.bigname.pim.client.web.controller;

import com.bigname.pim.core.domain.WebsitePage;
import com.bigname.pim.core.service.WebsitePageService;
import com.bigname.pim.core.service.WebsiteService;
import com.bigname.pim.core.util.BreadcrumbsBuilder;
import com.m7.xtreme.common.datatable.model.Request;
import com.m7.xtreme.common.datatable.model.Result;
import com.m7.xtreme.common.util.CollectionsUtil;
import com.m7.xtreme.xcore.domain.ValidatableEntity;
import com.m7.xtreme.xcore.exception.EntityNotFoundException;
import com.m7.xtreme.xcore.service.BaseService;
import com.m7.xtreme.xcore.util.ID;
import com.m7.xtreme.xcore.web.controller.ControllerSupport;
import org.javatuples.Pair;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@Controller
@RequestMapping("pim/website")
public class WebsitePageController extends ControllerSupport {

    private WebsitePageService websitePageService;
    private WebsiteService websiteService;

    /*public WebsitePageController(WebsitePageService websitePageService) {
        super(websitePageService, WebsitePage.class, new BreadcrumbsBuilder());
        this.websitePageService = websitePageService;
    }*/

    public WebsitePageController(WebsitePageService websitePageService, WebsiteService websiteService) {
        this.websitePageService = websitePageService;
        this.websiteService = websiteService;
    }

    @RequestMapping(value = "/pages/data")
    @ResponseBody
    public Result<Map<String, String>> getWebsitePages(HttpServletRequest request) {
        return new Result<Map<String, String>>().buildResult(new Request(request),
                dataTableRequest -> websitePageService.findAll(dataTableRequest.getPageRequest(Sort.by(new Sort.Order(Sort.Direction.ASC, "externalId"))), false),
                paginatedResult -> {
                    List<Map<String, String>> dataObjects = new ArrayList<>();
                    paginatedResult.forEach(e -> dataObjects.add(e.toMap()));
                    return dataObjects;
                });
    }

    @RequestMapping(value = {"/{id}", "pages/create"})
    public ModelAndView pageDetails(@PathVariable(value = "id", required = false) String id,
                                    @RequestParam(name = "reload", required = false) boolean reload,
                                    @RequestParam Map<String, Object> parameterMap,
                                    HttpServletRequest request) {

        /*Map<String, Object> model = new HashMap<>();
        model.put("active", "WEBSITES");
        model.put("mode", id == null ? "CREATE" : "DETAILS");
        model.put("view", "website/websitePage" + (reload ? "_body" : ""));
        return id == null ? super.details(model) : websitePageService.get(ID.EXTERNAL_ID(id), false)
                .map(websitePage -> {
                    model.put("websitePage", websitePage);
                    return super.details(id, model);
                }).orElseThrow(() -> new EntityNotFoundException("Unable to find Page with Id: " + id));*/

        Map<String, Object> model = new HashMap<>();
        model.put("active", "WEBSITES");
        if (id == null) {
            model.put("mode", "CREATE");
            model.put("websitePage", new WebsitePage());
        } else {
            Optional<WebsitePage> websitePage = websitePageService.get(ID.EXTERNAL_ID(id), false);
            if (websitePage.isPresent()) {
                model.put("mode", "DETAILS");
                model.put("websitePage", websitePage.get());
                model.put("breadcrumbs", new BreadcrumbsBuilder().init(id, WebsitePage.class, request, parameterMap, new BaseService[]{websiteService, websitePageService}).build());
            } else {
                throw new EntityNotFoundException("Unable to find Website with Id: " + id);
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
