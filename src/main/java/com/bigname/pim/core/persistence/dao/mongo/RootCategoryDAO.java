package com.bigname.pim.core.persistence.dao.mongo;

import com.bigname.pim.core.domain.RootCategory;
import com.m7.xtreme.xcore.persistence.dao.BaseAssociationDAO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

/**
 * Created by sruthi on 31-08-2018.
 */
public interface RootCategoryDAO extends BaseAssociationDAO<RootCategory>, MongoRepository<RootCategory, String> {

    Page<RootCategory> findByCatalogIdAndActiveIn(String catalogId, String active[], Pageable pageable);
    long countByCatalogId(String catalogId);
    List<RootCategory> findByCatalogId(String catalogId);
    List<RootCategory> findByCatalogIdOrderBySequenceNumAscSubSequenceNumDesc(String catalogId);
    List<RootCategory> findByCatalogIdAndRootCategoryIdIn(String catalogId, String[] rootCategoryIds);
    Optional<RootCategory> findFirstByCatalogIdAndRootCategoryId(String catalogId, String rootCategoryIds);
    List<RootCategory> findByCatalogIdAndSequenceNumAndSubSequenceNumGreaterThanEqualOrderBySubSequenceNumAsc(String catalogId, long sequenceNum, int subSequenceNum);

    Optional<RootCategory> findTopByCatalogIdAndSequenceNumOrderBySubSequenceNumDesc(String catalogId, long sequenceNum);
    List<RootCategory> findByRootCategoryId(String categoryId);
}
