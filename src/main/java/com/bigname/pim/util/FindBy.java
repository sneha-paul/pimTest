package com.bigname.pim.util;

/**
 * Created by Manu on 8/7/2018.
 */
public enum FindBy {
    INTERNAL_ID, EXTERNAL_ID;

    public static FindBy findBy(boolean external) {
        return external ? EXTERNAL_ID : INTERNAL_ID;
    }
}
