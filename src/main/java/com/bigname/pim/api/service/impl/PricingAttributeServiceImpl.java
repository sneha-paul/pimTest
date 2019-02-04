package com.bigname.pim.api.service.impl;

import com.bigname.core.service.BaseServiceSupport;
import com.bigname.pim.api.domain.PricingAttribute;
import com.bigname.pim.api.persistence.dao.PricingAttributeDAO;
import com.bigname.pim.api.service.PricingAttributeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import javax.validation.Validator;
import java.time.LocalDateTime;
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

}
