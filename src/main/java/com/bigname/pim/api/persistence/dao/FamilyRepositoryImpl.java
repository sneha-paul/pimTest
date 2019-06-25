package com.bigname.pim.api.persistence.dao;

import com.bigname.pim.api.domain.Family;
import com.m7.xtreme.xcore.persistence.dao.mongo.GenericRepositoryImpl;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
/**
 * @author Manu V NarayanaPrasad (manu@blacwood.com)
 * @since 1.0
 */
public class FamilyRepositoryImpl extends GenericRepositoryImpl<Family, Criteria> implements FamilyRepository {
    public FamilyRepositoryImpl(MongoTemplate mongoTemplate) {
        super(mongoTemplate, Family.class);
    }
}
