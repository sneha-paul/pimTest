package com.bigname.pim.api.domain;

import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Page;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotEmpty;
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

    private String description;

    public Product() {
        super();
    }

    public Product(String externalId, String productName) {
        super(externalId);
        this.productName = productName;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    void setExternalId() {
        this.productId = getExternalId();
    }

    @Override
    public Product merge(Product product) {
        this.setExternalId(product.getExternalId());
        this.setProductName(product.getProductName());
        this.setDescription(product.getDescription());
        this.setActive(product.getActive());
        return this;
    }

    @Override
    public Map<String, String> toMap() {
        Map<String, String> map = new LinkedHashMap<>();
        map.put("externalId", getExternalId());
        map.put("productName", getProductName());
        map.put("active", getActive());
        return map;
    }
}
