package com.bigname.pim.api.domain;

import org.springframework.data.annotation.Transient;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by sruthi on 12-11-2018.
 */
public class VerificationToken extends Entity<VerificationToken>{

    @Transient
    private String verificationId;

    private String token;

    private User user;

    private Date expiryDate;

    public String getverificationId() {
        return getExternalId();
    }

    public void setverificationId(String verificationId) {
        this.verificationId = verificationId;
        setExternalId(verificationId);
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public VerificationToken() {
        super();
    }

    public VerificationToken(String token, User user) {
        super(token);
        this.token = token;
        this.user = user;
    }

    public Date getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(Date expiryDate) {
        this.expiryDate = expiryDate;
    }

    @Override
    void setExternalId() {
        this.verificationId = getExternalId();

    }

    @Override
    public VerificationToken merge(VerificationToken verificationToken) {
        return null;
    }

    @Override
    public Map<String, String> toMap() {
        Map<String, String> map = new LinkedHashMap<>();
        map.put("externalId", getExternalId());
        map.put("token", getToken());
        map.put("user", String.valueOf(getUser()));
        map.put("expiryDate", String.valueOf(getExpiryDate()));
        map.put("active", getActive());
        return map;
    }
}
