package com.bigname.pim.api.service;

import com.bigname.pim.api.domain.Catalog;
import com.bigname.pim.api.domain.Website;
import com.bigname.pim.api.domain.WebsiteCatalog;
import com.bigname.pim.api.persistence.dao.WebsiteDAO;
import com.bigname.pim.util.FindBy;
import com.bigname.pim.util.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;

import java.util.Map;
import java.util.Optional;

public interface WebsiteService extends BaseService<Website, WebsiteDAO> {

    /**
     * Method to get catalogs of a website in paginated format.
     *
     * @param websiteId Internal or External id of the Website
     * @param findBy Type of the website id, INTERNAL_ID or EXTERNAL_ID
     * @param pageable The pageable object
     * @param activeRequired activeRequired Boolean flag
     * @return
     */
    Page<Map<String, Object>> getWebsiteCatalogs(String websiteId, FindBy findBy, Pageable pageable, boolean... activeRequired);

    /**
     * Method to get available catalogs of a website in paginated format.
     *
     * @param id Internal or External id of the Website
     * @param findBy Type of the website id, INTERNAL_ID or EXTERNAL_ID
     * @param page page number
     * @param size page size
     * @param sort sort object
     * @return
     */
    Page<Catalog> getAvailableCatalogsForWebsite(String id, FindBy findBy, int page, int size, Sort sort);

    /**
     * Method to add catalog for a website.
     *
     * @param id Internal or External id of the Website
     * @param findBy1 Type of the website id, INTERNAL_ID or EXTERNAL_ID
     * @param catalogId Internal or External id of the Catalog
     * @param findBy2 Type of the catalog id, INTERNAL_ID or EXTERNAL_ID
     * @return
     */
    WebsiteCatalog addCatalog(String id, FindBy findBy1, String catalogId, FindBy findBy2);

    Optional<Website> getWebsiteByName(String name);
    Optional<Website> getWebsiteByUrl(String url);

    Page<Map<String, Object>> findAllWebsiteCatalogs(String websiteId, FindBy findBy, String searchField, String keyword, Pageable pageable, boolean... activeRequired);


}
