package com.bigname.pim.api.service.impl;

import com.bigname.pim.api.domain.ProductVariant;
import com.bigname.pim.api.persistence.dao.ProductVariantDAO;
import com.bigname.pim.api.service.ProductVariantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.Validator;

/**
 * Created by sruthi on 20-09-2018.
 */

@Service
public class ProductVariantServiceImpl extends BaseServiceSupport<ProductVariant, ProductVariantDAO> implements ProductVariantService {

    private ProductVariantDAO productVariantDAO;


    @Autowired
    public ProductVariantServiceImpl(ProductVariantDAO productVariantDAO, Validator validator) {
        super(productVariantDAO, "productVariant", validator);
        this.productVariantDAO = productVariantDAO;
    }

    @Override
    protected ProductVariant createOrUpdate(ProductVariant productVariant) {
        return productVariantDAO.save(productVariant);
    }

}
