package com.bigname.pim.core.service;

import com.bigname.pim.core.domain.Config;
import com.bigname.pim.core.persistence.dao.mongo.ConfigDAO;
import com.m7.xtreme.xcore.service.BaseService;

/**
 * Created by sanoop on 12/02/2019.
 */
public interface ConfigService extends BaseService<Config , ConfigDAO> {
}
