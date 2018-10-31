package com.bigname.pim.api.persistence.dao;

import com.bigname.pim.api.domain.Website;
import com.bigname.pim.api.domain.WebsiteCatalog;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
//@Repository
public interface WebsiteDAO extends BaseDAO<Website>, MongoRepository<Website, String>, WebsiteRepository {

}
