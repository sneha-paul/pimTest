package com.bigname.pim.api.service;

import com.bigname.pim.api.domain.Category;
import com.bigname.pim.api.domain.RelatedCategory;
import com.bigname.pim.api.persistence.dao.CategoryDAO;
import com.bigname.pim.util.FindBy;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * Created by sruthi on 29-08-2018.
 */
public interface CategoryService extends  BaseService<Category, CategoryDAO> {
    List<Category> getAllWithExclusions(String[] excludedIds, FindBy findBy);
    List<Category> getAvailableSubCategoriesForCategory(String id, FindBy findBy);
    Page<Category> getRelatedCategory(String categoryId, FindBy findBy, int page, int size, boolean... activeRequired);
    RelatedCategory addCategory(String id, FindBy findBy1, String categoryId, FindBy findBy2);
}

