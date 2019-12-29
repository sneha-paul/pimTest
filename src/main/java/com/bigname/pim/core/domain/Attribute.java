package com.bigname.pim.core.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.m7.xtreme.common.util.StringUtil;
import com.m7.xtreme.xcore.domain.ValidatableEntity;
import org.springframework.data.annotation.Transient;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Manu V NarayanaPrasad (manu@blacwood.com)
 * @since 1.0
 */
public class Attribute extends ValidatableEntity<Attribute> {

    @NotEmpty(message = "Attribute name cannot be empty")
    @NotBlank(message = "Attribute name cannot be blank")
    private String name;
    private String label;
    private UIType _uiType = UIType.INPUT_BOX;
    private String dataType = "string";  //Initial version only supports String type
    private String id;
    private String regEx;
    private String selectable = "N";
    private String active = "Y";
    private String parentAttributeId; //Full Id of the parent attribute, if any
    private long sequenceNum;
    private int subSequenceNum;

    @Transient
    @JsonIgnore
    private AttributeGroup attributeGroup;

    // Map of all attributeOptions for an attribute instance
    private Map<String, AttributeOption> options = new LinkedHashMap<>();

    //TODO - can be removed later, so far option grouping can be achieved without this
    //Map of attributeOptionFullIds grouped by parentOptionId (Normal ID, not FullID). (This will be empty when parentAttributeId is empty)
    private Map<String, List<String>> parentBasedOptions = new HashMap<>();

    //TODO - this can also be removed, since the database lookup for this is not that expensive
    //Reference map of usage. Key will be familyId and values will be familyAttributeFullIds.
    private Map<String, List<String>> referenceMap = new HashMap<>();

    public Attribute() {}

    public Attribute(String name) {
        if(isEmpty(name)) {
            throw new IllegalArgumentException("The name constructor argument for Attribute() cannot be empty");
        }
        this.name = name;
        this.label = name;
    }

    private Attribute(Attribute attributeDTO, Map<String, AttributeGroup> attributeGroups) {
        this(attributeDTO.getName());
        this.setUiType(attributeDTO.getUiType());
        if(isNotEmpty(attributeDTO.getParentAttributeId())) {
            this.setParentAttributeId(attributeDTO.getParentAttributeId());
        }
        AttributeGroup attributeGroup = null;
        AttributeGroup attributeGroupDTO = attributeDTO.getAttributeGroup();
        orchestrate();
        this.setId(StringUtil.getUniqueName(this.getId(), AttributeGroup.getAllAttributeIds(attributeGroups)));
        this.setRegEx(attributeDTO.getRegEx());
        this.setDataType(attributeDTO.getDataType());
        AttributeGroup defaultGroup = new AttributeGroup(AttributeGroup.DEFAULT_GROUP, null);
        /*if(!attributeGroups.containsKey(defaultGroup.getId())) {
            attributeGroup = attributeGroups.put(defaultGroup.getId(), defaultGroup);
        }*/
        String parentFullId = isNotEmpty(attributeGroupDTO) && isNotEmpty(attributeGroupDTO.getParentGroup()) ? attributeGroupDTO.getParentGroup().getFullId() : null;
        AttributeGroup parent = isNotEmpty(parentFullId) ? AttributeGroup.getAttributeGroup(parentFullId, attributeGroups) : null;
        if(isNotEmpty(attributeGroupDTO)) {
            if (isNotEmpty(attributeGroupDTO.getFullId())) {
                attributeGroup = AttributeGroup.getAttributeGroup(attributeGroupDTO.getFullId(), attributeGroups);
            } else if (isNotEmpty(attributeGroupDTO.getName())) {
                if(isEmpty(parent)) {
                    attributeGroup = new AttributeGroup(attributeGroupDTO.getName(), null);
                    attributeGroups.put(attributeGroup.getId(), attributeGroup);
                } else {
                    attributeGroup = new AttributeGroup(attributeGroupDTO.getName(), null, parent);
                }

            }
        }
        if(isEmpty(attributeGroup)) {
            attributeGroups.put(defaultGroup.getId(), defaultGroup);
            attributeGroup = attributeGroups.get(defaultGroup.getId());
        }
        this.attributeGroup = attributeGroup;
        if(!this.attributeGroup.getAttributes().containsKey(this.getId())) {
            this.attributeGroup.getAttributes().put(this.getId(), this);
        }
    }

