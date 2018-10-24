package com.bigname.pim.api.domain;

import com.bigname.common.util.ConversionUtil;
import com.bigname.common.util.StringUtil;
import com.bigname.common.util.ValidationUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.data.annotation.Transient;

import javax.validation.constraints.NotEmpty;
import java.util.*;
import java.util.stream.Collectors;


/**
 * @author Manu V NarayanaPrasad (manu@blacwood.com)
 * @since 1.0
 */
public class FamilyAttributeGroup extends ValidatableEntity {

    public static final String DETAILS_GROUP = "Details Group";
    public static final String FEATURES_GROUP = "Features Group";
    public static final String DEFAULT_GROUP = "Default Group";
    public static final String DEFAULT_GROUP_ID = "DEFAULT_GROUP";
    public static final String DETAILS_GROUP_ID = getDetailsMasterGroup(null).getId();
    public static final String DETAILS_LEAF_GROUP_FULL_ID = DETAILS_GROUP_ID + "|" + DEFAULT_GROUP_ID + "|" + DEFAULT_GROUP_ID;
    public static final String FEATURES_GROUP_ID = getFeaturesMasterGroup(null).getId();
    public static final String FEATURES_LEAF_GROUP_FULL_ID = FEATURES_GROUP_ID + "|" + DEFAULT_GROUP_ID + "|" + DEFAULT_GROUP_ID;
    /**
     * Name of the Group
     */
    @NotEmpty(message = "Group name cannot be empty")
    private String name;

    private String label;

    private String id;

    private String fullId;

    private String defaultGroup = "N";

    private String active = "Y";

    private long sequenceNum;

    private int subSequenceNum;

    private String masterGroup = "N";

    @Transient
    @JsonIgnore
    private FamilyAttributeGroup parentGroup;

    private Map<String, FamilyAttributeGroup> childGroups = new LinkedHashMap<>();

    private Map<String, FamilyAttribute> attributes = new LinkedHashMap<>();

    public FamilyAttributeGroup() {}

    public FamilyAttributeGroup(String name, String label, boolean masterGroup) {
        this.name = name;
        this.label = isNotEmpty(label) ? label : name;
        this.setMasterGroup(masterGroup ? "Y" : "N");
        if(DEFAULT_GROUP.equals(this.name)) {
            setDefaultGroup("Y");
        }
        orchestrate();
    }

