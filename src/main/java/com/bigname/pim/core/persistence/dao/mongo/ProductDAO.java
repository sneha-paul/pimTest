package com.bigname.pim.core.persistence.dao.mongo;

import com.bigname.pim.core.domain.Product;
import com.m7.xtreme.xcore.persistence.dao.mongo.GenericDAO;

/**
 * Created by sruthi on 19-09-2018.
 */
public interface ProductDAO extends GenericDAO<Product>, ProductRepository {
}

