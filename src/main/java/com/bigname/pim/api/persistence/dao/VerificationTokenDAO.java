package com.bigname.pim.api.persistence.dao;

import com.bigname.pim.api.domain.VerificationToken;
import com.m7.xtreme.xcore.persistence.dao.mongo.GenericDAO;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Created by dona on 09-01-2019.
 */
public interface VerificationTokenDAO extends GenericDAO<VerificationToken>, VerificationTokenRepository {

    VerificationToken findByToken(String token);

}
