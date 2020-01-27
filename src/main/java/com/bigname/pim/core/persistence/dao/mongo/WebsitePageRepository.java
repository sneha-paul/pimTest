package com.bigname.pim.core.persistence.dao.mongo;

import com.bigname.pim.core.domain.WebsitePage;
import com.m7.xtreme.xcore.persistence.dao.GenericRepository;
import org.springframework.data.mongodb.core.query.Criteria;

public interface WebsitePageRepository extends GenericRepository<WebsitePage, Criteria> {
}
