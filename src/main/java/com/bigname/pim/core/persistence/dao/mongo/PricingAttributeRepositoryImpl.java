package com.bigname.pim.core.persistence.dao.mongo;

import com.bigname.pim.core.domain.PricingAttribute;
import com.m7.xtreme.xcore.persistence.dao.mongo.GenericRepositoryImpl;
import org.springframework.data.mongodb.core.query.Criteria;

/**
 * @author Manu V NarayanaPrasad (manu@blacwood.com)
 * @since 1.0
 */
public class PricingAttributeRepositoryImpl extends GenericRepositoryImpl<PricingAttribute, Criteria> implements PricingAttributeRepository {
    public PricingAttributeRepositoryImpl() {
        super(PricingAttribute.class);
    }
}
