package com.bigname.pim.api.persistence.dao;

import com.bigname.pim.api.domain.AttributeCollection;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * @author Manu V NarayanaPrasad (manu@blacwood.com)
 * @since 1.0
 */
public interface AttributeCollectionDAO extends BaseDAO<AttributeCollection>, MongoRepository<AttributeCollection, String>, AttributeCollectionRepository {
}
