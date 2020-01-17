package com.bigname.pim.core.service;

import com.bigname.pim.core.domain.*;
import com.bigname.pim.core.persistence.dao.mongo.ProductDAO;
import com.m7.xtreme.xcore.service.BaseService;
import com.m7.xtreme.xcore.util.Criteria;
import com.m7.xtreme.xcore.util.ID;
import com.m7.xtreme.xcore.util.Toggle;
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


    Page<Category> findAvailableCategoriesForProduct(ID<String> productId, String searchField, String keyword, Pageable pageable, boolean... activeRequired);

    /**
     * Method to get variants of a product in paginated format.
     *
     * @param productId Internal or External id of the Product
     * @param channelId Id of the Channel to which the product belongs
     * @param page page number
     * @param size page size
     * @param sort sort Object
     * @param activeRequired activeRequired Boolean flag
     * @return
     */
    Page<ProductVariant> getProductVariants(ID<String> productId, String channelId, int page, int size, Sort sort, boolean... activeRequired);

    /**
     * Method to get variants of a product in list format.
     *
     * @param productId Internal or External id of the Product
     * @param channelId Internal or External id of the Channel to which the product belongs
     * @param sort sort Object
     * @param activeRequired activeRequired Boolean flag
     * @return
     */
    List<ProductVariant> getProductVariants(ID<String> productId, String channelId, Sort sort, boolean... activeRequired);

    /**
     * Method to get variant of a product
     *
     * @param productId Internal or External id of the Product
     * @param channelId Internal or External id of the Channel to which the product belongs
     * @param productVariantId Internal or External id of the ProductVariant of the product
     * @param activeRequired activeRequired Boolean flag
     * @return
     */
    Optional<ProductVariant> getProductVariant(ID<String> productId, String channelId, ID<String> productVariantId, boolean... activeRequired);

    /**
     * Method to get available variants of a product in paginated format.
     *
     * @param productId Internal or External id of the Product
     * @param channelId Internal or External id of the Channel to which the product belongs
     * @param sort sort Object
     * @return
     */
    Page<Map<String, String>> getAvailableVariants(ID<String> productId, String channelId, int page, int size, Sort sort);

    /**
     * Method to get categories of a product in paginated format.
     *
     * @param productId Internal or External id of the Product
     * @param page page number
     * @param size page size
     * @param sort sort Object
     * @param activeRequired activeRequired Boolean flag
     * @return
     */
    Page<ProductCategory> getProductCategories(ID<String> productId, int page, int size, Sort sort, boolean... activeRequired);

    /**
     * Method to get available categories of a product in paginated format.
     *
     * @param productId Internal or External id of the Product
     * @param page page number
     * @param size page size
     * @param sort sort object
     * @return
     */
    Page<Category> getAvailableCategoriesForProduct(ID<String> productId, int page, int size, Sort sort, boolean... activeRequired);

    /**
     * Method to add category for a product.
     *
     * @param productId Internal or External id of the Product
     * @param categoryId Internal or External id of the Category
     * @return
     */
    ProductCategory addCategory(ID<String> productId, ID<String> categoryId);

    boolean toggleProductCategory(ID<String> productId, ID<String> categoryId, Toggle active);

    /**
     * Method to get categories of a Product in paginated format.
     *
     * @param productId Internal or External id of the Product
     * @param pageable The pageable object
     * @param activeRequired activeRequired Boolean flag
     * @return
     */
    Page<Map<String,Object>> getCategories(ID<String> productId, Pageable pageable, boolean... activeRequired);

    Product addAssets(ID<String> productId, String channelId, List<ID<String>> assetIds, FileAsset.AssetFamily assetFamily);

    Product reorderAssets(ID<String> productId, String channelId, List<ID<String>> assetIds, FileAsset.AssetFamily assetFamily);

    Product setAsDefaultAsset(ID<String> productId, String channelId, ID<String> assetId, FileAsset.AssetFamily assetFamily);

    Product deleteAsset(ID<String> productId, String channelId, ID<String> assetId, FileAsset.AssetFamily assetFamily);

    Page<Map<String, Object>> findAllProductCategories(ID<String> productId, String searchField, String keyword, Pageable pageable, boolean... activeRequired);

    boolean toggleProduct(ID<String> productId, Toggle toggle);
    
    List<CategoryProduct> getAllCategoryProductsWithProductId(ID<String> productId);

    void updateCategoryProduct(CategoryProduct categoryProduct);

    List<Map<String, Object>> findAllVariants(Criteria criteria, boolean... activeRequired);

    Product create(Product product, ID.Type type);
}