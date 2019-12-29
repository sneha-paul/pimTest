package com.bigname.pim.core.persistence.dao.mongo;

import com.bigname.pim.core.domain.Website;
import com.m7.xtreme.xcore.persistence.dao.mongo.GenericDAO;

import java.util.Optional;

//@Repository
public interface WebsiteDAO extends GenericDAO<Website>, WebsiteRepository {
    Optional<Website> findByWebsiteName(String websiteName);
    Optional<Website> findByUrl(String url);

}
