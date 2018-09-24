package com.bigname.pim.api.domain;

import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Page;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotEmpty;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by sruthi on 19-09-2018.
 */
@Document
public class Product extends Entity<Product> {


    @Transient
    @NotEmpty(message = "Product Id cannot be empty")
    String productId;

    @Indexed(unique = true)
    @NotEmpty(message = "Product Name cannot be empty")
    private String productName;

    private String productDescription;



    private String productFamilyId;

    @Transient
    private ProductFamily productFamily;

    private Map<String, Object> familyAttributes = new HashMap<>();

    public Product() {
        super();
    }

    public Product(String externalId, String productName, String productFamilyId) {
        super(externalId);
        this.productName = productName;
        this.productFamilyId = productFamilyId;

    }

    public String getProductId() {
        return getExternalId();
    }

    public void setProductId(String productId) {
        this.productId = productId;
        setExternalId(productId);
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductFamilyId() {
        return productFamilyId;
    }

    public void setProductFamilyId(String productFamilyId) {
        this.productFamilyId = productFamilyId;
    }

    public ProductFamily getProductFamily() {
        return productFamily;
    }

    public void setProductFamily(ProductFamily productFamily) {
        this.productFamily = productFamily;
    }

    public Map<String, Object> getFamilyAttributes() {
        return familyAttributes;
    }

    public void setFamilyAttributes(Map<String, Object> familyAttributes) {
        this.familyAttributes = familyAttributes;
    }

    void setExternalId() {
        this.productId = getExternalId();
    }

    @Override
    public Product merge(Product product) {
        this.setExternalId(product.getExternalId());
        this.setProductName(product.getProductName());
        this.setProductFamilyId(product.getProductFamilyId());
        this.setActive(product.getActive());
        this.setFamilyAttributes(product.getFamilyAttributes());
        return this;
    }

    @Override
    public Map<String, String> toMap() {
        Map<String, String> map = new LinkedHashMap<>();
        map.put("externalId", getExternalId());
        map.put("productName", getProductName());
        map.put("productFamilyId",getProductFamilyId());
        map.put("active", getActive());
        return map;
    }
}
