package com.bigname.pim.api.persistence.dao;

import com.bigname.pim.api.domain.CategoryProduct;
import com.m7.xtreme.xcore.persistence.mongo.dao.BaseAssociationDAO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

/**
 * Created by sruthi on 26-09-2018.
 */
public interface CategoryProductDAO extends BaseAssociationDAO<CategoryProduct>, MongoRepository<CategoryProduct, String> {
    Page<CategoryProduct> findByCategoryIdAndActiveIn(String websiteId, String active[], Pageable pageable);
    List<CategoryProduct> findByCategoryIdAndProductIdIn(String categoryId, String[] productIds);
    Optional<CategoryProduct> findFirstByCategoryIdAndProductId(String categoryId, String productIds);
    List<CategoryProduct> findByCategoryIdAndSequenceNumAndSubSequenceNumGreaterThanEqualOrderBySubSequenceNumAsc(String categoryId, long sequenceNum, int subSequenceNum);
    List<CategoryProduct> findByCategoryId(String categoryId);
    List<CategoryProduct> findByProductId(String productId);
}
