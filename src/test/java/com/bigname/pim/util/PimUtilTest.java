package com.bigname.pim.util;

import com.bigname.pim.api.domain.Website;
import com.m7.xtreme.common.util.ConversionUtil;
import com.m7.xtreme.common.util.PimUtil;
import com.m7.xtreme.xcore.util.FindBy;
import org.junit.Assert;
import org.junit.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author Manu V NarayanaPrasad (manu@blacwood.com)
 * @since 1.0
 */
public class PimUtilTest {
    @Test
    public void getActiveOptions() throws Exception {
        assertTrue(PimUtil.getValueOrDefault(Boolean.TRUE, null));
        assertTrue(PimUtil.getValue(new boolean[] {true}).orElse(false));
        assertTrue(PimUtil.getValueOrDefault(Boolean.TRUE, new boolean[0]));
        assertFalse(PimUtil.getValue(new boolean[0]).orElse(false));

        assertTrue(PimUtil.getValue(0, new boolean[] {true}).orElse(false));
        assertTrue(PimUtil.getValue(1, new boolean[] {true, true}).orElse(false));
        assertTrue(PimUtil.getValue(2, new boolean[] {true, true, true}).orElse(false));
        assertFalse(PimUtil.getValue(2, new boolean[] {true, true, false}).orElse(false));

        String[] activeOptions = PimUtil.getActiveOptions();
        assertTrue(activeOptions.length == 1 && activeOptions[0].equals("Y"));
        assertFalse(PimUtil.showDiscontinued());

        activeOptions = PimUtil.getActiveOptions(false);
        assertTrue(activeOptions.length == 2 && activeOptions[0].equals("Y") && activeOptions[1].equals("N"));
        assertFalse(PimUtil.showDiscontinued(false));

        activeOptions = PimUtil.getActiveOptions(true);
        assertTrue(activeOptions.length == 1 && activeOptions[0].equals("Y"));
        assertFalse(PimUtil.showDiscontinued(true));

        activeOptions = PimUtil.getActiveOptions(false, false);
        assertTrue(activeOptions.length == 0);
        assertFalse(PimUtil.showDiscontinued(false, false));

        activeOptions = PimUtil.getActiveOptions(false, true);
        assertTrue(activeOptions.length == 1 && activeOptions[0].equals("N"));
        assertFalse(PimUtil.showDiscontinued(false, true));

        activeOptions = PimUtil.getActiveOptions(true, false);
        assertTrue(activeOptions.length == 1 && activeOptions[0].equals("Y"));
        assertFalse(PimUtil.showDiscontinued(true, false));

        activeOptions = PimUtil.getActiveOptions(true, true);
        assertTrue(activeOptions.length == 2 && activeOptions[0].equals("Y") && activeOptions[1].equals("N"));
        assertFalse(PimUtil.showDiscontinued(true, true));

        activeOptions = PimUtil.getActiveOptions(false, false, false);
        assertTrue(activeOptions.length == 0);
        assertFalse(PimUtil.showDiscontinued(false, false, false));

        activeOptions = PimUtil.getActiveOptions(false, false, true);
        assertTrue(activeOptions.length == 0);
        assertTrue(PimUtil.showDiscontinued(false, false, true));

        activeOptions = PimUtil.getActiveOptions(false, true, false);
        assertTrue(activeOptions.length == 1 && activeOptions[0].equals("N"));
        assertFalse(PimUtil.showDiscontinued(false, true, false));

        activeOptions = PimUtil.getActiveOptions(false, true, true);
        assertTrue(activeOptions.length == 1 && activeOptions[0].equals("N"));
        assertTrue(PimUtil.showDiscontinued(false, true, true));

        activeOptions = PimUtil.getActiveOptions(true, false, false);
        assertTrue(activeOptions.length == 1 && activeOptions[0].equals("Y"));
        assertFalse(PimUtil.showDiscontinued(true, false, false));

        activeOptions = PimUtil.getActiveOptions(true, false, true);
        assertTrue(activeOptions.length == 1 && activeOptions[0].equals("Y"));
        assertTrue(PimUtil.showDiscontinued(true, false, true));

        activeOptions = PimUtil.getActiveOptions(true, true, false);
        assertTrue(activeOptions.length == 2 && activeOptions[0].equals("Y") && activeOptions[1].equals("N"));
        assertFalse(PimUtil.showDiscontinued(true, true, false));

        activeOptions = PimUtil.getActiveOptions(true, true, true);
        assertTrue(activeOptions.length == 2 && activeOptions[0].equals("Y") && activeOptions[1].equals("N"));
        assertTrue(PimUtil.showDiscontinued(true, true, true));


    }

