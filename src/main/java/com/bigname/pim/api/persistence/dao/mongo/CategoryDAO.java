package com.bigname.pim.api.persistence.dao.mongo;

import com.bigname.pim.api.domain.Category;
import com.m7.xtreme.xcore.persistence.dao.mongo.GenericDAO;

/**
 * Created by sruthi on 29-08-2018.
 */
public interface CategoryDAO extends GenericDAO<Category>, CategoryRepository {
}
