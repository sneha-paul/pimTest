package com.bigname.pim.api.persistence.dao.mongo;

import com.bigname.pim.api.domain.AssetFamily;
import com.m7.xtreme.xcore.persistence.dao.GenericRepository;
import org.springframework.data.mongodb.core.query.Criteria;

/**
 * Created by sanoop on 14/02/2019.
 */
public interface AssetFamilyRepository extends GenericRepository<AssetFamily, Criteria> {
}
