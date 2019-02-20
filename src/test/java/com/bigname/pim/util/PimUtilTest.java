package com.bigname.pim.util;

import com.bigname.common.util.ConversionUtil;
import org.junit.Assert;
import org.junit.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.Assert.*;

/**
 * @author Manu V NarayanaPrasad (manu@blacwood.com)
 * @since 1.0
 */
public class PimUtilTest {
    @Test
    public void getActiveOptions() throws Exception {
        assertTrue(PimUtil.getValue(Boolean.TRUE, null));
        assertTrue(PimUtil.getValue(new boolean[] {true}).orElse(false));
        assertTrue(PimUtil.getValue(Boolean.TRUE, new boolean[0]));
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
    public void getValue() throws Exception {
    }

    @Test
    public void getValue1() throws Exception {
    }

    @Test
    public void getValue2() throws Exception {
    }

    @Test
    public void sort() throws Exception {
    }

    @Test
    public void getIdedMap() throws Exception {
    }

    @Test
    public void getTokenizedParameter() throws Exception {
    }

    @Test
    public void buildCriteria() throws Exception {
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