package com.bigname.pim.api.persistence.dao;

import com.bigname.pim.api.domain.Catalog;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

/**
 * Created by Manu on 8/9/2018.
 */
public interface CatalogDAO extends BaseDAO<Catalog>, MongoRepository<Catalog, String> {
    List<Catalog> findByIdNotInAndActiveInOrderByCatalogNameAsc(String[] excludedIds, String active[]);
    List<Catalog> findByCatalogIdNotInAndActiveInOrderByCatalogNameAsc(String[] excludedIds, String active[]);
}
