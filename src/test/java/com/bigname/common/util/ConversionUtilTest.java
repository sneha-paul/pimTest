package com.bigname.common.util;

import com.bigname.pim.api.domain.Website;
import com.bigname.pim.api.service.WebsiteService;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Created by dona on 26-02-2019.
 */
public class ConversionUtilTest {

    @Autowired
    WebsiteService websiteService;

    @Test
    public void toId() throws Exception {
        Assert.assertEquals(ConversionUtil.toId("envelope"), "ENVELOPE");
        Assert.assertEquals(ConversionUtil.toId("envelope data"), "ENVELOPE_DATA");
        Assert.assertEquals(ConversionUtil.toId("ENVELOPE_DATA"), "ENVELOPE_DATA");
        Assert.assertEquals(ConversionUtil.toId("ENVELOPE12"), "ENVELOPE12");
        Assert.assertEquals(ConversionUtil.toId("ENVELOPE 1"), "ENVELOPE_1");
        Assert.assertEquals(ConversionUtil.toId(""), "");


    }

    @Test
    public void getValue() throws Exception {
        Assert.assertEquals(ConversionUtil.getValue("websiteId"), "websiteId");
        Assert.assertEquals(ConversionUtil.getValue(""), "");
    }

    @Test
    public void getValue1() throws Exception {
        int var = 123;
        Assert.assertEquals(ConversionUtil.getValue(var), 123);
        Assert.assertEquals(ConversionUtil.getValue(""), "");

    }

    @Test
    public void getValue2() throws Exception {
        boolean var = true;
        boolean var1 = false;
        Assert.assertEquals(ConversionUtil.getValue(var), true);
        Assert.assertEquals(ConversionUtil.getValue(var1), false);
    }

    @Test
    public void toList() throws Exception {
        Website website = new Website();
        website.setWebsiteName("Envelope");

        List<Website> websiteList = new ArrayList<>();
        websiteList.add(website);
        List<Website> websiteList1 = new ArrayList<>();

        Assert.assertEquals(ConversionUtil.toList(website), websiteList);
        Assert.assertEquals(ConversionUtil.toList(null), websiteList1);

    }

    

    @Test
    public void toGenericList() throws Exception {

       /* Website website = new Website();
        website.setWebsiteName("Envelope");

        List<Website> websiteList = new ArrayList<>();
        websiteList.add(website);
        Assert.assertEquals(ConversionUtil.toGenericList(website), websiteList);*/

    }

    @Test
    public void toGenericMap() throws Exception {
        /*Website website = new Website();
        website.setWebsiteName("Envelope");
        website.setWebsiteId("ENVELOPE");


        Map<String, Object> websiteMap = new HashMap<>();
        websiteMap.put("activeFromDate", null);
        websiteMap.put("activeToDate", null);
        websiteMap.put("discontinuedToDate", null);
        websiteMap.put("lastModifiedDateTime", null);
        websiteMap.put("externalId", "ENVELOPE");
        websiteMap.put("active","N");
        websiteMap.put("createdDateTime", null);
        websiteMap.put("discontinued", "N");
        websiteMap.put("discontinuedFromDate", null);
        websiteMap.put("url", null);
        websiteMap.put("websiteName", "Envelope");
        websiteMap.put("websiteId", "ENVELOPE");
        websiteMap.put("lastModifiedUser", null);
        websiteMap.put("id", website.getId());
        websiteMap.put("createdUser", null);

        List<Map<String, Object>> websiteListMap = new ArrayList<>();
        websiteListMap.add(websiteMap);

        Assert.assertEquals(ConversionUtil.toGenericMap(website), websiteListMap);*/
    }

    @Test
    public void toGenericMap1() throws Exception {
        List<Object> websiteList =  new ArrayList<>();
        websiteList.add("Envelope");
        websiteList.add("URL");

        Map<String, Object> websiteMap = new HashMap<>();
        websiteMap.put("activeFromDate", null);
        websiteMap.put("activeToDate", null);
        websiteMap.put("discontinuedToDate", null);
        websiteMap.put("lastModifiedDateTime", null);
        websiteMap.put("externalId", "ENVELOPE");
        websiteMap.put("active","N");
        websiteMap.put("createdDateTime", null);
        websiteMap.put("discontinued", "N");
        websiteMap.put("discontinuedFromDate", null);
        websiteMap.put("url", null);
        websiteMap.put("websiteName", "Envelope");
        websiteMap.put("websiteId", "ENVELOPE");
        websiteMap.put("lastModifiedUser", null);
        //websiteMap.put("id", );
        websiteMap.put("createdUser", null);

        List<Map<String, Object>> websiteListMap = new ArrayList<>();
        websiteListMap.add(websiteMap);
    }

    @Test
    public void toJSONString() throws Exception {
        Website website = new Website();
        website.setWebsiteName("Envelope");
        website.setWebsiteId("ENVELOPE");

        String actual = "{\"id\":\""+website.getId()+"\",\"externalId\":\"ENVELOPE\",\"active\":\"N\",\"discontinued\":\"N\",\"activeFromDate\":null,\"activeToDate\":null,\"discontinuedFromDate\":null,\"discontinuedToDate\":null,\"createdUser\":null,\"createdDateTime\":null,\"lastModifiedUser\":null,\"lastModifiedDateTime\":null,\"websiteId\":\"ENVELOPE\",\"websiteName\":\"Envelope\",\"url\":null}";
        Assert.assertEquals(ConversionUtil.toJSONString(website),actual);
    }

    @Test
    public void toJSONMap() throws Exception {
        Website website = new Website();
        website.setWebsiteName("Envelope");
        website.setWebsiteId("ENVELOPE");

        Map<String, Object> websiteMap = new HashMap<>();
        websiteMap.put("activeFromDate", null);
        websiteMap.put("activeToDate", null);
        websiteMap.put("discontinuedToDate", null);
        websiteMap.put("lastModifiedDateTime", null);
        websiteMap.put("externalId", "ENVELOPE");
        websiteMap.put("active","N");
        websiteMap.put("createdDateTime", null);
        websiteMap.put("discontinued", "N");
        websiteMap.put("discontinuedFromDate", null);
        websiteMap.put("url", null);
        websiteMap.put("websiteName", "Envelope");
        websiteMap.put("websiteId", "ENVELOPE");
        websiteMap.put("lastModifiedUser", null);
        websiteMap.put("id", website.getId());
        websiteMap.put("createdUser", null);

        Assert.assertEquals(ConversionUtil.toJSONMap(website),websiteMap);
        Assert.assertEquals(ConversionUtil.toJSONMap(null),null);
    }

    @Test
    public void getFileSize() throws Exception {
        Assert.assertEquals(ConversionUtil.getFileSize(8),"1 KB");
        Assert.assertEquals(ConversionUtil.getFileSize(0),"0 KB");
    }

    @Test
    public void getEOD() throws Exception {
        LocalDate dateTest = LocalDate.of(2017, 11, 6);
        LocalDateTime actualDateTime = LocalDateTime.of(2017,11,6,23,59,59);

        Assert.assertEquals(ConversionUtil.getEOD(dateTest),actualDateTime);
    }


}
