package com.bigname.core.util;

import com.m7.xtreme.xcore.util.FindBy;
import org.junit.Assert;
import org.junit.Test;

import static com.m7.xtreme.xcore.util.FindBy.EXTERNAL_ID;
import static com.m7.xtreme.xcore.util.FindBy.INTERNAL_ID;


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