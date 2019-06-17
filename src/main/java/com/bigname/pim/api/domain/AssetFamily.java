package com.bigname.pim.api.domain;

import com.m7.xtreme.xcore.domain.Entity;
import com.m7.xtreme.xcore.domain.ValidatableEntity;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.Indexed;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import java.util.LinkedHashMap;
import java.util.Map;
import static com.bigname.pim.api.domain.Website.Property.*;
import static com.m7.xtreme.common.util.RegExBuilder.*;

/**
 * Created by sanoop on 14/02/2019.
 */
public class AssetFamily extends Entity<AssetFamily> {

    @Transient
    @NotEmpty(message = "AssetFamily Id cannot be empty", groups = {ValidatableEntity.CreateGroup.class, ValidatableEntity.DetailsGroup.class})
    @Pattern(regexp = "[" + ALPHA + NUMERIC + UNDERSCORE + "]", message = "assetFamily.assetFamilyId.invalid")
    private String assetFamilyId;

    @Indexed(unique = true)
    @NotEmpty(message = "AssetFamily name cannot be empty", groups = {ValidatableEntity.CreateGroup.class, ValidatableEntity.DetailsGroup.class})
    @Pattern(regexp = "[" + ALPHA + NUMERIC + SPACE + "]", message = "assetFamily.assetFamilyName.invalid")
    private String assetFamilyName;

    private String description;

    public AssetFamily() { super(); }

    public AssetFamily(Map<String, Object> properties) {
        this.setAssetFamilyName((String) properties.get(WEBSITE_NAME.name()));
        this.setAssetFamilyId((String) properties.get(WEBSITE_ID.name()));
        this.setDescription((String) properties.get(URL.name()));
        this.setActive((String) properties.get(ACTIVE.name()));
    }

    public String getAssetFamilyId() {return getExternalId();}

    public void setAssetFamilyId(String assetFamilyId) {
        this.assetFamilyId = assetFamilyId;
        setExternalId(assetFamilyId);
    }

    public String getAssetFamilyName() {return assetFamilyName;}

    public void setAssetFamilyName(String assetFamilyName) {this.assetFamilyName = assetFamilyName;}

    public String getDescription() {return description;}

    public void setDescription(String description) {this.description = description;}

    protected void setExternalId() {
        this.assetFamilyId = getExternalId();
    }

    @Override
    public AssetFamily merge(AssetFamily assetFamily) {
        for (String group : assetFamily.getGroup()) {
            switch (group) {
                case "DETAILS":
                    this.setExternalId(assetFamily.getExternalId());
                    this.setAssetFamilyName(assetFamily.getAssetFamilyName());
                    this.setDescription(assetFamily.getDescription());
                    mergeBaseProperties(assetFamily);
                    break;
            }
        }
        return this;
    }

    @Override
    public Map<String, String> toMap () {

        Map<String, String> map = new LinkedHashMap<>();
        map.put("externalId", getExternalId());
        map.put("assetFamilyName", getAssetFamilyName());
        map.put("description", getDescription());
        map.putAll(getBasePropertiesMap());
        return map;
    }


}
