package com.bigname.pim.api.domain;

import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotEmpty;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by sruthi on 20-09-2018.
 */
@Document
public class ProductVariant extends Entity<ProductVariant> {

    @Transient
    @NotEmpty(message = "ProductVariant Id cannot be empty")
    String productVariantId;

    @Indexed(unique = true)
    @NotEmpty(message = "ProductVariant Name cannot be empty")
    private String productVariantName;

    private String description;

    public ProductVariant() {
        super();
    }

    public ProductVariant(String externalId, String productVariantName) {
        super(externalId);
        this.productVariantName = productVariantName;
    }

    public String getProductVariantId() {
        return getExternalId();
    }

    public void setProductVariantId(String productVariantId) {
        this.productVariantId = productVariantId;
        setExternalId(productVariantId);
    }

    public String getProductVariantName() {
        return productVariantName;
    }

    public void setProductVariantName(String productVariantName) {
        this.productVariantName = productVariantName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    void setExternalId() {
        this.productVariantId = getExternalId();
    }

    @Override
    public ProductVariant merge(ProductVariant productVariant) {
        this.setExternalId(productVariant.getExternalId());
        this.setProductVariantName(productVariant.getProductVariantName());
        this.setDescription(productVariant.getDescription());
        this.setActive(productVariant.getActive());
        return this;
    }

    @Override
    public Map<String, String> toMap() {
        Map<String, String> map = new LinkedHashMap<>();
        map.put("externalId", getExternalId());
        map.put("productVariantName", getProductVariantName());
        map.put("active", getActive());
        return map;
    }
}
