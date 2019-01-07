package com.bigname.pim.api.persistence.dao;

import com.bigname.pim.api.domain.Website;
import com.bigname.pim.util.FindBy;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.Optional;

/**
 * @author Manu V NarayanaPrasad (manu@blacwood.com)
 * @since 1.0
 */
public class WebsiteRepositoryImpl extends GenericRepositoryImpl<Website> implements WebsiteRepository {

    public WebsiteRepositoryImpl(MongoTemplate mongoTemplate) {
        super(mongoTemplate, Website.class);
    }

}
