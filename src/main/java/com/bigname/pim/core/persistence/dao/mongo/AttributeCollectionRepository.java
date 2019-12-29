package com.bigname.pim.core.persistence.dao.mongo;

import com.bigname.pim.core.domain.AttributeCollection;
import com.m7.xtreme.xcore.persistence.dao.GenericRepository;
import org.springframework.data.mongodb.core.query.Criteria;

/**
 * @author Manu V NarayanaPrasad (manu@blacwood.com)
 * @since 1.0
 */
public interface AttributeCollectionRepository extends GenericRepository<AttributeCollection, Criteria> {
}
