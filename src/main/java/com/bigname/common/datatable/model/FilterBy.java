package com.bigname.common.datatable.model;

import java.util.HashMap;
import java.util.Map;

/**
 * The FilterBy class
 *
 * @author Manu V NarayanaPrasad (manu@blacwood.com)
 * @since 1.0
 */
public class FilterBy {
    /** The map of filters. */
    private Map<String, String> filters = new HashMap<>();


    /** The global search. */
    private boolean globalSearch;

    /**
     * Instantiates a new filter by.
     */
    public FilterBy() {

    }

    /**
     * Gets the map of filters.
     *
     * @return the mapOfFilters
     */
    public Map<String, String> getFilters() {
        return filters;
    }

    /**
     * Adds the sort.
     *
     * @param filterColumn the filter column
     * @param filterValue the filter value
     */
    public void addFilter(String filterColumn, String filterValue) {
        filters.put(filterColumn, filterValue);
    }

    /**
     * Checks if is global search.
     *
     * @return the globalSearch
     */
    public boolean isGlobalSearch() {
        return globalSearch;
    }

    /**
     * Sets the global search.
     *
     * @param globalSearch the globalSearch to set
     */
    public void setGlobalSearch(boolean globalSearch) {
        this.globalSearch = globalSearch;
    }

}
