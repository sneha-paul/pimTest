package com.bigname.pim.api.web.controller;

import com.bigname.pim.api.domain.Website;
import com.bigname.pim.api.service.WebsiteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

import static com.bigname.pim.util.ConvertUtil.toBoolean;
import static com.bigname.pim.util.Defaults.PAGE_SIZE;
import static com.bigname.pim.util.Defaults.PAGE_SIZES;
import static com.m7.xtreme.xcore.util.FindBy.findBy;

@RestController
@RequestMapping("pim/api/websites")
public class WebsiteResource {
    private final WebsiteService websiteService;


    @Autowired
    public WebsiteResource(WebsiteService websiteService) {
        this.websiteService = websiteService;
    }

    @RequestMapping(method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    public Website create(@RequestBody Website website) {

        return websiteService.create(website);
    }

    @RequestMapping(path = "/{id}", method = RequestMethod.GET)
    public Optional<Website> getWebsite(
            @PathVariable("id") String id,
            @RequestParam(value = "activeOnly", defaultValue = "true") String activeOnly,
            @RequestParam(value = "useExternalId", defaultValue = "false") String useExternalId) {

        return websiteService.get(id, findBy(toBoolean(useExternalId)), toBoolean(activeOnly));
    }

    @RequestMapping(path = "/{id}", method = RequestMethod.PUT)
    @ResponseStatus(HttpStatus.OK)
    public Website updateWebsite(
            @PathVariable String id,
            @RequestBody Website website,
            @RequestParam(value = "useExternalId", defaultValue = "true") String useExternalId) {

        return websiteService.update(id, findBy(toBoolean(useExternalId)), website);
    }

    @RequestMapping(method = RequestMethod.GET)
    public Page<Website> getWebsites(
            @RequestParam(value = "ids", required = false) String[] ids,
            @RequestParam(value = "page", required = false) int page,
            @RequestParam(value = "size", required = false) int size,
            @RequestParam(value = "sort", required = false) String[][] sort,
            @RequestParam(value = "activeOnly", defaultValue = "true") String activeOnly,
            @RequestParam(value = "useExternalId", defaultValue = "false") String useExternalId) {

        if(size < PAGE_SIZES[0]) {
            size = PAGE_SIZE;
        }
        if(ids != null && ids.length > 0) {
//            return websiteService.getWebsites(ids, findBy(toBoolean(useExternalId)), page, size, null, toBoolean(activeOnly))
        } else {

        }
        return null;
    }


}
