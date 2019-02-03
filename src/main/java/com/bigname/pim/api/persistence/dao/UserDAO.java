package com.bigname.pim.api.persistence.dao;

import com.bigname.core.persistence.dao.GenericDAO;
import com.bigname.pim.api.domain.User;

/**
 * Created by sruthi on 05-11-2018.
 */
public interface UserDAO extends GenericDAO<User>, UserRepository {

    User findByEmail(String email);

}
