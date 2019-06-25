
package com.bigname.pim.api.persistence.dao;

import com.bigname.pim.api.domain.Role;
import com.m7.xtreme.xcore.persistence.dao.mongo.GenericRepositoryImpl;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;


/**
 * Created by sruthi on 27-02-2019.
 */

public class RoleRepositoryImpl extends GenericRepositoryImpl<Role, Criteria> implements RoleRepository {
    public RoleRepositoryImpl(MongoTemplate mongoTemplate) {
        super(mongoTemplate, Role.class);
    }
}

