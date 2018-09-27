package com.bigname.pim.api.domain;

import org.springframework.data.mongodb.core.index.Indexed;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by sruthi on 26-09-2018.
 */
public class CategoryProduct extends EntityAssociation<Category, Product> {

    /**
     * Internal id of the Website document, not the externalId (websiteId) attribute
     */
    @Indexed
    private String categoryId;

    /**
     * Internal id of the Catalog document, not the externalId (catalogId) attribute
     */
    @Indexed
    private String productId;

    public CategoryProduct() {
        super();
    }

    public CategoryProduct(String categoryId, String productId, int subSequenceNum) {
        super();
        this.categoryId = categoryId;
        this.productId = productId;
        setSubSequenceNum(subSequenceNum);
        setActive("Y");
    }

    public CategoryProduct init(Category category, Product product) {
        super.init(category, product);
        return this;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String catalogId) {
        this.productId = productId;
    }

    @Override
    public Map<String, String> toMap() {
        Map<String, String> map = new LinkedHashMap<>();
        map.put("productId", getChild().getProductId());
        map.put("productName", getChild().getProductName());
        map.put("active", getActive());
        map.put("sequenceNum", Long.toString(getSequenceNum()));
        map.put("subSequenceNum", Integer.toString(getSubSequenceNum()));
        return map;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CategoryProduct that = (CategoryProduct) o;

        if (categoryId != null ? !categoryId.equals(that.categoryId) : that.categoryId != null) return false;
        return productId != null ? productId.equals(that.productId) : that.productId == null;

    }

    @Override
    public int hashCode() {
        int result = categoryId != null ? categoryId.hashCode() : 0;
        result = 31 * result + (productId != null ? productId.hashCode() : 0);
        return result;
    }
}
