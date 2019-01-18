package com.bigname.pim.api.persistence.dao;

import com.bigname.pim.api.domain.RelatedCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

/**
 * Created by sruthi on 30-08-2018.
 */
public interface RelatedCategoryDAO extends BaseAssociationDAO<RelatedCategory>, MongoRepository<RelatedCategory, String> {

    Page<RelatedCategory> findByCategoryIdAndActiveIn(String categoryId, String active[], Pageable pageable);
    List<RelatedCategory> findByCategoryIdAndSubCategoryIdIn(String categoryId, String[] subCategoryIds);
    Optional<RelatedCategory> findFirstByCategoryIdAndSubCategoryId(String categoryId, String subCategoryId);
    List<RelatedCategory> findByCategoryIdAndSequenceNumAndSubSequenceNumGreaterThanEqualOrderBySubSequenceNumAsc(String categoryId, long sequenceNum, int subSequenceNum);
    List<RelatedCategory> findByActiveIn(String active[]);
    List<RelatedCategory> findBySubCategoryId(String subCategoryId);
    long countByCategoryId(String categoryId);
    List<RelatedCategory> findByCategoryId(String categoryId);
}
