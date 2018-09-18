package com.bigname.pim.api.service.impl;

import com.bigname.pim.api.domain.*;
import com.bigname.pim.api.persistence.dao.CategoryDAO;
import com.bigname.pim.api.persistence.dao.RelatedCategoryDAO;
import com.bigname.pim.api.service.CategoryService;
import com.bigname.pim.util.FindBy;
import com.bigname.pim.util.PimUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Created by sruthi on 29-08-2018.
 */
@Service
public class CategoryServiceImpl extends BaseServiceSupport<Category, CategoryDAO> implements CategoryService{

    private CategoryDAO categoryDAO;
    private RelatedCategoryDAO relatedCategoryDAO;

    @Autowired
    public CategoryServiceImpl(CategoryDAO categoryDAO, RelatedCategoryDAO relatedCategoryDAO) {
        super(categoryDAO, "category");
        this.categoryDAO = categoryDAO;
        this.relatedCategoryDAO = relatedCategoryDAO;
    }

    @Override
    protected Category createOrUpdate(Category category) {
        return categoryDAO.save(category);
    }


    @Override
    public Page<Category> getAllWithExclusions(String[] excludedIds, FindBy findBy, int page, int size, Sort sort, boolean... activeRequired) {
       // return findBy == FindBy.INTERNAL_ID ? categoryDAO.findByIdNotInAndActiveInOrderByCategoryNameAsc(excludedIds, PimUtil.getActiveOptions(true)) : categoryDAO.findByCategoryIdNotInAndActiveInOrderByCategoryNameAsc(excludedIds, PimUtil.getActiveOptions(true));
        if(sort == null) {
            sort = Sort.by(new Sort.Order(Sort.Direction.ASC, "categoryId"));
        }
        Pageable pageable = PageRequest.of(page, size, sort);
        return findBy == FindBy.INTERNAL_ID ? categoryDAO.findByIdNotInAndActiveIn(excludedIds, PimUtil.getActiveOptions(activeRequired), pageable) : categoryDAO.findByCategoryIdNotInAndActiveIn(excludedIds, PimUtil.getActiveOptions(activeRequired), pageable);
    }

 /*   @Override
    public Page<Category> getAvailableSubCategoriesForCategory(String id, FindBy findBy) {
        Optional<Category> category = get(id, findBy, false);
        Set<String> categoryIds = new HashSet<>();
        if(category.isPresent()) {
            categoryIds.add(category.get().getId());
            relatedCategoryDAO.findByCategoryId(category.get().getId()).forEach(rc -> categoryIds.add(rc.getSubCategoryId()));
        }
        return getAllWithExclusions(categoryIds.toArray(new String[0]), FindBy.INTERNAL_ID);
    }
*/

    @Override
    public Page<Category> getAvailableSubCategoriesForCategory(String id, FindBy findBy, int page, int size, Sort sort) {
        Optional<Category> category = get(id, findBy, false);
        Set<String> categoryIds = new HashSet<>();
        category.ifPresent(category1 -> relatedCategoryDAO.findByCategoryId(category1.getId()).forEach(rc -> categoryIds.add(rc.getCategoryId())));
        return getAllWithExclusions(categoryIds.toArray(new String[0]), FindBy.INTERNAL_ID, page, size, sort);
    }

    @Override
    public Page<RelatedCategory> getSubCategories(String categoryId, FindBy findBy, int page, int size, Sort sort, boolean... activeRequired) {
       /* Pageable pageable = PageRequest.of(page, size, Sort.by(new Sort.Order(Sort.Direction.ASC, "sequenceNum"), new Sort.Order(Sort.Direction.DESC, "subSequenceNum")));
        Optional<Category> _category = get(categoryId, findBy, activeRequired);
        if(_category.isPresent()) {
            Category category = _category.get();
            Page<RelatedCategory> relatedCategories = relatedCategoryDAO.findByCategoryIdAndActiveIn(category.getId(), PimUtil.getActiveOptions(activeRequired), pageable);
            List<String> subCategoryIds = new ArrayList<>();
            relatedCategories.forEach(rc -> subCategoryIds.add(rc.getSubCategoryId()));
            if(subCategoryIds.size() > 0) {
                Map<String, Category> subCategoriesMap = PimUtil.getIdedMap(getAll(subCategoryIds.toArray(new String[0]), FindBy.INTERNAL_ID, null, activeRequired), FindBy.INTERNAL_ID);
                relatedCategories.forEach(rc -> rc.init(category, subCategoriesMap.get(rc.getSubCategoryId())));
            }
            return relatedCategories;
        }
        return new PageImpl<>(new ArrayList<>(), pageable, 0);*/
        if(sort == null) {
            sort = Sort.by(new Sort.Order(Sort.Direction.ASC, "sequenceNum"), new Sort.Order(Sort.Direction.DESC, "subSequenceNum"));
        }
        Pageable pageable = PageRequest.of(page, size, sort);
        Optional<Category> _category = get(categoryId, findBy, activeRequired);
        if(_category.isPresent()) {
            Category category = _category.get();
            Page<RelatedCategory> relatedCategories = relatedCategoryDAO.findByCategoryIdAndActiveIn(category.getId(), PimUtil.getActiveOptions(activeRequired), pageable);
            List<String> subCategoryIds = new ArrayList<>();
            relatedCategories.forEach(rc -> subCategoryIds.add(rc.getSubCategoryId()));
            if(subCategoryIds.size() > 0) {
                Map<String, Category> categoriesMap = PimUtil.getIdedMap(getAll(subCategoryIds.toArray(new String[0]), FindBy.INTERNAL_ID, null, activeRequired), FindBy.INTERNAL_ID);
                relatedCategories.forEach(rc -> rc.init(category, categoriesMap.get(rc.getSubCategoryId())));
            }
            return relatedCategories;
        }
        return new PageImpl<>(new ArrayList<>(), pageable, 0);
    }

    @Override
    public RelatedCategory addSubCategory(String id, FindBy findBy1, String subCategoryId, FindBy findBy2) {
        Optional<Category> category = get(id, findBy1, false);
        if(category.isPresent()) {
            Optional<Category> subCategory = get(subCategoryId, findBy2);
            if(subCategory.isPresent()) {
                Optional<RelatedCategory> top = relatedCategoryDAO.findTopBySequenceNumOrderBySubSequenceNumDesc(0);
                return relatedCategoryDAO.save(new RelatedCategory(category.get().getId(), subCategory.get().getId(), top.isPresent() ? top.get().getSubSequenceNum() : 0));
            }
        }
        return null;
    }
}
