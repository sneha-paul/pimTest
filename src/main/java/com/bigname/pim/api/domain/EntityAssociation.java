package com.bigname.pim.api.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.UUID;

/**
 * Created by manu on 8/19/18.
 */
@Document
public class EntityAssociation {
    @Id
    private String id;

    private long sequenceNum;
    private int subSequenceNum;
    private String active;

    protected EntityAssociation() {
        setId(UUID.randomUUID().toString());
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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
}
