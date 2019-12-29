package com.bigname.pim.core.service;

import com.bigname.pim.core.domain.Catalog;
import com.bigname.pim.core.domain.Website;
import com.bigname.pim.core.domain.WebsiteCatalog;
import com.bigname.pim.core.persistence.dao.mongo.WebsiteDAO;
import com.m7.xtreme.xcore.service.BaseService;
import com.m7.xtreme.xcore.util.ID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface WebsiteService extends BaseService<Website, WebsiteDAO> {


    Page<Catalog> findAvailableCatalogsForWebsite(ID<String> websiteId, String searchField, String keyword, Pageable pageable, boolean... activeRequired);

    /**
     * Method to get catalogs of a website in paginated format.
     *
     * @param websiteId Internal or External id of the Website
     * @param pageable The pageable object
     * @param activeRequired activeRequired Boolean flag
     * @return
     */
    Page<Map<String, Object>> getWebsiteCatalogs(ID<String> websiteId, Pageable pageable, boolean... activeRequired);

    /**
     * Method to get available catalogs of a website in paginated format.
     *
     * @param websiteId Internal or External id of the Website
     * @param page page number
     * @param size page size
     * @param sort sort object
     * @return
     */
    Page<Catalog> getAvailableCatalogsForWebsite(ID<String> websiteId, int page, int size, Sort sort, boolean... activeRequired);

    /**
     * Method to add catalog for a website.
     *
     * @param websiteId Internal or External id of the Website
     * @param catalogId Internal or External id of the Catalog
     * @return
     */
    WebsiteCatalog addCatalog(ID<String> websiteId, ID<String> catalogId);

    Optional<Website> getWebsiteByName(String name);
    Optional<Website> getWebsiteByUrl(String url);
    Page<Map<String, Object>> findAllWebsiteCatalogs(ID<String> websiteId, String searchField, String keyword, Pageable pageable, boolean... activeRequired);
    List<WebsiteCatalog> getAllWebsiteCatalogs(String websiteInternalId);

}
