package com.bigname.pim.api.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.util.UUID;

/**
 * Created by manu on 8/18/18.
 */
@Document
abstract public class Entity<T> implements Serializable {
    @Id
    private String id;

    @Indexed(unique = true)
    @NotEmpty(message = "External Id cannot be empty")
    private String externalId;

    private String active;

    protected Entity() {
        this.id = UUID.randomUUID().toString();
    }

    public Entity(@NotEmpty(message = "External Id cannot be empty") String externalId) {
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
        this.active = active;
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

    abstract public T merge(T t);
}
