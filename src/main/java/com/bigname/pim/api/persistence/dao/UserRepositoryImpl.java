package com.bigname.pim.api.persistence.dao;

import com.bigname.core.persistence.dao.GenericRepositoryImpl;
import com.bigname.pim.api.domain.User;
import org.springframework.data.mongodb.core.MongoTemplate;

/**
 * @author Manu V NarayanaPrasad (manu@blacwood.com)
 * @since 1.0
 */
public class UserRepositoryImpl extends GenericRepositoryImpl<User> implements UserRepository {
    public UserRepositoryImpl(MongoTemplate mongoTemplate) {
        super(mongoTemplate, User.class);
    }
}