    public FamilyAttributeGroup(String name, String label, FamilyAttributeGroup parentGroup) {
        this(name, label, false);
        this.parentGroup = parentGroup;
        this.parentGroup.getChildGroups().put(this.getId(), this);
        orchestrate();
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

    public boolean isEmpty() {
        return isEmpty(id) && isEmpty(name);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFullId() {
        if(isEmpty(fullId)) {
            fullId = getFullId(this);
        }
        return fullId;
    }

    public void setFullId(String fullId) {
        this.fullId = fullId;
    }

    public String getDefaultGroup() {
        return defaultGroup;
    }

    public void setDefaultGroup(String defaultGroup) {
        this.defaultGroup = toYesNo(defaultGroup, "Y");
    }

    public String getActive() {
        return active;
    }

    public void setActive(String active) {
        this.active = toYesNo(active, "Y");
    }

    public String getMasterGroup() {
        return masterGroup;
    }

    public void setMasterGroup(String masterGroup) {
        this.masterGroup = toYesNo(masterGroup, "Y");
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

    public FamilyAttributeGroup getParentGroup() {
        return parentGroup;
    }

    public FamilyAttributeGroup setParentGroup(FamilyAttributeGroup parentGroup) {
        this.parentGroup = parentGroup;
        if(!parentGroup.isEmpty()) {
            setFullId(getFullId(this));
            if(isNotEmpty(this.getChildGroups())) {
                this.getChildGroups().forEach((k, c) -> c.setFullId(c.getFullId(c)));
            }
        }
        return this;
    }

    public Map<String, FamilyAttributeGroup> getChildGroups() {
        return childGroups;
    }

    public void setChildGroups(Map<String, FamilyAttributeGroup> childGroups) {
        this.childGroups = childGroups;
    }

    public FamilyAttributeGroup addChildGroup(FamilyAttributeGroup childGroup) {
        if(isNotEmpty(childGroup)) {
            if(!childGroups.containsKey(childGroup.getId())) {
                childGroups.put(childGroup.getId(), childGroup);
            }
        }
        return this;
    }

    public Map<String, FamilyAttribute> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, FamilyAttribute> attributes) {
        this.attributes = attributes;
    }

    @Override
    public void orchestrate() {
        setActive(getActive());
        if(isEmpty(getId())) {
            setId(toId(getName()));
        }
        setFullId(getFullId(this));
    }

    public int getLevel() {
        return getPipedValues(getFullId()).size();
    }

    public boolean isAvailable(String channelId) {
        return getAttributes().entrySet().stream().anyMatch(e -> FamilyAttribute.Scope.NOT_APPLICABLE != e.getValue().getScope().get(channelId));
    }

    public Map<String, String> toMap() {
        Map<String, String> map = new LinkedHashMap<>();
        map.put("id", getId());
        map.put("name", getName());
        map.put("active", getActive());
        map.put("parentGroup", isNotEmpty(parentGroup) ? parentGroup.getName() : "");
        map.put("numOfAttributes", Integer.toString(getAttributes().size()));
        map.put("numOfChildGroups", Integer.toString(getChildGroups().size()));
        return map;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FamilyAttributeGroup that = (FamilyAttributeGroup) o;

        return this.getFullId(this).equals(that.getFullId(that));
    }

    @Override
    public int hashCode() {
        return getFullId(this).hashCode();
    }

    private String getFullId(FamilyAttributeGroup attributeGroup) {
        String fullId = "";
        if(isNotEmpty(attributeGroup.getParentGroup()) && !attributeGroup.getParentGroup().isEmpty()) {
            fullId += getFullId(attributeGroup.getParentGroup()) + "|" + attributeGroup.getId();
        } else {
            fullId = attributeGroup.getId();
        }
        return fullId;
    }

    private static FamilyAttributeGroup getLeafGroup(List<String> groupIds, Map<String, FamilyAttributeGroup> groups) {
        if(isNotEmpty(groupIds) && isNotEmpty(groupIds.get(0)) && groups.containsKey(groupIds.get(0))) {
            if(groupIds.size() > 1) {
                FamilyAttributeGroup child = getLeafGroup(groupIds.subList(1, groupIds.size()), groups.get(groupIds.get(0)).getChildGroups());
                FamilyAttributeGroup parent = groups.get(groupIds.get(0));
                setParent(child, parent);
                return child;
            } else {
                return groups.get(groupIds.get(0));
            }
        } else {
            return null;
        }
    }

    private static void setParent(FamilyAttributeGroup child, FamilyAttributeGroup parent) {
        if(!booleanValue(child.getMasterGroup())) {
            if (isEmpty(child.getParentGroup())) {
                child.setParentGroup(parent);
            } else {
                setParent(child.getParentGroup(), parent);
            }
        }
    }

    public static FamilyAttributeGroup getLeafGroup(String fullId, Map<String, FamilyAttributeGroup> familyMasterGroups) {
        return getLeafGroup(getPipedValues(fullId), familyMasterGroups);
    }

    public static FamilyAttributeGroup getMasterGroup(String fullId, Map<String, FamilyAttributeGroup> familyMasterGroups) {
        List<String> groupIds = getPipedValues(fullId);
        if(isNotEmpty(groupIds) && familyMasterGroups.containsKey(groupIds.get(0))) {
            return familyMasterGroups.get(groupIds.get(0));
        } else {
            return null;
        }
    }


    public static boolean addAttribute(FamilyAttribute attribute, Map<String, FamilyAttributeGroup> familyAttributeGroups) {
        boolean success = false;
        FamilyAttributeGroup attributeMasterGroup = attribute.getAttributeGroup().getParentGroup().getParentGroup();
        if(isNotEmpty(attributeMasterGroup)) {
            FamilyAttributeGroup familyMasterGroup = familyAttributeGroups.get(attributeMasterGroup.getId());
            if(isNotEmpty(familyMasterGroup)) {
                FamilyAttributeGroup attributeLevel2Group = attributeMasterGroup.getChildGroups().get(DEFAULT_GROUP_ID);
                FamilyAttributeGroup familyLevel2Group = familyMasterGroup.getChildGroups().get(DEFAULT_GROUP_ID);
                if(isNotEmpty(familyLevel2Group) && isNotEmpty(attributeLevel2Group) && isNotEmpty(attribute.getAttributeGroup())) {
                    FamilyAttributeGroup attributeLeafGroup = getLeafGroup(attribute.getAttributeGroup().getFullId(), familyAttributeGroups);
                    if(isNotEmpty(attributeLeafGroup)) {
                        FamilyAttributeGroup familyLeafGroup = familyLevel2Group.getChildGroups().get(attributeLeafGroup.getId());
                        if(isEmpty(familyLeafGroup)) {
                            familyLeafGroup = new FamilyAttributeGroup(attributeLeafGroup.getName(), attributeLeafGroup.getLabel(), familyLevel2Group);
                        }
                        attribute.setAttributeGroup(familyLeafGroup);
                        familyLeafGroup.getAttributes().put(attribute.getId(), attribute);
                        success = true;
                    }
                }
            } else {
                familyAttributeGroups.put(attributeMasterGroup.getId(), attributeMasterGroup);
                success = true;
            }
        }
        return success;
    }



    public static FamilyAttributeGroup getDetailsMasterGroup(String label) {
        return new FamilyAttributeGroup(DETAILS_GROUP, isEmpty(label) ? "Details" : label, true);
    }

    public static FamilyAttributeGroup getFeaturesMasterGroup(String label) {
        return new FamilyAttributeGroup(FEATURES_GROUP, isEmpty(label) ? "Features" : label, true);
    }

    public static FamilyAttributeGroup getDefaultGroup(String name, String label, FamilyAttributeGroup parentGroup) {
        return new FamilyAttributeGroup(name, label, parentGroup);
    }

    public static FamilyAttributeGroup createDefaultMasterGroup(boolean isSelectable) {
        if(isSelectable) {
            return createDefaultFeaturesGroup().getParentGroup().getParentGroup();
        } else {
            return createDefaultDetailsGroup().getParentGroup().getParentGroup();
        }
    }

    public static FamilyAttributeGroup createDefaultLeafGroup(boolean isSelectable) {
        if(isSelectable) {
            return createDefaultFeaturesGroup();
        } else {
            return createDefaultDetailsGroup();
        }
    }

    public static FamilyAttributeGroup createDefaultDetailsGroup() {
        return new FamilyAttributeGroup(FamilyAttributeGroup.DEFAULT_GROUP, "Details Group", new FamilyAttributeGroup(FamilyAttributeGroup.DEFAULT_GROUP, "Default Group(L2)", FamilyAttributeGroup.getDetailsMasterGroup("Details")));
    }

    public static FamilyAttributeGroup createDefaultFeaturesGroup() {
        return new FamilyAttributeGroup(FamilyAttributeGroup.DEFAULT_GROUP, "Features Group", new FamilyAttributeGroup(FamilyAttributeGroup.DEFAULT_GROUP, "Default Group(L2)", FamilyAttributeGroup.getFeaturesMasterGroup("Product Features")));
    }

    public static FamilyAttributeGroup createMasterGroup(String name, String label) {
        return createLeafGroup(name, label).getParentGroup().getParentGroup();
    }

    public static FamilyAttributeGroup createLeafGroup(String name, String label) {
        return new FamilyAttributeGroup(FamilyAttributeGroup.DEFAULT_GROUP, isEmpty(label) ? name : label, new FamilyAttributeGroup(FamilyAttributeGroup.DEFAULT_GROUP, "Default Group(L2)",new FamilyAttributeGroup(name, label, true)));
    }

    public static FamilyAttributeGroup createLeafGroup(String name, String label, FamilyAttributeGroup masterGroup) {
        if(isNotEmpty(masterGroup) && isNotEmpty(masterGroup.getChildGroups()) && isNotEmpty(masterGroup.getChildGroups().get(DEFAULT_GROUP_ID))) {
            return new FamilyAttributeGroup(name, label, masterGroup.getChildGroups().get(DEFAULT_GROUP_ID));
        } else {
            return null;
        }
    }

    public static List<FamilyAttributeGroup> getAllAttributeGroups(Map<String, FamilyAttributeGroup> groups, GetMode mode, boolean firstRun, int... level) {
        final List<FamilyAttributeGroup> attributeGroups = new ArrayList<>();
        map(groups);
        final List<FamilyAttributeGroup> groupsList = new ArrayList<>();
        // This will happen only when no attributes registered yet for an attribute group
        // We have two default master groups, details group and features group.
        if(firstRun ) { //This is the first run of the recursion

            if(!groups.containsKey(FamilyAttributeGroup.DETAILS_GROUP_ID)) {
                //Won't contain the default details master group, so add it
                groupsList.add(FamilyAttributeGroup.createDefaultMasterGroup(false));
            }

            if(!groups.containsKey(FamilyAttributeGroup.FEATURES_GROUP_ID)) {
                //Won't contain the default features master group, so add it
                groupsList.add(FamilyAttributeGroup.createDefaultMasterGroup(true));
            }
        }

        //Extract all the groups from the map to a list for easy processing
        groupsList.addAll(groups.entrySet().stream().map(Map.Entry::getValue).collect(Collectors.toList()));

        //Sort all the groups - sequenceNum ASC and subSequenceNum DESC
        groupsList.sort((o1, o2) -> {
            if(o1.getSequenceNum() == o2.getSequenceNum()) {
                return o2.getSequenceNum() < o1.getSequenceNum() ? -1 : o2.getSequenceNum() > o1.getSequenceNum() ? 1 : 0;
            } else {
                return o1.getSequenceNum() < o2.getSequenceNum() ? -1 : o1.getSequenceNum() > o2.getSequenceNum() ? 1 : 0;
            }
        });

        switch (mode) {
            case MASTER_ONLY:
                attributeGroups.addAll(groupsList.stream().filter(g -> ValidationUtil.isEmpty(g.getParentGroup())).collect(Collectors.toList()));
                break;
            case NON_MASTER_PARENT_ONLY:
                attributeGroups.addAll(groupsList.stream().filter(g -> ValidationUtil.isNotEmpty(g.getParentGroup()) && ValidationUtil.isNotEmpty(g.getChildGroups())).collect(Collectors.toList()));
                break;
            case LEVEL:
                int _level = level == null || level.length != 1 || level[0] < 1 || level[0] > 3 ? 3 : level[0];
                groupsList.forEach(g -> {
                    if(g.getLevel() == _level) {
                        attributeGroups.add(g);
                    } else if(isNotEmpty(g.getChildGroups())){
                        attributeGroups.addAll(getAllAttributeGroups(g.getChildGroups(), mode, false, _level));
                    }
                });
                break;
            case LEAF_ONLY:
                groupsList.forEach(g -> {
                    if(isEmpty(g.getChildGroups())) {
                        attributeGroups.add(g);
                    } else {
                        attributeGroups.addAll(getAllAttributeGroups(g.getChildGroups(), mode, false));
                    }
                });
                break;
            case All:
                groupsList.forEach(g -> {
                    attributeGroups.add(g);
                    if(isNotEmpty(g.getChildGroups())) {
                        attributeGroups.addAll(getAllAttributeGroups(g.getChildGroups(), mode, false));
                    }
                });
        }
        return attributeGroups;
    }

    public static List<FamilyAttribute> getAllAttributes(Map<String, FamilyAttributeGroup> familyGroups) {
        List<FamilyAttribute> attributes = new ArrayList<>();
        getAllAttributeGroups(familyGroups, GetMode.LEAF_ONLY, true).forEach(attributeGroup -> attributeGroup.getAttributes().forEach((s, attribute) -> attributes.add(attribute)));
        return attributes;
    }

    public static Map<String, FamilyAttribute> getAllAttributesMap(Map<String, FamilyAttributeGroup> familyGroups) {
        return getAllAttributes(familyGroups).stream().collect(Collectors.toMap(FamilyAttribute::getId, e -> e));
    }

    public static List<String> getAllAttributeIds(Map<String, FamilyAttributeGroup> familyGroups) {
        List<String> attributes = new ArrayList<>();
        getAllAttributeGroups(familyGroups, GetMode.LEAF_ONLY, true).forEach(attributeGroup -> attributeGroup.getAttributes().forEach((s, attribute) -> attributes.add(attribute.getId())));
        return attributes;
    }

    public static Map<String, FamilyAttributeGroup> map(Map<String, FamilyAttributeGroup> attributeGroups, FamilyAttributeGroup... parent) {

        attributeGroups.forEach((k, attributeGroup) -> {
            if(isNotEmpty(ConversionUtil.toList(parent))) {
                attributeGroup.setParentGroup(parent[0]);
            }
            if(isNotEmpty(attributeGroup.getChildGroups())) {
                map(attributeGroup.getChildGroups(), attributeGroup);
            } else if(isNotEmpty(attributeGroup.getAttributes())) {
                attributeGroup.getAttributes().forEach((k1, attribute) -> attribute.setAttributeGroup(attributeGroup));
            }
        });
        return attributeGroups;
    }

    public static String getUniqueLeafGroupLabel(FamilyAttributeGroup leafGroup, String separator) {
        if(isNotEmpty(leafGroup) && isNotEmpty(leafGroup.getParentGroup()) && isNotEmpty(leafGroup.getParentGroup().getParentGroup())) {
            FamilyAttributeGroup masterGroup = leafGroup.getParentGroup().getParentGroup();

            if(DEFAULT_GROUP_ID.equals(leafGroup.getId())) {
                return masterGroup.getLabel();
            } else {
                return masterGroup.getLabel() + separator + leafGroup.getLabel();
            }
        }
        return isNotEmpty(leafGroup) ? leafGroup.getLabel() : "";
    }

    public enum GetMode {
        MASTER_ONLY,
        NON_MASTER_PARENT_ONLY,
        LEVEL,
        LEAF_ONLY,
        All

    }

}
