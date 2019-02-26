package com.bigname.common.util;

import org.junit.Assert;
import org.junit.Test;

/**
 * Created by dona on 26-02-2019.
 */
public class ConversionUtilTest {

    @Test
    public void toId() throws Exception {
        Assert.assertEquals(ConversionUtil.toId("envelope"), "ENVELOPE");
        Assert.assertEquals(ConversionUtil.toId("envelope data"), "ENVELOPE_DATA");
        Assert.assertEquals(ConversionUtil.toId("ENVELOPE_DATA"), "ENVELOPE_DATA");
    }

    @Test
    public void getValue() throws Exception {
        Assert.assertEquals(ConversionUtil.getValue("websiteId"), "websiteId");
    }

    @Test
    public void getValue1() throws Exception {
        int var = 123;
        Assert.assertEquals(ConversionUtil.getValue(var), 123);
    }

    @Test
    public void getValue2() throws Exception {
        boolean var = true;
        boolean var1 = false;
        Assert.assertEquals(ConversionUtil.getValue(var), true);
        Assert.assertEquals(ConversionUtil.getValue(var1), false);
    }

    /*@Test
    public void toGenericList() throws Exception {

        Website website = new Website();
        List<Website> websiteList = new ArrayList<>();

        Assert.assertEquals(ConversionUtil.toGenericList(website), websiteList);

    }*/
}
