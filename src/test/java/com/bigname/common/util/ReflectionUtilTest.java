package com.bigname.common.util;

import com.bigname.pim.api.domain.Website;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

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