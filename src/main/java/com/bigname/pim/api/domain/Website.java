package com.bigname.pim.api.domain;

import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.Indexed;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import java.util.LinkedHashMap;
import java.util.Map;

import static com.bigname.common.util.RegExBuilder.*;


public class Website extends Entity<Website> {

    @Transient
    @NotEmpty(message = "website.websiteId.empty")
    @Pattern(regexp = "[" + ALPHA + UNDERSCORE + "]", message = "website.websiteId.invalid")
    private String websiteId;

    @Indexed(unique = true)
    @NotEmpty(message = "website.websiteName.empty")
    @Pattern(regexp = "[" + ALPHA + NUMERIC + SPACE + "]", message = "website.websiteName.invalid")
    private String websiteName;

    @Indexed(unique = true)
    @NotEmpty(message = "website.url.empty")
    private String url;

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

    @Override
    public Map<String, String> toMap() {
        Map<String, String> map = new LinkedHashMap<>();
        map.put("externalId", getExternalId());
        map.put("websiteName", getWebsiteName());
        map.put("url", getUrl());
        map.put("active", getActive());
        return map;
    }

}
