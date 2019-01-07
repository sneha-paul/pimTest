package com.bigname.pim.api.persistence.dao;

import com.bigname.pim.api.domain.ProductVariant;
import org.springframework.data.mongodb.core.MongoTemplate;

/**
 * @author Manu V NarayanaPrasad (manu@blacwood.com)
 * @since 1.0
 */
public class ProductVariantRepositoryImpl extends GenericRepositoryImpl<ProductVariant> implements ProductVariantRepository {
    public ProductVariantRepositoryImpl(MongoTemplate mongoTemplate) {
        super(mongoTemplate, ProductVariant.class);
    }
}
