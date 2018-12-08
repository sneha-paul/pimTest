package com.bigname.pim.util;

import java.util.Arrays;
import java.util.Set;
import java.util.TreeSet;

/**
 * @author Manu V NarayanaPrasad (manu@blacwood.com)
 * @since 1.0
 */
public class PIMConstants {
    public static final String DEFAULT_CHANNEL_ID = "ECOMMERCE";

    public static final int MAX_FETCH_SIZE = 300;

    public static class EncodedCharater {
        public static final String PIPE = "%7C";
    }

    public static Set<Integer> DEFAULT_QUANTITY_BREAKS = new TreeSet<>(Arrays.asList(50, 100, 250, 500, 1000));
}
