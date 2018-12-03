package com.bigname.pim.api.domain;

import org.springframework.data.mongodb.core.index.Indexed;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by sruthi on 31-08-2018.
 */
public class RootCategory extends  EntityAssociation<WebsiteCatalog, Category> {

    @Indexed
    private String websiteCatalogId;

    /**
     * Internal id of the Category document, not the externalId (categoryId) attribute
     */
    @Indexed
    private String rootCategoryId;

    public RootCategory() { super(); }

    public RootCategory(String websiteCatalogId, String categoryId, int subSequenceNum) {
        super();
        this.websiteCatalogId = websiteCatalogId;
        this.rootCategoryId = categoryId;
        setSubSequenceNum(subSequenceNum);
        setActive("Y");
    }

    public RootCategory init(WebsiteCatalog websiteCatalog, Category rootCategory) {
        super.init(websiteCatalog, rootCategory);
        return this;
    }

    public String getWebsiteCatalogId() {
        return websiteCatalogId;
    }

    public void setWebsiteCatalogId(String websiteCatalogId) {
        this.websiteCatalogId = websiteCatalogId;
    }

    public String getRootCategoryId() {
        return rootCategoryId;
    }

    public void setRootCategoryId(String rootCategoryId) {
        this.rootCategoryId = rootCategoryId;
    }

    @Override
    public Map<String, String> toMap() {
        Map<String, String> map = new LinkedHashMap<>();
        map.put("externalId", getChild().getCategoryId());
        map.put("rootCategoryName", getChild().getCategoryName());
        map.put("active", getActive());
        map.put("sequenceNum", Long.toString(getSequenceNum()));
        map.put("subSequenceNum", Integer.toString(getSubSequenceNum()));
        return map;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RootCategory)) return false;

        RootCategory that = (RootCategory) o;

        if (websiteCatalogId != null ? !websiteCatalogId.equals(that.websiteCatalogId) : that.websiteCatalogId != null) return false;
        return rootCategoryId != null ? rootCategoryId.equals(that.rootCategoryId) : that.rootCategoryId == null;
    }

    @Override
    public int hashCode() {
        int result = websiteCatalogId != null ? websiteCatalogId.hashCode() : 0;
        result = 31 * result + (rootCategoryId != null ? rootCategoryId.hashCode() : 0);
        return result;
    }
}
