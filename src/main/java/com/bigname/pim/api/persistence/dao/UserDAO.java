package com.bigname.pim.api.persistence.dao;

import com.bigname.pim.api.domain.User;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Created by sruthi on 05-11-2018.
 */
public interface UserDAO extends BaseDAO<User>, MongoRepository<User, String>, UserRepository {

    User findByEmail(String email);

}
