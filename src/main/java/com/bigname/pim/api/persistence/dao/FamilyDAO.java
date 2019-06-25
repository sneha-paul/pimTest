package com.bigname.pim.api.persistence.dao;

import com.bigname.pim.api.domain.Family;
import com.m7.xtreme.xcore.persistence.dao.mongo.GenericDAO;

/**
 * Created by manu on 9/4/18.
 */
public interface FamilyDAO extends GenericDAO<Family>, FamilyRepository {
}
