package com.bigname.pim.api.service;

import com.bigname.core.service.BaseService;
import com.bigname.core.util.FindBy;
import com.bigname.core.util.Toggle;
import com.bigname.pim.api.domain.Category;
import com.bigname.pim.api.domain.CategoryProduct;
import com.bigname.pim.api.domain.Product;
import com.bigname.pim.api.domain.RelatedCategory;
import com.bigname.pim.api.persistence.dao.CategoryDAO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Map;

/**
 * Created by sruthi on 29-08-2018.
 */
public interface CategoryService extends BaseService<Category, CategoryDAO> {
//    Page<Category> getAllWithExclusions(String[] excludedIds, FindBy findBy, int page, int size, Sort sort, boolean... activeRequired);

    Page<Product> findAvailableProductsForCategory(String categoryId, FindBy externalId, String searchField, String keyword, Pageable pageable, boolean... activeRequired);

    Page<Category> findAvailableSubCategoriesForCategory(String categoryId, FindBy findBy, String searchField, String keyword, Pageable pageable, boolean... activeRequired);

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
     * @param pageable The pageable object
     * @param activeRequired activeRequired Boolean flag
     * @return
     */
    Page<Map<String, Object>> getSubCategories(String categoryId, FindBy findBy, Pageable pageable, boolean... activeRequired);

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

    boolean toggleSubCategory(String categoryId, FindBy categoryIdFindBy, String subCategoryId, FindBy subCategoryIdFindBy, Toggle active);

    boolean toggleProduct(String categoryId, FindBy categoryIdFindBy, String productId, FindBy productIdFindBy, Toggle active);

    Page<CategoryProduct> getCategoryProducts(String categoryId, FindBy findBy, int page, int size, Sort sort, boolean... activeRequired);

    /**
     * Method to get products of a Category in paginated format.
     *
     * @param categoryId Internal or External id of the Category
     * @param findBy Type of the category id, INTERNAL_ID or EXTERNAL_ID
     * @param pageable The pageable object
     * @param activeRequired activeRequired Boolean flag
     * @return
     */
    Page<Map<String, Object>> getCategoryProducts(String categoryId, FindBy findBy, Pageable pageable, boolean... activeRequired);

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
    Page<Product> getAvailableProductsForCategory(String id, FindBy findBy, int page, int size, Sort sort, boolean... activeRequired);

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

    /**
     * Method to set the sequencing of two subCategories
     * @param categoryId Internal or External id of the Category
     * @param categoryIdFindBy Type of the category id, INTERNAL_ID or EXTERNAL_ID
     * @param sourceId Internal or External id of the subCategory, whose sequencing needs to be set
     * @param sourceIdFindBy Type of the source subCategory id, INTERNAL_ID or EXTERNAL_ID
     * @param destinationId Internal or External id of the subCategory at the destination slot
     * @param destinationIdFindBy Type of the destination subCategory id, INTERNAL_ID or EXTERNAL_ID
     * @return true if sequencing got modified, false otherwise
     */
    boolean setSubCategorySequence(String categoryId, FindBy categoryIdFindBy, String sourceId, FindBy sourceIdFindBy, String destinationId, FindBy destinationIdFindBy);

    /**
     * Method to set the sequencing of two products inside a category
     * @param categoryId Internal or External id of the Category
     * @param categoryIdFindBy Type of the category id, INTERNAL_ID or EXTERNAL_ID
     * @param sourceId Internal or External id of the product, whose sequencing needs to be set
     * @param sourceIdFindBy Type of the source product id, INTERNAL_ID or EXTERNAL_ID
     * @param destinationId Internal or External id of the product at the destination slot
     * @param destinationIdFindBy Type of the destination product id, INTERNAL_ID or EXTERNAL_ID
     * @return true if sequencing got modified, false otherwise
     */
    boolean setProductSequence(String categoryId, FindBy categoryIdFindBy, String sourceId, FindBy sourceIdFindBy, String destinationId, FindBy destinationIdFindBy);

    Page<Map<String, Object>> findAllSubCategories(String categoryId, FindBy findBy, String searchField, String keyword, Pageable pageable, boolean... activeRequired);

    Page<Map<String, Object>> findAllCategoryProducts(String categoryId, FindBy findBy, String searchField, String keyword, Pageable pageable, boolean... activeRequired);

    List<CategoryProduct> getAllCategoryProducts(String categoryInternalId);
}