    public static Attribute buildInstance(Attribute attributeDTO, Map<String, AttributeGroup> attributeGroups) {
        return new Attribute(attributeDTO, attributeGroups);
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

    public String getFullId() {
        return (isNotEmpty(getAttributeGroup()) ? getAttributeGroup().getFullId() : "DEFAULT_GROUP" ) + "|" + getId();
    }

    public String getRegEx() {
        return regEx;
    }

    public void setRegEx(String regEx) {
        this.regEx = regEx;
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

    public Map<String, List<String>> getParentBasedOptions() {
        return parentBasedOptions;
    }

    public void setParentBasedOptions(Map<String, List<String>> parentBasedOptions) {
        this.parentBasedOptions = parentBasedOptions;
    }

    public Map<String, List<String>> getReferenceMap() {
        return referenceMap;
    }

    public void setReferenceMap(Map<String, List<String>> referenceMap) {
        this.referenceMap = referenceMap;
    }

    public String getParentAttributeId() {
        return parentAttributeId;
    }

    public void setParentAttributeId(String parentAttributeId) {
        this.parentAttributeId = parentAttributeId;
    }

    public AttributeGroup getTopLevelGroup() {
        if(isEmpty(getAttributeGroup()) || isEmpty(getAttributeGroup().getParentGroup())) {
            return getAttributeGroup();
        } else {
            AttributeGroup group = getAttributeGroup();
            while(isNotEmpty(group.getParentGroup())) {
                group = group.getParentGroup();
            }
            return group;
        }
    }

    @Override
    public void orchestrate() {
        setSelectable(getSelectable());
        if(isEmpty(getId())) {
            String id = toId(getName());
//            if(id.length() > PIMConstants.ID_MAX_LENGTH) {
//                id = id.substring(0, PIMConstants.ID_MAX_LENGTH);
//            }
            setId(id);
        }
    }

    public Attribute merge(Attribute attribute) {
        this.setName(attribute.getName());
        this.setLabel(isNotEmpty(attribute.getLabel()) ? attribute.getLabel() : attribute.getName());
        //TODO - update attribute id - If the attribute id is used in family, then we should update all those references
        this.setUiType(attribute.getUiType());
        if(isEmpty(getParentAttributeId()) && isNotEmpty(attribute.getParentAttributeId())) {
            this.setParentAttributeId(attribute.getParentAttributeId());
        } else if(isNotEmpty(getParentAttributeId()) && !getParentAttributeId().equals(attribute.getParentAttributeId())) {
            this.setParentAttributeId(attribute.getParentAttributeId());
            //TODO - remove all parent bases options
        }
        return this;
    }

    public Map<String, String> toMap() {
        Map<String, String> map = new LinkedHashMap<>();
        map.put("externalId", getId());
        map.put("fullId", getAttributeGroup().getFullId() + '|' + getId());
        map.put("uiType", getUiType().name());
        map.put("dataType", getDataType());
        map.put("name", getName());
        map.put("group", AttributeGroup.getFullGroupLabel(getAttributeGroup(), "|"));
        map.put("selectable", getSelectable());
        map.put("parentAttributeId", getParentAttributeId());
        map.put("options", Integer.toString(options.size()));
        return map;
    }

    public int compare(Attribute attribute, String property, String direction) {
        property = isEmpty(property) ? "" : property.trim();
        switch (property) {
            case "id":
                return Direction.getDirection(direction) * getId().compareTo(attribute.getId());
            case "group":
                return Direction.getDirection(direction) * AttributeGroup.getFullGroupLabel(getAttributeGroup(), "|").compareTo(AttributeGroup.getFullGroupLabel(attribute.getAttributeGroup(), "|"));
            case "selectable":
                return Direction.getDirection(direction) * getSelectable().compareTo(attribute.getSelectable());
            default:
                return Direction.getDirection(direction) * getName().compareTo(attribute.getName());
        }
    }

    /*public static Attribute findAttribute(String attributeId, Map<String, AttributeGroup> familyAttributeGroups) {
        return AttributeGroup
                .map(familyAttributeGroups).entrySet().parallelStream()
                .flatMap(g -> g.getValue().getChildGroups().entrySet().parallelStream())
                .flatMap(g -> g.getValue().getChildGroups().entrySet().parallelStream())
                .flatMap(g -> g.getValue().getAttributes().entrySet().parallelStream())
                .filter(a -> a.getValue().getId().equals(attributeId))
                .map(Map.Entry::getValue)
                .collect(Collectors.toList()).get(0);
    }*/



    public enum UIType {
        INPUT_BOX("Input Box", "N"),
        DROPDOWN("Dropdown", "Y"),
        MULTI_SELECT("Multi Select", "Y") {
            @Override
            public boolean isMultiSelect() {
                return true;
            }
        },
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

        public static Map<String, String> getAll() {
            Map<String, String> all = new HashMap<>();
            for(UIType uiType : values()) {
                all.put(uiType.name(), uiType.getLabel());
            }
            return all.entrySet()
                    .stream()
                    .sorted(Map.Entry.comparingByValue())
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
        }
    }

    @Override
    public String toString() {
        return "Attribute{" +
                "name='" + name + '\'' +
                ", dataType='" + dataType + '\'' +
                ", attributeGroup=" + attributeGroup +
                '}';
    }
    public Map<String, Object> diff(Attribute attribute, boolean... ignoreInternalId) {
        boolean _ignoreInternalId = ignoreInternalId != null && ignoreInternalId.length > 0 && ignoreInternalId[0];
        Map<String, Object> diff = new HashMap<>();
        if (!_ignoreInternalId && !this.getId().equals(attribute.getId())) {
            diff.put("internalId", attribute.getId());
        }
        if (!this.getName().equals(attribute.getName())) {
            diff.put("name", attribute.getName());
        }
        if (!this.getActive().equals(attribute.getActive())) {
            diff.put("active", attribute.getActive());
        }
        return diff;
    }
}
