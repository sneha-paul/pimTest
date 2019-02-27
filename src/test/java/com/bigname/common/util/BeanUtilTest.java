package com.bigname.common.util;

import com.bigname.pim.api.domain.Website;
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
       /* Website website1 = new Website();
        Class<? extends BeanUtilTest> website = getClass();
        List<Field> actual = new ArrayList<>();
       // actual.add(website1.);

        Assert.assertEquals(BeanUtil.getAllFields(Website.class),actual);*/
    }

    @Test
    public void getAllFieldNames() throws Exception {
        List<String> actual = Arrays.asList("group", "id", "externalId", "active", "discontinued", "activeFromDate", "activeToDate", "activeFrom", "activeTo", "discontinuedFromDate", "discontinuedToDate", "discontinuedFrom", "discontinuedTo", "createdUser", "createdDateTime", "lastModifiedUser", "lastModifiedDateTime", "websiteId", "websiteName", "url", "catalogs");
        Assert.assertEquals(BeanUtil.getAllFieldNames(Website.class),actual);
    }

}