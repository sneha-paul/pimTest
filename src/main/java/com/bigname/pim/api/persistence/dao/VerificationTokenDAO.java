package com.bigname.pim.api.persistence.dao;

import com.bigname.pim.api.domain.VerificationToken;
import com.m7.xtreme.xcore.persistence.mongo.dao.BaseDAO;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Created by dona on 09-01-2019.
 */
public interface VerificationTokenDAO extends BaseDAO<VerificationToken>, MongoRepository<VerificationToken, String> {

    VerificationToken findByToken(String token);

}
