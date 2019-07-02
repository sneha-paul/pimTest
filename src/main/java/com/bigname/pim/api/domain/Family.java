package com.bigname.pim.api.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.m7.xtreme.xcore.domain.MongoEntity;
import com.m7.xtreme.xcore.exception.EntityNotFoundException;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.Indexed;

import javax.validation.constraints.NotEmpty;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by manu on 9/1/18.
 */
public class Family extends MongoEntity<Family> {

    @Transient
    @NotEmpty(message = "Family Id cannot be empty", groups = {CreateGroup.class, DetailsGroup.class})
    String familyId;

    @Indexed(unique = true)
    @NotEmpty(message = "Family Name cannot be empty", groups = {CreateGroup.class, DetailsGroup.class})
    private String familyName;

    private Map<String, FamilyAttributeGroup> attributes = new LinkedHashMap<>();

    private Map<String, VariantGroup> variantGroups = new LinkedHashMap<>();

    private Map<String, String> channelVariantGroups = new HashMap<>();

    @Transient
    @JsonIgnore
    private Map<String, FamilyAttribute> attributesMap = new HashMap<>();

    public Family() {
        super();
    }

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
        if (!added) { /*Adding the attribute failed */ }
        return this;
    }

    public Family updateAttribute(FamilyAttribute attributeDTO) {
        getAttribute(getAttributeFullId(attributeDTO))
                .map(attribute -> attribute.merge(attributeDTO))
                .orElseThrow(() -> new EntityNotFoundException("Unable to find Attribute with Id: " + attributeDTO.getFullId()));
        return this;
    }

    public Family addAttributeOption(FamilyAttributeOption familyAttributeOptionDTO, AttributeOption attributeOption) {
        FamilyAttributeOption familyAttributeOption = new FamilyAttributeOption(familyAttributeOptionDTO, attributeOption);
        String attributeId = getAttributeFullId(familyAttributeOption.getFamilyAttributeId());
        FamilyAttributeGroup.getLeafGroup(attributeId.substring(0, attributeId.lastIndexOf("|")), getAttributes())
                .getAttributes()
                .get(attributeId.substring(attributeId.lastIndexOf("|") + 1)).getOptions().put(familyAttributeOption.getId(), familyAttributeOption);
        return this;
    }

    public String getAttributeFullId(FamilyAttribute attribute) {
        return getAttributeFullId(attribute.getFullId());
    }

    public String getAttributeFullId(String attributeId) {
        //FullId chains must contain at least 4 id nodes(MASTER_GROUP|LEVEL_2|GROUP_ID|ATTRIBUTE_ID. If it contains less than 4 nodes, then find the fullId
        List<String> idChain = getPipedValues(attributeId);
        return idChain.size() < 1 ? null : idChain.size() > 3 ? attributeId :
                getAllAttributes().stream()
                        .filter(attribute1 -> attribute1.getId().equals(idChain.get(0))).findFirst()
                        .map(FamilyAttribute::getFullId)
                        .orElseThrow(() -> new EntityNotFoundException("Unable to find Attribute with Id: " + attributeId));
    }

    public String getAttributeOptionFullId(FamilyAttributeOption attributeOption) {
        //FullId chains must contain at least 5 id nodes(MASTER_GROUP|LEVEL_2|GROUP_ID|ATTRIBUTE_ID|OPTION_ID. If it contains less than 5 nodes, then find the fullId
        List<String> idChain = getPipedValues(attributeOption.getFullId());
        return idChain.size() < 2 ? null : idChain.size() > 4 ? attributeOption.getFullId() :
                getAllAttributes().stream()
                        .filter(attribute -> attribute.getId().equals(idChain.get(idChain.size() - 2))).findFirst()
                        .map(attribute -> attribute.getFullId() + "|" + idChain.get(idChain.size() - 1))
                        .orElseThrow(() -> new EntityNotFoundException("Unable to find Attribute with Id: " + attributeOption.getFullId()));
    }

    public String getAttributeFullId(FamilyAttributeOption attributeOption) {
        String attributeOptionFullId = getAttributeOptionFullId(attributeOption);
        return attributeOptionFullId == null ? null : attributeOptionFullId.substring(0, attributeOptionFullId.lastIndexOf("|"));
    }

    public Optional<FamilyAttribute> getAttribute(String attributeFullId) {
        FamilyAttribute attribute = null;
        // attributeFullId is required
        if(isNotEmpty(attributeFullId)) {

            //All grouped attributes for the collection
            Map<String, FamilyAttributeGroup> attributeGroups = getAttributes();

            //Split attributeFullId to individual ids
            List<String> ids = new ArrayList<>(getPipedValues(attributeFullId));

            //Since attributeFullId is not empty, ids will at least have one element
            //Get the attributeId, which will be the last id in the ids list
            String attributeId = ids.remove(ids.size() - 1);

            //An attribute can't live outside a group. We can proceed further if there is at least one id left in the ids list
            if(!ids.isEmpty()) {
                //Top level attribute group
                FamilyAttributeGroup group = attributeGroups.get(ids.remove(0));
                //Go recursively to the innermost group
                while(!ids.isEmpty()) {
                    group = group.getChildGroups().get(ids.remove(0));
                }
                attribute = group.getAttributes().get(attributeId);
            }
        }
        return isNotNull(attribute) ? Optional.of(attribute) : Optional.empty();

    }

    public Map<String, VariantGroup> getVariantGroups() {
        return variantGroups;
    }

    public void setVariantGroups(Map<String, VariantGroup> variantGroups) {
        this.variantGroups = variantGroups;
    }

    public Map<String, String> getChannelVariantGroups() {
        return channelVariantGroups;
    }

    public void setChannelVariantGroups(Map<String, String> channelVariantGroups) {
        this.channelVariantGroups = channelVariantGroups;
    }

    public List<FamilyAttribute> getAvailableParentAttributes(FamilyAttribute forAttribute) {
        // An attribute can only have a maximum of one parent attribute.
        // Get all the existing parents and add to the excludedAttributeIds list.
        List<String> excludedAttributeIds = getAllAttributes().stream().filter(attribute -> isNotEmpty(attribute.getParentAttributeId())).map(FamilyAttribute::getParentAttributeId).collect(Collectors.toList());
        if(isNotEmpty(forAttribute.getId())) {
            excludedAttributeIds.add(forAttribute.getId());
        }
        // Only multi-select attributes can be used as parents
        return getAllAttributes().stream().filter(attribute -> "Y".equals(attribute.getUiType().isSelectable()) && !excludedAttributeIds.contains(attribute.getId())).sorted(Comparator.comparing(FamilyAttribute::getName)).collect(Collectors.toList());
    }

    //TODO - enable this after implementing the variant group lock functionality
    /*public void setChannelVariantGroup(String channelId, String variantGroupId) {
        if(!getChannelVariantGroups().containsKey(channelId)) {
            this.channelVariantGroups.put(channelId, variantGroupId);
        }
    }*/

    public Map<String, FamilyAttribute> getAllAttributesMap() {
        if (isNull(attributesMap) || attributesMap.isEmpty()) {
            attributesMap = FamilyAttributeGroup.getAllAttributesMap(this);
        }
        return attributesMap;
    }

    public Map<String, FamilyAttribute> getAllAttributesMap(boolean cached) {
        if (cached) {
            return getAllAttributesMap();
        } else {
            attributesMap = FamilyAttributeGroup.getAllAttributesMap(this);
            return attributesMap;
        }
    }

    public Collection<FamilyAttribute> getAllAttributes() {
        return getAllAttributesMap().values();
    }

    @Override
    protected void setExternalId() {
        this.familyId = getExternalId();
    }

    @Override
    public Family merge(Family family) {
        for (String group : family.getGroup()) {
            switch (group) {
                case "DETAILS":
                    this.setExternalId(family.getExternalId());
                    this.setFamilyName(family.getFamilyName());
                    this.setActive(family.getActive());
                    break;
                case "ATTRIBUTES":
                    this.setAttributes(family.getAttributes());
                    break;
                case "VARIANT_GROUPS":
                    this.setVariantGroups(family.getVariantGroups());
                    this.setChannelVariantGroups(family.getChannelVariantGroups());
                    break;

            }
        }
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
        if (getVariantGroups().containsKey(variantGroup.getId())) {
            return false;
        } else {
            getVariantGroups().put(variantGroup.getId(), variantGroup);
            return false;
        }
    }

    public Map<String, List<FamilyAttribute>> getVariantGroupAttributes(String variantGroupId) {
        VariantGroup variantGroup = getVariantGroups().get(variantGroupId);
        Map<String, FamilyAttribute> familyAttributes = FamilyAttributeGroup.getAllAttributesMap(variantGroup.getFamily());
        Map<String, List<FamilyAttribute>> variantGroupAttributes = new LinkedHashMap<>();
        List<FamilyAttribute> productAttributes = new ArrayList<>(familyAttributes.values());

        for (Map.Entry<Integer, List<String>> entry : variantGroup.getVariantAxis().entrySet()) {
            variantGroupAttributes.put("AXIS_ATTRIBUTES_L" + entry.getKey(), entry.getValue().stream().map(familyAttributes::get).collect(Collectors.toList()));
            productAttributes.removeAll(variantGroupAttributes.get("AXIS_ATTRIBUTES_L" + entry.getKey()));
        }

        for (Map.Entry<Integer, List<String>> entry : variantGroup.getVariantAttributes().entrySet()) {
            variantGroupAttributes.put("VARIANT_ATTRIBUTES_L" + entry.getKey(), entry.getValue().stream().map(familyAttributes::get).collect(Collectors.toList()));
            productAttributes.removeAll(variantGroupAttributes.get("VARIANT_ATTRIBUTES_L" + entry.getKey()));
        }

        variantGroupAttributes.put("PRODUCT_ATTRIBUTES", productAttributes);

        return variantGroupAttributes;
    }

    public Map<String, List<FamilyAttribute>> getVariantGroupAxisAttributes(String variantGroupId) {
        VariantGroup variantGroup = getVariantGroups().get(variantGroupId);
        Map<String, FamilyAttribute> familyAttributes = FamilyAttributeGroup.getAllAttributesMap(variantGroup.getFamily());
        List<FamilyAttribute> availableAxisAttributes = new ArrayList<>();
        familyAttributes.values().stream()
                .filter(attribute -> attribute.getActive().equals("Y") && attribute.getSelectable().equals("Y")/* && attribute.getScopable().equals("N")*/)
                .forEach(availableAxisAttributes::add);
        Map<String, List<FamilyAttribute>> variantGroupAxisAttributes = new LinkedHashMap<>();

        for (Map.Entry<Integer, List<String>> entry : variantGroup.getVariantAxis().entrySet()) {
            variantGroupAxisAttributes.put("AXIS_ATTRIBUTES_L" + entry.getKey(), entry.getValue().stream().map(familyAttributes::get).collect(Collectors.toList()));
            availableAxisAttributes.removeAll(variantGroupAxisAttributes.get("AXIS_ATTRIBUTES_L" + entry.getKey()));
        }

        variantGroupAxisAttributes.put("AVAILABLE_AXIS_ATTRIBUTES", availableAxisAttributes);

        return variantGroupAxisAttributes;
    }

    public Family updateVariantGroupAttributes(String variantGroupId, String[] variantLevel1AttributeIds, String[] variantLevel2AttributeIds) {
        VariantGroup variantGroup = getVariantGroups().get(variantGroupId);
        String[][] variantAttributeIds = new String[variantGroup.getLevel()][];
        variantAttributeIds[0] = variantLevel1AttributeIds;
        if (variantAttributeIds.length > 1) {
            variantAttributeIds[1] = variantLevel2AttributeIds;
        }
        int level = 0;
        for (String[] variantLevelAttributeIds : variantAttributeIds) {
            level++;
            if (!variantGroup.getVariantAttributes().containsKey(level)) {
                variantGroup.getVariantAttributes().put(level, new ArrayList<>());
            }
            List<String> variantAttributes = variantGroup.getVariantAttributes().get(level);
            if (isNotEmpty(variantGroup)) {
                variantAttributes.clear();
                for (String attributeId : variantLevelAttributeIds) {
                    if (!variantGroup.getVariantAxis().get(level).contains(attributeId)) {
                        variantAttributes.add(attributeId);
                    }
                }
            }
        }
        return this;
    }

    public Family updateVariantGroupAxisAttributes(String variantGroupId, String[] axisLevel1AttributeIds, String[] axisLevel2AttributeIds) {
        VariantGroup variantGroup = getVariantGroups().get(variantGroupId);
        String[][] axesAttributeIds = new String[variantGroup.getLevel()][];
        if (isNull(axisLevel1AttributeIds)) {
            axisLevel1AttributeIds = new String[0];
        }
        axesAttributeIds[0] = axisLevel1AttributeIds;
        if (axesAttributeIds.length > 1) {
            if (isNull(axisLevel2AttributeIds)) {
                axisLevel2AttributeIds = new String[0];
            }
            axesAttributeIds[1] = axisLevel2AttributeIds;
        }
        Map<String, FamilyAttribute> familyAxisAttributes = new HashMap<>();
        FamilyAttributeGroup.getAllAttributes(this).stream()
                .filter(attribute -> attribute.getActive().equals("Y") && attribute.getSelectable().equals("Y") /*&& attribute.getScopable().equals("N")*/)
                .forEach(attribute -> familyAxisAttributes.put(attribute.getId(), attribute));
        int level = 0;
        for (String[] axisAttributeIds : axesAttributeIds) {
            level++;
            if (isNotNull(axisAttributeIds)) {
                if (!variantGroup.getVariantAxis().containsKey(level)) {
                    variantGroup.getVariantAxis().put(level, new ArrayList<>());
                }
                List<String> axisAttributes = variantGroup.getVariantAxis().get(level);
                if (isNotEmpty(variantGroup)) {
                    axisAttributes.clear();
                    axisAttributes.addAll(Arrays.asList(axisAttributeIds));
                }
            }
        }
        return this;
    }

    public Map<String, Object> diff(Family family, boolean... ignoreInternalId) {
        boolean _ignoreInternalId = ignoreInternalId != null && ignoreInternalId.length > 0 && ignoreInternalId[0];
        Map<String, Object> diff = new HashMap<>();
        if (!_ignoreInternalId && !this.getId().equals(family.getId())) {
            diff.put("internalId", family.getId());
        }
        if ( !this.getFamilyName().equals(family.getFamilyName())) {
            diff.put("familyName", family.getFamilyName());
        }
        if ( !this.getAttributes().equals(family.getAttributes())) {
            diff.put("attributes", family.getAttributes());
        }
        if ( !this.getVariantGroups().equals(family.getVariantGroups())) {
            diff.put("variantGroups", family.getVariantGroups());
        }
        if ( !this.getChannelVariantGroups().equals(family.getChannelVariantGroups())) {
            diff.put("channelvariantGroups", family.getChannelVariantGroups());
        }
        return diff;
    }
}