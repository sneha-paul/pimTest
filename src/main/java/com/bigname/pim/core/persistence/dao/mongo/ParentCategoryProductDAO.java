package com.bigname.pim.core.persistence.dao.mongo;

import com.bigname.pim.core.domain.ParentCategoryProduct;
import com.m7.xtreme.xcore.persistence.dao.BaseAssociationDAO;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface ParentCategoryProductDAO extends BaseAssociationDAO<ParentCategoryProduct>, MongoRepository<ParentCategoryProduct, String> {
    Optional<ParentCategoryProduct> findFirstByCategoryIdAndProductId(String categoryId, String productIds);
    List<ParentCategoryProduct> findByCategoryIdAndProductIdIn(String categoryId, String[] productIds);
    List<ParentCategoryProduct> findByCategoryIdAndSequenceNumAndSubSequenceNumGreaterThanEqualOrderBySubSequenceNumAsc(String categoryId, long sequenceNum, int subSequenceNum);
}
