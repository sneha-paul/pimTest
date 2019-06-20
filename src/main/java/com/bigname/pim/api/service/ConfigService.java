package com.bigname.pim.api.service;

import com.bigname.pim.api.domain.Config;
import com.bigname.pim.api.persistence.dao.ConfigDAO;
import com.m7.xtreme.xcore.service.mongo.BaseService;

/**
 * Created by sanoop on 12/02/2019.
 */
public interface ConfigService extends BaseService<Config , ConfigDAO> {
}
