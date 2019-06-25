package com.bigname.pim.client.web.controller;

import com.m7.xtreme.xcore.domain.Entity;
import com.m7.xtreme.xcore.domain.ValidatableEntity;
import com.m7.xtreme.xcore.exception.FileNotFoundException;
import com.m7.xtreme.xcore.service.BaseService;
import org.javatuples.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import static com.m7.xtreme.common.util.ValidationUtil.isEmpty;
import static com.m7.xtreme.common.util.ValidationUtil.isNotEmpty;


/**
 * @author Manu V NarayanaPrasad (manu@blacwood.com)
 * @since 1.0
 */
abstract public class ControllerSupport {

    @Value("${app.asset.location:/usr/local/pim/uploads/assets/}")
    protected String assetFileStorageLocation;

    protected Logger LOGGER = LoggerFactory.getLogger(ControllerSupport.class);

    abstract protected <E extends ValidatableEntity> Map<String, Pair<String, Object>> validate(E e, Map<String, Object> context, Class<?>... groups);

    @SuppressWarnings("unchecked")
    protected <E extends ValidatableEntity> boolean isValid(E e, Map<String, Object> model, Class<?>... groups) {
        Map<String, Object> context = model.containsKey("context") ? (Map<String, Object>) model.remove("context") : new HashMap<>();
        model.put("fieldErrors", validate(e, context, groups));
        model.put("group", e.getGroup());
        return isEmpty(model.get("fieldErrors"));
    }

    protected <T extends Entity> boolean isValid(T e, Map<String, Object> model, BaseService<T, ?> service, Class<?>... groups) {
        Map<String, Object> context = model.containsKey("context") ? (Map<String, Object>) model.remove("context") : new HashMap<>();
        model.put("fieldErrors", service.validate(e, context, groups));
        model.put("group", e.getGroup());
        return isEmpty(model.get("fieldErrors"));
    }

    protected Map<String, Object> getAttributesMap(HttpServletRequest request) {
        Map<String, Object> attributesMap = new HashMap<>();
        Map<String, String[]> parameterMap = request.getParameterMap();
        if(isNotEmpty(request)) {
            parameterMap.forEach((param, value) -> {
                if(value.length == 0) {
                    attributesMap.put(param, "");
                } else if(value.length == 1) {
                    attributesMap.put(param, value[0]);
                } else {
                    attributesMap.put(param, value);
                }
            });
        }
        return attributesMap;
    }

    protected String getReferrerURL(HttpServletRequest request, String defaultURL, String compareURL) {
        String referrer = request.getHeader("referer");
        if(referrer != null && referrer.contains("/pim/")) {
            referrer = referrer.substring(request.getHeader("referer").indexOf("/pim/"));
            referrer = referrer.substring(referrer.indexOf("/pim/"));
            if(referrer.startsWith(defaultURL) || (!"".equals(compareURL) && referrer.startsWith(compareURL))) {
                referrer = defaultURL;
            }
            String hash = request.getParameter("hash");
            if(hash != null) {
                referrer += "#" + hash;
            }
        } else {
            referrer = defaultURL;
        }
        return referrer;
    }

    protected ResponseEntity<Resource> downloadFile(String fileLocation, String fileName, HttpServletRequest request) {

        // Load file as Resource
        Resource resource = loadFileAsResource(fileLocation, fileName);

        // Try to determine file's content type
        String contentType = null;
        try {
            contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        } catch (IOException ex) {
            LOGGER.error("Could not determine file type.");
        }

        // Fallback to the default content type if type could not be determined
        if(contentType == null) {
            contentType = "application/octet-stream";
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }

    protected ResponseEntity<Resource> downloadAsset(String fileName, HttpServletRequest request) {
        return downloadFile(assetFileStorageLocation, fileName, request);
    }

    protected Resource loadFileAsResource(String fileLocation, String fileName) {
        try {
            Path filePath = Paths.get(fileLocation).resolve(fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if(resource.exists()) {
                return resource;
            } else {
                throw new FileNotFoundException("File not found " + fileName);
            }
        } catch (MalformedURLException ex) {
            throw new FileNotFoundException("File not found " + fileName, ex);
        }
    }
}