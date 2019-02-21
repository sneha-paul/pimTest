package com.bigname.common.util;

import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.*;


/**
 * @author Manu V NarayanaPrasad (manu@blacwood.com)
 * @since 1.0
 */
public class StringUtilTest {
    @Test
    public void split() throws Exception {
        Assert.assertEquals(StringUtil.split("Horizontal 4-Corner;Horizontal 2-Corner (Top Right, Bottom Left);Horizontal 2-Corner (Top Left, Bottom Right);Vertical 4-Corner|Horizontal 4-Corner;Horizontal 2-Corner (Top Right, Bottom Left);Horizontal 2-Corner (Top Left, Bottom Right);Vertical 4-Corner|Horizontal 4-Corner;Horizontal 2-Corner (Top Right, Bottom Left);Horizontal 2-Corner (Top Left, Bottom Right);Vertical 4-Corner|Horizontal 4-Corner;Horizontal 2-Corner (Top Right, Bottom Left);Horizontal 2-Corner (Top Left, Bottom Right)|Horizontal 4-Corner;Horizontal 2-Corner (Top Right, Bottom Left);Horizontal 2-Corner (Top Left, Bottom Right);Vertical 4-Corner|Horizontal 4-Corner;Horizontal 2-Corner (Top Right, Bottom Left);Horizontal 2-Corner (Top Left, Bottom Right)", "|;".split("")).size(), 22);
        Assert.assertEquals(StringUtil.split("Single Value", new String[0]).size(), 1);
    }

}