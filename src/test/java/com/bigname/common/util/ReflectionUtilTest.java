package com.bigname.common.util;

import com.bigname.pim.api.domain.Website;
import com.m7.xtreme.common.util.ReflectionUtil;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by sruthi on 27-02-2019.
 */
public class ReflectionUtilTest {
    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void newInstance() throws Exception {
        Assert.assertNotEquals(ReflectionUtil.newInstance(Website.class), new Website());
    }

}