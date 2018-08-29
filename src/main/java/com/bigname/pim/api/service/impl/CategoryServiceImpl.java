package com.bigname.pim.api.service.impl;

import com.bigname.pim.api.domain.Category;
import com.bigname.pim.api.persistence.dao.CategoryDAO;
import com.bigname.pim.api.service.CategoryService;
import com.bigname.pim.util.FindBy;
import com.bigname.pim.util.PimUtil;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by sruthi on 29-08-2018.
 */
@Service
public class CategoryServiceImpl extends BaseServiceSupport<Category, CategoryDAO> implements CategoryService{

    private CategoryDAO categoryDAO;

    public CategoryServiceImpl(CategoryDAO categoryDAO) {
        super(categoryDAO, "category");
        this.categoryDAO = categoryDAO;
    }

    @Override
    protected Category createOrUpdate(Category category) {
        return categoryDAO.save(category);
    }


    @Override
    public List<Category> getAllWithExclusions(String[] excludedIds, FindBy findBy) {
        return findBy == FindBy.INTERNAL_ID ? categoryDAO.findByIdNotInAndActiveInOrderByCategoryNameAsc(excludedIds, PimUtil.getActiveOptions(true)) : categoryDAO.findByCategoryIdNotInAndActiveInOrderByCategoryNameAsc(excludedIds, PimUtil.getActiveOptions(true));
    }

}
