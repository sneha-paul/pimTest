
package com.bigname.pim.api.persistence.dao.mongo;

import com.bigname.pim.api.domain.Role;
import com.m7.xtreme.xcore.persistence.dao.mongo.GenericRepositoryImpl;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;


/**
 * Created by sruthi on 27-02-2019.
 */

public class RoleRepositoryImpl extends GenericRepositoryImpl<Role, Criteria> implements RoleRepository {
    public RoleRepositoryImpl() {
        super(Role.class);
    }
}

