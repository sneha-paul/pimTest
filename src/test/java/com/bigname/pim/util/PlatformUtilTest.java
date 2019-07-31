package com.bigname.pim.util;

import com.bigname.pim.api.domain.Website;
import com.m7.xtreme.common.util.ConversionUtil;
import com.m7.xtreme.common.util.PlatformUtil;
import com.m7.xtreme.xcore.util.FindBy;
import com.m7.xtreme.xcore.util.ID;
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
public class PlatformUtilTest {
    @Test
    public void getActiveOptions() throws Exception {
        assertTrue(PlatformUtil.getValueOrDefault(Boolean.TRUE, null));
        assertTrue(PlatformUtil.getValue(new boolean[] {true}).orElse(false));
        assertTrue(PlatformUtil.getValueOrDefault(Boolean.TRUE, new boolean[0]));
        assertFalse(PlatformUtil.getValue(new boolean[0]).orElse(false));

        assertTrue(PlatformUtil.getValue(0, new boolean[] {true}).orElse(false));
        assertTrue(PlatformUtil.getValue(1, new boolean[] {true, true}).orElse(false));
        assertTrue(PlatformUtil.getValue(2, new boolean[] {true, true, true}).orElse(false));
        assertFalse(PlatformUtil.getValue(2, new boolean[] {true, true, false}).orElse(false));

        String[] activeOptions = PlatformUtil.getActiveOptions();
        assertTrue(activeOptions.length == 1 && activeOptions[0].equals("Y"));
        assertFalse(PlatformUtil.showDiscontinued());

        activeOptions = PlatformUtil.getActiveOptions(false);
        assertTrue(activeOptions.length == 2 && activeOptions[0].equals("Y") && activeOptions[1].equals("N"));
        assertFalse(PlatformUtil.showDiscontinued(false));

        activeOptions = PlatformUtil.getActiveOptions(true);
        assertTrue(activeOptions.length == 1 && activeOptions[0].equals("Y"));
        assertFalse(PlatformUtil.showDiscontinued(true));

        activeOptions = PlatformUtil.getActiveOptions(false, false);
        assertTrue(activeOptions.length == 0);
        assertFalse(PlatformUtil.showDiscontinued(false, false));

        activeOptions = PlatformUtil.getActiveOptions(false, true);
        assertTrue(activeOptions.length == 1 && activeOptions[0].equals("N"));
        assertFalse(PlatformUtil.showDiscontinued(false, true));

        activeOptions = PlatformUtil.getActiveOptions(true, false);
        assertTrue(activeOptions.length == 1 && activeOptions[0].equals("Y"));
        assertFalse(PlatformUtil.showDiscontinued(true, false));

        activeOptions = PlatformUtil.getActiveOptions(true, true);
        assertTrue(activeOptions.length == 2 && activeOptions[0].equals("Y") && activeOptions[1].equals("N"));
        assertFalse(PlatformUtil.showDiscontinued(true, true));

        activeOptions = PlatformUtil.getActiveOptions(false, false, false);
        assertTrue(activeOptions.length == 0);
        assertFalse(PlatformUtil.showDiscontinued(false, false, false));

        activeOptions = PlatformUtil.getActiveOptions(false, false, true);
        assertTrue(activeOptions.length == 0);
        assertTrue(PlatformUtil.showDiscontinued(false, false, true));

        activeOptions = PlatformUtil.getActiveOptions(false, true, false);
        assertTrue(activeOptions.length == 1 && activeOptions[0].equals("N"));
        assertFalse(PlatformUtil.showDiscontinued(false, true, false));

        activeOptions = PlatformUtil.getActiveOptions(false, true, true);
        assertTrue(activeOptions.length == 1 && activeOptions[0].equals("N"));
        assertTrue(PlatformUtil.showDiscontinued(false, true, true));

        activeOptions = PlatformUtil.getActiveOptions(true, false, false);
        assertTrue(activeOptions.length == 1 && activeOptions[0].equals("Y"));
        assertFalse(PlatformUtil.showDiscontinued(true, false, false));

        activeOptions = PlatformUtil.getActiveOptions(true, false, true);
        assertTrue(activeOptions.length == 1 && activeOptions[0].equals("Y"));
        assertTrue(PlatformUtil.showDiscontinued(true, false, true));

        activeOptions = PlatformUtil.getActiveOptions(true, true, false);
        assertTrue(activeOptions.length == 2 && activeOptions[0].equals("Y") && activeOptions[1].equals("N"));
        assertFalse(PlatformUtil.showDiscontinued(true, true, false));

        activeOptions = PlatformUtil.getActiveOptions(true, true, true);
        assertTrue(activeOptions.length == 2 && activeOptions[0].equals("Y") && activeOptions[1].equals("N"));
        assertTrue(PlatformUtil.showDiscontinued(true, true, true));


    }

