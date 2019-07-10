package com.bigname.pim.api.persistence.dao.mongo;

import com.bigname.pim.api.domain.Product;
import com.m7.xtreme.xcore.persistence.dao.mongo.GenericDAO;

/**
 * Created by sruthi on 19-09-2018.
 */
public interface ProductDAO extends GenericDAO<Product>, ProductRepository {
}

