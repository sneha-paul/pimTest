package com.bigname.pim.client.web.controller;

import com.bigname.common.datatable.model.Result;
import com.bigname.common.util.CollectionsUtil;
import com.bigname.core.exception.EntityNotFoundException;
import com.bigname.core.util.FindBy;
import com.bigname.core.web.controller.BaseController;
import com.bigname.pim.api.domain.AssetFamily;
import com.bigname.pim.api.service.AssetFamilyService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by sanoop on 14/02/2019.
 */
@Controller
@RequestMapping("pim/assetFamilies")
public class AssetFamilyController extends BaseController<AssetFamily ,AssetFamilyService>{
    private AssetFamilyService assetFamilyService;

    public AssetFamilyController(AssetFamilyService assetFamilyService) {
        super(assetFamilyService, AssetFamily.class);
        this.assetFamilyService = assetFamilyService;
    }

    //create assetsFamily
    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> create(AssetFamily assetFamily) {
        Map<String, Object> model = new HashMap<>();
        if (isValid(assetFamily, model, AssetFamily.CreateGroup.class)) {
            assetFamily.setActive("N");
            assetFamilyService.create(assetFamily);
            model.put("success", true);
        }
        return model;
    }

    //update channel
    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    @ResponseBody
    public Map<String, Object> update(@PathVariable(value = "id") String id, AssetFamily assetFamily) {
        Map<String, Object> model = new HashMap<>();
        model.put("context", CollectionsUtil.toMap("id", id));
        if (isValid(assetFamily, model, assetFamily.getGroup().length == 1 && assetFamily.getGroup()[0].equals("DETAILS") ? AssetFamily.DetailsGroup.class : null)) {
            assetFamilyService.update(id, FindBy.EXTERNAL_ID, assetFamily);
            model.put("success", true);
            if (!id.equals(assetFamily.getAssetFamilyName())) {
                model.put("refreshUrl", "/pim/assetFamilies/" + assetFamily.getAssetFamilyId());
            }
        }
        return model;
    }

    // details channel
    @RequestMapping(value = {"/{id}", "/create"})
    public ModelAndView details(@PathVariable(value = "id", required = false) String id,
                                @RequestParam(name = "reload", required = false) boolean reload) {

        Map<String, Object> model = new HashMap<>();
        model.put("active", "ASSET_FAMILY");
        model.put("mode", id == null ? "CREATE" : "DETAILS");
        model.put("view", "settings/assetFamily" + (reload ? "_body" : ""));

        return id == null ? super.details(model) : assetFamilyService.get(id, FindBy.EXTERNAL_ID, false)
                .map(assetFamily -> {
                    model.put("assetFamily", assetFamily);
                    return super.details(id, model);
                }).orElseThrow(() -> new EntityNotFoundException("Unable to find AssetFamily with Id: " + id));
    }

    //find all model and view
    @RequestMapping()
    public ModelAndView all() {
        Map<String, Object> model = new HashMap<>();
        model.put("active", "ASSET_FAMILY");
        model.put("view", "settings/assetFamilies");
        model.put("title", "Asset Families");
        return new ModelAndView("settings/assetFamilies",model);
    }

    @RequestMapping(value =  {"/list", "/data"})
    @ResponseBody
    @SuppressWarnings("unchecked")
    public Result<Map<String, String>> all(HttpServletRequest request) {
        return all(request, "assetFamilyName");
    }


}
