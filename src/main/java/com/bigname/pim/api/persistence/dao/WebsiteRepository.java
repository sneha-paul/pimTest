package com.bigname.pim.api.persistence.dao;

import com.bigname.pim.api.domain.Website;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Map;

/**
 * @author Manu V NarayanaPrasad (manu@blacwood.com)
 * @since 1.0
 */
public interface WebsiteRepository extends GenericRepository<Website> {
//    Optional<Website> findById(String id, FindBy findBy, Class<Website> clazz);
Page<Map<String, Object>> getWebsiteCatalogs(String websiteId, Pageable pageable);
}
