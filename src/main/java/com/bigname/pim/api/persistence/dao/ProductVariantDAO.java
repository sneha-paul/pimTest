package com.bigname.pim.api.persistence.dao;

import com.bigname.pim.api.domain.ProductVariant;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Created by sruthi on 20-09-2018.
 */
public interface ProductVariantDAO extends BaseDAO<ProductVariant>, MongoRepository<ProductVariant, String> {
}