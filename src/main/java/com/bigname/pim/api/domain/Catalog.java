package com.bigname.pim.api.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Page;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.UUID;

/**
 * Created by Manu on 8/9/2018.
 */
@Document
public class Catalog extends Entity<Catalog> {


    @Transient
    @NotEmpty(message = "Catalog Id cannot be empty")
    String catalogId;

    @Indexed(unique = true)
    @NotEmpty(message = "Catalog Name cannot be empty")
    private String catalogName;

    private String description;

    @Transient
    private Page<Category> rootCategories;

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

    public Page<Category> getRootCategories() {
        return rootCategories;
    }

    public void setRootCategories(Page<Category> rootCategories) {
        this.rootCategories = rootCategories;
    }

    void setExternalId() {
        this.catalogId = getExternalId();
    }

    public Catalog merge(Catalog catalog) {
        this.setExternalId(catalog.getExternalId());
        this.setCatalogName(catalog.getCatalogName());
        this.setDescription(catalog.getDescription());
        this.setActive(catalog.getActive());
        return this;
    }

}
