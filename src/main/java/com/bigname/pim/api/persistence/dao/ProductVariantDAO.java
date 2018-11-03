package com.bigname.pim.api.persistence.dao;

import com.bigname.pim.api.domain.ProductVariant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

/**
 * Created by sruthi on 20-09-2018.
 */
public interface ProductVariantDAO extends BaseDAO<ProductVariant>, MongoRepository<ProductVariant, String>, ProductVariantRepository {
    Page<ProductVariant> findAllByProductIdAndChannelIdAndActiveIn(String productId, String channelId, String active[], Pageable pageable);
    Optional<ProductVariant> findByProductIdAndChannelIdAndProductVariantIdAndActiveIn(String productId, String channelId, String productVariantId, String active[]);
}