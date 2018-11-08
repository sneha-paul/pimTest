package com.bigname.pim.api.domain;

import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.validation.constraints.NotEmpty;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by sruthi on 05-11-2018.
 */
@Document
public class User extends Entity<User> implements UserDetails{

    @Indexed(unique = true)
    @NotEmpty(message = "{user.email.empty}", groups = {CreateGroup.class, DetailsGroup.class})
    private String email;

    @NotEmpty(message = "{user.userName.empty}", groups = {CreateGroup.class, DetailsGroup.class})
    private String userName;

    @NotEmpty(message = "{user.password.empty}", groups = {CreateGroup.class})
    private String password;

    private String avatar = "avatar.png";

    public User() {
        super();
    }


    public String getEmail() {
        return getExternalId();
    }

    public void setEmail(String email) {
        this.email = email;
        setExternalId(email);
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    @Override
    void setExternalId() {
        this.email = getExternalId();
    }

    @Override
    public User merge(User user) {
        for(String group : user.getGroup()){
            switch(group) {
                case "DETAILS":
                    this.setExternalId(user.getExternalId());
                    this.setEmail(user.getEmail());
                    this.setUserName(user.getUserName());
                    this.setActive(user.getActive());
                    this.setAvatar(user.getAvatar());
                    break;
            }
        }
        return this;
    }

    @Override
    public Map<String, String> toMap() {
        Map<String, String> map = new LinkedHashMap<>();
        map.put("externalId", getExternalId());
        map.put("userName", getUserName());
        map.put("password", getPassword());
        map.put("active", getActive());
        return map;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Arrays.asList(new SimpleGrantedAuthority("ROLE_USER")); //TODO - should change
    }

    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return userName;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
