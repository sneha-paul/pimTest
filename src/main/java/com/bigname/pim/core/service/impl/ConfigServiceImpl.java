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
}