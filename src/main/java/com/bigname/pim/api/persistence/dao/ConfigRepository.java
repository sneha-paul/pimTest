package com.bigname.pim.api.persistence.dao;

import com.bigname.pim.api.domain.Config;
import com.m7.xtreme.xcore.persistence.dao.GenericRepository;
import org.springframework.data.mongodb.core.query.Criteria;

/**
 * Created by sanoop on 12/02/2019.
 */
public interface ConfigRepository extends GenericRepository<Config, Criteria> {
}
