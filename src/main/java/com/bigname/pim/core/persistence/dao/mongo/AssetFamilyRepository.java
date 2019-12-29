package com.bigname.pim.core.persistence.dao.mongo;

import com.bigname.pim.core.domain.AssetFamily;
import com.m7.xtreme.xcore.persistence.dao.GenericRepository;
import org.springframework.data.mongodb.core.query.Criteria;

/**
 * Created by sanoop on 14/02/2019.
 */
public interface AssetFamilyRepository extends GenericRepository<AssetFamily, Criteria> {
}
