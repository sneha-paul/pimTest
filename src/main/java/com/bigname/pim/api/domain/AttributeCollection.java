package com.bigname.pim.api.domain;

import com.bigname.core.domain.Entity;
import com.fasterxml.jackson.annotation.JsonIgnore;
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

    @Transient
    @JsonIgnore
    private List<Attribute> allAttributes = new ArrayList<>();

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

    public List<Attribute> getAllAttributes() {
        return allAttributes;
    }

    public void setAllAttributes(List<Attribute> allAttributes) {
        this.allAttributes = allAttributes;
    }

    @Override
    protected void setExternalId() {
        this.collectionId = getExternalId();
    }

    @Override
    public AttributeCollection merge(AttributeCollection collection) {
        for (String group : collection.getGroup()) {
            switch (group) {
                case "DETAILS":
                    this.setExternalId(collection.getExternalId());
                    this.setCollectionName(collection.getCollectionName());
                    this.setActive(collection.getActive());
                    break;
                case "ATTRIBUTES":
                    this.setAttributes(collection.getAttributes());
                    break;
            }
        }
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

    public Map<String, Object> diff(AttributeCollection attributeCollection, boolean... ignoreInternalId) {
        boolean _ignoreInternalId = ignoreInternalId != null && ignoreInternalId.length > 0 && ignoreInternalId[0];
        Map<String, Object> diff = new HashMap<>();
        if (!_ignoreInternalId && !this.getId().equals(attributeCollection.getId())) {
            diff.put("internalId", attributeCollection.getId());
        }
        if ( !this.getCollectionName().equals(attributeCollection.getCollectionName())) {
            diff.put("collectionName", attributeCollection.getCollectionName());
        }

        if ( !this.getAttributes().equals(attributeCollection.getAttributes())) {
            diff.put("attributes", attributeCollection.getAttributes());
        }

        return diff;
    }
}