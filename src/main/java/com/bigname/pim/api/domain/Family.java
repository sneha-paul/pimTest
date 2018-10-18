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

    private Map<String, VariantGroup> variantGroups = new LinkedHashMap<>();

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

    public Map<String, VariantGroup> getVariantGroups() {
        return variantGroups;
    }

    public void setVariantGroups(Map<String, VariantGroup> variantGroups) {
        this.variantGroups = variantGroups;
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
        this.setVariantGroups(family.getVariantGroups());
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

    public boolean addVariantGroup(VariantGroup variantGroup) {
        if(getVariantGroups().containsKey(variantGroup.getId())) {
            return false;
        } else {
            getVariantGroups().put(variantGroup.getId(), variantGroup);
            return false;
        }
    }

    public Map<String, List<FamilyAttribute>> getVariantGroupAttributes(String variantGroupId) {
        VariantGroup variantGroup = getVariantGroups().get(variantGroupId);
        List<FamilyAttribute> familyAttributes = FamilyAttributeGroup.getAllAttributes(getAttributes());
        Map<String, List<FamilyAttribute>> variantGroupAttributes = new LinkedHashMap<>();
        List<FamilyAttribute> productAttributes = new ArrayList<>(familyAttributes);

        for (Map.Entry<Integer, List<FamilyAttribute>> entry : variantGroup.getVariantAxis().entrySet()) {
            variantGroupAttributes.put("AXIS_ATTRIBUTES_L" + entry.getKey(), entry.getValue());
            productAttributes.removeAll(entry.getValue());
        }

        for (Map.Entry<Integer, List<FamilyAttribute>> entry : variantGroup.getVariantAttributes().entrySet()) {
            variantGroupAttributes.put("VARIANT_ATTRIBUTES_L" + entry.getKey(), entry.getValue());
            productAttributes.removeAll(entry.getValue());
        }

        variantGroupAttributes.put("PRODUCT_ATTRIBUTES", productAttributes);

        return variantGroupAttributes;
    }

    public Map<String, List<FamilyAttribute>> getVariantGroupAxisAttributes(String variantGroupId) {
        VariantGroup variantGroup = getVariantGroups().get(variantGroupId);
        List<FamilyAttribute> availableAxisAttributes = new ArrayList<>();
        FamilyAttributeGroup.getAllAttributes(getAttributes()).stream()
                .filter(attribute -> attribute.getActive().equals("Y") && attribute.getSelectable().equals("Y") && attribute.getScopable().equals("N"))
                .forEach(availableAxisAttributes::add);
        Map<String, List<FamilyAttribute>> variantGroupAxisAttributes = new LinkedHashMap<>();

        for (Map.Entry<Integer, List<FamilyAttribute>> entry : variantGroup.getVariantAxis().entrySet()) {
            variantGroupAxisAttributes.put("AXIS_ATTRIBUTES_L" + entry.getKey(), entry.getValue());
            availableAxisAttributes.removeAll(entry.getValue());
        }

        variantGroupAxisAttributes.put("AVAILABLE_AXIS_ATTRIBUTES", availableAxisAttributes);

        return variantGroupAxisAttributes;
    }

    public Family updateVariantGroupAttributes(String variantGroupId, String[] variantLevel1AttributeIds, String[] variantLevel2AttributeIds) {
        VariantGroup variantGroup = getVariantGroups().get(variantGroupId);
        String[][] variantAttributeIds = new String[variantGroup.getLevel()][];
        variantAttributeIds[0] = variantLevel1AttributeIds;
        if(variantAttributeIds.length > 1) {
            variantAttributeIds[1] = variantLevel2AttributeIds;
        }
        Map<String, FamilyAttribute> familyAttributes = new HashMap<>();
        FamilyAttributeGroup.getAllAttributes(getAttributes()).forEach(attribute -> familyAttributes.put(attribute.getId(), attribute));
        int level = 0;
        for(String[] variantLevelAttributeIds : variantAttributeIds) {
            level ++;
            if(!variantGroup.getVariantAttributes().containsKey(level)) {
                variantGroup.getVariantAttributes().put(level, new ArrayList<>());
            }
            List<FamilyAttribute> variantAttributes = variantGroup.getVariantAttributes().get(level);
            if (isNotEmpty(variantGroup)) {
                variantAttributes.clear();
                for (String attributeId : variantLevelAttributeIds) {
                    if (!variantGroup.getVariantAxis().get(level).contains(familyAttributes.get(attributeId))) {
                        variantAttributes.add(familyAttributes.get(attributeId));
                    }
                }
            }
        }
        return this;
    }

    public Family updateVariantGroupAxisAttributes(String variantGroupId, String[] axisLevel1AttributeIds, String[] axisLevel2AttributeIds) {
        VariantGroup variantGroup = getVariantGroups().get(variantGroupId);
        String[][] axesAttributeIds = new String[variantGroup.getLevel()][];
        if(isNull(axisLevel1AttributeIds)) {
            axisLevel1AttributeIds = new String[0];
        }
        axesAttributeIds[0] = axisLevel1AttributeIds;
        if(axesAttributeIds.length > 1) {
            if(isNull(axisLevel2AttributeIds)) {
                axisLevel2AttributeIds = new String[0];
            }
            axesAttributeIds[1] = axisLevel2AttributeIds;
        }
        Map<String, FamilyAttribute> familyAxisAttributes = new HashMap<>();
        FamilyAttributeGroup.getAllAttributes(getAttributes()).stream()
                .filter(attribute -> attribute.getActive().equals("Y") && attribute.getSelectable().equals("Y") && attribute.getScopable().equals("N"))
                .forEach(attribute -> familyAxisAttributes.put(attribute.getId(), attribute));
        int level = 0;
        for(String[] axisAttributeIds : axesAttributeIds) {
            level ++;
            if(isNotNull(axisAttributeIds)) {
                if (!variantGroup.getVariantAxis().containsKey(level)) {
                    variantGroup.getVariantAxis().put(level, new ArrayList<>());
                }
                List<FamilyAttribute> axisAttributes = variantGroup.getVariantAxis().get(level);
                if (isNotEmpty(variantGroup)) {
                    axisAttributes.clear();
                    for (String attributeId : axisAttributeIds) {
                        axisAttributes.add(familyAxisAttributes.get(attributeId));
                    }
                }
            }
        }
        return this;
    }
}
