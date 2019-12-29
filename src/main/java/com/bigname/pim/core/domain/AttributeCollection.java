package com.bigname.pim.core.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.m7.xtreme.common.util.StringUtil;
import com.m7.xtreme.xcore.domain.MongoEntity;
import com.m7.xtreme.xcore.exception.EntityNotFoundException;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.Indexed;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Manu V NarayanaPrasad (manu@blacwood.com)
 * @since 1.0
 */
public class AttributeCollection extends MongoEntity<AttributeCollection> {

    @Transient
    @NotEmpty(message = "Collection Id cannot be empty", groups = {CreateGroup.class, DetailsGroup.class})
    @NotBlank(message = "Collection Id cannot be blank", groups = {CreateGroup.class, DetailsGroup.class})
    private String collectionId;

    @Indexed(unique = true)
    @NotEmpty(message = "Collection Name cannot be empty", groups = {CreateGroup.class, DetailsGroup.class})
    @NotBlank(message = "Collection Name cannot be blank", groups = {CreateGroup.class, DetailsGroup.class})
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

    public AttributeCollection updateAttribute(Attribute attributeDTO) {
        getAttribute(getAttributeFullId(attributeDTO))
                .map(attribute -> attribute.merge(attributeDTO))
                .orElseThrow(() -> new EntityNotFoundException("Unable to find Attribute with Id: " + attributeDTO.getFullId()));
        return this;
    }

    public AttributeCollection addAttributeOption(AttributeOption attributeOptionDTO) {
        attributeOptionDTO.setAttributeId(getAttributeFullId(attributeOptionDTO)); // If the attributeId is not fullId, this method will convert it to fullId
        getAttribute(attributeOptionDTO.getAttributeId())
                .map(attribute -> {
                    attributeOptionDTO.setId(StringUtil.getUniqueName(attributeOptionDTO.getId(), new ArrayList<>(attribute.getOptions().keySet())));
                    attribute.getOptions().put(attributeOptionDTO.getId(), attributeOptionDTO);
                    String fullParentOptionId = attributeOptionDTO.getParentOptionFullId();
                    if(isNotEmpty(fullParentOptionId)) {
                        String simpleParentOptionId = fullParentOptionId.substring(fullParentOptionId.lastIndexOf("|") + 1);
                        if(!attribute.getParentBasedOptions().containsKey(simpleParentOptionId)) {
                            attribute.getParentBasedOptions().put(simpleParentOptionId, new ArrayList<>());
                        }
                        attribute.getParentBasedOptions().get(simpleParentOptionId).add(attributeOptionDTO.getId());
                    }
                    return attribute;
                });
        return this;
    }

    public AttributeCollection updateAttributeOption(AttributeOption attributeOptionDTO) {
        String attributeFullId = getAttributeFullId(attributeOptionDTO);
        attributeOptionDTO.setAttributeId(attributeFullId); // If the attributeId is not fullId, this method will convert it to fullId
        String attributeOptionFullId = getPipedValue(attributeFullId, attributeOptionDTO.getId());
        getAttributeOption(attributeOptionFullId)
                .map(attributeOption -> {
                    //If parent option is modified, update the parentBasedOptions map in the corresponding attribute
                    String existingFullParentOptionId = attributeOption.getParentOptionFullId();
                    String newFullParentOptionId = attributeOptionDTO.getParentOptionFullId();
                    if(existingFullParentOptionId != null && newFullParentOptionId != null) {
                        if (!existingFullParentOptionId.equals(newFullParentOptionId)) {   // Parent option modified

                            //Get the attribute
                            getAttribute(attributeFullId)
                                    .map(attribute -> {
                                        String existingSimpleParentOptionId = existingFullParentOptionId.substring(existingFullParentOptionId.lastIndexOf("|") + 1);
                                        String newSimpleParentOptionId = newFullParentOptionId.substring(newFullParentOptionId.lastIndexOf("|") + 1);

                                        //If the parentBaseOptions have no entry for the newParentOptionId, create an empty entry
                                        if (!attribute.getParentBasedOptions().containsKey(newSimpleParentOptionId)) {
                                            attribute.getParentBasedOptions().put(newSimpleParentOptionId, new ArrayList<>());
                                        }
                                        //Swap the optionId in the parentBasedOptions map, from existingParentId to newParentId
                                        attribute.getParentBasedOptions().get(newSimpleParentOptionId).add(attributeOptionDTO.getId());
                                        //Remove the optionId from
                                        attribute.getParentBasedOptions().get(existingSimpleParentOptionId).remove(attributeOptionDTO.getId());
                                        return attribute;
                                    });
                        }
                    }
                    attributeOption.merge(attributeOptionDTO);

                    return attributeOption;
                }).orElseThrow(() -> new EntityNotFoundException("Unable to find Attribute Option with Id: " + attributeOptionDTO.getFullId()));
        return this;
    }

    public String getAttributeFullId(Attribute attribute) {
        //FullId chains must contain at least 2 id nodes(GROUP_ID|ATTRIBUTE_ID. If it contains only 1 node, then find the fullId
        List<String> idChain = getPipedValues(attribute.getFullId());
        return idChain.size() < 1 ? null : idChain.size() > 1 ? attribute.getFullId() :
                getAllAttributes().stream()
                    .filter(attribute1 -> attribute1.getId().equals(idChain.get(0))).findFirst()
                    .map(Attribute::getFullId)
                    .orElseThrow(() -> new EntityNotFoundException("Unable to find Attribute with Id: " + attribute.getFullId()));
    }

