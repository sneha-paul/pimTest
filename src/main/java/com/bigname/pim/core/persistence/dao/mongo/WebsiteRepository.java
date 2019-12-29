package com.bigname.pim.core.persistence.dao.mongo;

import com.bigname.pim.core.domain.Catalog;
import com.bigname.pim.core.domain.Website;
import com.bigname.pim.core.domain.WebsiteCatalog;
import com.m7.xtreme.xcore.persistence.dao.GenericRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.query.Criteria;

import java.util.List;
import java.util.Map;

/**
 * @author Manu V NarayanaPrasad (manu@blacwood.com)
 * @since 1.0
 */
public interface WebsiteRepository extends GenericRepository<Website, Criteria> {
//    Optional<Website> findById(String id, FindBy findBy, Class<Website> clazz);
    Page<Map<String, Object>> getWebsiteCatalogs(String websiteId, Pageable pageable, boolean... activeRequired);

    Page<Map<String, Object>> findAllWebsiteCatalogs(String websiteId, String searchField, String keyword, Pageable pageable, boolean... activeRequired);

    List<WebsiteCatalog> getAllWebsiteCatalogs(String websiteId, boolean... activeRequired);

    Page<Catalog> findAvailableCatalogsForWebsite(String websiteId, String searchField, String keyword, Pageable pageable, boolean... activeRequired);
}
