package com.bigname.pim.api.service.impl;

import com.bigname.pim.api.domain.ProductFamily;
import com.bigname.pim.api.persistence.dao.ProductFamilyDAO;
import com.bigname.pim.api.service.ProductFamilyService;

/**
 * Created by manu on 9/4/18.
 */
public class ProductFamilyServiceImpl extends BaseServiceSupport<ProductFamily, ProductFamilyDAO> implements ProductFamilyService {

    private ProductFamilyDAO productFamilyDAO;

    public ProductFamilyServiceImpl(ProductFamilyDAO productFamilyDAO) {
        super(productFamilyDAO, "productFamily");
        this.productFamilyDAO = productFamilyDAO;
    }

    @Override
    protected ProductFamily createOrUpdate(ProductFamily productFamily) {
        return productFamilyDAO.save(productFamily);
    }
}
