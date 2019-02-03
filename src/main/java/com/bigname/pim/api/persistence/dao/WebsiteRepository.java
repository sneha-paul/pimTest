package com.bigname.pim.api.persistence.dao;

import com.bigname.core.persistence.dao.GenericRepository;
import com.bigname.pim.api.domain.Catalog;
import com.bigname.pim.api.domain.Website;
import com.bigname.pim.api.domain.WebsiteCatalog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

/**
 * @author Manu V NarayanaPrasad (manu@blacwood.com)
 * @since 1.0
 */
public interface WebsiteRepository extends GenericRepository<Website> {
//    Optional<Website> findById(String id, FindBy findBy, Class<Website> clazz);
    Page<Map<String, Object>> getWebsiteCatalogs(String websiteId, Pageable pageable);

    Page<Map<String, Object>> findAllWebsiteCatalogs(String websiteId, String searchField, String keyword, Pageable pageable, boolean... activeRequired);

    List<WebsiteCatalog> getAllWebsiteCatalogs(String websiteId, boolean... activeRequired);

    Page<Catalog> findAvailableCatalogsForWebsite(String websiteId, String searchField, String keyword, Pageable pageable, boolean... activeRequired);
}
