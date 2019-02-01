package com.bigname.pim.api.persistence.dao;

import com.bigname.pim.api.domain.Event;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Map;

/**
 * Created by dona on 30-01-2019.
 */
public interface EventRepository extends GenericRepository<Event> {
    Page<Map<String, Object>> getEventData(Pageable pageable);
}
