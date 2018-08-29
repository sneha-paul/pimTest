package com.bigname.pim.api.service;

import com.bigname.pim.api.domain.Catalog;
import com.bigname.pim.api.persistence.dao.CatalogDAO;
import com.bigname.pim.util.FindBy;
import com.bigname.pim.util.Toggle;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Optional;

/**
 * Created by Manu on 8/9/2018.
 */
public interface CatalogService extends BaseService<Catalog, CatalogDAO> {
    List<Catalog> getAllWithExclusions(String[] excludedIds, FindBy findBy);
}
