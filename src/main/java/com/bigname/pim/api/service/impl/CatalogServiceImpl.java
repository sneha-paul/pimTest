package com.bigname.pim.api.service.impl;

import com.bigname.pim.api.domain.Catalog;
import com.bigname.pim.api.domain.Website;
import com.bigname.pim.api.domain.WebsiteCatalog;
import com.bigname.pim.api.persistence.dao.CatalogDAO;
import com.bigname.pim.api.persistence.dao.WebsiteCatalogDAO;
import com.bigname.pim.api.service.CatalogService;
import com.bigname.pim.api.service.WebsiteService;
import com.bigname.pim.util.FindBy;
import com.bigname.pim.util.PimUtil;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class CatalogServiceImpl extends BaseServiceSupport<Catalog, CatalogDAO> implements CatalogService {

    private CatalogDAO catalogDAO;
    private WebsiteCatalogDAO websiteCatalogDAO;


    public CatalogServiceImpl(CatalogDAO catalogDAO, WebsiteCatalogDAO websiteCatalogDAO) {
        super(catalogDAO, "catalog");
        this.catalogDAO = catalogDAO;
        this.websiteCatalogDAO = websiteCatalogDAO;
    }


    @Override
    protected Catalog createOrUpdate(Catalog catalog) {
        return catalogDAO.save(catalog);
    }


    @Override
    public List<Catalog> getAllWithExclusions(String[] excludedIds, FindBy findBy) {
        return findBy == FindBy.INTERNAL_ID ? catalogDAO.findByIdNotInAndActiveInOrderByCatalogNameAsc(excludedIds, PimUtil.getActiveOptions(true)) : catalogDAO.findByCatalogIdNotInAndActiveInOrderByCatalogNameAsc(excludedIds, PimUtil.getActiveOptions(true));
    }
}
