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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
    public List<Map<String, Object>> getParams(ID<String> configId) {
        List<Map<String, Object>> parameter = new ArrayList<>();
        get(configId, false).ifPresent(config -> {
            parameter.add(config.getSiteParameters());
        });
        return parameter;
    }

    @Override
    public void deleteConfigParam(String configId, String paramName) {
        /*get(ID.EXTERNAL_ID(configId), false).ifPresent(config -> {
            List<Map<String, Object>> paramList = getParams(ID.EXTERNAL_ID(config.getConfigId()));
            paramList.forEach(param -> {
                boolean isKey = param.containsKey(paramName);
                if(isKey) {
                    Config config1 = config.getParameter(paramName, Config.class);
                    param.remove(paramName);
                }
            });
            paramList.forEach(param -> param.forEach((k,v) -> config.setParameter(k,v)));
            config.setGroup("DETAILS");
            update(ID.EXTERNAL_ID(configId), config);
        });*/
    }
}