package com.bigname.pim.client.web.controller;

import com.bigname.pim.api.domain.Config;
import com.bigname.pim.api.service.ConfigService;
import com.m7.common.util.CollectionsUtil;
import com.m7.xcore.exception.EntityNotFoundException;
import com.m7.xcore.util.FindBy;
import com.m7.xcore.web.controller.BaseController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by sanoop on 12/02/2019.
 */
@Controller
@RequestMapping("pim/configs")
public class ConfigController extends BaseController<Config, ConfigService> {

    private ConfigService configService;

    public ConfigController(ConfigService configService) {
        super(configService, Config.class);
        this.configService = configService;
    }

    //create channel
    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> create(Config config) {
        Map<String, Object> model = new HashMap<>();
        if (isValid(config, model, Config.CreateGroup.class)) {
            config.setActive("Y");
            configService.create(config);
            model.put("success", true);
        }
        return model;
    }

    //update channel
    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    @ResponseBody
    public Map<String, Object> update(@PathVariable(value = "id") String id, Config config) {
        Map<String, Object> model = new HashMap<>();
        model.put("context", CollectionsUtil.toMap("id", id));
        if (isValid(config, model, config.getGroup().length == 1 && config.getGroup()[0].equals("DETAILS") ? Config.DetailsGroup.class : null)) {
            configService.update(id, FindBy.EXTERNAL_ID, config);
            model.put("success", true);
            if (!id.equals(config.getConfigId())) {
                model.put("refreshUrl", "/pim/configs/" + config.getConfigId());
            }
        }
        return model;
    }

    // details channel
    @RequestMapping(value = {"/{id}", "/create"})
    public ModelAndView details(@PathVariable(value = "id", required = false) String id,
                                @RequestParam(name = "reload", required = false) boolean reload) {

        Map<String, Object> model = new HashMap<>();
        model.put("active", "CONFIG");
        model.put("mode", id == null ? "CREATE" : "DETAILS");
        model.put("view", "config/config" + (reload ? "_body" : ""));

        return id == null ? super.details(model) : configService.get(id, FindBy.EXTERNAL_ID, false)
                .map(config -> {
                    model.put("config", config);
                    return super.details(id, model);
                }).orElseThrow(() -> new EntityNotFoundException("Unable to find Config with Id: " + id));
    }


    //find all model and view
    @RequestMapping()
    public ModelAndView all() {
        Map<String, Object> model = new HashMap<>();
        model.put("active", "CONFIG");
        return new ModelAndView("config/configs", model);
    }

}