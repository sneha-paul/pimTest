package com.bigname.common.datatable.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * The datatable result model
 *
 * @author Manu V NarayanaPrasad (manu@blacwood.com)
 * @since 1.0
 */
public class Result<T> {
    /** The draw. */
    private String draw;

    /** The records filtered. */
    private String recordsFiltered;

    /** The records total. */
    private String recordsTotal;

    /** The list of data objects. */
    @JsonProperty("data")
    List<T> dataObjects;

    /**
     * Gets the draw.
     *
     * @return the draw
     */
    public String getDraw() {
        return draw;
    }

    /**
     * Sets the draw.
     *
     * @param draw the draw to set
     */
    public void setDraw(String draw) {
        this.draw = draw;
    }

    /**
     * Gets the records filtered.
     *
     * @return the recordsFiltered
     */
    public String getRecordsFiltered() {
        return recordsFiltered;
    }

    /**
     * Sets the records filtered.
     *
     * @param recordsFiltered the recordsFiltered to set
     */
    public void setRecordsFiltered(String recordsFiltered) {
        this.recordsFiltered = recordsFiltered;
    }

    /**
     * Gets the records total.
     *
     * @return the recordsTotal
     */
    public String getRecordsTotal() {
        return recordsTotal;
    }

    /**
     * Sets the records total.
     *
     * @param recordsTotal the recordsTotal to set
     */
    public void setRecordsTotal(String recordsTotal) {
        this.recordsTotal = recordsTotal;
    }

    /**
     * Gets the list of data objects.
     *
     * @return the dataObjects
     */
    public List<T> getDataObjects() {
        return dataObjects;
    }

    /**
     * Sets the list of data objects.
     *
     * @param dataObjects the list Of dataObjects to set
     */
    public void setDataObjects(List<T> dataObjects) {
        this.dataObjects = dataObjects;
    }

}
