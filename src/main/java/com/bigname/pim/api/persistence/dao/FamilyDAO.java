package com.bigname.pim.api.persistence.dao;

import com.bigname.pim.api.domain.Family;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Created by manu on 9/4/18.
 */
public interface FamilyDAO extends BaseDAO<Family>, MongoRepository<Family, String>, FamilyRepository {
}
