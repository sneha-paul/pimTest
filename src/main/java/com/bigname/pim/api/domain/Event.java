package com.bigname.pim.api.domain;

import com.bigname.core.domain.Entity;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by dona on 30-01-2019.
 */
public class Event extends Entity<Event> {


    /*
    * string user
    * localDateTime  timeStamp
    * string details
    * enum eventType (create,updation,login,logout,error)
    * Map<string, object> data (eventData)
    * string entity
    * */

    public enum Type{
        CREATE, UPDATE, ERROR, LOGIN, LOGOUT
    }

    private String user;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime timeStamp;

    private String details;

    private Type eventType;

    private Map<String, Object> data;

    private String entity;

    public Event() {
        super();
        setExternalId(getId());
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public Type getEventType() {
        return eventType;
    }

    public void setEventType(Type eventType) {
        this.eventType = eventType;
    }

    public LocalDateTime getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(LocalDateTime timeStamp) {
        this.timeStamp = timeStamp;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public void setData(Map<String, Object> data) {
        this.data = data;
    }

    public String getEntity() {
        return entity;
    }

    public void setEntity(String entity) {
        this.entity = entity;
    }

    @Override
    protected void setExternalId() {

    }

    @Override
    public Event merge(Event event) {
        return null;
    }

    @Override
    public Map<String, String> toMap() {

        Map<String, String> map = new LinkedHashMap<>();
        map.put("user", getUser());
        map.put("entity", getEntity());
        map.put("details", getDetails());
        map.put("eventType", getEventType().name());
        map.put("timeStamp", String.valueOf(getTimeStamp()));
        map.put("data", String.valueOf(getData()));
        return map;
    }

}
