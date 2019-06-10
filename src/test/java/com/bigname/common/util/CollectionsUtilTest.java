package com.bigname.common.util;

import com.m7.common.util.CollectionsUtil;
import org.junit.Assert;
import org.junit.Test;

import java.util.*;
import java.util.stream.Stream;

/**
 * Created by sruthi on 27-02-2019.
 */
public class CollectionsUtilTest {

    @Test
    public void filterMap() throws Exception {
        Map<String, Object> input=new HashMap<>();
        input.put("50", 9);
        input.put("25", 6);
        input.put("10", 3);
        List<String> keyList=new ArrayList<>();
        keyList.add("50");
        keyList.add("10");
        Map<String, Object> output=new HashMap<>();
        output.put("25", 6);
        Assert.assertEquals(CollectionsUtil.filterMap(input, keyList), output);
    }

    @Test
    public void toLinkedMap() throws Exception {
        Stream<String[]>
                str = Stream
                .of(new String[][] { { "a", "GeeksForGeeks" },
                        { "b", "geeks" },
                        { "c", "Geeks" },
                        { "d", "gks" },
                        { "e", "GeeksName" },
                        { "f", "GeeksPlace" }});
        LinkedHashMap map=new LinkedHashMap();
        map.put("a", "GeeksForGeeks");
        map.put("b", "geeks");
        map.put("c", "Geeks");
        map.put("d", "gks");
        map.put("e", "GeeksName");
        map.put("f", "GeeksPlace");
        Assert.assertEquals(str.collect( CollectionsUtil.toLinkedMap(p -> p[0], p -> p[1])), map);
    }

    @Test
    public void toTreeMap() throws Exception {
        Stream<String[]>
                str = Stream
                .of(new String[][] { { "a", "GeeksForGeeks" },
                        { "b", "geeks" },
                        { "c", "Geeks" },
                        { "d", "gks" },
                        { "e", "GeeksName" },
                        { "f", "GeeksPlace" }});
        TreeMap map=new TreeMap();
        map.put("a", "GeeksForGeeks");
        map.put("b", "geeks");
        map.put("c", "Geeks");
        map.put("d", "gks");
        map.put("e", "GeeksName");
        map.put("f", "GeeksPlace");

        Assert.assertEquals(str.collect( CollectionsUtil.toLinkedMap(p -> p[0], p -> p[1])), map);
    }

    @Test
    public void generifyMap() throws Exception {
        Map<String, Integer> input=new HashMap<>();
        input.put("50", 9);
        input.put("25", 6);
        input.put("10", 3);
        Map<String, Object> output=new HashMap<>();
        output.put("50", 9);
        output.put("25", 6);
        output.put("10", 3);

        Assert.assertEquals(CollectionsUtil.generifyMap(input), output);
    }

    @Test
    public void toMap() throws Exception {
        Object[] obj= new Object[]{ "name", "Test1.com", "externalId", "TEST_1", "url", "www.test1.com", "active", "Y" };
        Map<String, Object> map=new HashMap<>();
        map.put("name", "Test1.com");
        map.put("externalId", "TEST_1");
        map.put("url", "www.test1.com");
        map.put("active", "Y");
        Assert.assertEquals(CollectionsUtil.toMap(obj), map);
    }

    @Test
    public void compareMaps() throws Exception {
        Map<String, Object> map1=new HashMap<>();
        map1.put("50", 9);
        map1.put("25", 6);
        map1.put("10", 3);

        Map<String, Object> map2=new HashMap<>();
        map2.put("50", 4);
        map2.put("25", 6);
        map2.put("10", 66);

        Map<String, Object> out=new HashMap<>();
        map2.put("50", 9);
        map2.put("10", 3);
        Assert.assertEquals(CollectionsUtil.compareMaps(map1, map2), out);
    }

    @Test
    public void flattenMap() throws Exception {
        Map<String, Map<String, Object>> input = new HashMap<>();
        Map<String, Object> objInput=new HashMap<>();
        Map<String, Object> objInput1=new HashMap<>();
        Map<String, Object> objInput2=new HashMap<>();

        objInput.put("30", 9);
        objInput.put("45", 6);
        objInput.put("10", 3);
        input.put("plan", objInput);

        objInput1.put("50", 1);
        objInput1.put("25", 2);
        objInput1.put("10", 3);
        input.put("map3", objInput1);

        objInput2.put("50", 3);
        objInput2.put("25", 77);
        objInput2.put("20", 6);
        input.put("map2", objInput2);

        Map<String, Object> map=new HashMap<>();
        map.put("45", 6);
        map.put("25", 77);
        map.put("50", 3);
        map.put("30", 9);
        map.put("20", 6);
        map.put("10", 3);
        Assert.assertEquals(CollectionsUtil.flattenMap(input), map);
    }

    @Test
    public void buildMapString() throws Exception {
        Map<String, Map<String, Object>> input = new HashMap<>();
        Map<String, Object> objInput=new HashMap<>();
        Map<String, Object> objInput1=new HashMap<>();

        objInput.put("25", 6);
        objInput.put("50", 61);
        input.put("plan", objInput);

        objInput1.put("25", 45);
        objInput1.put("50", 20);
        input.put("map1", objInput1);

        String out= "25,0,45;50,0,20;25,1,6;50,1,61;";
        Assert.assertEquals(CollectionsUtil.buildMapString(input,0).toString(), out);
    }

}