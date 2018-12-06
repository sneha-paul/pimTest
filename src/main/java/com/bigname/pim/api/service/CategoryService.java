package com.bigname.pim.api.service;

import com.bigname.pim.api.domain.*;
import com.bigname.pim.api.persistence.dao.CategoryDAO;
import com.bigname.pim.util.FindBy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Map;

/**
 * Created by sruthi on 29-08-2018.
 */
public interface CategoryService extends  BaseService<Category, CategoryDAO> {
//    Page<Category> getAllWithExclusions(String[] excludedIds, FindBy findBy, int page, int size, Sort sort, boolean... activeRequired);

    List<Map<String, Object>> getCategoryHierarchy(boolean... activeRequired);

    /**
     * Method to get available subCategories of a category in paginated format.
     *
     * @param id Internal or External id of the Category
     * @param findBy Type of the category id, INTERNAL_ID or EXTERNAL_ID
     * @param page page number
     * @param size page size
     * @param sort sort object
     * @return
     */
    Page<Category> getAvailableSubCategoriesForCategory(String id, FindBy findBy, int page, int size, Sort sort, boolean... activeRequired);

    /**
     * Method to get subCategories of a Category in paginated format.
     *
     * @param categoryId Internal or External id of the Category
     * @param findBy Type of the category id, INTERNAL_ID or EXTERNAL_ID
     * @param page page number
     * @param size page size
     * @param sort sort Object
     * @param activeRequired activeRequired Boolean flag
     * @return
     */
    Page<RelatedCategory> getSubCategories(String categoryId, FindBy findBy, int page, int size, Sort sort, boolean... activeRequired);

    /**
     * Method to add subCategory for a category.
     *
     * @param id Internal or External id of the Category
     * @param findBy1 Type of the category id, INTERNAL_ID or EXTERNAL_ID
     * @param categoryId Internal or External id of the Category
     * @param findBy2 Type of the category id, INTERNAL_ID or EXTERNAL_ID
     * @return
     */
    RelatedCategory addSubCategory(String id, FindBy findBy1, String categoryId, FindBy findBy2);

    /**
     * Method to get products of a Category in paginated format.
     *
     * @param categoryId Internal or External id of the Category
     * @param findBy Type of the category id, INTERNAL_ID or EXTERNAL_ID
     * @param page page number
     * @param size page size
     * @param sort sort Object
     * @param activeRequired activeRequired Boolean flag
     * @return
     */
    Page<CategoryProduct> getCategoryProducts(String categoryId, FindBy findBy, int page, int size, Sort sort, boolean... activeRequired);

    /**
     * Method to get available products of a category in paginated format.
     *
     * @param id Internal or External id of the Category
     * @param findBy Type of the category id, INTERNAL_ID or EXTERNAL_ID
     * @param page page number
     * @param size page size
     * @param sort sort object
     * @return
     */
    Page<Product> getAvailableProductsForCategory(String id, FindBy findBy, int page, int size, Sort sort);

    /**
     * Method to add product for a category.
     *
     * @param id Internal or External id of the Category
     * @param findBy1 Type of the category id, INTERNAL_ID or EXTERNAL_ID
     * @param productId Internal or External id of the Product
     * @param findBy2 Type of the product id, INTERNAL_ID or EXTERNAL_ID
     * @return
     */
    CategoryProduct addProduct(String id, FindBy findBy1, String productId, FindBy findBy2);

}

