package com.bigname.pim.api.domain;

import org.springframework.data.mongodb.core.index.Indexed;

/**
 * Created by sruthi on 31-08-2018.
 */
public class RootCategory extends  EntityAssociation {

    /**
     * Internal id of the Catalog document, not the externalId (catalogId) attribute
     */
    @Indexed
    private String catalogId;

    /**
     * Internal id of the Category document, not the externalId (categoryId) attribute
     */
    @Indexed
    private String categoryId;

    public RootCategory() { super(); }

    public RootCategory(String catalogId, String categoryId) {
        super();
        this.catalogId = catalogId;
        this.categoryId = categoryId;
        setActive("Y");
    }

    public String getCatalogId() {
        return catalogId;
    }

    public void setCatalogId(String catalogId) {
        this.catalogId = catalogId;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RootCategory)) return false;

        RootCategory that = (RootCategory) o;

        if (catalogId != null ? !catalogId.equals(that.catalogId) : that.catalogId != null) return false;
        return categoryId != null ? categoryId.equals(that.categoryId) : that.categoryId == null;
    }

    @Override
    public int hashCode() {
        int result = catalogId != null ? catalogId.hashCode() : 0;
        result = 31 * result + (categoryId != null ? categoryId.hashCode() : 0);
        return result;
    }
}
