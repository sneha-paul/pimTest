package com.bigname.pim.api.persistence.dao;

import com.bigname.pim.api.domain.Catalog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Created by Manu on 8/9/2018.
 */
public interface CatalogDAO extends BaseDAO<Catalog>, MongoRepository<Catalog, String> {
    Page<Catalog> findByIdNotInAndActiveIn(String[] excludedIds, String active[], Pageable pageable);
    Page<Catalog> findByCatalogIdNotInAndActiveIn(String[] excludedIds, String active[], Pageable pageable);
}
