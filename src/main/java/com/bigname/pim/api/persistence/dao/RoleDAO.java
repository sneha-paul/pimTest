package com.bigname.pim.api.persistence.dao;

import com.bigname.pim.api.domain.Role;
import com.m7.xcore.persistence.dao.GenericDAO;


/**
 * Created by sruthi on 27-02-2019.
 */


public interface RoleDAO extends GenericDAO<Role>, RoleRepository {

    Role findByRole(String role);

}
