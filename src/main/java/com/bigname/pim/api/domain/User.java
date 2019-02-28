package com.bigname.pim.api.domain;

import com.bigname.core.domain.Entity;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.validation.constraints.NotEmpty;
import java.util.*;

/**
 * Created by sruthi on 05-11-2018.
 */
@Document
public class User extends Entity<User> implements UserDetails {

    @Indexed(unique = true)
    @NotEmpty(message = "{user.email.empty}", groups = {CreateGroup.class, DetailsGroup.class})
    private String email;

    @NotEmpty(message = "{user.userName.empty}", groups = {CreateGroup.class, DetailsGroup.class})
    private String userName;

    @NotEmpty(message = "{user.password.empty}", groups = {CreateGroup.class, ChangePasswordGroup.class})
    private String password;

    @Transient
    private String confirmPassword;

    private String avatar = "avatar.png";

    private String status;

    private List<String> userRole;

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

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<String> getUserRole() {
        return userRole;
    }

    public void setUserRole(List<String> userRole) {
        this.userRole = userRole;
    }

    @Override
    protected void setExternalId() {
        this.email = getExternalId();
    }

    @Override
    public User merge(User user) {
        for (String group : user.getGroup()) {
            switch (group) {
                case "DETAILS":
                    this.setExternalId(user.getExternalId());
                    this.setEmail(user.getEmail());
                    this.setUserName(user.getUserName());
                    this.setActive(user.getActive());
                    this.setStatus(user.getStatus());
                    this.setAvatar(user.getAvatar());
                    break;
                case "CHANGE-PASSWORD":
                    this.setPassword(user.getPassword());
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
        map.put("status", getStatus());
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
        return "Y".equals(getActive());
    }

    public interface ChangePasswordGroup {
    }

    public Map<String, Object> diff(User user, boolean... ignoreInternalId) {
        boolean _ignoreInternalId = ignoreInternalId != null && ignoreInternalId.length > 0 && ignoreInternalId[0];
        Map<String, Object> diff = new HashMap<>();
        if (!_ignoreInternalId && !this.getId().equals(user.getId())) {
            diff.put("internalId", user.getId());
        }
        if (!this.getEmail().equals(user.getEmail())) {
            diff.put("email", user.getEmail());
        }
        if (!this.getUserName().equals(user.getUsername())) {
            diff.put("userName", user.getUserName());
        }
        if (!this.getPassword().equals(user.getPassword())) {
            diff.put("password", user.getPassword());
        }

        if (!this.getAvatar().equals(user.getAvatar())) {
            diff.put("avatar", user.getAvatar());
        }
        if (!this.getStatus().equals(user.getStatus())) {
            diff.put("status", user.getStatus());
        }
        return diff;
    }
}

