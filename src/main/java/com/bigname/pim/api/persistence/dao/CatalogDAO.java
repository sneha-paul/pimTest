package com.bigname.pim.api.persistence.dao;

import com.bigname.pim.api.domain.Catalog;
import com.m7.xtreme.xcore.persistence.dao.mongo.GenericDAO;

/**
 * Created by Manu on 8/9/2018.
 */
public interface CatalogDAO extends GenericDAO<Catalog>, CatalogRepository {
}
