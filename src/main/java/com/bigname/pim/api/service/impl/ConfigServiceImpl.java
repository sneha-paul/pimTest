package com.bigname.pim.api.service.impl;

import com.bigname.pim.api.domain.Config;

import com.bigname.pim.api.persistence.dao.ConfigDAO;

import com.bigname.pim.api.service.ConfigService;
import com.m7.xcore.service.BaseServiceSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.Validator;

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

}