    @Test
    public void showDiscontinued() throws Exception {
        Assert.assertEquals(PlatformUtil.showDiscontinued(true), false);
        Assert.assertEquals(PlatformUtil.showDiscontinued(false), false);
    }

    @Test
    public void getActiveFlags() throws Exception {
        Assert.assertArrayEquals(PlatformUtil.getActiveFlags(true), new boolean[] {true, false, false});
        Assert.assertArrayEquals(PlatformUtil.getActiveFlags(false), new boolean[] {true, true, false});

    }

    @Test
    public void getLength() throws Exception {
        Assert.assertEquals(PlatformUtil.getLength(true), 1);
        Assert.assertEquals(PlatformUtil.getLength(), 0);
    }

    @Test
    public void getValue() throws Exception {
        Assert.assertEquals(PlatformUtil.getValue("name").toString(), Optional.of("name").toString());
        Assert.assertEquals(PlatformUtil.getValue("").toString(), Optional.of("").toString());
        Assert.assertEquals(PlatformUtil.getValue(0,false).toString(), Optional.of(false).toString());
        Assert.assertEquals(PlatformUtil.getValue(1,false).toString(), Optional.empty().toString());
        Assert.assertEquals(PlatformUtil.getValue(1,false).toString(), Optional.empty().toString());
        Assert.assertEquals(PlatformUtil.getValue(true).toString(), Optional.of(true).toString());
    }

    @Test
    public void getValueOrDefault() throws Exception {
        Assert.assertEquals(PlatformUtil.getValue(false,false).toString(), Optional.of(false).toString());
        Assert.assertEquals(PlatformUtil.getValue(false,true).toString(), Optional.of(false).toString());
        Assert.assertEquals(PlatformUtil.getValue(true,false).toString(), Optional.of(true).toString());
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

        Assert.assertEquals(PlatformUtil.sort(websiteList, sortedIds), sortedList);
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

        Assert.assertEquals(PlatformUtil.getIdedMap(Ids, ID.Type.INTERNAL_ID), map);
        Map<String, Website> map1= new HashMap<>();
        map1.put(website.getExternalId(), website);
        Assert.assertEquals(PlatformUtil.getIdedMap(Ids, ID.Type.EXTERNAL_ID), map1);

    }

    @Test
    public void getTokenizedParameter() throws Exception {
        Map<String, String> map=new HashMap<>();
        map.put("my", "name");
        map.put("is", "duke");
        Assert.assertEquals(PlatformUtil.getTokenizedParameter("my|name|is|duke"), map);
    }

    @Test
    public void buildCriteria() throws Exception {
/*        Map<String, Object> map=new HashMap<>();
        map.put("name", "Test1.com");
        map.put("externalId", "TEST_1");
        map.put("url", "www.test1.com");
        map.put("active", "Y");
        Criteria criteria = new Criteria();

       Assert.assertEquals(PlatformUtil.buildCriteria(map), null);*/
        //TODO
    }

    @Test
    public void getStatusOptions() throws Exception {
        Assert.assertArrayEquals(PlatformUtil.getStatusOptions("Active"), new boolean[] {false, false, false});
        Assert.assertArrayEquals(PlatformUtil.getStatusOptions(""), new boolean[] {true,true,false});
    }

