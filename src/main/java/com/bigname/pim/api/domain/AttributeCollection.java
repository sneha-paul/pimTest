package com.bigname.pim.api.domain;

import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.Indexed;

import javax.validation.constraints.NotEmpty;
import java.util.*;

/**
 * @author Manu V NarayanaPrasad (manu@blacwood.com)
 * @since 1.0
 */
public class AttributeCollection extends Entity<AttributeCollection> {

    @Transient
    @NotEmpty(message = "Collection Id cannot be empty", groups = {CreateGroup.class, DetailsGroup.class})
    private String collectionId;

    @Indexed(unique = true)
    @NotEmpty(message = "Collection Name cannot be empty", groups = {CreateGroup.class, DetailsGroup.class})
    private String collectionName;

    private Map<String, AttributeGroup> attributes = new LinkedHashMap<>();

    public AttributeCollection() {
        super();
    }

    public String getCollectionId() {
        return getExternalId();
    }

    public void setCollectionId(String collectionId) {
        this.collectionId = collectionId;
        setExternalId(collectionId);
    }

    public String getCollectionName() {
        return collectionName;
    }

    public void setCollectionName(String collectionName) {
        this.collectionName = collectionName;
    }

    public Map<String, AttributeGroup> getAttributes() {
        return attributes;
    }

    public Map<String, AttributeGroup> getMappedAttributes() {
        return AttributeGroup.map(attributes);
    }

    public void setAttributes(Map<String, AttributeGroup> attributes) {
        this.attributes = attributes;
    }

    public AttributeCollection addAttribute(Attribute attributeDTO) {
        Map<String, AttributeGroup> attributeGroups = getAttributes();
        Attribute.buildInstance(attributeDTO, attributeGroups);
        return this;
    }

    public AttributeCollection addAttributeOption(AttributeOption attributeOptionDTO) {
        String attributeFullId = attributeOptionDTO.getAttributeId();
        AttributeGroup.getAttributeGroup(attributeFullId.substring(0, attributeFullId.lastIndexOf("|")), getMappedAttributes())
                .getAttributes()
                .get(attributeFullId.substring(attributeFullId.lastIndexOf("|") + 1)).getOptions().put(attributeOptionDTO.getId(), attributeOptionDTO);
        return this;
    }

    @Override
    void setExternalId() {
        this.collectionId = getExternalId();
    }

    @Override
    public AttributeCollection merge(AttributeCollection collection) {
        this.setExternalId(collection.getExternalId());
        this.setCollectionName(collection.getCollectionName());
        this.setActive(collection.getActive());
        this.setAttributes(collection.getAttributes());
        return this;
    }

    @Override
    public Map<String, String> toMap() {
        Map<String, String> map = new LinkedHashMap<>();
        map.put("externalId", getExternalId());
        map.put("collectionName", getCollectionName());
        map.put("active", getActive());
        return map;
    }

    /*public AttributeGroup getMasterGroup(String groupId) {
        Map<String, AttributeGroup> attributeGroupsMap = getAttributes();

        List<AttributeGroup> list = attributeGroupsMap.entrySet().stream()
                .filter(e -> e.getValue().getMasterGroup().equals("Y") &&
                        e.getKey().equals(groupId)).map(Map.Entry::getValue).collect(Collectors.toList());
        return isNotEmpty(list) ? list.get(0) : null;
    }*/
}
