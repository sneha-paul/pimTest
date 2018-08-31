package com.bigname.pim.api.service.impl;

import com.bigname.pim.api.domain.*;
import com.bigname.pim.api.persistence.dao.CatalogDAO;
import com.bigname.pim.api.persistence.dao.RootCategoryDAO;
import com.bigname.pim.api.persistence.dao.WebsiteCatalogDAO;
import com.bigname.pim.api.service.CatalogService;
import com.bigname.pim.api.service.CategoryService;
import com.bigname.pim.api.service.WebsiteService;
import com.bigname.pim.util.FindBy;
import com.bigname.pim.util.PimUtil;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class CatalogServiceImpl extends BaseServiceSupport<Catalog, CatalogDAO> implements CatalogService {

    private CatalogDAO catalogDAO;
    private WebsiteCatalogDAO websiteCatalogDAO;
    private RootCategoryDAO rootCategoryDAO;
    private CategoryService categoryService;


    public CatalogServiceImpl(CatalogDAO catalogDAO, WebsiteCatalogDAO websiteCatalogDAO, RootCategoryDAO rootCategoryDAO, CategoryService categoryService) {
        super(catalogDAO, "catalog");
        this.catalogDAO = catalogDAO;
        this.websiteCatalogDAO = websiteCatalogDAO;
        this.rootCategoryDAO = rootCategoryDAO;
        this.categoryService = categoryService;
    }


    @Override
    protected Catalog createOrUpdate(Catalog catalog) {
        return catalogDAO.save(catalog);
    }


    @Override
    public List<Catalog> getAllWithExclusions(String[] excludedIds, FindBy findBy) {
        return findBy == FindBy.INTERNAL_ID ? catalogDAO.findByIdNotInAndActiveInOrderByCatalogNameAsc(excludedIds, PimUtil.getActiveOptions(true)) : catalogDAO.findByCatalogIdNotInAndActiveInOrderByCatalogNameAsc(excludedIds, PimUtil.getActiveOptions(true));
    }

    @Override
    public List<Category> getAvailableRootCategoriesForCatalog(String id, FindBy findBy) {

        Optional<Catalog> catalog = get(id, findBy, false);

        Set<String> categoryIds = new HashSet<>();

        if(catalog.isPresent()) {
            rootCategoryDAO.findByCatalogId(catalog.get().getId()).forEach(rc -> categoryIds.add(rc.getCategoryId()));
        }

        return categoryService.getAllWithExclusions(categoryIds.toArray(new String[0]), FindBy.INTERNAL_ID);
    }

    @Override
    public Page<Category> getRootCategories(String catalogId, FindBy findBy, int page, int size, boolean... activeRequired) {
        Pageable pageable = PageRequest.of(page, size, Sort.Direction.ASC, "sequenceNum", "subSequenceNum");
        List<Category> categories = new ArrayList<>();
        long totalCategories = 0;
        Optional<Catalog> _catalog = get(catalogId, findBy, activeRequired);
        if(_catalog.isPresent()) {
            Catalog catalog = _catalog.get();
            Page<RootCategory> rootCategory = rootCategoryDAO.findByCatalogIdAndActiveIn(catalog.getId(), PimUtil.getActiveOptions(activeRequired), pageable);
            List<String> categoryIds = new ArrayList<>();
            rootCategory.forEach(rc -> categoryIds.add(rc.getCategoryId()));
            if(categoryIds.size() > 0) {
                categories = categoryService.getAll(categoryIds.toArray(new String[0]), FindBy.INTERNAL_ID, null, activeRequired);
                totalCategories = rootCategoryDAO.countByCatalogId(catalog.getId());
            }
        }
        return new PageImpl<>(categories, pageable, totalCategories);
    }

    @Override
    public RootCategory addCategory(String id, FindBy findBy1, String categoryId, FindBy findBy2) {
        Optional<Catalog> catalog = get(id, findBy1, false);
        if(catalog.isPresent()) {
            Optional<Category> category = categoryService.get(categoryId, findBy2);
            if(catalog.isPresent()) {
                return rootCategoryDAO.save(new RootCategory(catalog.get().getId(), category.get().getId()));
            }
        }

        return null;
    }
}
