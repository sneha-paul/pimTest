
package com.bigname.pim.api.service.impl;

import com.bigname.pim.api.domain.Role;
import com.bigname.pim.api.persistence.dao.RoleDAO;
import com.bigname.pim.api.service.RoleService;
import com.m7.xcore.service.BaseServiceSupport;
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

