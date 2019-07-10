package com.bigname.pim.api.persistence.dao.mongo;

import com.bigname.pim.api.domain.AttributeCollection;
import com.m7.xtreme.xcore.persistence.dao.GenericRepository;
import org.springframework.data.mongodb.core.query.Criteria;

/**
 * @author Manu V NarayanaPrasad (manu@blacwood.com)
 * @since 1.0
 */
public interface AttributeCollectionRepository extends GenericRepository<AttributeCollection, Criteria> {
}
