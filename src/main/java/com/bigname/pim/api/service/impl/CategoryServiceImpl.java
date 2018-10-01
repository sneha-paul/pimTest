package com.bigname.pim.api.service.impl;

import com.bigname.pim.api.domain.*;
import com.bigname.pim.api.persistence.dao.CategoryDAO;
import com.bigname.pim.api.persistence.dao.CategoryProductDAO;
import com.bigname.pim.api.persistence.dao.RelatedCategoryDAO;
import com.bigname.pim.api.service.CategoryService;
import com.bigname.pim.api.service.ProductService;
import com.bigname.pim.util.FindBy;
import com.bigname.pim.util.PimUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import javax.validation.Validator;
import java.util.*;

/**
 * Created by sruthi on 29-08-2018.
 */
@Service
public class CategoryServiceImpl extends BaseServiceSupport<Category, CategoryDAO> implements CategoryService{

    private CategoryDAO categoryDAO;
    private RelatedCategoryDAO relatedCategoryDAO;
    private CategoryProductDAO categoryProductDAO;
    private ProductService productService;

    @Autowired
    public CategoryServiceImpl(CategoryDAO categoryDAO, Validator validator, RelatedCategoryDAO relatedCategoryDAO, CategoryProductDAO categoryProductDAO, ProductService productService) {
        super(categoryDAO, "category", validator);
        this.categoryDAO = categoryDAO;
        this.relatedCategoryDAO = relatedCategoryDAO;
        this.categoryProductDAO = categoryProductDAO;
        this.productService = productService;
    }

    @Override
    public Category createOrUpdate(Category category) {
        return categoryDAO.save(category);
    }


    @Override
    public Page<Category> getAllWithExclusions(String[] excludedIds, FindBy findBy, int page, int size, Sort sort, boolean... activeRequired) {
        if(sort == null) {
            sort = Sort.by(new Sort.Order(Sort.Direction.ASC, "categoryId"));
        }
        Pageable pageable = PageRequest.of(page, size, sort);
        return findBy == FindBy.INTERNAL_ID ? categoryDAO.findByIdNotInAndActiveIn(excludedIds, PimUtil.getActiveOptions(activeRequired), pageable) : categoryDAO.findByCategoryIdNotInAndActiveIn(excludedIds, PimUtil.getActiveOptions(activeRequired), pageable);
    }

    @Override
    public Page<Category> getAvailableSubCategoriesForCategory(String id, FindBy findBy, int page, int size, Sort sort, boolean... activeRequired) {
        Optional<Category> category = get(id, findBy, false);
        Set<String> categoryIds = new HashSet<>();
        category.ifPresent(category1 -> relatedCategoryDAO.findByCategoryId(category1.getId()).forEach(rc -> categoryIds.add(rc.getSubCategoryId())));
        categoryIds.add(category.get().getId());
        return getAllWithExclusions(categoryIds.toArray(new String[0]), FindBy.INTERNAL_ID, page, size, sort, false);
    }

    @Override
    public Page<RelatedCategory> getSubCategories(String categoryId, FindBy findBy, int page, int size, Sort sort, boolean... activeRequired) {
        if(sort == null) {
            sort = Sort.by(new Sort.Order(Sort.Direction.ASC, "sequenceNum"), new Sort.Order(Sort.Direction.DESC, "subSequenceNum"));
        }
        Pageable pageable = PageRequest.of(page, size, sort);
        Optional<Category> _category = get(categoryId, findBy, false);
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
            Optional<Category> subCategory = get(subCategoryId, findBy2, false);
            if(subCategory.isPresent()) {
                Optional<RelatedCategory> top = relatedCategoryDAO.findTopBySequenceNumOrderBySubSequenceNumDesc(0);
                return relatedCategoryDAO.save(new RelatedCategory(category.get().getId(), subCategory.get().getId(), top.map(EntityAssociation::getSubSequenceNum).orElse(0)));
            }
        }
        return null;
    }

    @Override
    public Page<CategoryProduct> getCategoryProducts(String categoryId, FindBy findBy, int page, int size, Sort sort, boolean... activeRequired) {
        if(sort == null) {
            sort = Sort.by(new Sort.Order(Sort.Direction.ASC, "sequenceNum"), new Sort.Order(Sort.Direction.DESC, "subSequenceNum"));
        }
        Pageable pageable = PageRequest.of(page, size, sort);
        Optional<Category> _category = get(categoryId, findBy, false);
        if(_category.isPresent()) {
            Category category = _category.get();
            Page<CategoryProduct> categoryProducts = categoryProductDAO.findByCategoryIdAndActiveIn(category.getId(), PimUtil.getActiveOptions(activeRequired), pageable);
            List<String> productIds = new ArrayList<>();
            categoryProducts.forEach(cp -> productIds.add(cp.getProductId()));
            if(productIds.size() > 0) {
                Map<String, Product> productsMap = PimUtil.getIdedMap(productService.getAll(productIds.toArray(new String[0]), FindBy.INTERNAL_ID, null, activeRequired), FindBy.INTERNAL_ID);
                categoryProducts.forEach(cp -> cp.init(category, productsMap.get(cp.getProductId())));
            }
            return categoryProducts;
        }
        return new PageImpl<>(new ArrayList<>(), pageable, 0);
    }

    @Override
    public Page<Product> getAvailableProductsForCategory(String id, FindBy findBy, int page, int size, Sort sort) {
        Optional<Category> category = get(id, findBy, false);
        Set<String> productIds = new HashSet<>();
        category.ifPresent(category1 -> categoryProductDAO.findByCategoryId(category1.getId()).forEach(cp -> productIds.add(cp.getProductId())));
        return productService.getAllWithExclusions(productIds.toArray(new String[0]), FindBy.INTERNAL_ID, page, size, sort, false);
    }

    @Override
    public CategoryProduct addProduct(String id, FindBy findBy1, String productId, FindBy findBy2) {
        Optional<Category> category = get(id, findBy1, false);
        if(category.isPresent()) {
            Optional<Product> product = productService.get(productId, findBy2, false);
            if(product.isPresent()) {
                Optional<CategoryProduct> top = categoryProductDAO.findTopBySequenceNumOrderBySubSequenceNumDesc(0);
                return categoryProductDAO.save(new CategoryProduct(category.get().getId(), product.get().getId(), top.map(categoryProduct -> categoryProduct.getSubSequenceNum() + 1).orElse(0)));
            }
        }
        return null;
    }
}
