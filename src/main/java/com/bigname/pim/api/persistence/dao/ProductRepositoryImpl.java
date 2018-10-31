package com.bigname.pim.api.persistence.dao;

import com.bigname.pim.api.domain.Product;
import org.springframework.data.mongodb.core.MongoTemplate;

/**
 * @author Manu V NarayanaPrasad (manu@blacwood.com)
 * @since 1.0
 */
public class ProductRepositoryImpl extends GenericRepositoryImpl<Product> implements ProductRepository {

    public ProductRepositoryImpl(MongoTemplate mongoTemplate) {
        super(mongoTemplate);
    }
}
