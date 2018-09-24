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

    private String productFamilyId;

    public ProductVariant() {
        super();
    }

    public ProductVariant(String externalId, String productVariantName, String productFamilyId) {
        super(externalId);
        this.productVariantName = productVariantName;
        this.productFamilyId = productFamilyId;
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

    public String getProductFamilyId() {
        return productFamilyId;
    }

    public void setProductFamilyId(String productFamilyId) {
        this.productFamilyId = productFamilyId;
    }

    void setExternalId() {
        this.productVariantId = getExternalId();
    }

    @Override
    public ProductVariant merge(ProductVariant productVariant) {
        this.setExternalId(productVariant.getExternalId());
        this.setProductVariantName(productVariant.getProductVariantName());
        this.setProductFamilyId(productVariant.getProductFamilyId());
        this.setActive(productVariant.getActive());
        return this;
    }

    @Override
    public Map<String, String> toMap() {
        Map<String, String> map = new LinkedHashMap<>();
        map.put("externalId", getExternalId());
        map.put("productVariantName", getProductVariantName());
        map.put("productFamilyId",getProductFamilyId());
        map.put("active", getActive());
        return map;
    }
}
