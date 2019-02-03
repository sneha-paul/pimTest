package com.bigname.core.persistence.dao;

import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * @author Manu V NarayanaPrasad (manu@blacwood.com)
 * @since 1.0
 */
public interface GenericDAO<T> extends BaseDAO<T>, MongoRepository<T, String>, GenericRepository<T> {

}
