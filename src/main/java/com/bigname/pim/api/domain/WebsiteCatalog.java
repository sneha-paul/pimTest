package com.bigname.pim.api.domain;

import com.m7.xcore.domain.EntityAssociation;
import org.springframework.data.mongodb.core.index.Indexed;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by manu on 8/19/18.
 */

public class WebsiteCatalog extends EntityAssociation<Website, Catalog> {


    /**
     * Internal id of the Website document, not the externalId (websiteId) attribute
     */
    @Indexed
    private String websiteId;

    /**
     * Internal id of the Catalog document, not the externalId (catalogId) attribute
     */
    @Indexed
    private String catalogId;



    public WebsiteCatalog() {
        super();
    }

    public WebsiteCatalog(String websiteId, String catalogId, int subSequenceNum) {
        super();
        this.websiteId = websiteId;
        this.catalogId = catalogId;
        setSubSequenceNum(subSequenceNum);
        setActive("Y");
    }

    public WebsiteCatalog init(Website website, Catalog catalog) {
        super.init(website, catalog);
        return this;
    }

    public String getWebsiteId() {
        return websiteId;
    }

    public void setWebsiteId(String websiteId) {
        this.websiteId = websiteId;
    }

    public String getCatalogId() {
        return catalogId;
    }

    public void setCatalogId(String catalogId) {
        this.catalogId = catalogId;
    }

    @Override
    public Map<String, Object> toMap(Map<String, Object> attributeMap) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("externalId", attributeMap.get("externalId"));
        map.put("catalogName", attributeMap.get("catalogName"));
        map.put("active", attributeMap.get("active"));
        map.put("sequenceNum", attributeMap.get("sequenceNum"));
        map.put("subSequenceNum", attributeMap.get("subSequenceNum"));
        return map;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        WebsiteCatalog that = (WebsiteCatalog) o;

        if (websiteId != null ? !websiteId.equals(that.websiteId) : that.websiteId != null) return false;
        return catalogId != null ? catalogId.equals(that.catalogId) : that.catalogId == null;

    }

    @Override
    public int hashCode() {
        int result = websiteId != null ? websiteId.hashCode() : 0;
        result = 31 * result + (catalogId != null ? catalogId.hashCode() : 0);
        return result;
    }
}

