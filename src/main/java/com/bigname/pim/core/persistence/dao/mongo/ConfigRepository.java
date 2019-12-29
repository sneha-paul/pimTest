package com.bigname.pim.core.persistence.dao.mongo;

import com.bigname.pim.core.domain.Config;
import com.m7.xtreme.xcore.persistence.dao.GenericRepository;
import org.springframework.data.mongodb.core.query.Criteria;

/**
 * Created by sanoop on 12/02/2019.
 */
public interface ConfigRepository extends GenericRepository<Config, Criteria> {
}
