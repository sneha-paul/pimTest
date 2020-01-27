package com.bigname.pim.core.persistence.dao.mongo;

import com.bigname.pim.core.domain.WebsitePage;
import com.m7.xtreme.xcore.persistence.dao.mongo.GenericRepositoryImpl;
import org.springframework.data.mongodb.core.query.Criteria;

public class WebsitePageRepositoryImpl extends GenericRepositoryImpl<WebsitePage, Criteria> implements WebsitePageRepository {
    public WebsitePageRepositoryImpl() {
        super(WebsitePage.class);
    }
}
