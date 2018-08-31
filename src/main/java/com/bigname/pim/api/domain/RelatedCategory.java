package com.bigname.pim.api.domain;

import org.springframework.data.mongodb.core.index.Indexed;

/**
 * Created by sruthi on 30-08-2018.
 */
public class RelatedCategory extends EntityAssociation {

    /**
     * Internal id of the Category document, not the externalId (categoryId) attribute
     */
    @Indexed
    private String categoryId;

    /**
     * Internal id of the Category document, not the externalId (categoryId) attribute
     */
    @Indexed
    private String parentCategoryId;

    public RelatedCategory() { super(); }

    public RelatedCategory(String categoryId, String parentCategoryId) {
        super();
        this.categoryId = categoryId;
        this.parentCategoryId = parentCategoryId;
        setActive("Y");
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getParentCategoryId() {
        return parentCategoryId;
    }

    public void setParentCategoryId(String parentCategoryId) {
        this.parentCategoryId = parentCategoryId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RelatedCategory)) return false;

        RelatedCategory that = (RelatedCategory) o;

        if (categoryId != null ? !categoryId.equals(that.categoryId) : that.categoryId != null) return false;
        return parentCategoryId != null ? parentCategoryId.equals(that.parentCategoryId) : that.parentCategoryId == null;
    }

    @Override
    public int hashCode() {
        int result = categoryId != null ? categoryId.hashCode() : 0;
        result = 31 * result + (parentCategoryId != null ? parentCategoryId.hashCode() : 0);
        return result;
    }
}
