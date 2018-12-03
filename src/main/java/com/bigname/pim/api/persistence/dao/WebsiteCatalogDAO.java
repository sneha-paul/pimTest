package com.bigname.pim.api.persistence.dao;

import com.bigname.pim.api.domain.Catalog;
import com.bigname.pim.api.domain.WebsiteCatalog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Optional;

/**
 * Created by manu on 8/19/18.
 */
public interface WebsiteCatalogDAO extends BaseAssociationDAO<WebsiteCatalog>, MongoRepository<WebsiteCatalog, String> {
    Page<WebsiteCatalog> findByWebsiteIdAndActiveIn(String websiteId, String[] active, Pageable pageable);
    long countByWebsiteId(String websiteId);
    List<WebsiteCatalog> findByWebsiteId(String websiteId);
    Optional<WebsiteCatalog> findByWebsiteIdAndCatalogId(String websiteId, String catalogId);
}
