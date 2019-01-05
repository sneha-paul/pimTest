package com.bigname.pim.client.web.controller;

import com.bigname.pim.api.domain.ValidatableEntity;
import org.javatuples.Pair;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

import static com.bigname.common.util.ValidationUtil.isEmpty;
import static com.bigname.common.util.ValidationUtil.isNotEmpty;

/**
 * @author Manu V NarayanaPrasad (manu@blacwood.com)
 * @since 1.0
 */
abstract public class ControllerSupport {
    abstract protected <E extends ValidatableEntity> Map<String, Pair<String, Object>> validate(E e, Map<String, Object> context, Class<?>... groups);

    @SuppressWarnings("unchecked")
    protected <E extends ValidatableEntity> boolean isValid(E e, Map<String, Object> model, Class<?>... groups) {
        Map<String, Object> context = model.containsKey("context") ? (Map<String, Object>) model.remove("context") : new HashMap<>();
        model.put("fieldErrors", validate(e, context, groups));
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
}
