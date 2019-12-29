package com.bigname.pim.core.persistence.dao.mongo;

import com.bigname.pim.core.domain.Catalog;
import com.m7.xtreme.xcore.persistence.dao.mongo.GenericDAO;

/**
 * Created by Manu on 8/9/2018.
 */
public interface CatalogDAO extends GenericDAO<Catalog>, CatalogRepository {
}
