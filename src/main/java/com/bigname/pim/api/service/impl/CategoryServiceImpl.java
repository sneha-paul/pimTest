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
            relatedCategoryDAO.findByCategoryId(category.get().getId()).forEach(rc -> categoryIds.add(rc.getCategoryId()));
        }

        return getAllWithExclusions(categoryIds.toArray(new String[0]), FindBy.INTERNAL_ID);
    }

    @Override
    public Page<Category> getRelatedCategory(String categoryId, FindBy findBy, int page, int size, boolean... activeRequired) {
        Pageable pageable = PageRequest.of(page, size, Sort.Direction.ASC, "sequenceNum", "subSequenceNum");
        List<Category> categories = new ArrayList<>();
        long totalCategories = 0;
        Optional<Category> _category = get(categoryId, findBy, activeRequired);
        if(_category.isPresent()) {
            Category category = _category.get();
            Page<RelatedCategory> relatedCategory = relatedCategoryDAO.findByCategoryIdAndActiveIn(category.getId(), PimUtil.getActiveOptions(activeRequired), pageable);
            List<String> categoryIds = new ArrayList<>();
            relatedCategory.forEach(rc -> categoryIds.add(rc.getCategoryId()));
            if(categoryIds.size() > 0) {
                categories = getAll(categoryIds.toArray(new String[0]), FindBy.INTERNAL_ID, null, activeRequired);
                totalCategories = relatedCategoryDAO.countByCategoryId(category.getId());
            }
        }
        return new PageImpl<>(categories, pageable, totalCategories);
    }

}
