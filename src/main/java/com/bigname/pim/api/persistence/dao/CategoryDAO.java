package com.bigname.pim.api.persistence.dao;

import com.bigname.pim.api.domain.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

/**
 * Created by sruthi on 29-08-2018.
 */
public interface CategoryDAO extends BaseDAO<Category>, MongoRepository<Category, String> {
    Page<Category> findByIdNotInAndActiveIn(String[] excludedIds, String active[], Pageable pageable);
    Page<Category> findByCategoryIdNotInAndActiveIn(String[] excludedIds, String active[], Pageable pageable);
}
