package com.bigname.core.util;

import org.junit.Assert;
import org.junit.Test;

import static com.bigname.core.util.Toggle.DISABLE;
import static com.bigname.core.util.Toggle.ENABLE;

/**
 * Created by sruthi on 23-02-2019.
 */
public class ToggleTest {

    @Test
    public void state() throws Exception {
      Assert.assertEquals(Toggle.ENABLE.state(), "Y");
      Assert.assertEquals(DISABLE.state(), "N");
    }

    @Test
    public void booleanValue() throws Exception {
        Assert.assertTrue(Toggle.ENABLE.booleanValue());
        Assert.assertFalse(DISABLE.booleanValue());
    }

    @Test
    public void get() throws Exception {
        Assert.assertEquals(Toggle.get("Y"), DISABLE);
        Assert.assertEquals(Toggle.get("N"), ENABLE);
    }

}