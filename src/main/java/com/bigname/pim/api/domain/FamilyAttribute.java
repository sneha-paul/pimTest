package com.bigname.pim.api.domain;

import com.bigname.common.util.StringUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.javatuples.Pair;
import org.springframework.data.annotation.Transient;

import javax.validation.constraints.NotEmpty;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Manu V NarayanaPrasad (manu@blacwood.com)
 * @since 1.0
 */
public class FamilyAttribute extends ValidatableEntity {

    @NotEmpty(message = "Attribute name cannot be empty")
    private String name;
    private String label;
    private Attribute.UIType _uiType = Attribute.UIType.INPUT_BOX;
    private String dataType = "string";  //Initial version only supports String type
    private String id;
    private String regEx;
    private String required = "N";
    private String selectable = "N";
    private String scopable = "N";
    private String active = "Y";
    private long sequenceNum;
    private int subSequenceNum;
    private String collectionId;
    private String attributeId;

    @Transient @JsonIgnore
    private Attribute attribute;

    @Transient
    @JsonIgnore
    private FamilyAttributeGroup attributeGroup;

    private Map<String, FamilyAttributeOption> options = new LinkedHashMap<>();

    public FamilyAttribute() {}

    public FamilyAttribute(FamilyAttribute attributeDTO, Map<String, FamilyAttributeGroup> familyGroups) {

        this(isNotEmpty(attributeDTO.getName()) ? attributeDTO.getName() : attributeDTO.getAttribute().getName(), isNotEmpty(attributeDTO.getLabel()) ? attributeDTO.getLabel() : attributeDTO.getAttribute().getLabel());
        this.attribute = attributeDTO.getAttribute();
        this.collectionId = attributeDTO.getCollectionId();
        this.attributeId = this.attribute.getFullId();
        this.setUiType(attributeDTO.getAttribute().getUiType());
        this.setRequired(attributeDTO.getRequired());
        this.setScopable(attributeDTO.getScopable());
        FamilyAttributeGroup attributeGroup = null;
        FamilyAttributeGroup attributeGroupDTO = attributeDTO.getAttributeGroup();
        this.setRequired(attributeDTO.getRequired());
        orchestrate();
        this.setId(StringUtil.getUniqueName(this.getId(), FamilyAttributeGroup.getAllAttributeIds(familyGroups)));
        if(isNotEmpty(attributeGroupDTO.getFullId()) && FamilyAttributeGroup.DEFAULT_GROUP_ID.equals(attributeGroupDTO.getFullId())) {
            attributeGroupDTO.setFullId(FamilyAttributeGroup.createDefaultLeafGroup(booleanValue(getUiType().isSelectable())).getFullId());
        }

        FamilyAttributeGroup.map(familyGroups);

        this.setRegEx(attributeDTO.getRegEx());
        this.setDataType(attributeDTO.getDataType());

        if(isNotEmpty(attributeGroupDTO)) {
            //Available parameters - attribute.name, attribute.attributeGroup.id, attribute.attributeGroup.name, attribute.attributeGroup.masterGroup, attribute.attributeGroup.parentGroup.id
            //attribute.name won't be empty in all the below scenarios
            //Scenario 1 - attribute.attributeGroup.id is not empty
            if(isEmpty(attributeGroupDTO.getName()) && isNotEmpty(attributeGroupDTO.getFullId())) { //Existing attributeGroup
                //Find the leaf group corresponding to the given id
                attributeGroup = FamilyAttributeGroup.getLeafGroup(attributeGroupDTO.getFullId(), familyGroups);
                if(isEmpty(attributeGroup)) { // Can't find the leaf group for the given id, so defaulting it to default group
                    attributeGroup = FamilyAttributeGroup.createDefaultLeafGroup(booleanValue(getUiType().isSelectable()));
                }
            } else { //Creating a new attributeGroup, Scenario 2 - attribute.attributeGroup.name is not empty
                if(booleanValue(attributeGroupDTO.getMasterGroup())) { // Scenario 3 - attribute.attributeGroup.masterGroup is 'Y'. Creating a new master FamilyAttributeGroup(can ignore attribute.attributeGroup.parentGroup.id, if present)
                    attributeGroup = FamilyAttributeGroup.createLeafGroup(attributeGroupDTO.getName(), null);
                    if(isEmpty(attributeGroup)) { //unable to create the new master group, so defaulting to default group
                        attributeGroup = FamilyAttributeGroup.createDefaultLeafGroup(booleanValue(getUiType().isSelectable()));
                    }
                } else { //Creating a new non-master attributeGroup
                    if (isEmpty(attributeGroupDTO.getParentGroup().getId())) { // Creating a new level3 group under the default level2 group
                        attributeGroup = FamilyAttributeGroup.createLeafGroup(attributeDTO.getName(), null, FamilyAttributeGroup.createDefaultMasterGroup(booleanValue(getUiType().isSelectable())));
                    } else { // Scenario 4 - attribute.attributeGroup.parentGroup.Id is not empty. Creating a new group under the default level2 group of the selected parent master group
                        FamilyAttributeGroup parentMasterGroup = FamilyAttributeGroup.getMasterGroup(attributeGroupDTO.getParentGroup().getFullId(), familyGroups);
                        if(isEmpty(parentMasterGroup)) {
                            parentMasterGroup = FamilyAttributeGroup.createDefaultMasterGroup(booleanValue(getUiType().isSelectable()));
                        }
                        attributeGroup = FamilyAttributeGroup.createLeafGroup(attributeGroupDTO.getName(), null, parentMasterGroup);
                    }
                }
            }
        } else { /* This is the case when both attribute.attributeGroup.id and attribute.attributeGroup.name are empty, which won't happen if validation rules are correct */
            attributeGroup = FamilyAttributeGroup.createDefaultLeafGroup(booleanValue(getUiType().isSelectable()));
        }

        setAttributeGroup(attributeGroup);
        if(!getAttributeGroup().getAttributes().containsKey(getId())) {
            getAttributeGroup().getAttributes().put(getId(), this);
        }
    }

