
package com.bigname.pim.core.persistence.dao.mongo;

import com.bigname.pim.core.domain.Role;
import com.m7.xtreme.xcore.persistence.dao.mongo.GenericRepositoryImpl;
import org.springframework.data.mongodb.core.query.Criteria;


/**
 * Created by sruthi on 27-02-2019.
 */

public class RoleRepositoryImpl extends GenericRepositoryImpl<Role, Criteria> implements RoleRepository {
    public RoleRepositoryImpl() {
        super(Role.class);
    }
}

