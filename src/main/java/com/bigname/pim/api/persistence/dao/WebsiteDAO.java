package com.bigname.pim.api.persistence.dao;

import com.bigname.pim.api.domain.Website;
import com.m7.xcore.persistence.dao.GenericDAO;

import java.util.Optional;

//@Repository
public interface WebsiteDAO extends GenericDAO<Website>, WebsiteRepository {
    Optional<Website> findByWebsiteName(String websiteName);
    Optional<Website> findByUrl(String url);

}
