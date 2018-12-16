package com.bigname.pim.api.domain;

import org.springframework.data.mongodb.core.index.Indexed;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by sruthi on 30-08-2018.
 */
public class RelatedCategory extends EntityAssociation<Category, Category> {

    /**
     * Internal id of the Category document, not the externalId (categoryId) attribute
     */
    @Indexed
    private String categoryId;

    /**
     * Internal id of the Category document, not the externalId (categoryId) attribute
     */
    @Indexed
    private String subCategoryId;

    @Indexed
    private String fullSubCategoryId;

    public RelatedCategory() { super(); }

    public RelatedCategory(String categoryId, String subCategoryId, String fullSubCategoryId, int subSequenceNum) {
        super();
        this.categoryId = categoryId;
        this.subCategoryId = subCategoryId;
        this.fullSubCategoryId = fullSubCategoryId + "|" + subCategoryId;
        setSubSequenceNum(subSequenceNum);
        setActive("Y");
    }

    public RelatedCategory init(Category category, Category subCategory) {
        super.init(category, subCategory);
        return this;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getSubCategoryId() {
        return subCategoryId;
    }

    public void setSubCategoryId(String subCategoryId) {
        this.subCategoryId = subCategoryId;
    }

    public String getFullSubCategoryId() {
        return fullSubCategoryId;
    }

    public void setFullSubCategoryId(String fullSubCategoryId) {
        this.fullSubCategoryId = fullSubCategoryId;
    }

    public static Map<String, Object> toMap(Map<String, Object> attributesMap) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("externalId", attributesMap.get("externalId"));
        map.put("subCategoryName", attributesMap.get("categoryName"));
        map.put("active", attributesMap.get("active"));
        map.put("sequenceNum", attributesMap.get("sequenceNum"));
        return map;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RelatedCategory)) return false;

        RelatedCategory that = (RelatedCategory) o;

        if (categoryId != null ? !categoryId.equals(that.categoryId) : that.categoryId != null) return false;
        return subCategoryId != null ? subCategoryId.equals(that.subCategoryId) : that.subCategoryId == null;
    }

    @Override
    public int hashCode() {
        int result = categoryId != null ? categoryId.hashCode() : 0;
        result = 31 * result + (subCategoryId != null ? subCategoryId.hashCode() : 0);
        return result;
    }
}
