package com.bigname.pim.api.service;

import com.bigname.pim.api.domain.Category;
import com.bigname.pim.api.domain.Product;
import com.bigname.pim.api.domain.ProductCategory;
import com.bigname.pim.api.domain.ProductVariant;
import com.bigname.pim.api.persistence.dao.ProductDAO;
import com.bigname.pim.util.FindBy;
import org.javatuples.Pair;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Created by sruthi on 19-09-2018.
 */
public interface ProductService extends BaseService<Product, ProductDAO> {
    /**
     * Method to get variants of a product in paginated format.
     *
     * @param productId Internal or External id of the Product
     * @param findBy Type of the product id, INTERNAL_ID or EXTERNAL_ID
     * @param channelId
     * @param page
     * @param size
     * @param sort
     * @param activeRequired
     * @return
     */
    Page<ProductVariant> getProductVariants(String productId, FindBy findBy, String channelId, int page, int size, Sort sort, boolean... activeRequired);
    List<ProductVariant> getProductVariants(String productId, FindBy findBy, String channelId, Sort sort, boolean... activeRequired);
    Optional<ProductVariant> getProductVariant(String productId, FindBy productIdFindBy, String channelId, String productVariantId, FindBy variantIdFindBy, boolean... activeRequired);
    Page<Map<String, String>> getAvailableVariants(String productId, FindBy findBy, String channelId, Integer pageNumber, Integer pageSize, Sort sort);
    Page<ProductCategory> getProductCategories(String productId, FindBy findBy, int page, int size, Sort sort, boolean... activeRequired);
    Page<Category> getAvailableCategoriesForProduct(String productId, FindBy findBy, int page, int size, Sort sort);
    ProductCategory addCategory(String productId, FindBy productIdFindBy, String categoryId, FindBy categoryIdFindBy);
}