    @Test
    public void showDiscontinued() throws Exception {
        Assert.assertEquals(PimUtil.showDiscontinued(true), false);
        Assert.assertEquals(PimUtil.showDiscontinued(false), false);
    }

    @Test
    public void getActiveFlags() throws Exception {
        Assert.assertArrayEquals(PimUtil.getActiveFlags(true), new boolean[] {true, false, false});
        Assert.assertArrayEquals(PimUtil.getActiveFlags(false), new boolean[] {true, true, false});

    }

    @Test
    public void getLength() throws Exception {
        Assert.assertEquals(PimUtil.getLength(true), 1);
        Assert.assertEquals(PimUtil.getLength(), 0);
    }

    @Test
    public void getValue() throws Exception {
        Assert.assertEquals(PimUtil.getValue("name").toString(), Optional.of("name").toString());
        Assert.assertEquals(PimUtil.getValue("").toString(), Optional.of("").toString());
        Assert.assertEquals(PimUtil.getValue(0,false).toString(), Optional.of(false).toString());
        Assert.assertEquals(PimUtil.getValue(1,false).toString(), Optional.empty().toString());
        Assert.assertEquals(PimUtil.getValue(1,false).toString(), Optional.empty().toString());
        Assert.assertEquals(PimUtil.getValue(true).toString(), Optional.of(true).toString());
    }

    @Test
    public void getValueOrDefault() throws Exception {
        Assert.assertEquals(PimUtil.getValue(false,false).toString(), Optional.of(false).toString());
        Assert.assertEquals(PimUtil.getValue(false,true).toString(), Optional.of(false).toString());
        Assert.assertEquals(PimUtil.getValue(true,false).toString(), Optional.of(true).toString());
    }

    @Test
    public void sort() throws Exception {

        Website website = new Website();
        website.setWebsiteName("Envelope");
        website.setWebsiteId("ENVELOPE");
        website.setUrl("www.envelope.com");
        Website website1 = new Website();
        website1.setWebsiteName("folders");
        website1.setWebsiteId("FOLDERS");
        website1.setUrl("www.folders.com");
        List<Website> websiteList= new ArrayList<>();
        websiteList.add(website);
        websiteList.add(website1);

        List<String> sortedIds = new ArrayList<>();
        sortedIds.add("FOLDERS");
        sortedIds.add("ENVELOPE");

        List<Website> sortedList= new ArrayList<>();
        sortedList.add(website1);
        sortedList.add(website);

        Assert.assertEquals(PimUtil.sort(websiteList, sortedIds), sortedList);
    }

    @Test
    public void getIdedMap() throws Exception {
        Website website = new Website();
        website.setWebsiteName("Envelope");
        website.setWebsiteId("ENVELOPE");
        website.setUrl("www.envelope.com");

        List<Website> Ids = new ArrayList<>();
        Ids.add(website);

        Map<String, Website> map= new HashMap<>();
        map.put(website.getId(), website);

        Assert.assertEquals(PimUtil.getIdedMap(Ids, FindBy.INTERNAL_ID), map);
        Map<String, Website> map1= new HashMap<>();
        map1.put(website.getExternalId(), website);
        Assert.assertEquals(PimUtil.getIdedMap(Ids, FindBy.EXTERNAL_ID), map1);

    }

    @Test
    public void getTokenizedParameter() throws Exception {
        Map<String, String> map=new HashMap<>();
        map.put("my", "name");
        map.put("is", "duke");
        Assert.assertEquals(PimUtil.getTokenizedParameter("my|name|is|duke"), map);
    }

    @Test
    public void buildCriteria() throws Exception {
/*        Map<String, Object> map=new HashMap<>();
        map.put("name", "Test1.com");
        map.put("externalId", "TEST_1");
        map.put("url", "www.test1.com");
        map.put("active", "Y");
        Criteria criteria = new Criteria();

       Assert.assertEquals(PimUtil.buildCriteria(map), null);*/
        //TODO
    }

    @Test
    public void getStatusOptions() throws Exception {
        Assert.assertArrayEquals(PimUtil.getStatusOptions("Active"), new boolean[] {false, false, false});
        Assert.assertArrayEquals(PimUtil.getStatusOptions(""), new boolean[] {true,true,false});
    }

