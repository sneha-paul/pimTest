package com.bigname.pim.api.persistence.dao;

import com.bigname.pim.api.domain.ProductFamily;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Created by manu on 9/4/18.
 */
public interface ProductFamilyDAO extends BaseDAO<ProductFamily>, MongoRepository<ProductFamily, String> {
}
