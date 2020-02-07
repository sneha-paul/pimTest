package com.bigname.pim.client.web.controller;

import com.bigname.pim.core.domain.Config;
import com.bigname.pim.core.service.ConfigService;
import com.bigname.pim.core.util.BreadcrumbsBuilder;
import com.m7.xtreme.common.datatable.model.Pagination;
import com.m7.xtreme.common.datatable.model.Request;
import com.m7.xtreme.common.datatable.model.Result;
import com.m7.xtreme.common.datatable.model.SortOrder;
import com.m7.xtreme.common.util.ValidationUtil;
import com.m7.xtreme.xcore.exception.EntityNotFoundException;
import com.m7.xtreme.xcore.util.ID;
import com.m7.xtreme.xcore.web.controller.BaseController;
import com.m7.xtreme.xplatform.model.Breadcrumbs;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

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
                    if(paramNames.length > 0) {
                        mode = "DETAILS";
                        param.put("configId", configId);
                        if(paramNames.length == 1) {
                            param.put("caseName", paramName);
                            param.put("name", paramName.toUpperCase());
                            param.put("scope", "GLOBAL");
                            String[] paramValue = config.getParameter(paramName.toUpperCase(), String.class).split("\\|");
                            param.put("value", paramValue[1]);
                            model.put("breadcrumbs", new Breadcrumbs("Config",
                                    "Config", "/pim/configs/", config.getConfigName(), "/pim/configs/" + config.getConfigId(),
                                    "Params", "/pim/configs/" + config.getConfigId() + "#params", paramName, ""));
                        } else {
                            param.put("caseName", paramNames[1]);
                            param.put("name", paramNames[1].toUpperCase());
                            param.put("scope", paramNames[0]);
                            param.put("value", config.getParameter(paramNames[1].toUpperCase(), String.class, paramNames[0]));
                        }
                    } else {
                        mode = "CREATE";
                    }
                    model.put("paramMap", param);
                    model.put("mode", mode);
                    return new ModelAndView("config/param", model);
                }).orElseThrow(() -> new EntityNotFoundException("Unable to find Config with Id: " + configId));
    }

    @RequestMapping("/{configId}/params/data")
    @ResponseBody
    public Result<Map<String, String>> getConfigParameters(@PathVariable(value = "configId") String configId, HttpServletRequest request) {
        Request dataTableRequest = new Request(request);
        Pagination pagination = dataTableRequest.getPagination();
        Result<Map<String, String>> result = new Result<>();

        result.setDraw(dataTableRequest.getDraw());
        Sort sort = null;
        if(pagination.hasSorts()) {
            sort = Sort.by(new Sort.Order(Sort.Direction.valueOf(SortOrder.fromValue(dataTableRequest.getOrder().getSortDir()).name()), dataTableRequest.getOrder().getName()));
        }
        List<Map<String, String>> dataObjects = new ArrayList<>();
        List<Map<String, String>> paramList = configService.getCasePreservedConfigParams(ID.EXTERNAL_ID(configId));
        paramList.forEach(param -> param.forEach((k, v) -> {
            Map<String, String> newMap = new HashMap<>();
            newMap.put("paramName", k);
            newMap.put("paramValue", String.valueOf(v));
            dataObjects.add(newMap);
        }));
        Page<Map<String, String>> paginatedResult = new PageImpl<>(dataObjects);
        result.setDataObjects(dataObjects);
        result.setRecordsTotal(Long.toString(paginatedResult.getTotalElements()));
        result.setRecordsFiltered(Long.toString(paginatedResult.getTotalElements()));
        return result;
    }

    @RequestMapping(value = "/{configId}/params", method = RequestMethod.PUT)
    @ResponseBody
    public Map<String, Object> createParameter(@PathVariable(value = "configId") String configId, @RequestParam Map<String, String> parameters) {
        Map<String, Object> model = new HashMap<>();
        boolean[] success = {false};
        configService.get(ID.EXTERNAL_ID(configId), false).ifPresent(config -> {
            Map<String, Map<String, String>> paramDb = config.getParams();
            Map<String, String> param = paramDb.get("GLOBAL");
            if(paramDb.get("GLOBAL") == null) {
                config.setParameter(parameters.get("paramName"), parameters.get("paramName") + "|" + parameters.get("paramValue"));
            } else {
                Set<String> keySet = param.keySet().stream().filter(k -> k.equals(parameters.get("paramName").toUpperCase())).collect(Collectors.toSet());
                if (keySet.isEmpty()) {
                    config.setParameter(parameters.get("paramName"), parameters.get("paramName") + "|" + parameters.get("paramValue"));
                } else {
                    Map<String, Map<String, String>> caseParamDb = config.getCasePreservedParams();
                    Map<String, String> caseParam = caseParamDb.get("GLOBAL");
                    keySet.forEach(key -> {
                        String[] value = config.getParameter(key, String.class).split("\\|");
                        caseParam.keySet().removeIf(k -> k.equals(value[0]));
                    });
                    param.put(parameters.get("paramName").toUpperCase(), parameters.get("paramName") + "|" + parameters.get("paramValue"));
                    paramDb.put("GLOBAL", param);
                    caseParam.put(parameters.get("paramName"), parameters.get("paramValue"));
                    caseParamDb.put("GLOBAL", caseParam);
                    config.setParams(paramDb);
                    config.setCasePreservedParams(caseParamDb);
                }
            }
            config.setGroup("PARAMS");
            configService.update(ID.EXTERNAL_ID(configId), config);
            success[0] = true;
        });
        model.put("success", success[0]);
        return model;
    }

    @RequestMapping(value = "/{configId}/params/{paramName}", method = RequestMethod.PUT)
    @ResponseBody
    public Map<String, Object> updateParameter(@PathVariable(value = "configId") String configId, @PathVariable(value = "paramName") String paramName, @RequestParam Map<String, String> parameters) {
        Map<String, Object> model = new HashMap<>();
        boolean[] success = {false};
        success[0] = configService.updateParameter(configId, paramName, parameters);
        model.put("success", success[0]);
        return model;
    }


    @RequestMapping(value = "/{configId}/params/{paramName}/delete", method = RequestMethod.PUT)
    @ResponseBody
    public Map<String, Object> deleteParams(@PathVariable(value = "configId") String configId, @PathVariable(value = "paramName") String paramName) {
        Map<String, Object> model = new HashMap<>();
        configService.deleteConfigParam(configId, paramName);
        model.put("success", true);
        return model;
    }
}