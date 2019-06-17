
package com.bigname.pim.api.persistence.dao;

import com.bigname.pim.api.domain.Role;
import com.m7.xtreme.xcore.persistence.dao.GenericRepositoryImpl;
import org.springframework.data.mongodb.core.MongoTemplate;


/**
 * Created by sruthi on 27-02-2019.
 */

public class RoleRepositoryImpl extends GenericRepositoryImpl<Role> implements RoleRepository {
    public RoleRepositoryImpl(MongoTemplate mongoTemplate) {
        super(mongoTemplate, Role.class);
    }
}

