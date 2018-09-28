package com.bigname.pim.api.service;

import com.bigname.pim.api.domain.*;
import com.bigname.pim.api.persistence.dao.CategoryDAO;
import com.bigname.pim.util.FindBy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;

import java.util.List;

/**
 * Created by sruthi on 29-08-2018.
 */
public interface CategoryService extends  BaseService<Category, CategoryDAO> {
    Page<Category> getAllWithExclusions(String[] excludedIds, FindBy findBy, int page, int size, Sort sort, boolean... activeRequired);
    Page<Category> getAvailableSubCategoriesForCategory(String id, FindBy findBy, int page, int size, Sort sort);
    Page<RelatedCategory> getSubCategories(String categoryId, FindBy findBy, int page, int size, Sort sort, boolean... activeRequired);
    RelatedCategory addSubCategory(String id, FindBy findBy1, String categoryId, FindBy findBy2);
    Page<CategoryProduct> getCategoryProducts(String websiteId, FindBy findBy, int page, int size, Sort sort, boolean... activeRequired);
    Page<Product> getAvailableProductsForCategory(String id, FindBy findBy, int page, int size, Sort sort);
    CategoryProduct addProduct(String id, FindBy findBy1, String productId, FindBy findBy2);

}

