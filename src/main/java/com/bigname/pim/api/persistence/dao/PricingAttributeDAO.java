package com.bigname.pim.api.persistence.dao;

import com.bigname.pim.api.domain.PricingAttribute;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Created by dona on 08-11-2018.
 */
public interface PricingAttributeDAO extends BaseDAO<PricingAttribute>, MongoRepository<PricingAttribute,String>, PricingAttributeRepository {
}
