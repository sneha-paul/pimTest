package com.bigname.pim.api.domain;

import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.Indexed;

import javax.validation.constraints.NotEmpty;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by manu on 9/1/18.
 */
public class Family extends Entity<Family> {

    @Transient
    @NotEmpty(message = "Family Id cannot be empty", groups = {CreateGroup.class, DetailsGroup.class})
    String familyId;

    @Indexed(unique = true)
    @NotEmpty(message = "Family Name cannot be empty", groups = {CreateGroup.class, DetailsGroup.class})
    private String familyName;

    private Map<String, FamilyAttributeGroup> attributes = new LinkedHashMap<>();

    public Family() {
        super();
    }

    /*public Family(String externalId, String familyName) {
        super(externalId);
        this.familyName = familyName;
    }*/

    public String getFamilyId() {
        return getExternalId();
    }

    public void setFamilyId(String familyId) {
        this.familyId = familyId;
        setExternalId(familyId);
    }

    public String getFamilyName() {
        return familyName;
    }

    public void setFamilyName(String familyName) {
        this.familyName = familyName;
    }

    public Map<String, FamilyAttributeGroup> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, FamilyAttributeGroup> attributes) {
        this.attributes = attributes;
    }

    public Family addAttribute(FamilyAttribute attributeDTO) {
        Map<String, FamilyAttributeGroup> familyAttributeGroups = getAttributes();
        FamilyAttribute attribute = new FamilyAttribute(attributeDTO, familyAttributeGroups);
        boolean added = FamilyAttributeGroup.addAttribute(attribute, familyAttributeGroups);
        if(!added) { /*Adding the attribute failed */ }
        return this;
    }

    public Family addAttributeOption(FamilyAttributeOption familyAttributeOptionDTO, AttributeOption attributeOption) {
        FamilyAttributeOption familyAttributeOption = new FamilyAttributeOption(familyAttributeOptionDTO, attributeOption);
        String attributeId = familyAttributeOption.getFamilyAttributeId();
        FamilyAttributeGroup.getLeafGroup(attributeId.substring(0, attributeId.lastIndexOf("|")), getAttributes())
                .getAttributes()
                .get(attributeId.substring(attributeId.lastIndexOf("|") + 1)).getOptions().put(familyAttributeOption.getId(), familyAttributeOption);
        return this;
    }

    @Override
    void setExternalId() {
        this.familyId = getExternalId();
    }

    @Override
    public Family merge(Family family) {
        this.setExternalId(family.getExternalId());
        this.setFamilyName(family.getFamilyName());
        this.setActive(family.getActive());
        this.setAttributes(family.getAttributes());
        return this;
    }

    @Override
    public Family cloneInstance() {
        Family clone = new Family();
        clone.setActive("N");
        clone.setExternalId(cloneValue(getExternalId()));
        clone.setFamilyName(cloneValue(getFamilyName()));
       // clone.setFamilyAttributes(cloneValue(getFamilyAttributes()));
        return clone;
    }

    @Override
    public Map<String, String> toMap() {
        Map<String, String> map = new LinkedHashMap<>();
        map.put("externalId", getExternalId());
        map.put("familyName", getFamilyName());
        map.put("active", getActive());
        return map;
    }

    public List<FamilyAttributeGroup> getAddonMasterGroups() {
        Map<String, FamilyAttributeGroup> attributeGroupsMap = getAttributes();
        return attributeGroupsMap.entrySet().stream()
                .filter(e -> e.getValue().getMasterGroup().equals("Y") &&
                !e.getKey().equals(FamilyAttributeGroup.DETAILS_GROUP_ID) &&
                !e.getKey().equals(FamilyAttributeGroup.FEATURES_GROUP_ID)).map(Map.Entry::getValue).collect(Collectors.toList());
    }

    public FamilyAttributeGroup getDetailsMasterGroup() {
        return getMasterGroup(FamilyAttributeGroup.DETAILS_GROUP_ID);
    }

    public FamilyAttributeGroup getFeaturesMasterGroup() {
        return getMasterGroup(FamilyAttributeGroup.FEATURES_GROUP_ID);
    }

    public FamilyAttributeGroup getMasterGroup(String groupId) {
        Map<String, FamilyAttributeGroup> attributeGroupsMap = getAttributes();

        List<FamilyAttributeGroup> list = attributeGroupsMap.entrySet().stream()
                .filter(e -> e.getValue().getMasterGroup().equals("Y") &&
                        e.getKey().equals(groupId)).map(Map.Entry::getValue).collect(Collectors.toList());
        return isNotEmpty(list) ? list.get(0) : null;
    }
}
