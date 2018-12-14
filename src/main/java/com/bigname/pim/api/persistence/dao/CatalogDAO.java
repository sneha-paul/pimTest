package com.bigname.pim.api.persistence.dao;

import com.bigname.pim.api.domain.Catalog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Map;

/**
 * Created by Manu on 8/9/2018.
 */
public interface CatalogDAO extends BaseDAO<Catalog>, MongoRepository<Catalog, String>, CatalogRepository {
}
