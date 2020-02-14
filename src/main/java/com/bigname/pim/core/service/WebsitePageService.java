package com.bigname.pim.core.service;

import com.bigname.pim.core.domain.WebsitePage;
import com.bigname.pim.core.persistence.dao.mongo.WebsitePageDAO;
import com.m7.xtreme.xcore.service.BaseService;
import com.m7.xtreme.xcore.util.ID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface WebsitePageService extends BaseService<WebsitePage, WebsitePageDAO> {
    Page<WebsitePage> getAll(ID<String> websiteId, int page, int size, Sort sort, boolean... activeRequired);

    <I> Optional<WebsitePage> get(ID<I> websiteId, ID<I> pageId, boolean... activeRequired);

    List<Map<String, Object>> getPageAttributes(String websiteId, String pageId);
}
