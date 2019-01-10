package com.bigname.pim.api.domain;

import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;

import javax.validation.constraints.NotEmpty;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Manu V NarayanaPrasad (manu@blacwood.com)
 * @since 1.0
 */

@CompoundIndexes({
        @CompoundIndex(name = "uniqueFileName", unique = true, def = "{'parentDirectoryId':1, 'fileName':1}")
})
public class VirtualFile extends Entity<VirtualFile> {

    @Transient
    @NotEmpty(message = "Id cannot be empty", groups = {CreateGroup.class, DetailsGroup.class})
    private String fileId;

    @NotEmpty(message = "Name cannot be empty", groups = {CreateGroup.class, DetailsGroup.class})
    private String fileName;

    private String isDirectory = "N";

    private String type;

    private String extension;

    private String parentDirectoryId;

    private String rootDirectoryId;


    public VirtualFile() {
        super();
    }

    public VirtualFile(boolean isDirectory) {
        super();
        this.setIsDirectory(isDirectory ? "Y" : "N");
    }

    public static final VirtualFile getRootInstance() {
        VirtualFile root = new VirtualFile(true);
        root.setFileId("ROOT");
        root.setFileName("Root");
        root.setParentDirectoryId("");
        root.setActive("Y");
        return root;
    }

    public String getFileId() {
        return getExternalId();
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
        setExternalId(fileId);
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getIsDirectory() {
        return isDirectory;
    }

    public void setIsDirectory(String isDirectory) {
        if(isDirectory == null) {
            isDirectory = "N";
        }
        this.isDirectory = toYesNo(isDirectory,"Y");
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    public String getParentDirectoryId() {
        return parentDirectoryId;
    }

    public void setParentDirectoryId(String parentDirectoryId) {
        this.parentDirectoryId = parentDirectoryId;
    }

    public String getRootDirectoryId() {
        return rootDirectoryId;
    }

    public void setRootDirectoryId(String rootDirectoryId) {
        this.rootDirectoryId = rootDirectoryId;
    }

    @Override
    void setExternalId() {
        this.fileId = getExternalId();
    }

    @Override
    public VirtualFile merge(VirtualFile virtualFile) {
        for (String group : virtualFile.getGroup()) {
            switch (group){
                case "DETAILS":
                    this.setExternalId(virtualFile.getExternalId());
                    this.setFileName(virtualFile.getFileName());
                    this.setActive(virtualFile.getActive());
                    this.setIsDirectory(virtualFile.getIsDirectory());
                    this.setType(virtualFile.getType());
                    this.setExtension(virtualFile.getExtension());
                    break;
            }
        }
        return this;
    }

    @Override
    public Map<String, String> toMap() {
        Map<String, String> map = new LinkedHashMap<>();
        map.put("externalId", getExternalId());
        map.put("name", getFileName());
        map.put("isDirectory", getIsDirectory());
        map.put("type", getType());
        map.put("extension", getExtension());
        map.put("active", getActive());
        return map;
    }
}
