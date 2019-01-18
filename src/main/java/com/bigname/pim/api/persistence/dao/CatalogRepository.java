package com.bigname.pim.api.persistence.dao;

import com.bigname.pim.api.domain.Catalog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Map;

/**
 * @author Manu V NarayanaPrasad (manu@blacwood.com)
 * @since 1.0
 */
public interface CatalogRepository extends GenericRepository<Catalog> {
    Page<Map<String, Object>> getRootCategories(String catalogId, Pageable pageable);

    Page<Map<String, Object>> findAllRootCategories(String catalogId, String searchField, String keyword, Pageable pageable, boolean[] activeRequired);
}
