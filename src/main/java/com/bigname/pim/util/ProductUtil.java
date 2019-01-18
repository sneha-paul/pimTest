package com.bigname.pim.util;

import java.util.List;
import java.util.Map;

/**
 * @author Manu V NarayanaPrasad (manu@blacwood.com)
 * @since 1.0
 */
public class ProductUtil {

    public static List<Map<String, Object>> orderAssets(List<Map<String, Object>> productAssets) {
        //order the list by sequenceNum ascending
        productAssets.sort((a1, a2) -> {
            int seq1 = (int) a1.get("sequenceNum");
            int seq2 = (int) a2.get("sequenceNum");
            return seq1 > seq2 ? 1 : -1;
        });
        return productAssets;
    }

}
