package com.bigname.pim.api.service;

import com.bigname.pim.api.domain.Catalog;
import com.bigname.pim.api.domain.Website;
import com.bigname.pim.api.persistence.dao.WebsiteDAO;
import com.bigname.pim.util.FindBy;
import org.springframework.data.domain.Page;

import java.util.List;

public interface WebsiteService extends BaseService<Website, WebsiteDAO> {
    Page<Catalog> getWebsiteCatalogs(String websiteId, FindBy findBy, int page, int size, boolean... activeRequired);
    List<Catalog> getAvailableCatalogsForWebsite(String id, FindBy findBy);
}
