package com.bigname.pim.api.service.impl;

import com.bigname.pim.api.domain.PricingAttribute;
import com.bigname.pim.api.persistence.dao.PricingAttributeDAO;
import com.bigname.pim.api.service.PricingAttributeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.Validator;

/**
 * Created by dona on 08-11-2018.
 */

@Service
public class PricingAttributeServiceImpl extends BaseServiceSupport<PricingAttribute, PricingAttributeDAO> implements PricingAttributeService {

    private  PricingAttributeDAO pricingAttributeDAO;

    @Autowired
    public PricingAttributeServiceImpl(PricingAttributeDAO pricingAttributeDAO, Validator validator){
        super(pricingAttributeDAO, "pricingAttribute", validator);
        this.pricingAttributeDAO = pricingAttributeDAO;

    }


    @Override
    public PricingAttribute createOrUpdate(PricingAttribute pricingAttribute) {return pricingAttributeDAO.save(pricingAttribute);}
}
