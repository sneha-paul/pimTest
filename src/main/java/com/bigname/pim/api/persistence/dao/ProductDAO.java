package com.bigname.pim.api.persistence.dao;

import com.bigname.pim.api.domain.Product;
import com.m7.xcore.persistence.dao.GenericDAO;

/**
 * Created by sruthi on 19-09-2018.
 */
public interface ProductDAO extends GenericDAO<Product>, ProductRepository {
}

