package com.bigname.pim.core.persistence.dao.mongo;

import com.bigname.pim.core.domain.Role;
import com.m7.xtreme.xcore.persistence.dao.mongo.GenericDAO;


/**
 * Created by sruthi on 27-02-2019.
 */


public interface RoleDAO extends GenericDAO<Role>, RoleRepository {

    Role findByRole(String role);

}
