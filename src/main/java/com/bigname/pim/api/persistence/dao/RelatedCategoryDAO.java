package com.bigname.pim.api.persistence.dao;

import com.bigname.pim.api.domain.RelatedCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

/**
 * Created by sruthi on 30-08-2018.
 */
public interface RelatedCategoryDAO extends MongoRepository<RelatedCategory, String> {

    Page<RelatedCategory> findByCategoryIdAndActiveIn(String categoryId, String active[], Pageable pageable);
    long countByCategoryId(String categoryId);
    List<RelatedCategory> findByCategoryId(String categoryId);
}
