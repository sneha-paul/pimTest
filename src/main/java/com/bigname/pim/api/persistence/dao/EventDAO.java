package com.bigname.pim.api.persistence.dao;

import com.bigname.core.persistence.dao.GenericDAO;
import com.bigname.pim.api.domain.Event;

/**
 * Created by dona on 30-01-2019.
 */
public interface EventDAO extends GenericDAO<Event>, EventRepository {
}
