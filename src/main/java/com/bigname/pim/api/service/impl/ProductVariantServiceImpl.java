package com.bigname.pim.api.service.impl;

import com.bigname.pim.api.domain.ProductVariant;
import com.bigname.pim.api.persistence.dao.ProductVariantDAO;
import com.bigname.pim.api.service.ProductVariantService;
import org.springframework.stereotype.Service;

/**
 * Created by sruthi on 20-09-2018.
 */

@Service
public class ProductVariantServiceImpl extends BaseServiceSupport<ProductVariant, ProductVariantDAO> implements ProductVariantService {

    private ProductVariantDAO productVariantDAO;


    public ProductVariantServiceImpl(ProductVariantDAO productVariantDAO) {
        super(productVariantDAO, "productVariant");
        this.productVariantDAO = productVariantDAO;
    }

    @Override
    protected ProductVariant createOrUpdate(ProductVariant productVariant) {
        return productVariantDAO.save(productVariant);
    }

}
