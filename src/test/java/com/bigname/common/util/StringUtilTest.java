package com.bigname.common.util;

import org.hibernate.validator.constraints.EAN;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;


/**
 * @author Manu V NarayanaPrasad (manu@blacwood.com)
 * @since 1.0
 */
public class StringUtilTest {
    @Test
    public void capitalize() throws Exception {
        Assert.assertEquals(StringUtil.capitalize("mynameisduke"), "Mynameisduke");
    }

    @Test
    public void toSnakeCase() throws Exception {
        Assert.assertEquals(StringUtil.toSnakeCase("MyNameIsDuke"), "MY_NAME_IS_DUKE");
        Assert.assertEquals(StringUtil.toSnakeCase("MyNameIsDuke", true), "my_name_is_duke");
    }

    @Test
    public void toCamelCase() throws Exception {
        Assert.assertEquals(StringUtil.toCamelCase("MY_NAME_IS_DUKE".toLowerCase()), "MyNameIsDuke");
        Assert.assertEquals(StringUtil.toCamelCase("MY_NAME_IS_DUKE", true), "myNameIsDuke");
        Assert.assertEquals(StringUtil.toCamelCase("my_name_is_duke", true), "myNameIsDuke");
    }

    @Test
    public void split() throws Exception {
        Assert.assertEquals(StringUtil.split("Horizontal 4-Corner;Horizontal 2-Corner (Top Right, Bottom Left);Horizontal 2-Corner (Top Left, Bottom Right);Vertical 4-Corner|Horizontal 4-Corner;Horizontal 2-Corner (Top Right, Bottom Left);Horizontal 2-Corner (Top Left, Bottom Right);Vertical 4-Corner|Horizontal 4-Corner;Horizontal 2-Corner (Top Right, Bottom Left);Horizontal 2-Corner (Top Left, Bottom Right);Vertical 4-Corner|Horizontal 4-Corner;Horizontal 2-Corner (Top Right, Bottom Left);Horizontal 2-Corner (Top Left, Bottom Right)|Horizontal 4-Corner;Horizontal 2-Corner (Top Right, Bottom Left);Horizontal 2-Corner (Top Left, Bottom Right);Vertical 4-Corner|Horizontal 4-Corner;Horizontal 2-Corner (Top Right, Bottom Left);Horizontal 2-Corner (Top Left, Bottom Right)", "|;".split("")).size(), 22);
        Assert.assertEquals(StringUtil.split("Single Value", new String[0]).size(), 1);
    }

    @Test
    public void split1() throws Exception {
        Assert.assertEquals(StringUtil.split("Horizontal 4-Corner;Horizontal 2-Corner (Top Right, Bottom Left);Horizontal 2-Corner (Top Left, Bottom Right);Vertical 4-Corner|Horizontal 4-Corner;Horizontal 2-Corner (Top Right, Bottom Left);Horizontal 2-Corner (Top Left, Bottom Right);Vertical 4-Corner|Horizontal 4-Corner;Horizontal 2-Corner (Top Right, Bottom Left);Horizontal 2-Corner (Top Left, Bottom Right);Vertical 4-Corner|Horizontal 4-Corner;Horizontal 2-Corner (Top Right, Bottom Left);Horizontal 2-Corner (Top Left, Bottom Right)|Horizontal 4-Corner;Horizontal 2-Corner (Top Right, Bottom Left);Horizontal 2-Corner (Top Left, Bottom Right);Vertical 4-Corner|Horizontal 4-Corner;Horizontal 2-Corner (Top Right, Bottom Left);Horizontal 2-Corner (Top Left, Bottom Right)",",").length, 13);
        Assert.assertEquals(StringUtil.split("Single Value", "").length, 1);
    }

    @Test
    public void concatinate() throws Exception {
        List<String> stringList=new ArrayList<>();
        stringList.add("Single");
        stringList.add("Value");
        Assert.assertEquals(StringUtil.concatinate(stringList," "), "Single Value");
    }

    @Test
    public void toMap() throws Exception {
        String[] stringArray= "name,Test1.com,externalId,TEST_1,url,www.test1.com,active,Y".split(",");
        Map<String, String> map=new HashMap<>();
        map.put("name", "Test1.com");
        map.put("externalId", "TEST_1");
        map.put("url", "www.test1.com");
        map.put("active", "Y");
        Assert.assertEquals(StringUtil.toMap(stringArray), map);
    }

    @Test
    public void getPipedValues() throws Exception {
        List<String> stringList=new ArrayList<>();
        stringList.add("my");
        stringList.add("name");
        stringList.add("is");
        stringList.add("duke");
      Assert.assertEquals(StringUtil.getPipedValues("my|name|is|duke"), stringList);
    }

    @Test
    public void getPipedValue() throws Exception {
        String[] str="my name is duke".split(" ");
        Assert.assertEquals(StringUtil.getPipedValue(str), "my|name|is|duke");
    }
   @Test
   public void getSimpleId() throws Exception {
       Assert.assertEquals(StringUtil.getSimpleId("my name is|duke"), "duke");
   }

    @Test
    public void splitPipeDelimited() throws Exception {
        Assert.assertEquals(StringUtil.splitPipeDelimited("my|name|is|duke"), "my name is duke".split(" "));
    }

    @Test
    public void splitPipeDelimitedAsList() throws Exception {
        List<String> stringList=new ArrayList<>();
        stringList.add("my");
        stringList.add("name");
        stringList.add("is");
        stringList.add("duke");
        Assert.assertEquals(StringUtil.splitPipeDelimitedAsList("my|name|is|duke"), stringList);
    }

    @Test
    public void getUniqueName() throws Exception {
        List<String> stringList=new ArrayList<>();
        stringList.add("my");
        stringList.add("name");
        stringList.add("is");
        stringList.add("duke");
        Assert.assertEquals(StringUtil.getUniqueName("duke", stringList),"duke_1");
    }

    @Test
    public void trim() throws Exception {
        Assert.assertEquals(StringUtil.trim("  my name is duke"), "my name is duke");
    }

    @Test
    public void trim1() throws Exception {
        Assert.assertEquals(StringUtil.trim(null,true),"");
        Assert.assertEquals(StringUtil.trim(null,false),null);
        Assert.assertEquals(StringUtil.trim("  my name is duke",true),"my name is duke");
    }
}