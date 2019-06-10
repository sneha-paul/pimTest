package com.bigname.common.util;

import com.m7.common.util.BeanUtil;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

/**
 * Created by sruthi on 27-02-2019.
 */
public class BeanUtilTest {

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void getAllFields() throws Exception {
        Assert.assertEquals(BeanUtil.getAllFields(Parent.class).size(), 2);
        Assert.assertEquals(BeanUtil.getAllFields(Child.class).size(), 4);
        Assert.assertEquals(BeanUtil.getAllFields(GrandChild.class).size(), 6);
    }

    @Test
    public void getAllFieldNames() throws Exception {
        List<String> parentActual = Arrays.asList("field1", "field2");
        Assert.assertEquals(BeanUtil.getAllFieldNames(Parent.class), parentActual);
        List<String> childActual = Arrays.asList("field1", "field2", "field3", "field4");
        Assert.assertEquals(BeanUtil.getAllFieldNames(Child.class), childActual);
        List<String> grandChildActual = Arrays.asList("field1", "field2", "field3", "field4", "field5", "field6");
        Assert.assertEquals(BeanUtil.getAllFieldNames(GrandChild.class), grandChildActual);
    }

}

class Parent {
    private int field1;
    private int field2;

    public int getField1() {
        return field1;
    }

    public void setField1(int field1) {
        this.field1 = field1;
    }

    public int getField2() {
        return field2;
    }

    public void setField2(int field2) {
        this.field2 = field2;
    }
}

class Child extends Parent {
    private int field3;
    private int field4;

    public int getField3() {
        return field3;
    }

    public void setField3(int field3) {
        this.field3 = field3;
    }

    public int getField4() {
        return field4;
    }

    public void setField4(int field4) {
        this.field4 = field4;
    }
}

class GrandChild extends Child {
    private int field5;
    private int field6;

    public int getField5() {
        return field5;
    }

    public void setField5(int field5) {
        this.field5 = field5;
    }

    public int getField6() {
        return field6;
    }

    public void setField6(int field6) {
        this.field6 = field6;
    }
}