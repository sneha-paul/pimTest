package com.bigname.common.datatable.model;

import java.util.HashMap;
import java.util.Map;

/**
 * The SortBy class
 *
 * @author Manu V NarayanaPrasad (manu@blacwood.com)
 * @since 1.0
 */
public class SortBy {
    /** The map of sorts. */
    private Map<String, SortOrder> sorts = new HashMap<>();

    /**
     * Instantiates a new sort by.
     */
    public SortBy() {

    }

    /**
     * Gets the sort bys.
     *
     * @return the sortBys
     */
    public Map<String, SortOrder> getSorts() {
        return sorts;
    }

    /**
     * Adds the sort.
     *
     * @param sortBy the sort by
     */
    public void addSort(String sortBy) {
        sorts.put(sortBy, SortOrder.ASC);
    }

    /**
     * Adds the sort.
     *
     * @param sortBy the sort by
     * @param sortOrder the sort order
     */
    public void addSort(String sortBy, SortOrder sortOrder) {
        sorts.put(sortBy, sortOrder);
    }

}
