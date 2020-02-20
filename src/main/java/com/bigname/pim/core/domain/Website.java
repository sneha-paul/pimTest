package com.bigname.pim.core.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.m7.xtreme.xcore.domain.MongoEntity;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Page;
import org.springframework.data.mongodb.core.index.Indexed;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import java.util.*;

import static com.bigname.pim.core.domain.Website.Property.*;
import static com.m7.xtreme.common.util.RegExBuilder.*;


public class Website extends MongoEntity<Website> {

    @Transient
    @NotEmpty(message = "Website Id cannot be empty", groups = {CreateGroup.class, DetailsGroup.class})
    @Pattern(regexp = "[" + ALPHA + NUMERIC + UNDERSCORE + "]", message = "website.websiteId.invalid")
    @NotBlank(message = "Website Id cannot be blank", groups = {CreateGroup.class, DetailsGroup.class})
    private String websiteId;

    @Indexed(unique = true)
    @NotEmpty(message = "Website name cannot be empty", groups = {CreateGroup.class, DetailsGroup.class})
    @Pattern(regexp = "[" + ALPHA + NUMERIC + SPACE + "]", message = "website.websiteName.invalid")
    @NotBlank(message = "Website name cannot be blank", groups = {CreateGroup.class, DetailsGroup.class})
    private String websiteName;

    @Indexed(unique = true)
    @NotEmpty(message = "Website URL cannot be empty", groups = {CreateGroup.class, DetailsGroup.class})
    @NotBlank(message = "Website URL cannot be blank", groups = {CreateGroup.class, DetailsGroup.class})
    private String url;

    private Map<String, String> urlRedirects = new HashMap<>();

    @Transient
    @JsonIgnore
    private Page<WebsiteCatalog> catalogs;

    public Website() {
        super();
    }

    public Website(Map<String, Object> properties) {
        this.setWebsiteName((String) properties.get(WEBSITE_NAME.name()));
        this.setWebsiteId((String) properties.get(WEBSITE_ID.name()));
        this.setUrl((String) properties.get(URL.name()));
        this.setActive((String) properties.get(ACTIVE.name()));
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

    protected void setExternalId() {
        this.websiteId = getExternalId();
    }

    public Map<String, String> getUrlRedirects() {
        return urlRedirects;
    }

    public void setUrlRedirects(Map<String, String> urlRedirects) {
        this.urlRedirects = urlRedirects;
    }

    @Override
    public Website cloneInstance() {
        Website clone = new Website();
        clone.setActive("N");
        clone.setExternalId(cloneValue(getExternalId()));
        clone.setWebsiteName(cloneValue(getWebsiteName()));
        clone.setUrl(cloneValue(getUrl()));
        return clone;
    }

    @Override
    public Website merge(Website website) {
        for (String group : website.getGroup()) {
            switch (group) {
                case "DETAILS":
                    this.setExternalId(website.getExternalId());
                    this.setWebsiteName(website.getWebsiteName());
                    this.setUrl(website.getUrl());
                    mergeBaseProperties(website);
                    break;
                case "URL":
                    this.setUrlRedirects(website.getUrlRedirects());
                    break;
            }
        }
        return this;
    }

    @Override
    public Map<String, String> toMap() {
        Map<String, String> map = new LinkedHashMap<>();
        map.put("externalId", getExternalId());
        map.put("websiteName", getWebsiteName());
        map.put("url", getUrl());
        map.putAll(getBasePropertiesMap());
        return map;
    }

    @Override
    public boolean equals(Map<String, Object> websiteMap) {
        return this.getId().equals(websiteMap.get(Property.ID.name()))
                && this.getWebsiteId().equals(websiteMap.get(Property.WEBSITE_ID.name()))
                && this.getWebsiteName().equals(websiteMap.get(Property.WEBSITE_NAME.name()))
                && this.getUrl().equals(websiteMap.get(Property.URL.name()))
                && this.getUrl().equals(websiteMap.get(Property.ACTIVE.name()));
    }

    public enum Property {
        ID, WEBSITE_ID, WEBSITE_NAME, URL, ACTIVE
    }

    @Override
    public void orchestrate() {
        super.orchestrate();
        if(isEmpty(this.websiteId)){
            this.websiteId = getExternalId();
        }
    }

    public Map<String, Object> diff(Website website, boolean... ignoreInternalId) {
        boolean _ignoreInternalId = ignoreInternalId != null && ignoreInternalId.length > 0 && ignoreInternalId[0];
        Map<String, Object> diff = new HashMap<>();
        if (!_ignoreInternalId && !this.getId().equals(website.getId())) {
            diff.put("internalId", website.getId());
        }
        if (!this.getWebsiteName().equals(website.getWebsiteName())) {
            diff.put("websiteName", website.getWebsiteName());
        }
        if (!this.getUrl().equals(website.getUrl())) {
            diff.put("url", website.getUrl());
        }
        if (!this.getActive().equals(website.getActive())) {
            diff.put("active", website.getActive());
        }

        return diff;
    }

    /*@Override
    public Website getCopy(Website website) {
        Website _website = new Website();
        _website.setWebsiteName(website.getWebsiteName());
        _website.setWebsiteId(website.getWebsiteId());
        _website.setUrl(website.getUrl());
        _website.setActive(website.getActive());
        _website.setDiscontinued(website.getDiscontinued());
        _website.setArchived(website.getArchived());
        _website.setVersionId(website.getVersionId());
        _website.setId(website.getId());
        return _website;
    }*/
}