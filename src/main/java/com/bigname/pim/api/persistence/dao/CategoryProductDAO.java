package com.bigname.pim.api.persistence.dao;

import com.bigname.pim.api.domain.CategoryProduct;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

/**
 * Created by sruthi on 26-09-2018.
 */
public interface CategoryProductDAO extends BaseAssociationDAO<CategoryProduct>, MongoRepository<CategoryProduct, String> {
    Page<CategoryProduct> findByCategoryIdAndActiveIn(String websiteId, String active[], Pageable pageable);
    List<CategoryProduct> findByCategoryId(String categoryId);
}
