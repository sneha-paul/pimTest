package com.bigname.core.service;

import com.bigname.common.util.ConversionUtil;
import com.bigname.core.domain.Entity;
import com.bigname.core.exception.DuplicateEntityException;
import com.bigname.core.exception.EntityCreateException;
import com.bigname.core.util.FindBy;
import com.bigname.pim.PimApplication;
import com.bigname.pim.api.domain.Event;
import com.bigname.pim.api.domain.User;
import com.bigname.pim.api.domain.Website;
import com.bigname.pim.api.persistence.dao.WebsiteDAO;
import com.bigname.pim.api.service.EventService;
import com.bigname.pim.api.service.WebsiteService;

import com.google.common.base.Preconditions;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.aop.framework.AopContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

import static com.bigname.core.util.FindBy.EXTERNAL_ID;
import static com.bigname.core.util.FindBy.INTERNAL_ID;

/**
 * Created by sruthi on 20-02-2019.
 */
@ActiveProfiles("test")
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@ContextConfiguration(classes={PimApplication.class})
public class BaseServiceSupportTest {

    protected String entityName;
    protected String externalIdProperty;
    protected String externalIdPropertyLabel;

    @Autowired
    private WebsiteDAO websiteDAO;

    @Autowired
    private WebsiteService websiteService;

    @Autowired
    private EventService eventService;

    @Before
    public void setUp() {
        websiteDAO.getMongoTemplate().dropCollection(Website.class);
    }

    @Test
    public void create() {
        Website websiteDTO = new Website();
        websiteDTO.setWebsiteName("Test1.com");
        websiteDTO.setWebsiteId("TEST1");
        websiteDTO.setActive("Y");
        websiteDTO.setUrl("https://www.test1.com");

        Event event = new Event();
        try {
            websiteService.get(websiteDTO.getExternalId(), EXTERNAL_ID, false)
                    .ifPresent(t1 ->    {
                        throw new DuplicateEntityException("Another " + entityName + " instance exists with the given " + entityName + " id:" + websiteDTO.getExternalId());
                    });
            websiteDTO.setCreatedDateTime(LocalDateTime.now());
            websiteDTO.setCreatedUser(getCurrentUser());
            Website website = websiteDAO.insert(websiteDTO);
            if(!websiteDTO.getClass().equals(Event.class)) {
                event.setEntity(getEntityName());
                event.setTimeStamp(website.getCreatedDateTime());
                event.setUser(getCurrentUser().map(Entity::getId).orElse(""));
                event.setEventType(Event.Type.CREATE);
                event.setDetails("New " + getEntityName() + " instance created with " + getExternalIdPropertyLabel() + ":" + website.getExternalId());
                Map<String, Object> dataObj = ConversionUtil.toJSONMap(website);
                dataObj.put("createdDateTime", website.getCreatedDateTime());
                event.setData(dataObj);
            }

        } catch(Exception e) {
            String message = "An error occurred while creating the " + entityName + " due to: "+ e.getMessage();
            if(!websiteDTO.getClass().equals(Event.class)) {
                event.setEntity(getEntityName());
                event.setTimeStamp(websiteDTO.getCreatedDateTime());
                event.setUser(getCurrentUser().map(Entity::getId).orElse(""));
                event.setEventType(Event.Type.ERROR);
                event.setDetails(message);
                Map<String, Object> dataObj = ConversionUtil.toJSONMap(websiteDTO);
                dataObj.put("createdDateTime", websiteDTO.getCreatedDateTime());
                event.setData(dataObj);
            }
            throw new EntityCreateException(message, e);
        } finally {
            if(!websiteDTO.getClass().equals(Event.class)) {
                eventService.create(event);
            }
        }
    }

   /* @Test
    public void update() {
        Website websiteDTO = new Website();
        websiteDTO.setWebsiteName("Test1.com");
        websiteDTO.setWebsiteId("TEST1");
        websiteDTO.setActive("Y");
        websiteDTO.setUrl("https://www.test1.com");
        websiteDAO.insert(websiteDTO);

        Website websiteDTONew = new Website();
        websiteDTONew.setWebsiteName("Test1.com");
        websiteDTONew.setWebsiteId("TEST1");
        websiteDTONew.setActive("Y");
        websiteDTONew.setUrl("https://www.test11.com");

        FindBy findBy = INTERNAL_ID;

        Event event = new Event();
        try {
            Optional<Website> _t1 = websiteService.get(websiteDTO.getId(), findBy, false);
            Website _t = null;
            if(_t1.isPresent()) {
                if(findBy == INTERNAL_ID && !entityName.equals("productVariant")) { //TODO need to handle productVariant update from controller using INTERNAL_ID
                    Preconditions.checkState(websiteDTO.getId().equals(websiteDTONew.getId()), "Illegal operation");
                }
                Website t1 = _t1.get();
                if(!websiteDTONew.getExternalId().equals(t1.getExternalId())) {
                    websiteService.get(websiteDTONew.getExternalId(), EXTERNAL_ID, false)
                            .ifPresent(t2 ->    {
                                throw new DuplicateEntityException("Another " + entityName + " instance exists with the given " + entityName + " id:" + websiteDTONew.getExternalId());
                            });
                }
                t1.merge(websiteDTONew);
                t1.setLastModifiedDateTime(LocalDateTime.now());
                t1.setLastModifiedUser(getCurrentUser());
                _t = websiteDAO.save(t1);
                if(!websiteDTONew.getClass().equals(Event.class)) {
                    event.setEntity(getEntityName());
                    event.setTimeStamp(_t.getLastModifiedDateTime());
                    event.setUser(getCurrentUser().map(Entity::getId).orElse(""));
                    event.setEventType(Event.Type.UPDATE);
                    event.setDetails("Updated " + getEntityName() + " instance with " + getExternalIdPropertyLabel() + ":" + _t.getExternalId());
                    Map<String, Object> dataObj = ConversionUtil.toJSONMap(_t);
                    dataObj.put("lastModifiedDateTime", _t.getLastModifiedDateTime());
                    dataObj.put("createdDateTime", _t.getCreatedDateTime());
                    event.setData(dataObj);
                }
            }
        } catch (Exception e) {
            String message = "Illegal operation";
            if(!websiteDTONew.getClass().equals(Event.class)) {
                event.setEntity(getEntityName());
                event.setTimeStamp(LocalDateTime.now());
                event.setUser(getCurrentUser().map(Entity::getId).orElse(""));
                event.setEventType(Event.Type.ERROR);
                event.setDetails(message);
                Map<String, Object> dataObj = ConversionUtil.toJSONMap(websiteDTONew);
                dataObj.put("lastModifiedDateTime", websiteDTONew.getLastModifiedDateTime());
                event.setData(dataObj);
            }
            throw new IllegalStateException(message);
        } finally {
            if(!websiteDTONew.getClass().equals(Event.class)) {
                eventService.create(event);
            }
        }

    }*/

    public Optional<User> getCurrentUser() {
        return Optional.ofNullable(SecurityContextHolder.getContext())
                .map(SecurityContext::getAuthentication)
                .filter(Authentication::isAuthenticated)
                .map(Authentication::getPrincipal)
                .map(User.class::cast);
    }

    public String getExternalIdProperty() {
        return externalIdProperty;
    }

    public String getExternalIdPropertyLabel() {
        return externalIdPropertyLabel;
    }

    public String getEntityName() {
        return entityName;
    }

    @After
    public void tearDown() {
        websiteDAO.getMongoTemplate().dropCollection(Website.class);
    }

}
