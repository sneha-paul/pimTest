package com.bigname.pim.api.domain;

import com.bigname.common.util.ValidationUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.javatuples.Pair;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;

import javax.validation.ConstraintViolation;
import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Created by manu on 8/18/18.
 */
@Document
abstract public class Entity<T> implements Serializable {
    @Id
    private String id;

    @Indexed(unique = true)
    private String externalId;

    private String active;

    @Transient
    @JsonIgnore
    private String group = "";

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
        this.active = active;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
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

    public Map<String, Pair<String, Object>> getValidationErrors(Set<ConstraintViolation<T>> violations) {
        Map<String, Pair<String, Object>> errors = new LinkedHashMap<>();
        if(violations.size() > 0) {
            violations.forEach(v -> {
                if(!errors.containsKey(v.getPropertyPath().toString())) {
                    errors.put(v.getPropertyPath().toString(), Pair.with(v.getMessage(), v.getInvalidValue()));
                }
            });
        }
        return errors;
    }

    abstract public T merge(T t);

    abstract public Map<String, String> toMap();

    public interface CreateGroup {}
    public interface DetailsGroup {}
}
