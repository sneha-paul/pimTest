package com.bigname.pim.api.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Map;
import java.util.UUID;

/**
 * Created by manu on 8/19/18.
 */
@Document
abstract public class EntityAssociation<P extends Entity, C extends Entity> {
    @Id
    private String id;

    @Transient
    private P parent;

    @Transient
    private C child;

    private long sequenceNum;
    private int subSequenceNum;
    private String active;

    protected EntityAssociation init(P parent, C child) {
        this.parent = parent;
        this.child = child;
        return this;
    }

    protected EntityAssociation() {
        setId(UUID.randomUUID().toString());
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public P getParent() {
        return parent;
    }

    public void setParent(P parent) {
        this.parent = parent;
    }

    public C getChild() {
        return child;
    }

    public void setChild(C child) {
        this.child = child;
    }

    public long getSequenceNum() {
        return sequenceNum;
    }

    public void setSequenceNum(long sequenceNum) {
        this.sequenceNum = sequenceNum;
    }

    public int getSubSequenceNum() {
        return subSequenceNum;
    }

    public void setSubSequenceNum(int subSequenceNum) {
        this.subSequenceNum = subSequenceNum;
    }

    public String getActive() {
        return active;
    }

    public void setActive(String active) {
        this.active = active;
    }

    abstract public Map<String, String> toMap();
}
