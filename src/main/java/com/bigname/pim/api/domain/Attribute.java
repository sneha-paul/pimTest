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
public class Attribute extends ValidatableEntity {

    @NotEmpty(message = "Attribute name cannot be empty")
    private String name;

    @NotEmpty(message = "Attribute entity type cannot be empty")
    private String entityType;

    private String label;
    private UIType _uiType = UIType.INPUT_BOX;
    private String dataType = "string";  //Initial version only supports String type
    private String id;
    private String regEx;
    private String required = "N";
    private String selectable = "N";
    private String active = "Y";
    private long sequenceNum;
    private int subSequenceNum;

    @Transient
    @JsonIgnore
    private AttributeGroup attributeGroup;

    private Map<String, AttributeOption> options = new LinkedHashMap<>();

    public Attribute() {}

    public Attribute(Attribute attributeDTO, Map<String, AttributeGroup> familyGroups) {
        this(attributeDTO.getName());
        this.setUiType(attributeDTO.getUiType());
        AttributeGroup attributeGroup, attributeGroupDTO = attributeDTO.getAttributeGroup();
        this.setRequired(attributeDTO.getRequired());
        orchestrate();
        this.setId(StringUtil.getUniqueName(this.getId(), AttributeGroup.getAllAttributeIds(familyGroups)));
        if(isNotEmpty(attributeGroupDTO.getFullId()) && AttributeGroup.DEFAULT_GROUP_ID.equals(attributeGroupDTO.getFullId())) {
            attributeGroupDTO.setFullId(AttributeGroup.createDefaultLeafGroup(booleanValue(getUiType().isSelectable())).getFullId());
        }

        AttributeGroup.map(familyGroups);

//        this.setRegEx(attributeDTO.getRegEx()); TODO - uncomment to enable validation
//        this.setDataType(attributeDTO.getDataType()); TODO - uncomment to enable multiple data type

        if(isNotEmpty(attributeGroupDTO)) {
            //Available parameters - attribute.name, attribute.attributeGroup.id, attribute.attributeGroup.name, attribute.attributeGroup.masterGroup, attribute.attributeGroup.parentGroup.id
            //attribute.name won't be empty in all the below scenarios
            //Scenario 1 - attribute.attributeGroup.id is not empty
            if(isEmpty(attributeGroupDTO.getName()) && isNotEmpty(attributeGroupDTO.getFullId())) { //Existing attributeGroup
                //Find the leaf group corresponding to the given id
                attributeGroup = AttributeGroup.getLeafGroup(attributeGroupDTO.getFullId(), familyGroups);
                if(isEmpty(attributeGroup)) { // Can't find the leaf group for the given id, so defaulting it to default group
                    attributeGroup = AttributeGroup.createDefaultLeafGroup(booleanValue(getUiType().isSelectable()));
                }
            } else { //Creating a new attributeGroup, Scenario 2 - attribute.attributeGroup.name is not empty
                if(booleanValue(attributeGroupDTO.getMasterGroup())) { // Scenario 3 - attribute.attributeGroup.masterGroup is 'Y'. Creating a new master AttributeGroup(can ignore attribute.attributeGroup.parentGroup.id, if present)
                    attributeGroup = AttributeGroup.createLeafGroup(attributeGroupDTO.getName(), null);
                    if(isEmpty(attributeGroup)) { //unable to create the new master group, so defaulting to default group
                        attributeGroup = AttributeGroup.createDefaultLeafGroup(booleanValue(getUiType().isSelectable()));
                    }
                } else { //Creating a new non-master attributeGroup
                    if (isEmpty(attributeGroupDTO.getParentGroup().getId())) { // Creating a new level3 group under the default level2 group
                        attributeGroup = AttributeGroup.createLeafGroup(attributeDTO.getName(), null, AttributeGroup.createDefaultMasterGroup(booleanValue(getUiType().isSelectable())));
                    } else { // Scenario 4 - attribute.attributeGroup.parentGroup.Id is not empty. Creating a new group under the default level2 group of the selected parent master group
                        AttributeGroup parentMasterGroup = AttributeGroup.getMasterGroup(attributeGroupDTO.getParentGroup().getFullId(), familyGroups);
                        if(isEmpty(parentMasterGroup)) {
                            parentMasterGroup = AttributeGroup.createDefaultMasterGroup(booleanValue(getUiType().isSelectable()));
                        }
                        attributeGroup = AttributeGroup.createLeafGroup(attributeGroupDTO.getName(), null, parentMasterGroup);
                    }
                }
            }
        } else { /* This is the case when both attribute.attributeGroup.id and attribute.attributeGroup.name are empty, which won't happen if validation rules are correct */
            attributeGroup = AttributeGroup.createDefaultLeafGroup(booleanValue(getUiType().isSelectable()));
        }

        this.attributeGroup = attributeGroup;
        if(!this.attributeGroup.getAttributes().containsKey(this.getId())) {
            this.attributeGroup.getAttributes().put(this.getId(), this);
        }
    }



    public Attribute(String name) {
        if(isEmpty(name)) {
            throw new IllegalArgumentException("The name constructor argument for Attribute() cannot be empty");
        }
        this.name = name;
        this.label = name;

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

    public String getEntityType() {
        return entityType;
    }

    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }

    public UIType getUiType() {
        return _uiType;
    }

    public void setUiType(UIType uiType) {
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

    public AttributeGroup getAttributeGroup() {
        return attributeGroup;
    }

    public void setAttributeGroup(AttributeGroup attributeGroup) {
        this.attributeGroup = attributeGroup;
    }

    public Map<String, AttributeOption> getOptions() {
        return options;
    }

    public void setOptions(Map<String, AttributeOption> options) {
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
        map.put("group", AttributeGroup.getUniqueLeafGroupLabel(getAttributeGroup(), "|"));
        map.put("required", getRequired());
        map.put("selectable", getSelectable());
        map.put("options", Integer.toString(options.size()));
        return map;
    }

    public static Attribute findAttribute(String attributeId, Map<String, AttributeGroup> familyAttributeGroups) {
        return AttributeGroup
                .map(familyAttributeGroups).entrySet().parallelStream()
                .flatMap(g -> g.getValue().getChildGroups().entrySet().parallelStream())
                .flatMap(g -> g.getValue().getChildGroups().entrySet().parallelStream())
                .flatMap(g -> g.getValue().getAttributes().entrySet().parallelStream())
                .filter(a -> a.getValue().getId().equals(attributeId))
                .map(Map.Entry::getValue)
                .collect(Collectors.toList()).get(0);
    }

    public enum UIType {
        INPUT_BOX("Input Box", "N"),
        DROPDOWN("Dropdown", "Y"),
        CHECKBOX("Checkbox", "Y") {
            @Override
            public boolean isMultiSelect() {
                return true;
            }
        },
        RADIO_BUTTON("Radio Button", "Y"),
        YES_NO("Yes/No", "N"),
        TEXTAREA("Textarea", "N"),
        DATE_PICKER("Date Picker", "N");

        String label = "";
        String selectable = "N";
        UIType(String label, String selectable) {
            this.label = label;
            this.selectable = selectable;
        }

        public String getLabel() {
            return label;
        }

        public String isSelectable() {
            return selectable;
        }

        public boolean isMultiSelect() {
            return false;
        }

        public static UIType get(String value) {
            for (UIType type : values()) {
                if(type.name().equals(value)) {
                    return type;
                }
            }
            return UIType.INPUT_BOX;

        }
    }
}
