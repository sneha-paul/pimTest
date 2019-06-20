package com.bigname.pim.api.persistence.dao;

import com.bigname.pim.api.domain.PricingAttribute;
import com.m7.xtreme.xcore.persistence.mongo.dao.GenericDAO;

/**
 * Created by dona on 08-11-2018.
 */
public interface PricingAttributeDAO extends GenericDAO<PricingAttribute>, PricingAttributeRepository {
}
