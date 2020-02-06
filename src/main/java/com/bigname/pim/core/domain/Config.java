package com.bigname.pim.core.domain;

import com.m7.xtreme.common.util.ConversionUtil;
import com.m7.xtreme.xcore.domain.MongoEntity;
import org.springframework.data.mongodb.core.index.Indexed;

import javax.validation.constraints.NotEmpty;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;


/**
 * Created by sanoop on 12/02/2019.
 */

public class Config extends MongoEntity<Config> {

    @Indexed(unique = true)
    @NotEmpty(message = "Config name cannot be empty", groups = {CreateGroup.class, DetailsGroup.class})
    private String configName;


    @NotEmpty(message = "Config id cannot be empty", groups = {CreateGroup.class, DetailsGroup.class})
    private String configId;

    private Map<String, Map<String, String>> params = new HashMap<>();
    private Map<String, Map<String, String>> casePreservedParams = new HashMap<>();

    public Config() {
        super();
    }

    public String getConfigId() {
        return getExternalId();
    }

    public void setConfigId(String configId) {
        this.configId = configId;
        setExternalId(configId);
    }


    public String getConfigName() {
        return configName;
    }

    public void setConfigName(String configName) {
        this.configName = configName;
    }

    public Map<String, Map<String, String>> getParams() {
        return params;
    }

    public void setParams(Map<String, Map<String, String>> params) {
        this.params = params;
    }

    public Map<String, Map<String, String>> getCasePreservedParams() {
        return casePreservedParams;
    }

    public void setCasePreservedParams(Map<String, Map<String, String>> casePreservedParams) {
        this.casePreservedParams = casePreservedParams;
    }

    public Config setParameter(String name, String value, String... websiteId) {
        String _websiteId = ConversionUtil.getValue(websiteId);
        _websiteId = isEmpty(_websiteId) ? "GLOBAL" : _websiteId.toUpperCase();
        if(isNotEmpty(name)) {
            if (!params.containsKey(_websiteId)) {
                params.put(_websiteId, new HashMap<>());
                casePreservedParams.put(_websiteId, new HashMap<>());
            }
            params.get(_websiteId).put(name.toUpperCase(), value);
            String[] caseValue = value.split("\\|");
            casePreservedParams.get(_websiteId).put(name, caseValue[1]);
        }
        return this;
    }

    public <T> T getParameter(String name, Class<T> type, String... websiteId){
        String _websiteId = ConversionUtil.getValue(websiteId);
        _websiteId = isEmpty(_websiteId) ? "GLOBAL" : _websiteId.toUpperCase();
        return (T)params.get(_websiteId).get(name);
    }

    public Map<String, String> getSiteParameters(String... websiteId) {
        String _websiteId = ConversionUtil.getValue(websiteId);
        _websiteId = isEmpty(_websiteId) ? "GLOBAL" : _websiteId.toUpperCase();
        Map<String, String> paramMap = params.get(_websiteId);
        return paramMap == null ?  new HashMap<>() :  new HashMap<>(paramMap);
    }

    public Map<String, String> getCasePreservedSiteParameters(String... websiteId) {
        String _websiteId = ConversionUtil.getValue(websiteId);
        _websiteId = isEmpty(_websiteId) ? "GLOBAL" : _websiteId.toUpperCase();
        Map<String, String> casePreservedParamMap = casePreservedParams.get(_websiteId);
        return casePreservedParamMap == null ?  new HashMap<>() :  new HashMap<>(casePreservedParamMap);
        //return new HashMap<>(casePreservedParams.get(_websiteId));
    }

    @Override
    protected void setExternalId() {
        this.configId = getExternalId();

    }

    @Override
    public void orchestrate() {
        super.orchestrate();
        setDiscontinued(getDiscontinued());
        if (booleanValue(getActive()) && booleanValue(getDiscontinued())) {
            setActive("N");
        }
    }

    @Override
    public Config merge(Config config) {

        for (String group : config.getGroup()) {
            switch (group) {
                case "DETAILS":
                    this.setExternalId(config.getExternalId());
                    this.setConfigName(config.getConfigName());
                    mergeBaseProperties(config);
                    break;
                case "PARAMS":
                    this.setCasePreservedParams(config.getCasePreservedParams());
                    this.setParams(config.getParams());
                    Map<String, String> parameter = config.getSiteParameters();
                    parameter.forEach((k,v) -> {
                        String[] value = v.split("\\|");
                        this.setParameter(value[0], v);
                    });
                    break;
            }
        }
        return this;
    }

    @Override
    public Map<String, String> toMap () {

        Map<String, String> map = new LinkedHashMap<>();
        map.put("externalId", getExternalId());
        map.put("configName", getConfigName());
        map.put("active", getActive());
        map.put("discontinued", getDiscontinued());

        return map;
    }

}




