package com.bigname.pim.core.persistence.dao.mongo;

import com.bigname.pim.core.domain.WebsitePage;
import com.m7.xtreme.xcore.persistence.dao.mongo.GenericDAO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface WebsitePageDAO extends GenericDAO<WebsitePage>, WebsitePageRepository {
    Page<WebsitePage> findByWebsiteIdAndActiveIn(String websiteId, String[] active, Pageable pageable);

    <I> Optional<WebsitePage> findByWebsiteIdAndExternalIdAndActiveIn(I websiteId, I pageId, String[] active);
}
