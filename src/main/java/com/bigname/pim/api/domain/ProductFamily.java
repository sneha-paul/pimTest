package com.bigname.pim.api.domain;

import com.bigname.common.util.StringUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.Indexed;

import javax.validation.constraints.NotEmpty;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    private Map<String, AttributeGroup> attributes = new LinkedHashMap<>();

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

    public Map<String, AttributeGroup> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, AttributeGroup> attributes) {
        this.attributes = attributes;
    }

    public ProductFamily addAttribute(Attribute attributeDTO) {
        Map<String, AttributeGroup> familyAttributeGroups = getAttributes();
        Attribute attribute = new Attribute(attributeDTO, familyAttributeGroups);
        boolean added = AttributeGroup.addAttribute(attribute, familyAttributeGroups);
        if(!added) { /*Adding the attribute failed */ }
        return this;
    }

    public ProductFamily addAttributeOption(AttributeOption attributeOptionDTO) {
        String attributeId = attributeOptionDTO.getAttributeId();
        AttributeGroup.getLeafGroup(attributeId.substring(0, attributeId.lastIndexOf("|")), getAttributes())
                .getAttributes()
                .get(attributeId.substring(attributeId.lastIndexOf("|") + 1)).getOptions().put(attributeOptionDTO.getId(), attributeOptionDTO);
        return this;
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
        this.setAttributes(productFamily.getAttributes());
        return this;
    }

    @Override
    public ProductFamily cloneInstance() {
        ProductFamily clone = new ProductFamily();
        clone.setActive("N");
        clone.setExternalId(cloneValue(getExternalId()));
        clone.setProductFamilyName(cloneValue(getProductFamilyName()));
       // clone.setProductFamilyAttributes(cloneValue(getProductFamilyAttributes()));
        return clone;
    }

    @Override
    public Map<String, String> toMap() {
        Map<String, String> map = new LinkedHashMap<>();
        map.put("externalId", getExternalId());
        map.put("productFamilyName", getProductFamilyName());
        map.put("active", getActive());
        return map;
    }

    public List<AttributeGroup> getAddonMasterGroups() {
        Map<String, AttributeGroup> attributeGroupsMap = getAttributes();
        return attributeGroupsMap.entrySet().stream()
                .filter(e -> e.getValue().getMasterGroup().equals("Y") &&
                !e.getKey().equals(AttributeGroup.DETAILS_GROUP_ID) &&
                !e.getKey().equals(AttributeGroup.FEATURES_GROUP_ID)).map(Map.Entry::getValue).collect(Collectors.toList());
    }

    public AttributeGroup getDetailsMasterGroup() {
        return getMasterGroup(AttributeGroup.DETAILS_GROUP_ID);
    }

    public AttributeGroup getFeaturesMasterGroup() {
        return getMasterGroup(AttributeGroup.FEATURES_GROUP_ID);
    }

    public AttributeGroup getMasterGroup(String groupId) {
        Map<String, AttributeGroup> attributeGroupsMap = getAttributes();

        List<AttributeGroup> list = attributeGroupsMap.entrySet().stream()
                .filter(e -> e.getValue().getMasterGroup().equals("Y") &&
                        e.getKey().equals(groupId)).map(Map.Entry::getValue).collect(Collectors.toList());
        return isNotEmpty(list) ? list.get(0) : null;
    }
}
