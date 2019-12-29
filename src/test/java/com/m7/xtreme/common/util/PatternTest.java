package com.m7.xtreme.common.util;

import com.m7.xtreme.common.util.Pattern;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by sruthi on 27-02-2019.
 */
public class PatternTest {
    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    /*@Test
    public void getRegEx() throws Exception {
        Assert.assertEquals(Pattern.NUMERIC.reg,"0-9");
        Assert.assertEquals(Pattern.LOWER_ALPHA.regEx,"a-z");
        Assert.assertEquals(Pattern.UPPER_ALPHA.regEx,"A-Z");
        Assert.assertEquals(Pattern.SPACE.regEx,"\\s");
        Assert.assertEquals(Pattern.HYPHEN.regEx,"-");
        Assert.assertEquals(Pattern.UNDERSCORE.regEx,"_");
    }*/

    @Test
    public void getRegEx1() throws Exception {

        Assert.assertEquals(Pattern.NUMERIC.getRegEx("*"),"[0-9*]");
        Assert.assertEquals(Pattern.LOWER_ALPHA.getRegEx("*"),"[a-z*]");
        Assert.assertEquals(Pattern.UPPER_ALPHA.getRegEx("*"),"[A-Z*]");
        Assert.assertEquals(Pattern.SPACE.getRegEx("*"),"[\\s*]");
        Assert.assertEquals(Pattern.HYPHEN.getRegEx("*"),"[-*]");
        Assert.assertEquals(Pattern.UNDERSCORE.getRegEx("*"),"[_*]");
    }

    @Test
    public void buildRegEx() throws Exception {
        Assert.assertEquals(Pattern.buildRegEx("a-z"),"[a-z]");
    }

}