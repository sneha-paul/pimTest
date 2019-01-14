package com.bigname.pim.api.service.impl;

import com.bigname.pim.api.domain.PricingAttribute;
import com.bigname.pim.api.persistence.dao.PricingAttributeDAO;
import com.bigname.pim.api.service.PricingAttributeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import javax.validation.Validator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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

    @Override
    public PricingAttribute createOrUpdate(PricingAttribute pricingAttribute) {return pricingAttributeDAO.save(pricingAttribute);}

    @Override
    public List<PricingAttribute> findAll(Map<String, Object> criteria) {
        return dao.findAll(criteria);
    }

    @Override
    public List<PricingAttribute> findAll(Criteria criteria) {
        return dao.findAll(criteria);
    }

    @Override
    public List<PricingAttribute> findAll(String searchField, String keyword, com.bigname.pim.util.Pageable pageable, boolean... activeRequired) {
        return pricingAttributeDAO.findAll(searchField, keyword, pageable, activeRequired);
    }

    @Override
    public Optional<PricingAttribute> findOne(Map<String, Object> criteria) {
        return dao.findOne(criteria);
    }

    @Override
    public Optional<PricingAttribute> findOne(Criteria criteria) {
        return dao.findOne(criteria);
    }
}
