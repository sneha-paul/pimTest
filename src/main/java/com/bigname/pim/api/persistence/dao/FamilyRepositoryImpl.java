package com.bigname.pim.api.persistence.dao;

import com.bigname.pim.api.domain.Family;
import org.springframework.data.mongodb.core.MongoTemplate;

/**
 * @author Manu V NarayanaPrasad (manu@blacwood.com)
 * @since 1.0
 */
public class FamilyRepositoryImpl extends GenericRepositoryImpl<Family> implements FamilyRepository {
    public FamilyRepositoryImpl(MongoTemplate mongoTemplate) {
        super(mongoTemplate);
    }
}
