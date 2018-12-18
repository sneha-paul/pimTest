package com.bigname.pim.api.domain;

import org.springframework.data.mongodb.core.index.Indexed;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by sruthi on 08-11-2018.
 */
public class ProductCategory extends EntityAssociation<Product, Category> {

    /**
     * Internal id of the Product document, not the externalId (productId) attribute
     */
    @Indexed
    private String productId;

    /**
     * Internal id of the Category document, not the externalId (categoryId) attribute
     */
    @Indexed
    private String productCategoryId;

    public ProductCategory(){ super();}

    public ProductCategory(String productId, String categoryId, int subSequenceNum) {
        super();
        this.productId = productId;
        this.productCategoryId = categoryId;
        setSubSequenceNum(subSequenceNum);
        setActive("Y");
    }

    public ProductCategory init(Product product, Category category) {
        super.init(product, category);
        return this;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getProductCategoryId() {
        return productCategoryId;
    }

    public void setProductCategoryId(String categoryId) {
        this.productCategoryId = categoryId;
    }

    @Override
    public Map<String, Object> toMap(Map<String, Object> attributesMap) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("externalId", attributesMap.get("externalId"));
        map.put("categoryName", attributesMap.get("categoryName"));
        map.put("active", attributesMap.get("active"));
        map.put("sequenceNum", attributesMap.get("sequenceNum"));
        return map;
    }
}
