package com.bigname.pim.core.service.impl;

import com.bigname.pim.core.domain.PricingAttribute;
import com.bigname.pim.core.persistence.dao.mongo.PricingAttributeDAO;
import com.bigname.pim.core.service.PricingAttributeService;
import com.m7.xtreme.xcore.service.impl.BaseServiceSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.Validator;

/**
 * Created by dona on 08-11-2018.
 */

@Service
public class PricingAttributeServiceImpl extends BaseServiceSupport<PricingAttribute, PricingAttributeDAO, PricingAttributeService> implements PricingAttributeService {

    private  PricingAttributeDAO pricingAttributeDAO;

    @Autowired
    public PricingAttributeServiceImpl(PricingAttributeDAO pricingAttributeDAO, Validator validator){
        super(pricingAttributeDAO, "pricingAttribute", validator);
        this.pricingAttributeDAO = pricingAttributeDAO;

    }

}
