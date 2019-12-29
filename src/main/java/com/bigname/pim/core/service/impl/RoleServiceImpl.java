
package com.bigname.pim.core.service.impl;

import com.bigname.pim.core.domain.Role;
import com.bigname.pim.core.persistence.dao.mongo.RoleDAO;
import com.bigname.pim.core.service.RoleService;
import com.m7.xtreme.xcore.service.impl.BaseServiceSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.Validator;


/**
 * Created by sruthi on 27-02-2019.
 */
@Service
public class RoleServiceImpl extends BaseServiceSupport<Role, RoleDAO, RoleService> implements RoleService {
    private RoleDAO roleDAO;

    @Autowired
    public RoleServiceImpl(RoleDAO roleDAO, Validator validator) {
        super(roleDAO, "role", validator);
        this.roleDAO = roleDAO;
    }
}

