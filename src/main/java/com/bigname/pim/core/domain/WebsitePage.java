package com.bigname.pim.core.domain;

import com.m7.xtreme.xcore.domain.MongoEntity;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;

import javax.persistence.Transient;
import java.util.LinkedHashMap;
import java.util.Map;

@CompoundIndexes({
        @CompoundIndex(name = "uniquePageUrl", unique = true, def = "{'websiteId':1, 'pageUrl':1}")
})
public class WebsitePage extends MongoEntity<WebsitePage> {

    @Transient
    private String pageId;

    private String websiteId;

    private String pageName;

    @Indexed(unique = true)
    private String pageUrl;

    private String friendlyUrl;

    private String redirectURL;

    private Map<String, Map<String, Object>> pageAttributes = new LinkedHashMap<>();

    public WebsitePage() {
        super();
    }

    public String getPageId() {
        return getExternalId();
    }

    public void setPageId(String pageId) {
        this.pageId = pageId;
        setExternalId(pageId);
    }

    public String getWebsiteId() {
        return websiteId;
    }

    public void setWebsiteId(String websiteId) {
        this.websiteId = websiteId;
    }

    public String getPageName() {
        return pageName;
    }

    public void setPageName(String pageName) {
        this.pageName = pageName;
    }

    public String getPageUrl() {
        return pageUrl;
    }

    public void setPageUrl(String pageUrl) {
        this.pageUrl = pageUrl;
    }

    public String getFriendlyUrl() {
        return friendlyUrl;
    }

    public void setFriendlyUrl(String friendlyUrl) {
        this.friendlyUrl = friendlyUrl;
    }

    public String getRedirectURL() {
        return redirectURL;
    }

    public void setRedirectURL(String redirectURL) {
        this.redirectURL = redirectURL;
    }

    public Map<String, Map<String, Object>> getPageAttributes() {
        return pageAttributes;
    }

    public void setPageAttributes(Map<String, Map<String, Object>> pageAttributes) {
        this.pageAttributes = pageAttributes;
    }

    @Override
    protected void setExternalId() {
        this.pageId = getExternalId();
    }

    @Override
    public Map<String, String> toMap() {
        Map<String, String> map = new LinkedHashMap<>();
        map.put("externalId", getExternalId());
        map.put("pageName", getPageName());
        map.put("pageUrl", getPageUrl());
        map.put("pageFriendlyUrl", getFriendlyUrl());
        map.put("redirectUrl", getRedirectURL());
        map.putAll(getBasePropertiesMap());
        return map;
    }

    @Override
    public WebsitePage merge(WebsitePage websitePage) {
        for (String group : websitePage.getGroup()) {
            switch (group) {
                case "DETAILS":
                    this.setExternalId(websitePage.getExternalId());
                    this.setPageName(websitePage.getPageName());
                    this.setPageUrl(websitePage.getPageUrl());
                    mergeBaseProperties(websitePage);
                    break;
            }
        }
        return this;
    }
}
