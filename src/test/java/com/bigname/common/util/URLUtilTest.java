package com.bigname.common.util;

import com.m7.common.util.URLUtil;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by sruthi on 27-02-2019.
 */
public class URLUtilTest {
    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void encode() throws Exception {
        String actual = "http%3A%2F%2Flocalhost%3A8081%2Fpim%2Fwebsites";
        Assert.assertEquals(URLUtil.encode("http://localhost:8081/pim/websites"), actual);
    }

}