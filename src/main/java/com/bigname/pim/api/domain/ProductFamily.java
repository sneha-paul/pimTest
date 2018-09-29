package com.bigname.pim.api.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
    @NotEmpty(message = "Product Family Id cannot be empty", groups = {CreateGroup.class, DetailsGroup.class})
    String productFamilyId;

    @Indexed(unique = true)
    @NotEmpty(message = "Product Family Name cannot be empty", groups = {CreateGroup.class, DetailsGroup.class})
    private String productFamilyName;

    private Map<String, AttributeGroup> productFamilyAttributes = new LinkedHashMap<>();

    private Map<String, AttributeGroup> productVariantFamilyAttributes = new LinkedHashMap<>();

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

    public Map<String, AttributeGroup> getProductFamilyAttributes() {
        return productFamilyAttributes;
    }

    public void setProductFamilyAttributes(Map<String, AttributeGroup> productFamilyAttributes) {
        this.productFamilyAttributes = productFamilyAttributes;
    }

    public Map<String, AttributeGroup> getProductVariantFamilyAttributes() {
        return productVariantFamilyAttributes;
    }

    public void setProductVariantFamilyAttributes(Map<String, AttributeGroup> productVariantFamilyAttributes) {
        this.productVariantFamilyAttributes = productVariantFamilyAttributes;
    }

    public Attribute setAttributeGroup(Attribute attribute) {
        attribute.setAttributeGroup(getAttributeGroup(attribute));
        return attribute;
    }

    public AttributeGroup getAttributeGroup(Attribute attribute) {
        String type = attribute.getType();
        String groupName = attribute.getAttributeGroupName();
        String groupId = attribute.getAttributeGroupId();

        AttributeGroup attributeGroup = type.equals("VARIANT") ? getProductVariantFamilyAttributes().get(groupId) : getProductFamilyAttributes().get(groupId);

        if(isEmpty(attributeGroup)) {
            if(isNotEmpty(groupName)) {
                attributeGroup = new AttributeGroup(groupName);
            } else {
                attributeGroup = AttributeGroup.getDefaultGroup();
            }
        }
        return attributeGroup;
    }

    public ProductFamily addAttribute(Attribute attribute) {
        setAttributeGroup(attribute);
        AttributeGroup attributeGroup = attribute.getAttributeGroup();
        Map<String, AttributeGroup> familyAttributes = attribute.getType().equals("VARIANT") ? getProductVariantFamilyAttributes() : getProductFamilyAttributes();
        if(familyAttributes.containsKey(attributeGroup.getId())) {
            familyAttributes.get(attributeGroup.getId()).addAttributes(attribute);
        } else {
            familyAttributes.put(attributeGroup.getId(), attributeGroup);
        }
        return this;
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
