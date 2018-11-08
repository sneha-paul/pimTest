package com.bigname.pim.api.persistence.dao;

import com.bigname.pim.api.domain.ProductCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

/**
 * Created by sruthi on 08-11-2018.
 */
public interface ProductCategoryDAO extends BaseAssociationDAO<ProductCategory>, MongoRepository<ProductCategory, String> {
    Page<ProductCategory> findByProductIdAndActiveIn(String productId, String active[], Pageable pageable);
    List<ProductCategory> findByProductId(String productId);
}
