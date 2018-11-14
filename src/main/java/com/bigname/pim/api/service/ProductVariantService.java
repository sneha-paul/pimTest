package com.bigname.pim.api.service;

import com.bigname.pim.api.domain.Entity;
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

    ProductVariant cloneInstance(String productId, FindBy productIdFindBy, String channelId, String productVariantId, FindBy variantIdFindBy, Entity.CloneType type);

    Page<ProductVariant> getAll(String productId, FindBy productIdFindBy,  String channelId, int page, int size, Sort sort, boolean... activeRequired);

    List<ProductVariant> getAll(String productId, FindBy productIdFindBy, String channelId, Sort sort, boolean... activeRequired);

    Page<ProductVariant> getAll(String productId, FindBy productIdFindBy,  String channelId, String[] variantIds, FindBy variantIdFindBy, int page, int size, Sort sort, boolean... activeRequired);

    List<ProductVariant> getAll(String productId, FindBy productIdFindBy,  String channelId, String[] variantIds, FindBy variantIdFindBy, Sort sort, boolean... activeRequired);

    Page<ProductVariant> getAllWithExclusions(String productId, FindBy productIdFindBy,  String channelId, String[] excludedVariantIds, FindBy variantIdFindBy, int page, int size, Sort sort, boolean... activeRequired);

    List<ProductVariant> getAllWithExclusions(String productId, FindBy productIdFindBy,  String channelId, String[] excludedVariantIds, FindBy variantIdFindBy, Sort sort, boolean... activeRequired);

    Optional<ProductVariant> get(String productId, FindBy productIdFindBy, String channelId, String productVariantId, FindBy variantIdFindBy, boolean... activeRequired);

}
