package com.bigname.pim.core.util;

import org.junit.Assert;
import org.junit.Test;

/**
 * Created by sruthi on 23-02-2019.
 */
public class ConvertUtilTest {

    @Test
    public void toBoolean() throws Exception {
        Assert.assertEquals(ConvertUtil.toBoolean(ConvertUtil.TRUE, ConvertUtil.BOOLEAN_DEFAULT), true);
        Assert.assertEquals(ConvertUtil.toBoolean(ConvertUtil.FALSE, ConvertUtil.BOOLEAN_DEFAULT), false);
        Assert.assertEquals(ConvertUtil.toBoolean(ConvertUtil.YES, ConvertUtil.BOOLEAN_DEFAULT), true);
        Assert.assertEquals(ConvertUtil.toBoolean(ConvertUtil.NO, ConvertUtil.BOOLEAN_DEFAULT), false);
        Assert.assertEquals(ConvertUtil.toBoolean(ConvertUtil.Y, ConvertUtil.BOOLEAN_DEFAULT), true);
        Assert.assertEquals(ConvertUtil.toBoolean(ConvertUtil.N, ConvertUtil.BOOLEAN_DEFAULT), false);
    }

}