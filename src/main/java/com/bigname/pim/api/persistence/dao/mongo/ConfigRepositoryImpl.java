package com.bigname.pim.api.persistence.dao.mongo;

import com.bigname.pim.api.domain.Config;
import com.m7.xtreme.xcore.persistence.dao.mongo.GenericRepositoryImpl;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
/**
 * Created by sanoop on 12/02/2019.
 */
public class ConfigRepositoryImpl extends GenericRepositoryImpl<Config, Criteria> implements ConfigRepository{
    public ConfigRepositoryImpl() {
        super(Config.class);
    }
}
