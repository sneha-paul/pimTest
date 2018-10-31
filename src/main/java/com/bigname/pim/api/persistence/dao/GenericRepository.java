package com.bigname.pim.api.persistence.dao;

import com.bigname.pim.util.FindBy;

import java.util.Optional;

/**
 * @author Manu V NarayanaPrasad (manu@blacwood.com)
 * @since 1.0
 */
public interface GenericRepository<T> {
    //Test method using mongoTemplate
    Optional<T> findById(String id, FindBy findBy, Class<T> clazz);
}
