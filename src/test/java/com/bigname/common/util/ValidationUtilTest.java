package com.bigname.common.util;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author Manu V NarayanaPrasad (manu@blacwood.com)
 * @since 1.0
 */
public class ValidationUtilTest {
    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void isNull() throws Exception {
        assertTrue(ValidationUtil.isNull(""));
        //assertTrue("Id must not be null",false);
        //assertTrue("Name must not be null",true);
    }

    @Test
    public void isNotNull() throws Exception {
        assertTrue(ValidationUtil.isNotNull(""));
    }

    @Test
    public void isEmpty() throws Exception {
        assertTrue(ValidationUtil.isEmpty(""));
        assertTrue("Id must not be empty",true);
        assertTrue("Name must not be empty", true);
    }

    @Test
    public void isNotEmpty() throws Exception {
        assertFalse(ValidationUtil.isNotEmpty(""));
    }

}