package com.bigname.pim.api.domain;

import com.bigname.core.domain.Entity;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Map;


/**
 * Created by sruthi on 27-02-2019.
 */
@Document
public class Role extends Entity<Role> {

    private String userName1;

    private String role;

    public Role() {
        super();
    }

   public Role(String userRole, String userName) {
        this.userName1 = userName;
        this.role = userRole;
    }

    public String getUserName() {
        return userName1;
    }

    public void setUserName(String userName) {
        this.userName1 = userName;
    }

    public String getUserRole() {
        return role;
    }

    public void setUserRole(String userRole) {
        this.role = userRole;
    }

    @Override
    protected void setExternalId() {

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