    public FamilyAttribute(@NotEmpty(message = "The name constructor argument for FamilyAttribute() cannot be empty") String name, String label) {
        this.name = name;
        this.label = isNotEmpty(label) ? label : name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLabel() {
        if(isEmpty(label)) {
            label = getName();
        }
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Attribute.UIType getUiType() {
        return _uiType;
    }

    public void setUiType(Attribute.UIType uiType) {
        this._uiType = uiType;
        setSelectable(uiType.isSelectable());
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFullId() {
        return (isNotEmpty(getAttributeGroup()) ? getAttributeGroup().getFullId() : "DEFAULT_GROUP" ) + "|" + getId();
    }

    public String getRegEx() {
        return regEx;
    }

    public void setRegEx(String regEx) {
        this.regEx = regEx;
    }

    public String getRequired() {
        return required;
    }

    public void setRequired(String required) {
        this.required = toYesNo(required, "Y");
    }

    public String getSelectable() {
        setSelectable(_uiType.isSelectable());
        return selectable;
    }

    public void setSelectable(String selectable) {
        this.selectable = toYesNo(selectable, "Y");
    }

    public String getScopable() {
        return scopable;
    }

    public void setScopable(String scopable) {
        this.scopable = toYesNo(scopable, "Y");
    }

    public String getActive() {
        return active;
    }

    public void setActive(String active) {
        this.active = toYesNo(active, "Y");
    }

    public long getSequenceNum() {
        return sequenceNum;
    }

    public void setSequenceNum(long sequenceNum) {
        this.sequenceNum = sequenceNum;
    }

    public int getSubSequenceNum() {
        return subSequenceNum;
    }

    public void setSubSequenceNum(int subSequenceNum) {
        this.subSequenceNum = subSequenceNum;
    }

    public String getAttributeId() {
        return attributeId;
    }

    public void setAttributeId(String attributeId) {
        this.attributeId = attributeId;
    }

    public String getCollectionId() {
        return collectionId;
    }

    public void setCollectionId(String collectionId) {
        this.collectionId = collectionId;
    }

    public Attribute getAttribute() {
        return attribute;
    }

    public void setAttribute(Attribute attribute) {
        this.attribute = attribute;
    }

    public FamilyAttributeGroup getAttributeGroup() {
        return attributeGroup;
    }

    public void setAttributeGroup(FamilyAttributeGroup attributeGroup) {
        this.attributeGroup = attributeGroup;
    }

    public Map<String, FamilyAttributeOption> getOptions() {
        return options;
    }

    public void setOptions(Map<String, FamilyAttributeOption> options) {
        this.options = options;
    }

    public Pair<String, Object> validate(Object value) {
        if(getUiType().isMultiSelect()) {
            String[] attributeValue = value instanceof String ? new String[] {(String) value} : (String[]) value;
            if (booleanValue(getRequired()) && (attributeValue.length == 0 || isEmpty(attributeValue[0]))) {
                return Pair.with(getLabel() + " cannot be empty", attributeValue);
            }
        } else {
            String attributeValue = (String) value;
            if (booleanValue(getRequired()) && isEmpty(attributeValue)) {
                return Pair.with(getLabel() + " cannot be empty", attributeValue);
            }
        }
        return null;
    }

    @Override
    public void orchestrate() {
        setRequired(getRequired());
        setSelectable(getSelectable());
        if(isEmpty(getId())) {
            setId(toId(getName()));
        }
    }

    public Map<String, String> toMap() {
        Map<String, String> map = new LinkedHashMap<>();
        map.put("id", getId());
        map.put("fullId", getAttributeGroup().getFullId() + '|' + getId());
        map.put("uiType", getUiType().name());
        map.put("dataType", getDataType());
        map.put("name", getName());
        map.put("group", FamilyAttributeGroup.getUniqueLeafGroupLabel(getAttributeGroup(), "|"));
        map.put("required", getRequired());
        map.put("selectable", getSelectable());
        map.put("options", Integer.toString(options.size()));
        return map;
    }

    public static FamilyAttribute findAttribute(String attributeId, Map<String, FamilyAttributeGroup> familyAttributeGroups) {
        return FamilyAttributeGroup
                .map(familyAttributeGroups).entrySet().parallelStream()
                .flatMap(g -> g.getValue().getChildGroups().entrySet().parallelStream())
                .flatMap(g -> g.getValue().getChildGroups().entrySet().parallelStream())
                .flatMap(g -> g.getValue().getAttributes().entrySet().parallelStream())
                .filter(a -> a.getValue().getId().equals(attributeId))
                .map(Map.Entry::getValue)
                .collect(Collectors.toList()).get(0);
    }
}
