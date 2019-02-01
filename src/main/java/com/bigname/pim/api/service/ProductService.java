package com.bigname.pim.api.service;

import com.bigname.pim.api.domain.*;
import com.bigname.pim.api.persistence.dao.ProductDAO;
import com.bigname.pim.util.FindBy;
import com.bigname.pim.util.Toggle;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Created by sruthi on 19-09-2018.
 */
public interface ProductService extends BaseService<Product, ProductDAO> {


    Page<Category> findAvailableCategoriesForProduct(String productId, FindBy externalId, String searchField, String keyword, Pageable pageable, boolean... activeRequired);

    /**
     * Method to get variants of a product in paginated format.
     *
     * @param productId Internal or External id of the Product
     * @param findBy Type of the product id, INTERNAL_ID or EXTERNAL_ID
     * @param channelId Id of the Channel to which the product belongs
     * @param page page number
     * @param size page size
     * @param sort sort Object
     * @param activeRequired activeRequired Boolean flag
     * @return
     */
    Page<ProductVariant> getProductVariants(String productId, FindBy findBy, String channelId, int page, int size, Sort sort, boolean... activeRequired);

    /**
     * Method to get variants of a product in list format.
     *
     * @param productId Internal or External id of the Product
     * @param findBy Type of the product id, INTERNAL_ID or EXTERNAL_ID
     * @param channelId Internal or External id of the Channel to which the product belongs
     * @param sort sort Object
     * @param activeRequired activeRequired Boolean flag
     * @return
     */
    List<ProductVariant> getProductVariants(String productId, FindBy findBy, String channelId, Sort sort, boolean... activeRequired);

    /**
     * Method to get variant of a product
     *
     * @param productId Internal or External id of the Product
     * @param productIdFindBy
     * @param channelId Internal or External id of the Channel to which the product belongs
     * @param productVariantId Internal or External id of the ProductVariant of the product
     * @param variantIdFindBy
     * @param activeRequired activeRequired Boolean flag
     * @return
     */
    Optional<ProductVariant> getProductVariant(String productId, FindBy productIdFindBy, String channelId, String productVariantId, FindBy variantIdFindBy, boolean... activeRequired);

    List<Product> create(List<Product> products);

    List<Product> update(List<Product> products);

    /**
     * Method to get available variants of a product in paginated format.
     *
     * @param productId Internal or External id of the Product
     * @param findBy Type of the product id, INTERNAL_ID or EXTERNAL_ID
     * @param channelId Internal or External id of the Channel to which the product belongs
     * @param pageNumber page number
     * @param pageSize page size
     * @param sort sort Object
     * @return
     */
    Page<Map<String, String>> getAvailableVariants(String productId, FindBy findBy, String channelId, Integer pageNumber, Integer pageSize, Sort sort);

    /**
     * Method to get categories of a product in paginated format.
     *
     * @param productId Internal or External id of the Product
     * @param findBy Type of the product id, INTERNAL_ID or EXTERNAL_ID
     * @param page page number
     * @param size page size
     * @param sort sort Object
     * @param activeRequired activeRequired Boolean flag
     * @return
     */
    Page<ProductCategory> getProductCategories(String productId, FindBy findBy, int page, int size, Sort sort, boolean... activeRequired);

    /**
     * Method to get available categories of a product in paginated format.
     *
     * @param productId Internal or External id of the Product
     * @param findBy Type of the product id, INTERNAL_ID or EXTERNAL_ID
     * @param page page number
     * @param size page size
     * @param sort sort object
     * @return
     */
    Page<Category> getAvailableCategoriesForProduct(String productId, FindBy findBy, int page, int size, Sort sort, boolean... activeRequired);

    /**
     * Method to add category for a product.
     *
     * @param productId Internal or External id of the Product
     * @param productIdFindBy Type of the product id, INTERNAL_ID or EXTERNAL_ID
     * @param categoryId Internal or External id of the Category
     * @param categoryIdFindBy Type of the category id, INTERNAL_ID or EXTERNAL_ID
     * @return
     */
    ProductCategory addCategory(String productId, FindBy productIdFindBy, String categoryId, FindBy categoryIdFindBy);

    boolean toggleProductCategory(String productId, FindBy productIdFindBy, String categoryId, FindBy categoryIdFindBy, Toggle active);

    /**
     * Method to get categories of a Product in paginated format.
     *
     * @param productId Internal or External id of the Product
     * @param findBy Type of the product id, INTERNAL_ID or EXTERNAL_ID
     * @param pageable The pageable object
     * @param activeRequired activeRequired Boolean flag
     * @return
     */
    Page<Map<String,Object>> getCategories(String productId, FindBy findBy, Pageable pageable, boolean... activeRequired);

    Product addAssets(String productId, FindBy findBy, String channelId, String[] assetIds, FileAsset.AssetFamily assetFamily);

    Product reorderAssets(String productId, FindBy findBy, String channelId, String[] assetIds, FileAsset.AssetFamily assetFamily);

    Product setAsDefaultAsset(String productId, FindBy findBy, String channelId, String assetId, FileAsset.AssetFamily assetFamily);

    Product deleteAsset(String productId, FindBy findBy, String channelId, String assetId, FileAsset.AssetFamily assetFamily);

    Page<Map<String, Object>> findAllProductCategories(String productId, FindBy findBy, String searchField, String keyword, Pageable pageable, boolean... activeRequired);

}