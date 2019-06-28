package com.bigname.pim.api.service;

import com.bigname.pim.api.domain.FileAsset;
import com.bigname.pim.api.domain.ProductVariant;
import com.bigname.pim.api.persistence.dao.ProductVariantDAO;
import com.m7.xtreme.xcore.domain.Entity;
import com.m7.xtreme.xcore.service.BaseService;
import com.m7.xtreme.xcore.util.FindBy;
import com.m7.xtreme.xcore.util.ID;
import com.m7.xtreme.xcore.util.Toggle;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Created by sruthi on 20-09-2018.
 */
public interface ProductVariantService extends BaseService<ProductVariant, ProductVariantDAO> {

    boolean toggle(ID<String> productId, String channelId, ID<String> productVariantId, Toggle active);

    ProductVariant cloneInstance(ID<String> productId, String channelId, ID<String> productVariantId, Entity.CloneType type);

    Page<ProductVariant> getAll(ID<String> productId,  String channelId, int page, int size, Sort sort, boolean... activeRequired);

    List<ProductVariant> getAll(List<ID<String>> productIds, String channelId, boolean... activeRequired);

    List<ProductVariant> getAll(ID<String> productId, String channelId, Sort sort, boolean... activeRequired);

    Page<ProductVariant> getAll(ID<String> productId,  String channelId, List<ID<String>> productVariantIds, int page, int size, Sort sort, boolean... activeRequired);

    List<ProductVariant> getAll(ID<String> productId,  String channelId, List<ID<String>> productVariantIds, Sort sort, boolean... activeRequired);

    Page<ProductVariant> getAllWithExclusions(ID<String> productId,  String channelId, List<ID<String>> excludedVariantIds, int page, int size, Sort sort, boolean... activeRequired);

    List<ProductVariant> getAllWithExclusions(ID<String> productId,  String channelId, List<ID<String>> excludedVariantIds, Sort sort, boolean... activeRequired);

    Page<ProductVariant> findAll(String searchField, String keyword, ID<String> productId, String channelId, Pageable pageable, boolean... activeRequired);

    <I> Optional<ProductVariant> get(ID<I> productId, String channelId, ID<I> productVariantId, boolean... activeRequired);

    Optional<ProductVariant> get(ID<String> productVariantId, String channelId, boolean... activeRequired);

    Page<ProductVariant> getProductVariantPricing(ID<String> productId, String channelId, ID<String> productVariantId, int page, int size, Sort sort, boolean... activeRequired);

    ProductVariant addAssets(ID<String> productId, String channelId, ID<String> productVariantId, List<ID<String>> assetIds, FileAsset.AssetFamily assetFamily);

    ProductVariant deleteAsset(ID<String> productId, String channelId, ID<String> productVariantId, ID<String> assetId, FileAsset.AssetFamily assetFamily);

    ProductVariant reorderAssets(ID<String> productId, String channelId, ID<String> productVariantId, List<ID<String>> assetIds, FileAsset.AssetFamily assetFamily);

    ProductVariant setAsDefaultAsset(ID<String> productId, String channelId, ID<String> productVariantId, ID<String> assetId, FileAsset.AssetFamily assetFamily);

    List<Map<String,Object>> getAll();

    boolean setProductVariantsSequence(ID<String> productId, ID<String> channelId, ID<String> sourceId, ID<String> destinationId);

}
