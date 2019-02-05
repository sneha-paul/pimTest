package com.bigname.pim.api.service.impl;


import com.bigname.core.service.BaseServiceSupport;
import com.bigname.pim.api.domain.Event;
import com.bigname.pim.api.persistence.dao.EventDAO;
import com.bigname.pim.api.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.validation.Validator;
import java.util.Map;

/**
 * Created by dona on 30-01-2019.
 */

@Service
public class EventServiceImpl extends BaseServiceSupport<Event, EventDAO, EventService> implements EventService {

    private EventDAO eventDAO;

    @Autowired
    public EventServiceImpl(EventDAO eventDAO, Validator validator){
        super(eventDAO, "event", validator);
        this.eventDAO = eventDAO;
    }

    @Override
    public Page<Map<String, Object>> getEventData(Pageable pageable, boolean... activeRequired) {
        return eventDAO.getEventData(pageable);
    }
}
