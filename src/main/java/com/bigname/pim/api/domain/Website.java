package com.bigname.pim.api.domain;

import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Page;
import org.springframework.data.mongodb.core.index.Indexed;

import javax.validation.constraints.NotEmpty;
import java.util.UUID;


public class Website extends Entity<Website> {


    @Transient
    @NotEmpty(message = "Website Id cannot be empty")
    String websiteId;

    @Indexed(unique = true)
    @NotEmpty(message = "Website Name cannot be empty")
    private String websiteName;

    @Indexed(unique = true)
    @NotEmpty(message = "Website URL cannot be empty")
    private String url;

    @Transient
    private Page<Catalog> catalogs;

    public Website() {
        super();
    }

    public Website(String externalId, String websiteName, String url) {
        super(externalId);
        this.websiteName = websiteName;
        this.url = url;
    }

    public String getWebsiteId() {
        return getExternalId();
    }

    public void setWebsiteId(String websiteId) {
        this.websiteId = websiteId;
        setExternalId(websiteId);
    }

    public String getWebsiteName() {
        return websiteName;
    }

    public void setWebsiteName(String websiteName) {
        this.websiteName = websiteName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Page<Catalog> getCatalogs() {
        return catalogs;
    }

    public void setCatalogs(Page<Catalog> catalogs) {
        this.catalogs = catalogs;
    }

    void setExternalId() {
        this.websiteId = getExternalId();
    }

    public Website merge(Website website) {
        this.setExternalId(website.getExternalId());
        this.setWebsiteName(website.getWebsiteName());
        this.setUrl(website.getUrl());
        this.setActive(website.getActive());
        return this;
    }

}
