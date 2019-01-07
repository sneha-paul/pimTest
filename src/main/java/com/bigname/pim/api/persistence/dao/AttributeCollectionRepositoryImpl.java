package com.bigname.pim.api.persistence.dao;

import com.bigname.pim.api.domain.AttributeCollection;
import org.springframework.data.mongodb.core.MongoTemplate;

/**
 * @author Manu V NarayanaPrasad (manu@blacwood.com)
 * @since 1.0
 */
public class AttributeCollectionRepositoryImpl extends GenericRepositoryImpl<AttributeCollection> implements AttributeCollectionRepository {
    public AttributeCollectionRepositoryImpl(MongoTemplate mongoTemplate) {
        super(mongoTemplate, AttributeCollection.class);
    }
}
