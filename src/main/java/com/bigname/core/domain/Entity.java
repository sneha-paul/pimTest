package com.bigname.core.domain;

import com.bigname.pim.api.domain.User;
import org.springframework.data.annotation.Id;
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

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate activeFrom;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate activeTo;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate discontinuedFrom;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate discontinuedTo;

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

    public LocalDate getActiveFrom() {
        return activeFrom;
    }

    public void setActiveFrom(LocalDate activeFrom) {
        this.activeFrom = activeFrom;
    }

    public LocalDate getActiveTo() {
        return activeTo;
    }

    public void setActiveTo(LocalDate activeTo) {
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

    public LocalDate getDiscontinuedFrom() {
        return discontinuedFrom;
    }

    public void setDiscontinuedFrom(LocalDate discontinuedFrom) {
        this.discontinuedFrom = discontinuedFrom;
    }

    public LocalDate getDiscontinuedTo() {
        return discontinuedTo;
    }

    public void setDiscontinuedTo(LocalDate discontinuedTo) {
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
        this.setDiscontinued(t.getDiscontinued());
        this.setDiscontinuedFrom(t.getDiscontinuedFrom());
        this.setDiscontinuedTo(t.getDiscontinuedTo());
    }

    @Override
    public void orchestrate() {
        super.orchestrate();
        liveOrchestration();

    }

    //Orchestration the needs to done based on date/time
    private void liveOrchestration() {
        //Orchestration is called prior to validation, perform discontinuation related orchestration if there is at least one date and the date range is valid
        if((getDiscontinuedFrom() != null && getDiscontinuedTo() != null && !getDiscontinuedFrom().isAfter(getDiscontinuedTo()))
                || !(getDiscontinuedFrom() != null && getDiscontinuedTo() != null)) {
            //If discontinueFromDate is not null and discontinueToDate is not null
            if (getDiscontinuedFrom() != null && getDiscontinuedTo() != null) {

                //If discontinueFromDate already passed and discontinueToDate not passed, discontinue the item
                if (!getDiscontinuedFrom().isAfter(LocalDate.now()) && !getDiscontinuedTo().isBefore(LocalDate.now())) {
                    setDiscontinued("Y");
                } else if (getDiscontinuedFrom().isAfter(LocalDate.now()) || getDiscontinuedTo().isBefore(LocalDate.now())) { // Otherwise set Discontinued to 'N'
                    setDiscontinued("N");
                }
            } else if (getDiscontinuedFrom() != null) { //If discontinueFromDate is not null and discontinueToDate is null

                //If discontinueFromDate already passed, discontinue the item
                if (!getDiscontinuedFrom().isAfter(LocalDate.now())) {
                    setDiscontinued("Y");
                } else if (getDiscontinuedFrom().isAfter(LocalDate.now())) { // Otherwise set Discontinued to 'N'
                    setDiscontinued("N");
                }
            } else if (getDiscontinuedTo() != null) { //If discontinueFromDate is null and discontinueToDate is not null

                //If discontinueToDate already passed, set Discontinued to 'N'
                if (getDiscontinuedTo().isBefore(LocalDate.now())) {
                    setDiscontinued("N");
                } else if (!getDiscontinuedTo().isBefore(LocalDate.now())) { // If its not passed, keep discontinued as 'Y'
                    setDiscontinued("Y");
                }
            }
        }
        //Orchestration is called prior to validation, perform activation related orchestration if there is at least one date and the date range is valid
        if((getActiveFrom() != null && getActiveTo() != null && !getActiveFrom().isAfter(getActiveTo()))
                || !(getActiveFrom() != null && getActiveTo() != null)) {
            //If activeFromDate is not null and activeToDate is not null
            if (getActiveFrom() != null && getActiveTo() != null) {

                //If activeFromDate already passed and activeToDate not passed, activate the item
                if (!getActiveFrom().isAfter(LocalDate.now()) && !getActiveTo().isBefore(LocalDate.now())) {
                    setActive("Y");
                } else if (getActiveFrom().isAfter(LocalDate.now()) || getActiveTo().isBefore(LocalDate.now())) { // Otherwise set Active to 'N'
                    setActive("N");
                }
            } else if (getActiveFrom() != null) { //If activeFromDate is not null and activeToDate is null

                //If activeFromDate already passed, activate the item
                if (!getActiveFrom().isAfter(LocalDate.now())) {
                    setActive("Y");
                } else if (getActiveFrom().isAfter(LocalDate.now())) { // Otherwise set Active to 'N'
                    setActive("N");
                }
            } else if (getActiveTo() != null) { //If activeFromDate is null and activeToDate is not null

                //If activeToDate already passed, set Active to 'N'
                if (getActiveTo().isBefore(LocalDate.now())) {
                    setActive("N");
                } else if (!getActiveTo().isBefore(LocalDate.now())) { // If its not passed, keep Active as 'Y'
                    setActive("Y");
                }
            }
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
