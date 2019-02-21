package com.bigname.core.domain;

import com.bigname.pim.api.domain.User;
import com.bigname.pim.util.PimUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * Created by manu on 8/18/18.
 */
@Document
abstract public class Entity<T extends Entity<T>> extends ValidatableEntity implements Serializable {
    @Id
    private String id;

    @Indexed(unique = true)
    private String externalId;

    private String active = "N";

    private String discontinued = "N";

    @Transient
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate activeFromDate;

    @Transient
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate activeToDate;

    @JsonIgnore
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime activeFrom;

    @JsonIgnore
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime activeTo;

    @Transient
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate discontinuedFromDate;

    @Transient
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate discontinuedToDate;

    @JsonIgnore
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime discontinuedFrom;

    @JsonIgnore
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime discontinuedTo;

    private String createdUser;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime createdDateTime;

    private String lastModifiedUser;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime lastModifiedDateTime;

    protected Entity() {
        this.id = UUID.randomUUID().toString();
    }

    public Entity(String externalId) {
        this.id = UUID.randomUUID().toString();
        this.externalId = externalId;
        setExternalId();
    }

    public String getId() {
        return id;
    }

    protected void setId(String id) {
        this.id = id;
    }

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = isNotEmpty(externalId) ? externalId.toUpperCase() : externalId;
        setExternalId();
    }

    public String getActive() {
        return active;
    }

    public void setActive(String active) {
        if(active == null) {
            active = "N";
        }
        this.active = toYesNo(active,"Y");
    }

    public LocalDate getActiveFromDate() {
        return activeFromDate;
    }

    public void setActiveFromDate(LocalDate activeFromDate) {
        this.activeFromDate = activeFromDate;
    }

    public LocalDate getActiveToDate() {
        return activeToDate;
    }

    public void setActiveToDate(LocalDate activeToDate) {
        this.activeToDate = activeToDate;
    }

    public LocalDateTime getActiveFrom() {
        if(activeFrom == null && activeFromDate != null){
            activeFrom = activeFromDate.atStartOfDay();
        }
        return activeFrom;
    }

    public void setActiveFrom(LocalDateTime activeFrom) {
        this.activeFrom = activeFrom;
    }

    public LocalDateTime getActiveTo() {
        if(activeTo == null && activeToDate != null){
            activeTo = activeToDate.plusDays(1).atStartOfDay().minusSeconds(1);
        }
        return activeTo;
    }

    public void setActiveTo(LocalDateTime activeTo) {
        this.activeTo = activeTo;
    }

    public String getDiscontinued() {
        return discontinued;
    }

    public void setDiscontinued(String discontinued) {
        if(discontinued == null) {
            discontinued = "N";
        }
        this.discontinued = toYesNo(discontinued,"Y");
    }

    public LocalDate getDiscontinuedFromDate() {
        return discontinuedFromDate;
    }

    public void setDiscontinuedFromDate(LocalDate discontinuedFromDate) {
        this.discontinuedFromDate = discontinuedFromDate;
    }

    public LocalDate getDiscontinuedToDate() {
        return discontinuedToDate;
    }

    public void setDiscontinuedToDate(LocalDate discontinuedToDate) {
        this.discontinuedToDate = discontinuedToDate;
    }

    public LocalDateTime getDiscontinuedFrom() {
        return discontinuedFrom;
    }

    public void setDiscontinuedFrom(LocalDateTime discontinuedFrom) {
        this.discontinuedFrom = discontinuedFrom;
    }

    public LocalDateTime getDiscontinuedTo() {
        return discontinuedTo;
    }

    public void setDiscontinuedTo(LocalDateTime discontinuedTo) {
        this.discontinuedTo = discontinuedTo;
    }

    public String getCreatedUser() {
        return createdUser;
    }

    public void setCreatedUser(Optional<User> createdUser) {
        this.createdUser = createdUser.map(user -> user.getId()).orElse("");
    }

    public LocalDateTime getCreatedDateTime() {
        return createdDateTime;
    }

    public void setCreatedDateTime(LocalDateTime createdDateTime) {
        this.createdDateTime = createdDateTime;
    }

    public String getLastModifiedUser() {
        return lastModifiedUser;
    }

    public void setLastModifiedUser(Optional<User> lastModifiedUser) {
        this.lastModifiedUser = lastModifiedUser.map(user -> user.getId()).orElse("");
    }

    public LocalDateTime getLastModifiedDateTime() {
        return lastModifiedDateTime;
    }

    public void setLastModifiedDateTime(LocalDateTime lastModifiedDateTime) {
        this.lastModifiedDateTime = lastModifiedDateTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Entity)) return false;

        Entity entity = (Entity) o;

        if (!getId().equals(entity.getId())) return false;
        return getExternalId().equals(entity.getExternalId());
    }

    @Override
    public int hashCode() {
        int result = getId().hashCode();
        result = 31 * result + (getExternalId() != null ? getExternalId().hashCode() : 1288);
        return result;
    }

    abstract protected void setExternalId();

    public T cloneInstance() {
        return null;
    }

    protected String cloneValue(String value) {
        return value + "_COPY";
    }

    abstract public T merge(T t);

    protected void mergeBaseProperties(T t) {
        this.setActive(t.getActive());
        this.setActiveFrom(t.getActiveFrom());
        this.setActiveTo(t.getActiveTo());
        this.setDiscontinued(t.getDiscontinued());
        this.setDiscontinuedFrom(t.getDiscontinuedFrom());
        this.setDiscontinuedTo(t.getDiscontinuedTo());
    }

    @Override
    public void orchestrate() {
        super.orchestrate();
        getActiveFrom();
        getActiveTo();
        getDiscontinuedFrom();
        getDiscontinuedTo();
        liveOrchestration();

    }

    //Orchestration the needs to done based on date/time
    private void liveOrchestration() {
        if(PimUtil.hasDiscontinued(getDiscontinued(), getDiscontinuedFrom(), getDiscontinuedTo())){
            setDiscontinued("Y");
        } else {
            setDiscontinued("N");
        }

        if(PimUtil.isActive(getActive(), getActiveFrom(), getActiveTo())){
            setActive("Y");
        } else {
            setActive("N");
        }

        //If the item is currently active and discontinued, deactivate the item
        if (booleanValue(getActive()) && booleanValue(getDiscontinued())){
            setActive("N");
        }
    }

    abstract public Map<String, String> toMap();

    protected Map<String, String> getBasePropertiesMap() {
        orchestrate();
        Map<String, String> map = new LinkedHashMap<>();
        map.put("externalId", getExternalId());
        map.put("active", getActive());
        map.put("activeFrom", isNotNull(getActiveFrom()) ? getActiveFrom().format(DateTimeFormatter.ISO_LOCAL_DATE) : "");
        map.put("activeTo", isNotNull(getActiveTo()) ? getActiveTo().format(DateTimeFormatter.ISO_LOCAL_DATE) : "");
        map.put("discontinued", getDiscontinued());
        map.put("discontinuedFrom", isNotNull(getDiscontinuedFrom()) ? getDiscontinuedFrom().format(DateTimeFormatter.ISO_LOCAL_DATE) : "");
        map.put("discontinuedTo", isNotNull(getDiscontinuedTo()) ? getDiscontinuedTo().format(DateTimeFormatter.ISO_LOCAL_DATE) : "");
        return map;
    }

    public enum CloneType {
        LIGHT, SHALLOW, DEEP;

        public static CloneType find(String value) {
            for (CloneType type : values()) {
                if(type.name().equalsIgnoreCase(value)) {
                    return type;
                }
            }
            return LIGHT;
        }
    }

    public boolean equals(T t) {
        return false;
    }

    public boolean equals(Map<String,Object> t) {
        return false;
    }



}
