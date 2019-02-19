package com.bigname.pim.api.persistence.dao;

import com.bigname.core.persistence.dao.GenericRepositoryImpl;
import com.bigname.pim.api.domain.Config;
import org.springframework.data.mongodb.core.MongoTemplate;

/**
 * Created by sanoop on 12/02/2019.
 */
public class ConfigRepositoryImpl extends GenericRepositoryImpl<Config> implements ConfigRepository{
    public ConfigRepositoryImpl(MongoTemplate mongoTemplate) {
        super(mongoTemplate, Config.class);
    }
}
