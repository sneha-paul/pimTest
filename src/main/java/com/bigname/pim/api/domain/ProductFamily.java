package com.bigname.pim.api.domain;

import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.Indexed;

import javax.validation.constraints.NotEmpty;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by manu on 9/1/18.
 */
public class ProductFamily extends Entity<ProductFamily> {

    @Transient
    @NotEmpty(message = "Product Family Id cannot be empty",groups = {CreateGroup.class, DetailsGroup.class})
    String productFamilyId;

    @Indexed(unique = true)
    @NotEmpty(message = "Product Family Name cannot be empty",groups = {CreateGroup.class, DetailsGroup.class})
    private String productFamilyName;

    private List<Attribute> productFamilyAttributes = new ArrayList<>();

    private List<Attribute> productVariantFamilyAttributes = new ArrayList<>();

    private List<Feature> productFamilyFeatures = new ArrayList<>();

    private List<Feature> productVariantFamilyFeatures = new ArrayList<>();

    public ProductFamily() {
        super();
    }

    public ProductFamily(String externalId, String productFamilyName) {
        super(externalId);
        this.productFamilyName = productFamilyName;
    }

    public String getProductFamilyId() {
        return getExternalId();
    }

    public void setProductFamilyId(String productFamilyId) {
        this.productFamilyId = productFamilyId;
        setExternalId(productFamilyId);
    }

    public String getProductFamilyName() {
        return productFamilyName;
    }

    public void setProductFamilyName(String productFamilyName) {
        this.productFamilyName = productFamilyName;
    }

    public List<Attribute> getProductFamilyAttributes() {
        return productFamilyAttributes;
    }

    public void setProductFamilyAttributes(List<Attribute> productFamilyAttributes) {
        this.productFamilyAttributes = productFamilyAttributes;
    }

    public List<Attribute> getProductVariantFamilyAttributes() {
        return productVariantFamilyAttributes;
    }

    public void setProductVariantFamilyAttributes(List<Attribute> productVariantFamilyAttributes) {
        this.productVariantFamilyAttributes = productVariantFamilyAttributes;
    }

    public List<Feature> getProductFamilyFeatures() {
        return productFamilyFeatures;
    }

    public void setProductFamilyFeatures(List<Feature> productFamilyFeatures) {
        this.productFamilyFeatures = productFamilyFeatures;
    }

    public List<Feature> getProductVariantFamilyFeatures() {
        return productVariantFamilyFeatures;
    }

    public void setProductVariantFamilyFeatures(List<Feature> productVariantFamilyFeatures) {
        this.productVariantFamilyFeatures = productVariantFamilyFeatures;
    }

    @Override
    void setExternalId() {
        this.productFamilyId = getExternalId();
    }

    @Override
    public ProductFamily merge(ProductFamily productFamily) {
        this.setExternalId(productFamily.getExternalId());
        this.setProductFamilyName(productFamily.getProductFamilyName());
        this.setActive(productFamily.getActive());
        this.setProductFamilyAttributes(productFamily.getProductFamilyAttributes());
        this.setProductVariantFamilyAttributes(productFamily.getProductVariantFamilyAttributes());
        this.setProductFamilyFeatures(productFamily.getProductFamilyFeatures());
        this.setProductVariantFamilyFeatures(productFamily.getProductVariantFamilyFeatures());
        return this;
    }

    @Override
    public Map<String, String> toMap() {
        Map<String, String> map = new LinkedHashMap<>();
        map.put("externalId", getExternalId());
        map.put("productFamilyName", getProductFamilyName());
        map.put("active", getActive());
        return map;
    }
}
