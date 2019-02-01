package com.bigname.pim.api.persistence.dao;

import com.bigname.pim.api.domain.Event;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Created by dona on 30-01-2019.
 */
public interface EventDAO extends BaseDAO<Event>, MongoRepository<Event, String>, EventRepository {
}
