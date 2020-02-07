package com.bigname.pim.core.service.impl;

import com.bigname.pim.core.domain.Config;
import com.bigname.pim.core.persistence.dao.mongo.ConfigDAO;
import com.bigname.pim.core.service.ConfigService;
import com.m7.xtreme.xcore.service.impl.BaseServiceSupport;
import com.m7.xtreme.xcore.util.ID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.validation.Validator;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by sanoop on 12/02/2019.
 */

@Service
public class ConfigServiceImpl extends BaseServiceSupport<Config, ConfigDAO, ConfigService> implements ConfigService {


    private ConfigDAO configDAO;

    @Autowired
    public ConfigServiceImpl(ConfigDAO configDAO, Validator validator) {
        super(configDAO, "Config", validator);
        this.configDAO = configDAO;
    }

    @Override
    public List<Map<String, String>> getConfigParams(ID<String> configId) {
        List<Map<String, String>> parameter = new ArrayList<>();
        get(configId, false).ifPresent(config -> {
            parameter.add(config.getSiteParameters());
        });
        return parameter;
    }

    @Override
    public List<Map<String, String>> getCasePreservedConfigParams(ID<String> configId, String... websiteId) {
        List<Map<String, String>> parameter = new ArrayList<>();
        get(configId, false).ifPresent(config -> {
            parameter.add(config.getCasePreservedSiteParameters(websiteId));
        });
        return parameter;
    }

    @Override
    public boolean updateParameter(String configId, String paramName, Map<String, String> parameters) {
        boolean[] success = {false};
        get(ID.EXTERNAL_ID(configId), false).ifPresent(config -> {
            /*Map<String, String> param = config.getSiteParameters();*/
            Map<String, Map<String, String>> paramDb = config.getParams();
            Map<String, String> param = paramDb.get("GLOBAL");
            /*Map<String, String> caseParam = config.getCasePreservedSiteParameters();*/
            Map<String, Map<String, String>> caseParamDb = config.getCasePreservedParams();
            Map<String, String> caseParam = caseParamDb.get("GLOBAL");
            Set<String> keySetParam;
            if(paramName.equals(parameters.get("paramName"))) {
                keySetParam = new HashSet<>();
            } else {
                keySetParam = param.keySet().stream().filter(k -> k.equals(parameters.get("paramName").toUpperCase())).collect(Collectors.toSet());
            }
            if(keySetParam.isEmpty()) {
                Set<String> keySet = param.keySet().stream().filter(k -> k.equals(paramName.toUpperCase())).collect(Collectors.toSet());
                keySet.forEach(key -> {
                    String[] value = config.getParameter(key, String.class).split("\\|");
                    caseParam.keySet().removeIf(k -> k.equals(value[0]));
                });
                param.keySet().removeIf(k -> k.equals(paramName.toUpperCase()));
                param.put(parameters.get("paramName").toUpperCase(), parameters.get("paramName") + "|" + parameters.get("paramValue"));
                caseParam.put(parameters.get("paramName"), parameters.get("paramValue"));
                paramDb.put("GLOBAL", param);
                caseParamDb.put("GLOBAL", caseParam);
                config.setParams(paramDb);
                config.setCasePreservedParams(caseParamDb);
                /*config.setParams(Map.of("GLOBAL", param));
                config.setCasePreservedParams(Map.of("GLOBAL", caseParam));*/
                config.setGroup("PARAMS");
                update(ID.EXTERNAL_ID(configId), config);
                success[0] = true;
            }
        });
        return success[0];
    }

    @Override
    public void deleteConfigParam(String configId, String paramName) {
        get(ID.EXTERNAL_ID(configId), false).ifPresent(config -> {
            /*Map<String, String> param = config.getSiteParameters();*/
            Map<String, Map<String, String>> paramDb = config.getParams();
            Map<String, String> param = paramDb.get("GLOBAL");
            /*Map<String, String> caseParam = config.getCasePreservedSiteParameters();*/
            Map<String, Map<String, String>> caseParamDb = config.getCasePreservedParams();
            Map<String, String> caseParam = caseParamDb.get("GLOBAL");
            Set<String> keySet = param.keySet().stream().filter(k -> k.equals(paramName.toUpperCase())).collect(Collectors.toSet());
            keySet.forEach(key -> {
                String[] value = config.getParameter(key, String.class).split("\\|");
                caseParam.keySet().removeIf(k -> k.equals(value[0]));
            });
            param.keySet().removeIf(k -> k.equals(paramName.toUpperCase()));config.setParams(Map.of("GLOBAL", param));
            /*config.setCasePreservedParams(Map.of("GLOBAL", caseParam));*/
            paramDb.put("GLOBAL", param);
            caseParamDb.put("GLOBAL", caseParam);
            config.setParams(paramDb);
            config.setCasePreservedParams(caseParamDb);
            config.setGroup("PARAMS");
            update(ID.EXTERNAL_ID(configId), config);
        });
    }
}