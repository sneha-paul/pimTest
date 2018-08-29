package com.bigname.pim.api.persistence.dao;

import com.bigname.pim.api.domain.Website;
import com.bigname.pim.api.domain.WebsiteCatalog;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface WebsiteDAO extends BaseDAO<Website>, MongoRepository<Website, String> {

}
