package com.bigname.pim.api.service.impl;

import com.bigname.pim.api.domain.Product;
import com.bigname.pim.api.persistence.dao.ProductDAO;
import com.bigname.pim.api.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.Validator;

/**
 * Created by sruthi on 19-09-2018.
 */
@Service
public class ProductServiceImpl extends BaseServiceSupport<Product, ProductDAO> implements ProductService {

    private ProductDAO productDAO;


    @Autowired
    public ProductServiceImpl(ProductDAO productDAO, Validator validator) {
        super(productDAO, "product", validator);
        this.productDAO = productDAO;
    }

    @Override
    protected Product createOrUpdate(Product product) {
        return productDAO.save(product);
    }

}
