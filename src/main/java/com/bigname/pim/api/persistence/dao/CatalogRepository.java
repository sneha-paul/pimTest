package com.bigname.pim.api.persistence.dao;

import com.bigname.pim.api.domain.Catalog;
import com.bigname.pim.api.domain.Category;
import com.bigname.pim.api.domain.RootCategory;
import com.m7.xtreme.xcore.persistence.mongo.dao.GenericRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

/**
 * @author Manu V NarayanaPrasad (manu@blacwood.com)
 * @since 1.0
 */
public interface CatalogRepository extends GenericRepository<Catalog> {
    Page<Map<String, Object>> getRootCategories(String catalogId, Pageable pageable, boolean... activeRequired);

    Page<Map<String, Object>> findAllRootCategories(String catalogId, String searchField, String keyword, Pageable pageable, boolean[] activeRequired);

    List<RootCategory> getAllRootCategories(String catalogId, boolean... activeRequired);

    Page<Category> findAvailableRootCategoriesForCatalog(String catalogId, String searchField, String keyword, Pageable pageable, boolean... activeRequired);
}
