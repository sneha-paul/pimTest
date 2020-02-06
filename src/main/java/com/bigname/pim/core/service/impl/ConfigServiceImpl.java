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
    public List<Map<String, String>> getCasePreservedConfigParams(ID<String> configId) {
        List<Map<String, String>> parameter = new ArrayList<>();
        get(configId, false).ifPresent(config -> {
            parameter.add(config.getCasePreservedSiteParameters());
        });
        return parameter;
    }

    @Override
    public boolean updateParameter(String configId, String paramName, Map<String, String> parameters) {
        get(ID.EXTERNAL_ID(configId), false).ifPresent(config -> {
            Map<String, String> param = config.getSiteParameters();
            Map<String, String> caseParam = config.getCasePreservedSiteParameters();
            Set<String> keySet = param.keySet().stream().filter(k -> k.equals(paramName.toUpperCase())).collect(Collectors.toSet());
            keySet.forEach(key -> {
                String[] value = config.getParameter(key, String.class).split("\\|");
                caseParam.keySet().removeIf(k -> k.equals(value[0]));
            });
            param.keySet().removeIf(k -> k.equals(paramName.toUpperCase()));

            Set<String> keySetParam = param.keySet().stream().filter(k -> k.equals(parameters.get("paramName").toUpperCase())).collect(Collectors.toSet());
            keySetParam.forEach(key -> {
                String[] value = config.getParameter(key, String.class).split("\\|");
                caseParam.keySet().removeIf(k -> k.equals(value[0]));
            });
            param.keySet().removeIf(k -> k.equals(parameters.get("paramName").toUpperCase()));

            param.put(parameters.get("paramName").toUpperCase(), parameters.get("paramName") + "|" + parameters.get("paramValue"));
            caseParam.put(parameters.get("paramName"),  parameters.get("paramValue"));
            config.setParams(Map.of("GLOBAL", param));
            config.setCasePreservedParams(Map.of("GLOBAL", caseParam));
            config.setGroup("PARAMS");
            update(ID.EXTERNAL_ID(configId), config);
        });
        return true;
    }

    /*@Override
    public void deleteConfigParam(String configId, String paramName) {
        get(ID.EXTERNAL_ID(configId), false).ifPresent(config -> {
            List<Map<String, String>> paramList = getCasePreservedConfigParams(ID.EXTERNAL_ID(config.getConfigId()));
            paramList.forEach(param -> {
                if(param.containsKey(paramName)) {
                    param.remove(paramName);
                }
            });
            paramList.forEach(param -> {
                Map<String, Map<String, String>> casePreserveMap = new HashMap<>();
                casePreserveMap.put("GLOBAL", param);
                config.setCasePreservedParams(casePreserveMap);

                Map<String, Map<String, String>> paramMap = new HashMap<>();
                Map<String, String> mapKeyUp = param.entrySet().stream().collect(Collectors.toMap(
                        entry -> (entry.getKey().toUpperCase()),
                        entry -> entry.getValue())
                );
                paramMap.put("GLOBAL", mapKeyUp);
                config.setParams(paramMap);
            });
            config.setGroup("PARAMS");
            update(ID.EXTERNAL_ID(configId), config);
        });
    }*/

    @Override
    public void deleteConfigParam(String configId, String paramName) {
        get(ID.EXTERNAL_ID(configId), false).ifPresent(config -> {
            Map<String, String> param = config.getSiteParameters();
            Map<String, String> caseParam = config.getCasePreservedSiteParameters();
            Set<String> keySet = param.keySet().stream().filter(k -> k.equals(paramName.toUpperCase())).collect(Collectors.toSet());
            keySet.forEach(key -> {
                String[] value = config.getParameter(key, String.class).split("\\|");
                caseParam.keySet().removeIf(k -> k.equals(value[0]));
            });
            param.keySet().removeIf(k -> k.equals(paramName.toUpperCase()));config.setParams(Map.of("GLOBAL", param));
            config.setCasePreservedParams(Map.of("GLOBAL", caseParam));
            config.setGroup("PARAMS");
            update(ID.EXTERNAL_ID(configId), config);
        });
    }
}