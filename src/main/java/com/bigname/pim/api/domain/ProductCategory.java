package com.bigname.pim.api.domain;

import org.springframework.data.mongodb.core.index.Indexed;

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
    private String categoryId;

    public ProductCategory(){ super();}

    public ProductCategory(String productId, String categoryId, int subSequenceNum) {
        super();
        this.productId = productId;
        this.categoryId = categoryId;
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

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    @Override
    public Map<String, String> toMap() {
        return null;
    }
}
