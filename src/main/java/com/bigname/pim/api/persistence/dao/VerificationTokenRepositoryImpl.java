package com.bigname.pim.api.persistence.dao;

import com.bigname.pim.api.domain.VerificationToken;
import com.m7.xtreme.xcore.persistence.dao.mongo.GenericRepositoryImpl;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;

/**
 * @author Manu V NarayanaPrasad (manu@blacwood.com)
 * @since 1.0
 */
public class VerificationTokenRepositoryImpl extends GenericRepositoryImpl<VerificationToken, Criteria> implements VerificationTokenRepository {
    public VerificationTokenRepositoryImpl(MongoTemplate mongoTemplate) {
        super(mongoTemplate, VerificationToken.class);
    }
}
