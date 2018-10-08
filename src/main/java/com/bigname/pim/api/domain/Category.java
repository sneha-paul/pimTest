package com.bigname.pim.api.domain;

import com.bigname.common.util.ValidationUtil;
import org.javatuples.Pair;
import org.javatuples.Triplet;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Page;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.validation.Errors;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by sruthi on 29-08-2018.
 */
@Document
public class Category extends Entity<Category> {


    @Transient
    @NotEmpty(message = "Category Id cannot be empty", groups = {CreateGroup.class, DetailsGroup.class})
    private String categoryId;

    @Indexed(unique = true)
    @NotEmpty(message = "Category Name cannot be empty", groups = {CreateGroup.class, DetailsGroup.class})
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

    /*public Category(String externalId, String categoryName) {
        super(externalId);
        this.categoryName = categoryName;
    }
*/
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

    void setExternalId() {
        this.categoryId = getExternalId();
    }


    @Override
    public Category cloneInstance() {
        Category clone = new Category();
        clone.setActive("N");
        clone.setExternalId(cloneValue(getExternalId()));
        clone.setCategoryName(cloneValue(getCategoryName()));
        clone.setDescription(cloneValue(getDescription()));
        return clone;
    }

    @Override
    public Category merge(Category category) {
        switch(category.getGroup()) {
            case "DETAILS":
                this.setExternalId(category.getExternalId());
                this.setCategoryName(category.getCategoryName());
                this.setDescription(category.getDescription());
                this.setLongDescription(category.getLongDescription());
                this.setActive(category.getActive());
                break;
            case "SEO":
                this.setMetaTitle(category.getMetaTitle());
                this.setMetaDescription(category.getMetaDescription());
                this.setMetaKeywords(category.getMetaKeywords());
                break;
        }

        return this;
    }

    @Override
    public Map<String, String> toMap() {
        Map<String, String> map = new LinkedHashMap<>();
        map.put("externalId", getExternalId());
        map.put("categoryName", getCategoryName());
        map.put("active", getActive());
        return map;
    }



    public interface SeoGroup {}
}
