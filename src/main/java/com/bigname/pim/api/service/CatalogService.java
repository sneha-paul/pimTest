package com.bigname.pim.api.service;

import com.bigname.pim.api.domain.Catalog;
import com.bigname.pim.api.domain.Category;
import com.bigname.pim.api.domain.RootCategory;
import com.bigname.pim.api.persistence.dao.CatalogDAO;
import com.bigname.pim.util.FindBy;
import com.bigname.pim.util.Toggle;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Optional;

/**
 * Created by Manu on 8/9/2018.
 */
public interface CatalogService extends BaseService<Catalog, CatalogDAO> {
//    Page<Catalog> getAllWithExclusions(String[] excludedIds, FindBy findBy, int page, int size, Sort sort, boolean... activeRequired);
    /**
     * Method to get categories of a WebsiteCatalog in paginated format.
     *
     * @param websiteCatalogId The websiteCatalogId
     * @param findBy Type of the catalog id, INTERNAL_ID or EXTERNAL_ID
     * @param page page number
     * @param size page size
     * @param sort sort Object
     * @param activeRequired activeRequired Boolean flag
     * @return
     */
    Page<RootCategory> getRootCategories(String websiteCatalogId, int page, int size,Sort sort, boolean... activeRequired);

    /**
     * Method to get available categories of a catalog in paginated format.
     *
     * @param websiteCatalogId Internal or External id of the WebsiteCatalog
     * @param page page number
     * @param size page size
     * @param sort sort object
     * @return
     */
    Page<Category> getAvailableRootCategoriesForCatalog(String websiteCatalogId, int page, int size, Sort sort);

    /**
     * Method to add category for a catalog.
     *
     * @param websiteId Internal or External id of the Website
     * @param websiteIdFindBy Type of the website id, INTERNAL_ID or EXTERNAL_ID
     * @param catalogId Internal or External id of the Catalog
     * @param catalogIdFindBy Type of the catalog id, INTERNAL_ID or EXTERNAL_ID
     * @param rootCategoryId Internal or External id of the Category
     * @param rootCategoryIdFindBy Type of the category id, INTERNAL_ID or EXTERNAL_ID
     * @return
     */

    RootCategory addRootCategory(String websiteId, FindBy websiteIdFindBy, String catalogId, FindBy catalogIdFindBy, String rootCategoryId, FindBy rootCategoryIdFindBy);
}
