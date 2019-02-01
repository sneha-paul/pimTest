package com.bigname.pim.api.service;

import com.bigname.pim.api.domain.Event;
import com.bigname.pim.api.persistence.dao.EventDAO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Map;

/**
 * Created by dona on 30-01-2019.
 */
public interface EventService extends BaseService<Event, EventDAO> {

    Page<Map<String, Object>> getEventData(Pageable pageable, boolean... activeRequired);
}
