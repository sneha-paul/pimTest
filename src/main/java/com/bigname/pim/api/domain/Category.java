package com.bigname.pim.api.domain;

import com.bigname.core.domain.Entity;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Page;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotEmpty;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import static com.bigname.pim.api.domain.Catalog.Property.ACTIVE;
import static com.bigname.pim.api.domain.Catalog.Property.DISCONTINUED;
import static com.bigname.pim.api.domain.Category.Property.*;

/**
 * Created by sruthi on 29-08-2018.
 */
@Document
public class Category extends Entity<Category> {


    @Transient
    @NotEmpty(message = "Category Id cannot be empty", groups = {CreateGroup.class, DetailsGroup.class})
    // @Pattern(regexp = "[" + ALPHA + NUMERIC + UNDERSCORE + "]", message = "category.categoryId.invalid")
    private String categoryId;

    @NotEmpty(message = "Category Name cannot be empty", groups = {CreateGroup.class, DetailsGroup.class})
    // @Pattern(regexp = "[" + ALPHA + NUMERIC + SPACE + "]", message = "category.categoryName.invalid")
    private String categoryName;

    private String description;

    private String longDescription;

    @NotEmpty(message = "SEO Meta Title cannot be empty", groups = {SeoGroup.class})
    private String metaTitle;

    @NotEmpty(message = "SEO Meta Description cannot be empty", groups = {SeoGroup.class})
    private String metaDescription;

    @NotEmpty(message = "SEO Meta Keywords cannot be empty", groups = {SeoGroup.class})
    private String metaKeywords;

    @Transient
    private Page<RelatedCategory> subCategories;

    public Category() {
        super();
    }

    public Category(Map<String, Object> properties) {
        this.setCategoryId((String) properties.get(CATEGORY_ID.name()));
        this.setCategoryName((String) properties.get(CATEGORY_NAME.name()));
        this.setDescription((String) properties.get(DESCRIPTION.name()));
        this.setActive((String) properties.get(ACTIVE.name()));
        this.setDiscontinued((String) properties.get(DISCONTINUED.name()));
    }

    public String getCategoryId() {
        return getExternalId();
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
        setExternalId(categoryId);
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Page<RelatedCategory> getSubCategories() {
        return subCategories;
    }

    public void setSubCategories(Page<RelatedCategory> subCategories) {
        this.subCategories = subCategories;
    }

    public String getLongDescription() {
        return longDescription;
    }

    public void setLongDescription(String longDescription) {
        this.longDescription = longDescription;
    }

    public String getMetaTitle() {
        return metaTitle;
    }

    public void setMetaTitle(String metaTitle) {
        this.metaTitle = metaTitle;
    }

    public String getMetaDescription() {
        return metaDescription;
    }

    public void setMetaDescription(String metaDescription) {
        this.metaDescription = metaDescription;
    }

    public String getMetaKeywords() {
        return metaKeywords;
    }

    public void setMetaKeywords(String metaKeywords) {
        this.metaKeywords = metaKeywords;
    }

    protected void setExternalId() {
        this.categoryId = getExternalId();
    }

    @Override
    public void orchestrate() {
        super.orchestrate();
    }

    @Override
    public Category cloneInstance() {
        Category clone = new Category();
        clone.setActive("N");
        clone.setDiscontinued("N");
        clone.setExternalId(cloneValue(getExternalId()));
        clone.setCategoryName(cloneValue(getCategoryName()));
        clone.setDescription(cloneValue(getDescription()));
        return clone;
    }

    @Override
    public Category merge(Category category) {
        for (String group : category.getGroup()) {
            switch (group) {
                case "DETAILS":
                    this.setExternalId(category.getExternalId());
                    this.setCategoryName(category.getCategoryName());
                    this.setDescription(category.getDescription());
                    this.setLongDescription(category.getLongDescription());
                    this.setActive(category.getActive());
                    mergeBaseProperties(category);
                    break;
                case "SEO":
                    this.setMetaTitle(category.getMetaTitle());
                    this.setMetaDescription(category.getMetaDescription());
                    this.setMetaKeywords(category.getMetaKeywords());
                    break;
            }
        }

        return this;
    }

    @Override
    public Map<String, String> toMap() {
        Map<String, String> map = new LinkedHashMap<>();
        map.put("categoryName", getCategoryName());
        map.putAll(getBasePropertiesMap());
        return map;
    }


    public interface SeoGroup {
    }

    @Override
    public boolean equals(Category category) {
        return this.getId().equals(category.getId())
                && this.getCategoryId().equals(category.getCategoryId())
                && this.getCategoryName().equals(category.getCategoryName())
                && this.getDescription().equals(category.getDescription());
    }

    @Override
    public boolean equals(Map<String, Object> categoryMap) {
        return this.getId().equals(categoryMap.get(Property.ID.name()))
                && this.getCategoryId().equals(categoryMap.get(Property.CATEGORY_ID.name()))
                && this.getCategoryName().equals(categoryMap.get(Property.CATEGORY_NAME.name()))
                && this.getDescription().equals(categoryMap.get(Property.DESCRIPTION.name()));

    }

      /*&& this.getLongDescription().equals(categoryMap.get(Property.LONG_DESCRIPTION.name()))
            && this.getMetaTitle().equals(categoryMap.get(Property.META_TITLE.name()))
            && this.getMetaDescription().equals(categoryMap.get(Property.META_DESCRIPTION.name()))
            && this.getMetaKeywords().equals(categoryMap.get(Property.META_KEYWORDS.name()));*/

    public enum Property{
        ID, CATEGORY_ID, CATEGORY_NAME, DESCRIPTION, ACTIVE, DISCONTINUED
    }
    /*LONG_DESCRIPTION, META_TITLE, META_DESCRIPTION, META_KEYWORDS,*/

    public Map<String, Object> diff(Category category, boolean... ignoreInternalId) {
        boolean _ignoreInternalId = ignoreInternalId != null && ignoreInternalId.length > 0 && ignoreInternalId[0];
        Map<String, Object> diff = new HashMap<>();
        if (!_ignoreInternalId && !this.getId().equals(category.getId())) {
            diff.put("internalId", category.getId());
        }
        if (!this.getCategoryName().equals(category.getCategoryName())) {
            diff.put("categoryName", category.getCategoryName());
            }
        if (!this.getDescription().equals(category.getDescription())) {
            diff.put("description", category.getDescription());
        }
        /*if (!this.getLongDescription().equals(category.getLongDescription())) {
            diff.put("longDescription", category.getLongDescription());
        }
        if (!this.getMetaTitle().equals(category.getMetaTitle())) {
            diff.put("metaTitle", category.getMetaTitle());
                        }
        if (!this.getMetaDescription().equals(category.getMetaDescription())) {
            diff.put("metaDescription", category.getMetaDescription());
        }
        if (!this.getMetaKeywords().equals(category.getMetaKeywords())) {
            diff.put("metaKeywords", category.getMetaKeywords());

        }*/


            return diff;
        }
    }

