package com.bigname.pim.api.service;

import com.bigname.pim.api.domain.Catalog;
import com.bigname.pim.api.domain.Category;
import com.bigname.pim.api.domain.RootCategory;
import com.bigname.pim.api.domain.WebsiteCatalog;
import com.bigname.pim.api.persistence.dao.mongo.CatalogDAO;
import com.m7.xtreme.xcore.service.BaseService;
import com.m7.xtreme.xcore.util.Archive;
import com.m7.xtreme.xcore.util.ID;
import com.m7.xtreme.xcore.util.Toggle;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Map;

/**
 * Created by Manu on 8/9/2018.
 */
public interface CatalogService extends BaseService<Catalog, CatalogDAO> {
//    Page<Catalog> getAllWithExclusions(String[] excludedIds, FindBy findBy, int page, int size, Sort sort, boolean... activeRequired);

    boolean toggleRootCategory(ID<String> catalogId, ID<String> rootCategoryId, Toggle active);

    Page<Category> findAvailableRootCategoriesForCatalog(ID<String> catalogId, String searchField, String keyword, Pageable pageable, boolean... activeRequired);
    /**
     * Method to get categories of a Catalog in paginated format.
     *
     * @param catalogId Internal or External id of the Catalog
     * @param pageable The pageable object
     * @param activeRequired activeRequired Boolean flag
     * @return
     */
    Page<Map<String, Object>> getRootCategories(ID<String> catalogId, Pageable pageable, boolean... activeRequired);

    List<Map<String, Object>> getCategoryHierarchy(ID<String> catalogId, boolean... activeRequired);

    /**
     * Method to set the sequencing of two rootCategories
     * @param catalogId Internal or External id of the Catalog
     * @param sourceId Internal or External id of the rootCategory, whose sequencing needs to be set
     * @param destinationId Internal or External id of the rootCategory at the destination slot
     * @return true if sequencing got modified, false otherwise
     */
    boolean setRootCategorySequence(ID<String> catalogId, ID<String> sourceId, ID<String> destinationId);

    /**
     * Method to get available categories of a catalog in paginated format.
     *
     * @param id Internal or External id of the Catalog
     * @param page page number
     * @param size page size
     * @param sort sort object
     * @return
     */
    Page<Category> getAvailableRootCategoriesForCatalog(ID<String> id, int page, int size, Sort sort, boolean... activeRequired);

    /**
     * Method to add category for a catalog.
     *
     * @param id Internal or External id of the Catalog
     * @param rootCategoryId Internal or External id of the Category
     * @return
     */
    RootCategory addRootCategory(ID<String> id, ID<String> rootCategoryId);

    Page<Map<String, Object>> findAllRootCategories(ID<String> catalogId, String searchField, String keyword, Pageable pageable, boolean... activeRequired);

    List<RootCategory> getAllRootCategories(String catalogInternalId);

    List<WebsiteCatalog> getAllWebsiteCatalogsWithCatalogId(String catalogInternalId);

    boolean toggleCatalog(ID<String> catalogId, Toggle toggle);

    void updateWebsiteCatalog(WebsiteCatalog websiteCatalog);

    void archiveCatalogAssociations(ID<String> catalogId, Archive archived, Catalog catalog);
}
