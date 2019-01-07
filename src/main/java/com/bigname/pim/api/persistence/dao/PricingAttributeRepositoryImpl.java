package com.bigname.pim.api.persistence.dao;

import com.bigname.pim.api.domain.PricingAttribute;
import org.springframework.data.mongodb.core.MongoTemplate;

/**
 * @author Manu V NarayanaPrasad (manu@blacwood.com)
 * @since 1.0
 */
public class PricingAttributeRepositoryImpl extends GenericRepositoryImpl<PricingAttribute> implements PricingAttributeRepository {
    public PricingAttributeRepositoryImpl(MongoTemplate mongoTemplate) {
        super(mongoTemplate, PricingAttribute.class);
    }
}
