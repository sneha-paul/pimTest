package com.bigname.pim.core.persistence.dao.mongo;

import com.bigname.pim.core.domain.Config;
import com.m7.xtreme.xcore.persistence.dao.mongo.GenericRepositoryImpl;
import org.springframework.data.mongodb.core.query.Criteria;
/**
 * Created by sanoop on 12/02/2019.
 */
public class ConfigRepositoryImpl extends GenericRepositoryImpl<Config, Criteria> implements ConfigRepository{
    public ConfigRepositoryImpl() {
        super(Config.class);
    }
}
