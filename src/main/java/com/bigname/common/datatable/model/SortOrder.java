package com.bigname.common.datatable.model;

/**
 * Enum for sort order
 *
 * @author Manu V NarayanaPrasad (manu@blacwood.com)
 * @since 1.0
 */
public enum SortOrder {
    /** The asc. */
    ASC("ASC"),
    /** The desc. */
    DESC("DESC");

    /** The value. */
    private final String value;

    /**
     * Instantiates a new sort order.
     *
     * @param v
     *            the v
     */
    SortOrder(String v) {
        value = v;
    }

    /**
     * From value.
     *
     * @param v
     *            the v
     * @return the sort order
     */
    public static SortOrder fromValue(String v) {
        for (SortOrder c : SortOrder.values()) {
            if (c.name().equalsIgnoreCase(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

    /**
     * Value.
     *
     * @return the string
     */
    public String value() {
        return value;
    }
}
