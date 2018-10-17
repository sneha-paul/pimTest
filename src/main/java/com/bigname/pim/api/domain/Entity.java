package com.bigname.pim.api.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.Map;
import java.util.UUID;

/**
 * Created by manu on 8/18/18.
 */
@Document
abstract public class Entity<T> extends ValidatableEntity implements Serializable {
    @Id
    private String id;

    @Indexed(unique = true)
    private String externalId;

    private String active;

    private String discontinued;

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
        this.externalId = externalId;
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

    public String getDiscontinued() {
        return discontinued;
    }

    public void setDiscontinued(String discontinued) {
        if(discontinued == null) {
            discontinued = "N";
        }
        this.discontinued = toYesNo(discontinued,"Y");
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
        result = 31 * result + getExternalId().hashCode();
        return result;
    }

    abstract void setExternalId();

    public T cloneInstance() {
        return null;
    }

    protected String cloneValue(String value) {
        return value + "_COPY";
    }

    abstract public T merge(T t);

    abstract public Map<String, String> toMap();

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

}
