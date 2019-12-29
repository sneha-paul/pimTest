package com.bigname.pim.core.service;

import com.bigname.pim.core.domain.*;
import com.bigname.pim.core.persistence.dao.mongo.CategoryDAO;
import com.m7.xtreme.xcore.service.BaseService;
import com.m7.xtreme.xcore.util.Archive;
import com.m7.xtreme.xcore.util.ID;
import com.m7.xtreme.xcore.util.Toggle;
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

    Page<Product> findAvailableProductsForCategory(ID<String> categoryId, String searchField, String keyword, Pageable pageable, boolean... activeRequired);

    Page<Category> findAvailableSubCategoriesForCategory(ID<String> categoryId, String searchField, String keyword, Pageable pageable, boolean... activeRequired);

    List<Map<String, Object>> getCategoryHierarchy(boolean... activeRequired);

    /**
     * Method to get available subCategories of a category in paginated format.
     *
     * @param categoryId Internal or External id of the Category
     * @param page page number
     * @param size page size
     * @param sort sort object
     * @return
     */
    Page<Category> getAvailableSubCategoriesForCategory(ID<String> categoryId, int page, int size, Sort sort, boolean... activeRequired);

    /**
     * Method to get subCategories of a Category in paginated format.
     *
     * @param categoryId Internal or External id of the Category
     * @param pageable The pageable object
     * @param activeRequired activeRequired Boolean flag
     * @return
     */
    Page<Map<String, Object>> getSubCategories(ID<String> categoryId, Pageable pageable, boolean... activeRequired);

    /**
     * Method to add subCategory for a category.
     *
     * @param categoryId Internal or External id of the Category
     * @param subCategoryId Internal or External id of the SubCategory
     * @return
     */
    RelatedCategory addSubCategory(ID<String> categoryId, ID<String> subCategoryId);

    boolean toggleSubCategory(ID<String> categoryId, ID<String> subCategoryId, Toggle active);

    boolean toggleProduct(ID<String> categoryId, ID<String> productId, Toggle active);

    Page<CategoryProduct> getCategoryProducts(ID<String> categoryId, int page, int size, Sort sort, boolean... activeRequired);

    /**
     * Method to get products of a Category in paginated format.
     *
     * @param categoryId Internal or External id of the Category
     * @param pageable The pageable object
     * @param activeRequired activeRequired Boolean flag
     * @return
     */
    Page<Map<String, Object>> getCategoryProducts(ID<String> categoryId, Pageable pageable, boolean... activeRequired);

    /**
     * Method to get available products of a category in paginated format.
     *
     * @param categoryId Internal or External id of the Category
     * @param page page number
     * @param size page size
     * @param sort sort object
     * @return
     */
    Page<Product> getAvailableProductsForCategory(ID<String> categoryId, int page, int size, Sort sort, boolean... activeRequired);

    /**
     * Method to add product for a category.
     *
     * @param categoryId Internal or External id of the Category
     * @param productId Internal or External id of the Product
     * @return
     */
    CategoryProduct addProduct(ID<String> categoryId, ID<String> productId);

    /**
     * Method to set the sequencing of two subCategories
     * @param categoryId Internal or External id of the Category
     * @param sourceId Internal or External id of the subCategory, whose sequencing needs to be set
     * @param destinationId Internal or External id of the subCategory at the destination slot
     * @return true if sequencing got modified, false otherwise
     */
    boolean setSubCategorySequence(ID<String> categoryId, ID<String> sourceId, ID<String> destinationId);

    /**
     * Method to set the sequencing of two products inside a category
     * @param categoryId Internal or External id of the Category
     * @param sourceId Internal or External id of the product, whose sequencing needs to be set
     * @param destinationId Internal or External id of the product at the destination slot
     * @return true if sequencing got modified, false otherwise
     */
    boolean setProductSequence(ID<String> categoryId, ID<String> sourceId, ID<String> destinationId);

    Page<Map<String, Object>> findAllSubCategories(ID<String> categoryId, String searchField, String keyword, Pageable pageable, boolean... activeRequired);

    Page<Map<String, Object>> findAllCategoryProducts(ID<String> categoryId, String searchField, String keyword, Pageable pageable, boolean... activeRequired);

    List<CategoryProduct> getAllCategoryProducts(ID<String> categoryId);

    boolean toggleCategory(ID<String> categoryId, Toggle toggle);

    List<RootCategory> getAllRootCategoriesWithCategoryId(ID<String> categoryId);

    List<RelatedCategory> getAllRelatedCategoriesWithSubCategoryId(ID<String> categoryId);

    List<ProductCategory> getAllProductCategoriesWithCategoryId(ID<String> categoryId);

    void updateRootCategory(RootCategory rootCategory);

    void updateRelatedCategory(RelatedCategory relatedCategory);

    void updateProductCategory(ProductCategory productCategory);

    void archiveCategoryAssociations(ID<String> categoryId, Archive archived, Category category);

    List<RelatedCategory> getAll();
}

