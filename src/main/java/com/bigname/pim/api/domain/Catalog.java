package com.bigname.pim.api.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Page;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by Manu on 8/9/2018.
 */
@Document
public class Catalog extends Entity<Catalog> {


    @Transient
    @NotEmpty(message = "Catalog Id cannot be empty",groups = {CreateGroup.class, DetailsGroup.class})
    String catalogId;

    @Indexed(unique = true)
    @NotEmpty(message = "Catalog Name cannot be empty", groups = {CreateGroup.class, DetailsGroup.class})
    private String catalogName;

    private String description;

    @Transient
    private Page<RootCategory> rootCategories;

    public Catalog() {
        super();
    }

    public Catalog(String externalId, String catalogName) {
        super(externalId);
        this.catalogName = catalogName;
    }

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

    void setExternalId() {
        this.catalogId = getExternalId();
    }

    @Override
    public Catalog merge(Catalog catalog) {
        switch(catalog.getGroup()) {
            case "DETAILS" :
                this.setExternalId(catalog.getExternalId());
                this.setCatalogName(catalog.getCatalogName());
                this.setDescription(catalog.getDescription());
                this.setActive(catalog.getActive());
                break;
        }

        return this;
    }

    @Override
    public Map<String, String> toMap() {
        Map<String, String> map = new LinkedHashMap<>();
        map.put("externalId", getExternalId());
        map.put("catalogName", getCatalogName());
        map.put("active", getActive());
        return map;
    }
}