    @Test
    public void getTimestamp() throws Exception {
        Assert.assertEquals(PimUtil.getTimestamp(), LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")));
    }

    @Test
    public void isActive(){
        LocalDateTime today = LocalDate.now().atStartOfDay();
        LocalDateTime todayEOD = ConversionUtil.getEOD(LocalDate.now());
        LocalDateTime yesterday = today.minusDays(1);
        LocalDateTime yesterdayEOD = todayEOD.minusDays(1);
        LocalDateTime tomorrow = today.plusDays(1);
        LocalDateTime tomorrowEOD = todayEOD.plusDays(1);

        Assert.assertTrue(PimUtil.isActive("Y", null, null));
        Assert.assertFalse(PimUtil.isActive("N", null, null));

        Assert.assertTrue(PimUtil.isActive("Y", yesterday, null));
        Assert.assertTrue(PimUtil.isActive("N", yesterday, null));
        Assert.assertTrue(PimUtil.isActive("Y", today, null));
        Assert.assertTrue(PimUtil.isActive("N", today, null));
        Assert.assertTrue(PimUtil.isActive("Y", tomorrow, null));
        Assert.assertFalse(PimUtil.isActive("N", tomorrow, null));

        Assert.assertTrue(PimUtil.isActive("Y", null, yesterdayEOD));
        Assert.assertFalse(PimUtil.isActive("N", null, yesterdayEOD));
        Assert.assertTrue(PimUtil.isActive("Y", null, todayEOD));
        Assert.assertTrue(PimUtil.isActive("N", null, todayEOD));
        Assert.assertTrue(PimUtil.isActive("Y", null, tomorrowEOD));
        Assert.assertTrue(PimUtil.isActive("N", null ,tomorrowEOD));
    }

    @Test
    public void isBetween(){
        LocalDateTime today = LocalDate.now().atStartOfDay();
        LocalDateTime todayEOD = ConversionUtil.getEOD(LocalDate.now());
        LocalDateTime yesterday = today.minusDays(1);
        LocalDateTime yesterdayEOD = todayEOD.minusDays(1);
        LocalDateTime tomorrow = today.plusDays(1);
        LocalDateTime tomorrowEOD = todayEOD.plusDays(1);

        Assert.assertTrue(PimUtil.isActive("Y", null, null));
        Assert.assertFalse(PimUtil.isActive("N", null, null));

        Assert.assertTrue(PimUtil.isActive("Y", yesterday, null));
        Assert.assertTrue(PimUtil.isActive("N", yesterday, null));
        Assert.assertTrue(PimUtil.isActive("Y", today, null));
        Assert.assertTrue(PimUtil.isActive("N", today, null));
        Assert.assertTrue(PimUtil.isActive("Y", tomorrow, null));
        Assert.assertFalse(PimUtil.isActive("N", tomorrow, null));

        Assert.assertTrue(PimUtil.isActive("Y", null, yesterdayEOD));
        Assert.assertFalse(PimUtil.isActive("N", null, yesterdayEOD));
        Assert.assertTrue(PimUtil.isActive("Y", null, todayEOD));
        Assert.assertTrue(PimUtil.isActive("N", null, todayEOD));
        Assert.assertTrue(PimUtil.isActive("Y", null, tomorrowEOD));
        Assert.assertTrue(PimUtil.isActive("N", null ,tomorrowEOD));
    }

    @Test
    public void hasDiscontinued(){
        LocalDateTime today = LocalDate.now().atStartOfDay();
        LocalDateTime todayEOD = ConversionUtil.getEOD(LocalDate.now());
        LocalDateTime yesterday = today.minusDays(1);
        LocalDateTime yesterdayEOD = todayEOD.minusDays(1);
        LocalDateTime tomorrow = today.plusDays(1);
        LocalDateTime tomorrowEOD = todayEOD.plusDays(1);

        Assert.assertTrue(PimUtil.hasDiscontinued("Y", null, null));
        Assert.assertFalse(PimUtil.hasDiscontinued("N", null, null));

        Assert.assertTrue(PimUtil.hasDiscontinued("Y", yesterday, null));
        Assert.assertTrue(PimUtil.hasDiscontinued("N", yesterday, null));
        Assert.assertTrue(PimUtil.hasDiscontinued("Y", today, null));
        Assert.assertTrue(PimUtil.hasDiscontinued("N", today, null));
        Assert.assertTrue(PimUtil.hasDiscontinued("Y", tomorrow, null));
        Assert.assertFalse(PimUtil.hasDiscontinued("N", tomorrow, null));

        Assert.assertTrue(PimUtil.hasDiscontinued("Y", null, yesterdayEOD));
        Assert.assertFalse(PimUtil.hasDiscontinued("N", null, yesterdayEOD));
        Assert.assertTrue(PimUtil.hasDiscontinued("Y", null, todayEOD));
        Assert.assertTrue(PimUtil.hasDiscontinued("N", null, todayEOD));
        Assert.assertTrue(PimUtil.hasDiscontinued("Y", null, tomorrowEOD));
        Assert.assertTrue(PimUtil.hasDiscontinued("N", null ,tomorrowEOD));
    }
}