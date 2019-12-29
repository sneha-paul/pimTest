package com.bigname.pim.core.domain;

import com.m7.xtreme.xcore.domain.MongoEntity;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Page;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import static com.bigname.pim.core.domain.Catalog.Property.*;

/**
 * Created by Manu on 8/9/2018.
 */
@Document
public class Catalog extends MongoEntity<Catalog> {


    @Transient
    @NotEmpty(message = "Catalog Id cannot be empty",groups = {CreateGroup.class, DetailsGroup.class})
    @NotBlank(message = "Catalog Id cannot be blank",groups = {CreateGroup.class, DetailsGroup.class})
    String catalogId;

    @Indexed(unique = true)
    @NotEmpty(message = "Catalog Name cannot be empty", groups = {CreateGroup.class, DetailsGroup.class})
    @NotBlank(message = "Catalog Id cannot be blank", groups = {CreateGroup.class, DetailsGroup.class})
    private String catalogName;

    private String description;

    @Transient
    private Page<RootCategory> rootCategories;

    public Catalog() {
        super();
    }

    public Catalog(Map<String, Object> properties) {
        this.setCatalogName((String) properties.get(CATALOG_NAME.name()));
        this.setCatalogId((String) properties.get(CATALOG_ID.name()));
        this.setDescription((String) properties.get(DESCRIPTION.name()));
        this.setActive((String) properties.get(ACTIVE.name()));
        this.setDiscontinued((String) properties.get(DISCONTINUED.name()));
    }

    /*public Catalog(String externalId, String catalogName) {
        super(externalId);
        this.catalogName = catalogName;
    }*/

    public String getCatalogId() {
        return getExternalId();
    }

    public void setCatalogId(String catalogId) {
        this.catalogId = catalogId;
        setExternalId(catalogId);
    }

    public String getCatalogName() {
        return catalogName;
    }

    public void setCatalogName(String catalogName) {
        this.catalogName = catalogName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Page<RootCategory> getRootCategories() {
        return rootCategories;
    }

    public void setRootCategories(Page<RootCategory> rootCategories) {
        this.rootCategories = rootCategories;
    }

    protected void setExternalId() {
        this.catalogId = getExternalId();
    }

    @Override
    public void orchestrate() {
        super.orchestrate();
       /* setDiscontinued(getDiscontinued());
        if (booleanValue(getActive()) && booleanValue(getDiscontinued())){
            setActive("N");
        }*/
    }

    @Override
    public Catalog merge(Catalog catalog) {
        for (String group : catalog.getGroup()) {
            switch(group) {
                case "DETAILS" :
                    this.setExternalId(catalog.getExternalId());
                    this.setCatalogName(catalog.getCatalogName());
                    this.setDescription(catalog.getDescription());
                    this.setActive(catalog.getActive());
                    mergeBaseProperties(catalog);
                    break;
            }
        }

        return this;
    }

    @Override
    public Catalog cloneInstance() {
        Catalog clone = new Catalog();
        clone.setActive("N");
        clone.setDiscontinued("N");
        clone.setExternalId(cloneValue(getExternalId()));
        clone.setCatalogName(cloneValue(getCatalogName()));
        clone.setDescription(cloneValue(getDescription()));
        return clone;
    }

    @Override
    public Map<String, String> toMap() {
        Map<String, String> map = new LinkedHashMap<>();
        map.put("catalogName", getCatalogName());
        map.putAll(getBasePropertiesMap());
        return map;
    }

    @Override
    public boolean equals(Catalog catalog) {
        return this.getId().equals(catalog.getId())
                && this.getCatalogId().equals(catalog.getCatalogId())
                && this.getCatalogName().equals(catalog.getCatalogName())
                && this.getDescription().equals(catalog.getDescription());
    }

    @Override
    public boolean equals(Map<String, Object> catalogMap) {
        return this.getId().equals(catalogMap.get(Property.ID.name()))
                && this.getCatalogId().equals(catalogMap.get(Property.CATALOG_ID.name()))
                && this.getCatalogName().equals(catalogMap.get(Property.CATALOG_NAME.name()))
                && this.getDescription().equals(catalogMap.get(Property.DESCRIPTION.name()));
    }

    public enum Property{
        ID, CATALOG_ID, CATALOG_NAME, DESCRIPTION, ACTIVE, DISCONTINUED
    }

    public Map<String, Object> diff(Catalog catalog, boolean ... ignoreInternalId) {
        boolean _ignoreInternalId = ignoreInternalId != null && ignoreInternalId.length > 0 && ignoreInternalId[0];
        Map<String, Object> diff = new HashMap<>();
        if(!_ignoreInternalId && !this.getId().equals(catalog.getId())){
            diff.put("internalId", catalog.getId());
        }
        if(!this.getCatalogName().equals(catalog.getCatalogName())){
            diff.put("catalogName", catalog.getCatalogName());
        }
        if(!this.getDescription().equals(catalog.getDescription())){
            diff.put("description", catalog.getDescription());
        }


        return diff;
    }

    /*@Override
    public Object getCopy(Catalog catalog) {
        Catalog _catalog = new Catalog();
        _catalog.setCatalogName(catalog.getCatalogName());
        _catalog.setCatalogId(catalog.getCatalogId());
        _catalog.setDescription(catalog.getDescription());
        _catalog.setVersionId(catalog.getVersionId());
        _catalog.setActive(catalog.getActive());
        _catalog.setArchived(catalog.getArchived());
        _catalog.setDiscontinued(catalog.getDiscontinued());
        _catalog.setId(catalog.getId());
        return _catalog;
    }*/
}
