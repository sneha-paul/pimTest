package com.bigname.pim.api.domain;

import com.bigname.core.domain.Entity;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.Indexed;

import javax.validation.constraints.NotEmpty;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Manu V NarayanaPrasad (manu@blacwood.com)
 * @since 1.0
 */
public class AssetCollection extends Entity<AssetCollection> {

    @Transient
    @NotEmpty(message = "Collection Id cannot be empty", groups = {CreateGroup.class, DetailsGroup.class})
    private String collectionId;

    @Indexed(unique = true)
    @NotEmpty(message = "Collection Name cannot be empty", groups = {CreateGroup.class, DetailsGroup.class})
    private String collectionName;

    private String rootId;

    public AssetCollection() {
        super();
    }

    public String getCollectionId() {
        return getExternalId();
    }

    public void setCollectionId(String collectionId) {
        this.collectionId = collectionId;
        setExternalId(collectionId);
    }

    public String getCollectionName() {
        return collectionName;
    }

    public void setCollectionName(String collectionName) {
        this.collectionName = collectionName;
    }

    public String getRootId() {
        return rootId;
    }

    public void setRootId(String rootId) {
        this.rootId = rootId;
    }

    @Override
    protected void setExternalId() {
        this.collectionId = getExternalId();
    }

    @Override
    public AssetCollection merge(AssetCollection collection) {
        for (String group : collection.getGroup()) {
            switch (group) {
                case "DETAILS":
                    this.setExternalId(collection.getExternalId());
                    this.setCollectionName(collection.getCollectionName());
                    this.setActive(collection.getActive());
                    break;
            }
        }
        return this;
    }

    @Override
    public Map<String, String> toMap() {
        Map<String, String> map = new LinkedHashMap<>();
        map.put("externalId", getExternalId());
        map.put("collectionName", getCollectionName());
        map.put("rootId", getRootId());
        map.put("active", getActive());
        return map;
    }

    public Map<String, String> toMap1() {
        Map<String, String> map = new LinkedHashMap<>();
        map.put("externalId", getExternalId());
        map.put("name", getCollectionName());
        map.put("parentId", "");
        map.put("id", getRootId());
        map.put("isDirectory", "Y");
        map.put("active", getActive());
        return map;
    }

    public Map<String, Object> diff(AssetCollection assetCollection, boolean... ignoreInternalId) {
        boolean _ignoreInternalId = ignoreInternalId != null && ignoreInternalId.length > 0 && ignoreInternalId[0];
        Map<String, Object> diff = new HashMap<>();
        if (!_ignoreInternalId && !this.getId().equals(assetCollection.getId())) {
            diff.put("internalId", assetCollection.getId());
        }
        if (!this.getCollectionName().equals(assetCollection.getCollectionName())) {
            diff.put("collectionName", assetCollection.getCollectionName());
        }
        if (!this.getRootId().equals(assetCollection.getRootId())) {
            diff.put("rootId", assetCollection.getRootId());
        }
        return diff;
    }
}
