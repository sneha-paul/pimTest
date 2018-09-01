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
    List<Catalog> getAllWithExclusions(String[] excludedIds, FindBy findBy);
    Page<Category> getRootCategories(String websiteId, FindBy findBy, int page, int size, boolean... activeRequired);
    List<Category> getAvailableRootCategoriesForCatalog(String id, FindBy findBy);
    RootCategory addRootCategory(String id, FindBy findBy1, String rootCategoryId, FindBy findBy2);
}
