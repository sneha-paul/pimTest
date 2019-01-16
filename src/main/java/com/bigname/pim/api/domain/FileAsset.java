package com.bigname.pim.api.domain;

import com.bigname.common.util.ConversionUtil;
import com.bigname.common.util.ValidationUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Manu V NarayanaPrasad (manu@blacwood.com)
 * @since 1.0
 */
public class FileAsset {

    public enum AssetFamily {
        ASSETS, TEMPLATES, FILES;
        public static AssetFamily getFamily(String value) {
            try {
                return valueOf(value);
            } catch (IllegalArgumentException e) {
                return FILES;
            }
        }
    }
    public enum Type {
        VIDEO, IMAGE, OTHER;
        public static Type getType(String value) {
            try {
                return valueOf(value);
            } catch (IllegalArgumentException e) {
                return OTHER;
            }
        }
    }
    private String id;
    private String name;
    private String internalName;
    private Type type = Type.OTHER;
    private int sequenceNum = 0;
    private String defaultFlag = "N";

    public FileAsset() {}

    public FileAsset(VirtualFile virtualFile, int... sequenceNum) {
       this.id = virtualFile.getId();
       this.name = virtualFile.getFileName();
       this.internalName = virtualFile.getInternalFileName();
       this.type = Type.getType(virtualFile.getType());
       if(sequenceNum != null && sequenceNum.length > 0) {
        this.sequenceNum = sequenceNum[0];
       }
    }

    public FileAsset(String id, String name, String internalFileName, String type, int sequenceNum) {
        this.id = id;
        this.name = name;
        this.internalName = internalFileName;
        this.type = Type.getType(type);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getInternalName() {
        return internalName;
    }

    public void setInternalName(String internalName) {
        this.internalName = internalName;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public int getSequenceNum() {
        return sequenceNum;
    }

    public void setSequenceNum(int sequenceNum) {
        this.sequenceNum = sequenceNum;
    }

    public String getDefaultFlag() {
        return defaultFlag;
    }

    public void setDefaultFlag(String defaultFlag) {
        this.defaultFlag = "Y".equalsIgnoreCase(defaultFlag) ? "Y" : "N";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FileAsset fileAsset = (FileAsset) o;

        return id.equals(fileAsset.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("id", getId());
        map.put("name", getName());
        map.put("internalName", getInternalName());
        map.put("type", getType().toString());
        map.put("sequenceNum", getSequenceNum());
        map.put("defaultFlag", getDefaultFlag());
        return map;
    }
}
