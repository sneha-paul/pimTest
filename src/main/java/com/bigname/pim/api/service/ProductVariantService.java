package com.bigname.pim.api.service;

import com.bigname.pim.api.domain.Entity;
import com.bigname.pim.api.domain.FileAsset;
import com.bigname.pim.api.domain.ProductVariant;
import com.bigname.pim.api.persistence.dao.ProductVariantDAO;
import com.bigname.pim.util.FindBy;
import com.bigname.pim.util.Toggle;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Optional;

/**
 * Created by sruthi on 20-09-2018.
 */
public interface ProductVariantService extends BaseService<ProductVariant, ProductVariantDAO> {

    boolean toggle(String productId, FindBy productIdFindBy,  String channelId, String productVariantId, FindBy variantIdFindBy, Toggle active);

    List<ProductVariant> create(List<ProductVariant> productVariants);

    List<ProductVariant> update(List<ProductVariant> productVariants);

    ProductVariant cloneInstance(String productId, FindBy productIdFindBy, String channelId, String productVariantId, FindBy variantIdFindBy, Entity.CloneType type);

    Page<ProductVariant> getAll(String productId, FindBy productIdFindBy,  String channelId, int page, int size, Sort sort, boolean... activeRequired);

    List<ProductVariant> getAll(String[] productIds, FindBy productIdFindBy, String channelId, boolean... activeRequired);

    List<ProductVariant> getAll(String productId, FindBy productIdFindBy, String channelId, Sort sort, boolean... activeRequired);

    Page<ProductVariant> getAll(String productId, FindBy productIdFindBy,  String channelId, String[] variantIds, FindBy variantIdFindBy, int page, int size, Sort sort, boolean... activeRequired);

    List<ProductVariant> getAll(String productId, FindBy productIdFindBy,  String channelId, String[] variantIds, FindBy variantIdFindBy, Sort sort, boolean... activeRequired);

    Page<ProductVariant> getAllWithExclusions(String productId, FindBy productIdFindBy,  String channelId, String[] excludedVariantIds, FindBy variantIdFindBy, int page, int size, Sort sort, boolean... activeRequired);

    List<ProductVariant> getAllWithExclusions(String productId, FindBy productIdFindBy,  String channelId, String[] excludedVariantIds, FindBy variantIdFindBy, Sort sort, boolean... activeRequired);

    Page<ProductVariant> findAll(String searchField, String keyword, String productId, FindBy findBy, String channelId, com.bigname.pim.util.Pageable pageable, boolean... activeRequired);

    Optional<ProductVariant> get(String productId, FindBy productIdFindBy, String channelId, String productVariantId, FindBy variantIdFindBy, boolean... activeRequired);

    Optional<ProductVariant> get(String productVariantId, FindBy findBy, String channelId, boolean... activeRequired);

    Page<ProductVariant> getProductVariantPricing(String productId, FindBy productIdFindBy, String channelId, String productVariantId, FindBy variantIdFindBy, int page, int size, Sort sort, boolean... activeRequired);

    ProductVariant addAssets(String productId, FindBy productIdFindBy, String channelId, String productVariantId, FindBy variantIdFindBy, String[] assetIds, FileAsset.AssetFamily assetFamily);

    ProductVariant deleteAsset(String productId, FindBy productIdFindBy, String channelId, String productVariantId, FindBy variantIdFindBy, String assetId, FileAsset.AssetFamily assetFamily);

    ProductVariant reorderAssets(String productId, FindBy productIdFindBy, String channelId, String productVariantId, FindBy variantIdFindBy, String[] assetIds, FileAsset.AssetFamily assetFamily);

    ProductVariant setAsDefaultAsset(String productId, FindBy productIdFindBy, String channelId, String productVariantId, FindBy variantIdFindBy, String assetId, FileAsset.AssetFamily assetFamily);
}
