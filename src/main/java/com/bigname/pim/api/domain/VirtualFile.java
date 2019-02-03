package com.bigname.pim.api.domain;

import com.bigname.common.util.ConversionUtil;
import com.bigname.core.domain.Entity;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotEmpty;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
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

    private List<String> parentIds = new ArrayList<>();

    private String rootDirectoryId;

    private long size;


    public VirtualFile() {
        super();
    }

    public VirtualFile(boolean isDirectory) {
        super();
        this.setIsDirectory(isDirectory ? "Y" : "N");
    }

    public VirtualFile(MultipartFile file, String parentDirectoryId, String rootDirectoryId) {
        this.setActive("Y");
        this.setFileId(this.getId());
        this.setFileName(file.getOriginalFilename());
        this.setParentDirectoryId(parentDirectoryId);
        this.setRootDirectoryId(rootDirectoryId);
        this.setSize(file.getSize());
        if(this.getFileName().contains(".")) {
            this.setExtension(this.getFileName().substring(this.getFileName().lastIndexOf(".") + 1));
            this.setType("IMAGE"); //TODO
        }
    }

    public static final VirtualFile getRootInstance() {
        VirtualFile root = new VirtualFile(true);
        root.setFileId("ROOT" + root.getId());
        root.setFileName("Root" + root.getId());
        root.setParentDirectoryId("");
        root.setActive("Y");
        return root;
    }

    public String getFileId() {
        return getExternalId();
    }

    public void setFileId(String fileId) {
        setExternalId(fileId);
        this.fileId = getExternalId();
    }

    public String getInternalFileName() {
        return getExternalId() + (isNotEmpty(getExtension()) ? "." + getExtension() : "");
    }

    public String getFileName() {
        return isNotNull(fileName) ? fileName.trim() : null;
    }

    public void setFileName(String fileName) {
        this.fileName = isNotNull(fileName) ? fileName.trim() : null;
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
        this.extension = isNotNull(extension) ? extension.trim() : null;
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

    public List<String> getParentIds() {
        return parentIds;
    }

    public List<String> setParentIds(List<String> parentIds) {
        this.parentIds = parentIds;
        return this.parentIds;
    }

    public List<String> addParentId(String parentId) {
        getParentIds().add(parentId);
        return getParentIds();
    }

    public long getSize() {
        return size;
    }

    public String getFormattedSize() {
        return ConversionUtil.getFileSize(getSize());
    }

    public void setSize(long size) {
        this.size = size;
    }

    @Override
    protected void setExternalId() {
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
        map.put("id", getId());
        map.put("externalId", getExternalId());
        map.put("name", getFileName());
        map.put("internalFileName", getInternalFileName());
        map.put("parentId", getParentDirectoryId());
        map.put("isDirectory", getIsDirectory());
        map.put("type", getType());
        map.put("extension", getExtension());
        map.put("active", getActive());
        return map;
    }
}
