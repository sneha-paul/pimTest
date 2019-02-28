package com.bigname.pim.api.domain;

import com.bigname.core.domain.Entity;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Map;


/**
 * Created by sruthi on 27-02-2019.
 */
@Document
public class Role extends Entity<Role> {

    private String roleId;

    private String role;

    public Role() {
        super();
    }

   public Role(String userRole, String roleId) {
        this.roleId = roleId;
        this.role = userRole;
    }

    public String getRoleId() {
        return getExternalId();
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
        setExternalId(roleId);
    }

    public String getUserRole() {
        return role;
    }

    public void setUserRole(String userRole) {
        this.role = userRole;
    }

    @Override
    protected void setExternalId() {
        this.roleId = getExternalId();
    }

    @Override
    public Role merge(Role role) {
        return null;
    }

    @Override
    public Map<String, String> toMap() {
        return null;
    }

}
