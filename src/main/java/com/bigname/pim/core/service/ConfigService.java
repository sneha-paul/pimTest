package com.bigname.pim.core.service;

import com.bigname.pim.core.domain.Config;
import com.bigname.pim.core.persistence.dao.mongo.ConfigDAO;
import com.m7.xtreme.xcore.service.BaseService;
import com.m7.xtreme.xcore.util.ID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Map;

/**
 * Created by sanoop on 12/02/2019.
 */
public interface ConfigService extends BaseService<Config , ConfigDAO> {
    List<Map<String, String>> getConfigParams(ID<String> configId);

    List<Map<String, String>> getCasePreservedConfigParams(ID<String> configId, String... websiteId);
}
