package com.bigname.core.util;

import org.junit.Assert;
import org.junit.Test;

import static com.bigname.core.util.FindBy.EXTERNAL_ID;
import static com.bigname.core.util.FindBy.INTERNAL_ID;

/**
 * Created by sruthi on 23-02-2019.
 */
public class FindByTest {

    @Test
    public void findBy() throws Exception {
        Assert.assertEquals(FindBy.findBy(true), EXTERNAL_ID);
        Assert.assertEquals(FindBy.findBy(false), INTERNAL_ID);
    }

}