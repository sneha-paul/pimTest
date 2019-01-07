package com.bigname.pim.api.persistence.dao;

import com.bigname.pim.util.FindBy;
import org.springframework.data.mongodb.core.query.Criteria;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author Manu V NarayanaPrasad (manu@blacwood.com)
 * @since 1.0
 */
public interface GenericRepository<T> {
    //Test method using mongoTemplate
    Optional<T> findById(String id, FindBy findBy);
    List<T> findAll(Map<String, Object> criteria);
    List<T> findAll(Criteria criteria);
    Optional<T> findOne(Map<String, Object> criteria);
    Optional<T> findOne(Criteria criteria);
}
