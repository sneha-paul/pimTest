package com.bigname.pim.api.persistence.dao;

import com.bigname.pim.api.domain.RootCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

/**
 * Created by sruthi on 31-08-2018.
 */
public interface RootCategoryDAO extends MongoRepository<RootCategory, String> {

    Page<RootCategory> findByCatalogIdAndActiveIn(String catalogId, String active[], Pageable pageable);
    long countByCatalogId(String catalogId);
    List<RootCategory> findByCatalogId(String catalogId);

}
