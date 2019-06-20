package com.bigname.pim.api.persistence.dao;

import com.bigname.pim.api.domain.ProductVariant;
import com.m7.xtreme.xcore.persistence.mongo.dao.GenericDAO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

/**
 * Created by sruthi on 20-09-2018.
 */
public interface ProductVariantDAO extends GenericDAO<ProductVariant>, ProductVariantRepository {
    Page<ProductVariant> findByProductIdAndChannelIdAndActiveIn(String productId, String channelId, String active[], Pageable pageable);
    List<ProductVariant> findByProductIdInAndChannelIdAndActiveIn(String productId[], String channelId, String active[]);
    Page<ProductVariant> findByProductIdAndChannelIdAndExternalIdInAndActiveIn(String productId, String channelId, String[] productVariantIds, String active[], Pageable pageable);
    Optional<ProductVariant> findByIdAndChannelIdAndActiveIn(String id, String channelId, String active[]);
    Optional<ProductVariant> findByExternalIdAndChannelIdAndActiveIn(String productVariantId, String channelId, String active[]);
    Optional<ProductVariant> findByProductIdAndChannelIdAndExternalIdAndActiveIn(String productId, String channelId, String productVariantId, String active[]);
    Page<ProductVariant> findByProductIdAndChannelIdAndExternalIdNotInAndActiveIn(String productId, String channelId, String[] productVariantIds, String active[], Pageable pageable);
    Page<ProductVariant> findByProductIdAndChannelIdAndProductVariantIdAndActiveIn(String productId, String channelId, String productVariantId, String active[], Pageable pageable);
    Optional<ProductVariant> findTopByProductIdAndChannelIdAndSequenceNumOrderBySubSequenceNumDesc(String productId, String channelId, int sequenceNum);
    List<ProductVariant> findByProductIdAndChannelIdAndSequenceNumAndSubSequenceNumGreaterThanEqualOrderBySubSequenceNumAsc(String productId, String channelId, long sequenceNum, int subSequenceNum);
}