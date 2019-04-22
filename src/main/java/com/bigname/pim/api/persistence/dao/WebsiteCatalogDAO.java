package com.bigname.pim.api.persistence.dao;

import com.bigname.core.persistence.dao.BaseAssociationDAO;
import com.bigname.pim.api.domain.WebsiteCatalog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

/**
 * Created by manu on 8/19/18.
 */
public interface WebsiteCatalogDAO extends BaseAssociationDAO<WebsiteCatalog>, MongoRepository<WebsiteCatalog, String> {
    Page<WebsiteCatalog> findByWebsiteIdAndActiveIn(String websiteId, String[] active, Pageable pageable);
    long countByWebsiteId(String websiteId);
    List<WebsiteCatalog> findByWebsiteId(String websiteId);
    List<WebsiteCatalog> findByCatalogId(String catalogId);
}
