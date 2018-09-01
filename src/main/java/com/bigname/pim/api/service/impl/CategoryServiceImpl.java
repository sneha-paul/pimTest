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
    public List<Category> getAllWithExclusions(String[] excludedIds, FindBy findBy) {
        return findBy == FindBy.INTERNAL_ID ? categoryDAO.findByIdNotInAndActiveInOrderByCategoryNameAsc(excludedIds, PimUtil.getActiveOptions(true)) : categoryDAO.findByCategoryIdNotInAndActiveInOrderByCategoryNameAsc(excludedIds, PimUtil.getActiveOptions(true));
    }

    @Override
    public List<Category> getAvailableSubCategoriesForCategory(String id, FindBy findBy) {
        Optional<Category> category = get(id, findBy, false);
        Set<String> categoryIds = new HashSet<>();
        if(category.isPresent()) {
            categoryIds.add(category.get().getId());
            relatedCategoryDAO.findByCategoryId(category.get().getId()).forEach(rc -> categoryIds.add(rc.getSubCategoryId()));
        }
        return getAllWithExclusions(categoryIds.toArray(new String[0]), FindBy.INTERNAL_ID);
    }

    @Override
    public Page<RelatedCategory> getSubCategories(String categoryId, FindBy findBy, int page, int size, boolean... activeRequired) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(new Sort.Order(Sort.Direction.ASC, "sequenceNum"), new Sort.Order(Sort.Direction.DESC, "subSequenceNum")));
        List<Category> subCategories = new ArrayList<>();
        long totalSubCategories = 0;
        Optional<Category> _category = get(categoryId, findBy, activeRequired);
        if(_category.isPresent()) {
            Category category = _category.get();
            Page<RelatedCategory> relatedCategories = relatedCategoryDAO.findByCategoryIdAndActiveIn(category.getId(), PimUtil.getActiveOptions(activeRequired), pageable);
            List<String> subCategoryIds = new ArrayList<>();
            relatedCategories.forEach(rc -> subCategoryIds.add(rc.getSubCategoryId()));
            if(subCategoryIds.size() > 0) {
                subCategories = getAll(subCategoryIds.toArray(new String[0]), FindBy.INTERNAL_ID, null, activeRequired);
                Map<String, Category> subCategoriesMap = new HashMap<>();
                subCategories.forEach(sc -> subCategoriesMap.put(sc.getId(), sc));
                relatedCategories.forEach(rc -> rc.init(category, subCategoriesMap.get(rc.getSubCategoryId())));
            }
            return relatedCategories;
        }
        return new PageImpl<RelatedCategory>(new ArrayList<>(), pageable, 0);
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
