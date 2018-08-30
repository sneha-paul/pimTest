package com.bigname.pim.api.domain;

import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Page;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotEmpty;

/**
 * Created by sruthi on 29-08-2018.
 */
@Document
public class Category extends Entity<Category> {


    @Transient
    @NotEmpty(message = "Category Id cannot be empty")
    String categoryId;

    @Indexed(unique = true)
    @NotEmpty(message = "Category Name cannot be empty")
    private String categoryName;

    private String description;

    @Transient
    private Page<Category> categories;

    public Category() {
        super();
    }

    public Category(String externalId, String categoryName) {
        super(externalId);
        this.categoryName = categoryName;
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

    public Page<Category> getCategories() {
        return categories;
    }

    public void setCategories(Page<Category> categories) {
        this.categories = categories;
    }

    void setExternalId() {
        this.categoryId = getExternalId();
    }

    @Override
    public Category merge(Category category) {
        this.setExternalId(category.getExternalId());
        this.setCategoryName(category.getCategoryName());
        this.setDescription(category.getDescription());
        this.setActive(category.getActive());
        return this;
    }
}
