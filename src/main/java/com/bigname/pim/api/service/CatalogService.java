package com.bigname.pim.api.service;

import com.bigname.pim.api.domain.Catalog;
import com.bigname.pim.api.domain.Category;
import com.bigname.pim.api.domain.RootCategory;
import com.bigname.pim.api.persistence.dao.CatalogDAO;
import com.bigname.pim.util.FindBy;
import com.bigname.pim.util.Pageable;
import com.bigname.pim.util.Toggle;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Map;

/**
 * Created by Manu on 8/9/2018.
 */
public interface CatalogService extends BaseService<Catalog, CatalogDAO> {
//    Page<Catalog> getAllWithExclusions(String[] excludedIds, FindBy findBy, int page, int size, Sort sort, boolean... activeRequired);

    boolean toggleRootCategory(String catalogId, FindBy catalogIdFindBy, String rootCategoryId, FindBy rootCategoryIdFindBy, Toggle active);
    /**
     * Method to get categories of a Catalog in paginated format.
     *
     * @param catalogId Internal or External id of the Catalog
     * @param findBy Type of the catalog id, INTERNAL_ID or EXTERNAL_ID
     * @param pageable The pageable object
     * @param activeRequired activeRequired Boolean flag
     * @return
     */
    Page<Map<String, Object>> getRootCategories(String catalogId, FindBy findBy, Pageable pageable, boolean... activeRequired);

    List<Map<String, Object>> getCategoryHierarchy(String catalogId, boolean... activeRequired);

    /**
     * Method to set the sequencing of two rootCategories
     * @param catalogId Internal or External id of the Catalog
     * @param catalogIdFindBy Type of the catalog id, INTERNAL_ID or EXTERNAL_ID
     * @param sourceId Internal or External id of the rootCategory, whose sequencing needs to be set
     * @param sourceIdFindBy Type of the source rootCategory id, INTERNAL_ID or EXTERNAL_ID
     * @param destinationId Internal or External id of the rootCategory at the destination slot
     * @param destinationIdFindBy Type of the destination rootCategory id, INTERNAL_ID or EXTERNAL_ID
     * @return true if sequencing got modified, false otherwise
     */
    boolean setRootCategorySequence(String catalogId, FindBy catalogIdFindBy, String sourceId, FindBy sourceIdFindBy, String destinationId, FindBy destinationIdFindBy);

    /**
     * Method to get available categories of a catalog in paginated format.
     *
     * @param id Internal or External id of the Catalog
     * @param findBy Type of the catalog id, INTERNAL_ID or EXTERNAL_ID
     * @param page page number
     * @param size page size
     * @param sort sort object
     * @return
     */
    Page<Category> getAvailableRootCategoriesForCatalog(String id, FindBy findBy, int page, int size, Sort sort);

    /**
     * Method to add category for a catalog.
     *
     * @param id Internal or External id of the Catalog
     * @param findBy1 Type of the catalog id, INTERNAL_ID or EXTERNAL_ID
     * @param rootCategoryId Internal or External id of the Category
     * @param findBy2 Type of the category id, INTERNAL_ID or EXTERNAL_ID
     * @return
     */
    RootCategory addRootCategory(String id, FindBy findBy1, String rootCategoryId, FindBy findBy2);

    Page<Map<String, Object>> findAllRootCategories(String catalogId, FindBy findBy, String searchField, String keyword, Pageable pageable, boolean... activeRequired);
}
