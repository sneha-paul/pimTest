package com.bigname.pim.client.web.controller;

import com.bigname.pim.core.domain.Config;
import com.bigname.pim.core.service.ConfigService;
import com.bigname.pim.core.util.BreadcrumbsBuilder;
import com.m7.xtreme.common.datatable.model.Result;
import com.m7.xtreme.common.util.CollectionsUtil;
import com.m7.xtreme.common.util.ValidationUtil;
import com.m7.xtreme.xcore.exception.EntityNotFoundException;
import com.m7.xtreme.xcore.util.ID;
import com.m7.xtreme.xcore.web.controller.BaseController;
import org.javatuples.Pair;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by sanoop on 12/02/2019.
 */
@Controller
@RequestMapping("pim/configs")
public class ConfigController extends BaseController<Config, ConfigService> {

    private ConfigService configService;

    public ConfigController(ConfigService configService) {
        super(configService, Config.class, new BreadcrumbsBuilder());
        this.configService = configService;
    }

    @RequestMapping(value = {"/{id}", "/create"})
    public ModelAndView details(@PathVariable(value = "id", required = false) String id,
                                @RequestParam(name = "reload", required = false) boolean reload) {

        Map<String, Object> model = new HashMap<>();
        model.put("active", "CONFIG");
        model.put("mode", id == null ? "CREATE" : "DETAILS");
        model.put("view", "config/config" + (reload ? "_body" : ""));

        return id == null ? super.details(model) : configService.get(ID.EXTERNAL_ID(id), false)
                .map(config -> {
                    model.put("config", config);
                    return super.details(id, model);
                }).orElseThrow(() -> new EntityNotFoundException("Unable to find Config with Id: " + id));
    }

    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> create(Config config) {
        Map<String, Object> model = new HashMap<>();
        if (isValid(config, model, Config.CreateGroup.class)) {
            config.setActive("Y");
            configService.create(config);
            model.put("success", true);
        }
        return model;
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    @ResponseBody
    public Map<String, Object> update(@PathVariable(value = "id") String configId,
                                      Config config) {
        return update(configId, config, "/pim/configs/", config.getGroup().length == 1 && config.getGroup()[0].equals("DETAILS") ? Config.DetailsGroup.class : null);
    }

    @RequestMapping()
    public ModelAndView all() {
        Map<String, Object> model = new HashMap<>();
        model.put("active", "CONFIG");
        return new ModelAndView("config/configs", model);
    }

    @RequestMapping(value = {"/list", "/data"})
    @ResponseBody
    @SuppressWarnings("unchecked")
    public Result<Map<String, String>> all(HttpServletRequest request) {
        return super.all(request, "configName");
    }

    @RequestMapping(value= {"/{configId}/params/{paramName}", "/{configId}/params/create"})
    public ModelAndView paramDetails(@PathVariable(value = "configId") String configId,
                                         @PathVariable(value = "paramName", required = false) String paramName) {
        String[] paramNames = ValidationUtil.isNotEmpty(paramName) ? paramName.split("\\|") : new String[0];
        return configService.get(ID.EXTERNAL_ID(configId), false)
                .map(config -> {
                    Map<String, Object> model = new HashMap<>();
                    Map<String, String> param = new HashMap<>();
                    String mode;
                    if(paramNames.length == 2) {
                        mode = "DETAILS";
                        param.put("name", paramNames[1]);
                        param.put("scope", paramNames[0]);
                        param.put("value", config.getParameter(paramNames[1], String.class, paramNames[0]));
                    } else {
                        mode = "CREATE";
                    }
                    model.put("param", param);
                    model.put("mode", mode);
                    return new ModelAndView("config/param", model);
                }).orElseThrow(() -> new EntityNotFoundException("Unable to find Config with Id: " + configId));

    }

    /*@RequestMapping("/{id}/params/data")
    @ResponseBody
    public Result<Map<String, Object>> getConfigParams(@PathVariable(value = "id") String id, HttpServletRequest request) {
        return getAssociationGridData(request,
                WebsiteCatalog.class,
                dataTableRequest -> {
                    if(isEmpty(dataTableRequest.getSearch())) {
                        return websiteService.getWebsiteCatalogs(ID.EXTERNAL_ID(id), dataTableRequest.getPageRequest(associationSortPredicate), dataTableRequest.getStatusOptions());
                    } else {
                        return websiteService.findAllWebsiteCatalogs(ID.EXTERNAL_ID(id), "catalogName", dataTableRequest.getSearch(), dataTableRequest.getPageRequest(associationSortPredicate), false);
                    }
                });
    }*/

    @RequestMapping(value = "/{configId}/params", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> createAttribute(@PathVariable(value = "configId") String configId, @RequestParam(value="param") String param) {
        Map<String, Object> model = new HashMap<>();
        Config config = configService.get(ID.EXTERNAL_ID(configId), false).orElse(null);
        String[] values = param.split("\\.");
        config.setParameter(values[0], values[1]);
        configService.update(ID.EXTERNAL_ID(configId), config);
        model.put("success", true);
        return model;
    }

}