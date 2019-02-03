package com.bigname.pim.api.service;

import com.bigname.core.service.BaseService;
import com.bigname.pim.api.domain.PricingAttribute;
import com.bigname.pim.api.persistence.dao.PricingAttributeDAO;

import java.util.List;

/**
 * Created by dona on 08-11-2018.
 */
public interface PricingAttributeService extends BaseService<PricingAttribute,PricingAttributeDAO> {

    List<PricingAttribute> create(List<PricingAttribute> pricingAttributes);

    List<PricingAttribute> update(List<PricingAttribute> pricingAttributes);
}
