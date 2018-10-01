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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import javax.validation.Validator;
import java.util.*;

@Service
public class CatalogServiceImpl extends BaseServiceSupport<Catalog, CatalogDAO> implements CatalogService {

    private CatalogDAO catalogDAO;
    private WebsiteCatalogDAO websiteCatalogDAO;
    private RootCategoryDAO rootCategoryDAO;
    private CategoryService categoryService;


    @Autowired
    public CatalogServiceImpl(CatalogDAO catalogDAO, Validator validator, WebsiteCatalogDAO websiteCatalogDAO, RootCategoryDAO rootCategoryDAO, CategoryService categoryService) {
        super(catalogDAO, "catalog", validator);
        this.catalogDAO = catalogDAO;
        this.websiteCatalogDAO = websiteCatalogDAO;
        this.rootCategoryDAO = rootCategoryDAO;
        this.categoryService = categoryService;
    }


    @Override
    public Catalog createOrUpdate(Catalog catalog) {
        return catalogDAO.save(catalog);
    }


    @Override
    public Page<Catalog> getAllWithExclusions(String[] excludedIds, FindBy findBy, int page, int size, Sort sort, boolean... activeRequired) {
        if(sort == null) {
            sort = Sort.by(new Sort.Order(Sort.Direction.ASC, "catalogId"));
        }
        Pageable pageable = PageRequest.of(page, size, sort);
        return findBy == FindBy.INTERNAL_ID ? catalogDAO.findByIdNotInAndActiveIn(excludedIds, PimUtil.getActiveOptions(activeRequired), pageable) : catalogDAO.findByCatalogIdNotInAndActiveIn(excludedIds, PimUtil.getActiveOptions(activeRequired), pageable);
    }

    @Override
    public Page<Category> getAvailableRootCategoriesForCatalog(String id, FindBy findBy, int page, int size, Sort sort) {
        Optional<Catalog> catalog = get(id, findBy, false);
        Set<String> categoryIds = new HashSet<>();
        catalog.ifPresent(catalog1 -> rootCategoryDAO.findByCatalogId(catalog1.getId()).forEach(rc -> categoryIds.add(rc.getRootCategoryId())));
        return categoryService.getAllWithExclusions(categoryIds.toArray(new String[0]), FindBy.INTERNAL_ID, page, size, sort, false);
    }

    @Override
    public Page<RootCategory> getRootCategories(String catalogId, FindBy findBy, int page, int size,Sort sort, boolean... activeRequired) {
        if(sort == null) {
            sort = Sort.by(new Sort.Order(Sort.Direction.ASC, "sequenceNum"), new Sort.Order(Sort.Direction.DESC, "subSequenceNum"));
        }
        Pageable pageable = PageRequest.of(page, size, sort);
        Optional<Catalog> _catalog = get(catalogId, findBy, false);
        if(_catalog.isPresent()) {
            Catalog catalog = _catalog.get();
            Page<RootCategory> rootCategories = rootCategoryDAO.findByCatalogIdAndActiveIn(catalog.getId(), PimUtil.getActiveOptions(activeRequired), pageable);
            List<String> categoryIds = new ArrayList<>();
            rootCategories.forEach(rc -> categoryIds.add(rc.getRootCategoryId()));
            if(categoryIds.size() > 0) {
                Map<String, Category> categoriesMap = PimUtil.getIdedMap(categoryService.getAll(categoryIds.toArray(new String[0]), FindBy.INTERNAL_ID, null, activeRequired), FindBy.INTERNAL_ID);
                rootCategories.forEach(rc -> rc.init(catalog, categoriesMap.get(rc.getRootCategoryId())));
            }
            return rootCategories;
        }
        return new PageImpl<>(new ArrayList<>(), pageable, 0);

    }

    @Override
    public RootCategory addRootCategory(String id, FindBy findBy1, String rootCategoryId, FindBy findBy2) {
        Optional<Catalog> catalog = get(id, findBy1, false);
        if(catalog.isPresent()) {
            Optional<Category> rootCategory = categoryService.get(rootCategoryId, findBy2, false);
            if(rootCategory.isPresent()) {
                Optional<RootCategory> top = rootCategoryDAO.findTopBySequenceNumOrderBySubSequenceNumDesc(0);
                return rootCategoryDAO.save(new RootCategory(catalog.get().getId(), rootCategory.get().getId(), top.map(rootCategory1 -> rootCategory1.getSubSequenceNum() + 1).orElse(0)));
            }
        }
        return null;
    }
}