    @Test
    public void getTimestamp() throws Exception {
        Assert.assertEquals(PlatformUtil.getTimestamp(), LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")));
    }

    @Test
    public void isActive(){
        LocalDateTime today = LocalDate.now().atStartOfDay();
        LocalDateTime todayEOD = ConversionUtil.getEOD(LocalDate.now());
        LocalDateTime yesterday = today.minusDays(1);
        LocalDateTime yesterdayEOD = todayEOD.minusDays(1);
        LocalDateTime tomorrow = today.plusDays(1);
        LocalDateTime tomorrowEOD = todayEOD.plusDays(1);

        Assert.assertTrue(PlatformUtil.isActive("Y", null, null));
        Assert.assertFalse(PlatformUtil.isActive("N", null, null));

        Assert.assertTrue(PlatformUtil.isActive("Y", yesterday, null));
        Assert.assertTrue(PlatformUtil.isActive("N", yesterday, null));
        Assert.assertTrue(PlatformUtil.isActive("Y", today, null));
        Assert.assertTrue(PlatformUtil.isActive("N", today, null));
        Assert.assertTrue(PlatformUtil.isActive("Y", tomorrow, null));
        Assert.assertFalse(PlatformUtil.isActive("N", tomorrow, null));

        Assert.assertTrue(PlatformUtil.isActive("Y", null, yesterdayEOD));
        Assert.assertFalse(PlatformUtil.isActive("N", null, yesterdayEOD));
        Assert.assertTrue(PlatformUtil.isActive("Y", null, todayEOD));
        Assert.assertTrue(PlatformUtil.isActive("N", null, todayEOD));
        Assert.assertTrue(PlatformUtil.isActive("Y", null, tomorrowEOD));
        Assert.assertTrue(PlatformUtil.isActive("N", null ,tomorrowEOD));
    }

    @Test
    public void isBetween(){
        LocalDateTime today = LocalDate.now().atStartOfDay();
        LocalDateTime todayEOD = ConversionUtil.getEOD(LocalDate.now());
        LocalDateTime yesterday = today.minusDays(1);
        LocalDateTime yesterdayEOD = todayEOD.minusDays(1);
        LocalDateTime tomorrow = today.plusDays(1);
        LocalDateTime tomorrowEOD = todayEOD.plusDays(1);

        Assert.assertTrue(PlatformUtil.isActive("Y", null, null));
        Assert.assertFalse(PlatformUtil.isActive("N", null, null));

        Assert.assertTrue(PlatformUtil.isActive("Y", yesterday, null));
        Assert.assertTrue(PlatformUtil.isActive("N", yesterday, null));
        Assert.assertTrue(PlatformUtil.isActive("Y", today, null));
        Assert.assertTrue(PlatformUtil.isActive("N", today, null));
        Assert.assertTrue(PlatformUtil.isActive("Y", tomorrow, null));
        Assert.assertFalse(PlatformUtil.isActive("N", tomorrow, null));

        Assert.assertTrue(PlatformUtil.isActive("Y", null, yesterdayEOD));
        Assert.assertFalse(PlatformUtil.isActive("N", null, yesterdayEOD));
        Assert.assertTrue(PlatformUtil.isActive("Y", null, todayEOD));
        Assert.assertTrue(PlatformUtil.isActive("N", null, todayEOD));
        Assert.assertTrue(PlatformUtil.isActive("Y", null, tomorrowEOD));
        Assert.assertTrue(PlatformUtil.isActive("N", null ,tomorrowEOD));
    }

    @Test
    public void hasDiscontinued(){
        LocalDateTime today = LocalDate.now().atStartOfDay();
        LocalDateTime todayEOD = ConversionUtil.getEOD(LocalDate.now());
        LocalDateTime yesterday = today.minusDays(1);
        LocalDateTime yesterdayEOD = todayEOD.minusDays(1);
        LocalDateTime tomorrow = today.plusDays(1);
        LocalDateTime tomorrowEOD = todayEOD.plusDays(1);

        Assert.assertTrue(PlatformUtil.hasDiscontinued("Y", null, null));
        Assert.assertFalse(PlatformUtil.hasDiscontinued("N", null, null));

        Assert.assertTrue(PlatformUtil.hasDiscontinued("Y", yesterday, null));
        Assert.assertTrue(PlatformUtil.hasDiscontinued("N", yesterday, null));
        Assert.assertTrue(PlatformUtil.hasDiscontinued("Y", today, null));
        Assert.assertTrue(PlatformUtil.hasDiscontinued("N", today, null));
        Assert.assertTrue(PlatformUtil.hasDiscontinued("Y", tomorrow, null));
        Assert.assertFalse(PlatformUtil.hasDiscontinued("N", tomorrow, null));

        Assert.assertTrue(PlatformUtil.hasDiscontinued("Y", null, yesterdayEOD));
        Assert.assertFalse(PlatformUtil.hasDiscontinued("N", null, yesterdayEOD));
        Assert.assertTrue(PlatformUtil.hasDiscontinued("Y", null, todayEOD));
        Assert.assertTrue(PlatformUtil.hasDiscontinued("N", null, todayEOD));
        Assert.assertTrue(PlatformUtil.hasDiscontinued("Y", null, tomorrowEOD));
        Assert.assertTrue(PlatformUtil.hasDiscontinued("N", null ,tomorrowEOD));
    }
}