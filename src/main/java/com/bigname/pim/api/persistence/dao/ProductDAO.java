package com.bigname.pim.api.persistence.dao;

import com.bigname.pim.api.domain.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Created by sruthi on 19-09-2018.
 */
public interface ProductDAO extends BaseDAO<Product>, MongoRepository<Product, String>, ProductRepository {
}

