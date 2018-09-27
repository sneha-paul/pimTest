package com.bigname.pim.api.domain;

import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotEmpty;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by sruthi on 20-09-2018.
 */
@Document
public class ProductVariant extends Entity<ProductVariant> {

    @Transient
    @NotEmpty(message = "ProductVariant Id cannot be empty", groups = {CreateGroup.class})
    String productVariantId;

    @Indexed(unique = true)
    @NotEmpty(message = "ProductVariant Name cannot be empty", groups = {CreateGroup.class})
    private String productVariantName;

    @NotEmpty(message = "Product Id cannot be empty", groups = {CreateGroup.class})
    private String productId;

    @Transient
    private Product product;

    private Map<String, Object> familyAttributes = new HashMap<>();

    public ProductVariant() {
        super();
    }

    public ProductVariant(Product product) {
        super();
        setProduct(product);
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

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
        if(product != null) {
            setProductId(product.getId());
        }
    }

    public Map<String, Object> getFamilyAttributes() {
        return familyAttributes;
    }

    public void setFamilyAttributes(Map<String, Object> familyAttributes) {
        this.familyAttributes = familyAttributes;
    }


    void setExternalId() {
        this.productVariantId = getExternalId();
    }

    @Override
    public ProductVariant merge(ProductVariant productVariant) {
        this.setExternalId(productVariant.getExternalId());
        this.setProductVariantName(productVariant.getProductVariantName());
        this.setActive(productVariant.getActive());
        this.setFamilyAttributes(productVariant.getFamilyAttributes());
        return this;
    }

    @Override
    public Map<String, String> toMap() {
        Map<String, String> map = new LinkedHashMap<>();
        map.put("externalId", getExternalId());
        map.put("productVariantName", getProductVariantName());
//        map.put("productFamilyId",getProductFamilyId());
        map.put("active", getActive());
        return map;
    }
}