    public String getAttributeOptionFullId(AttributeOption attributeOption) {
        //FullId chains must contain at least 3 id nodes(GROUP_ID|ATTRIBUTE_ID|OPTION_ID. If it contains only 1 node, then find the fullId
        List<String> idChain = getPipedValues(attributeOption.getFullId());
        return idChain.size() < 2 ? null : idChain.size() > 2 ? attributeOption.getFullId() :
                getAllAttributes().stream()
                    .filter(attribute -> attribute.getId().equals(idChain.get(0))).findFirst()
                    .map(attribute -> attribute.getFullId() + "|" + idChain.get(1))
                    .orElseThrow(() -> new EntityNotFoundException("Unable to find Attribute with Id: " + attributeOption.getFullId()));
    }

    public String getAttributeFullId(AttributeOption attributeOption) {
        String attributeOptionFullId = getAttributeOptionFullId(attributeOption);
        return attributeOptionFullId == null ? null : attributeOptionFullId.substring(0, attributeOptionFullId.lastIndexOf("|"));
    }

    public Optional<Attribute> getAttribute(String attributeFullId) {
        Attribute attribute = null;
        // attributeFullId is required
        if(isNotEmpty(attributeFullId)) {

            //All grouped attributes for the collection
            Map<String, AttributeGroup> attributeGroups = getAttributes();

            //Split attributeFullId to individual ids
            List<String> ids = new ArrayList<>(getPipedValues(attributeFullId));

            //Since attributeFullId is not empty, ids will at least have one element
            //Get the attributeId, which will be the last id in the ids list
            String attributeId = ids.remove(ids.size() - 1);

            //An attribute can't live outside a group. We can proceed further if there is at least one id left in the ids list
            if(!ids.isEmpty()) {
                //Top level attribute group
                AttributeGroup group = attributeGroups.get(ids.remove(0));
                //Go recursively to the innermost group
                while(!ids.isEmpty()) {
                    group = group.getChildGroups().get(ids.remove(0));
                }
                attribute = group.getAttributes().get(attributeId);
            }
        }
        return isNotNull(attribute) ? Optional.of(attribute) : Optional.empty();

    }



    public Optional<AttributeOption> getAttributeOption(String attributeOptionFullId) {
        AttributeOption attributeOption = null;
        // attributeOptionFullId is required
        if(isNotEmpty(attributeOptionFullId)) {

            //Split the attributeOptionFullId to individual ids
            List<String> ids = new ArrayList<>(getPipedValues(attributeOptionFullId));

            //Since attributeOptionFullId is not empty, ids will at least have one element
            //Get the attributeOptionId, which will be the last id in the ids list
            String attributeOptionId = ids.remove(ids.size() - 1);

            //An attribute can't live outside a group. We can proceed further if there is at least two ids left in the ids list, one for the group and one for the attribute
            if(ids.size() > 1) {
                // First build the attributeFullId from the remaining ids and get the attribute, and then get the option corresponding to the attributeOptionId from the attribute
                String attributeFullId = getPipedValue(ids.toArray(new String[0]));
                attributeOption = getAttribute(attributeFullId)
                                    .map(attribute -> attribute.getOptions().get(attributeOptionId))
                                    .orElseThrow(() -> new EntityNotFoundException("Unable to find Attribute with Id: " + attributeFullId));
            }
        }
        return isNotNull(attributeOption) ? Optional.of(attributeOption) : Optional.empty();
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
        map.put("archived", getArchived());
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

    public List<Attribute> getAvailableParentAttributes(Attribute forAttribute) {
        // An attribute can only have a maximum of one parent attribute.
        // Get all the existing parents and add to the excludedAttributeIds list.
        List<String> excludedAttributeIds = getAllAttributes().stream().filter(attribute -> isNotEmpty(attribute.getParentAttributeId())).map(Attribute::getParentAttributeId).collect(Collectors.toList());
        if(isNotEmpty(forAttribute.getId())) {
            excludedAttributeIds.add(forAttribute.getId());
        }
        // Only multi-select attributes can be used as parents
        return getAllAttributes().stream().filter(attribute -> "Y".equals(attribute.getUiType().isSelectable()) && !excludedAttributeIds.contains(attribute.getId())).sorted(Comparator.comparing(Attribute::getName)).collect(Collectors.toList());
    }

    /*@Override
    public Object getCopy(AttributeCollection attributeCollection) {
        AttributeCollection _attributeCollection = new AttributeCollection();
        _attributeCollection.setCollectionName(attributeCollection.getCollectionName());
        _attributeCollection.setCollectionId(attributeCollection.getCollectionId());
        _attributeCollection.setAttributes(attributeCollection.getAttributes());
        _attributeCollection.setActive(attributeCollection.getActive());
        _attributeCollection.setArchived(attributeCollection.getArchived());
        _attributeCollection.setDiscontinued(attributeCollection.getDiscontinued());
        _attributeCollection.setVersionId(attributeCollection.getVersionId());
        _attributeCollection.setId(attributeCollection.getId());
        return _attributeCollection;
    }*/
}