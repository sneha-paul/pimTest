package com.bigname.pim.api.service.impl;


import com.bigname.pim.api.domain.Event;
import com.bigname.pim.api.persistence.dao.EventDAO;
import com.bigname.pim.api.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import javax.validation.Validator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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
    public List<Event> findAll(Map<String, Object> criteria) {return dao.findAll(criteria);}

    @Override
    public List<Event> findAll(Criteria criteria) {
        return dao.findAll(criteria);
    }

    @Override
    public Optional<Event> findOne(Map<String, Object> criteria) {
        return dao.findOne(criteria);
    }

    @Override
    public Optional<Event> findOne(Criteria criteria) {
        return dao.findOne(criteria);
    }

    @Override
    protected Event createOrUpdate(Event event) {
        return eventDAO.save(event);
    }

    @Override
    public Page<Map<String, Object>> getEventData(Pageable pageable, boolean... activeRequired) {
        return eventDAO.getEventData(pageable);
    }
}
