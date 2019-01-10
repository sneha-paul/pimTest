package com.bigname.pim.client.web.controller;

import com.bigname.common.util.CollectionsUtil;
import com.bigname.common.util.ValidationUtil;
import com.bigname.pim.api.domain.AssetCollection;
import com.bigname.pim.api.domain.VirtualFile;
import com.bigname.pim.api.exception.EntityNotFoundException;
import com.bigname.pim.api.service.AssetCollectionService;
import com.bigname.pim.api.service.VirtualFileService;
import com.bigname.pim.util.FindBy;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Manu V NarayanaPrasad (manu@blacwood.com)
 * @since 1.0
 */
@Controller
@RequestMapping("pim/assetCollections")
public class AssetCollectionController extends BaseController<AssetCollection, AssetCollectionService> {

    private AssetCollectionService assetCollectionService;
    private VirtualFileService assetService;

    public AssetCollectionController(AssetCollectionService assetCollectionService, VirtualFileService assetService) {
        super(assetCollectionService, AssetCollection.class);
        this.assetCollectionService = assetCollectionService;
        this.assetService = assetService;
    }

    @RequestMapping()
    public ModelAndView all() {
        Map<String, Object> model = new HashMap<>();
        model.put("active", "ASSET_COLLECTIONS");
        return new ModelAndView("settings/assetCollections", model);
    }

    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> create( AssetCollection assetCollection) {
        Map<String, Object> model = new HashMap<>();
        if(isValid(assetCollection, model, AssetCollection.CreateGroup.class)) {
            assetCollection.setActive("Y");
            assetCollection.setRootId(assetService.create(VirtualFile.getRootInstance()).getId());
            assetCollectionService.create(assetCollection);
            model.put("success", true);
        }
        return model;
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    @ResponseBody
    public Map<String, Object> update(@PathVariable(value = "id") String id, AssetCollection assetCollection) {
        Map<String, Object> model = new HashMap<>();
        model.put("context", CollectionsUtil.toMap("id", id));
        if(isValid(assetCollection, model, assetCollection.getGroup().length == 1 && assetCollection.getGroup()[0].equals("DETAILS") ? AssetCollection.DetailsGroup.class : null)) {
            assetCollectionService.update(id, FindBy.EXTERNAL_ID, assetCollection);
            model.put("success", true);
        }
        return model;
    }

    @RequestMapping(value = {"/{id}", "/create"})
    public ModelAndView details(@PathVariable(value = "id", required = false) String id) {
        Map<String, Object> model = new HashMap<>();
        model.put("active", "ASSET_COLLECTIONS");
        model.put("mode", id == null ? "CREATE" : "DETAILS");
        model.put("view", "settings/assetCollection");
        return id == null ? super.details(model) : assetCollectionService.get(id, FindBy.EXTERNAL_ID, false)
                .map(assetCollection -> {
                    model.put("assetCollection", assetCollection);
                    return super.details(id, model);
                }).orElseThrow(() -> new EntityNotFoundException("Unable to find Asset Collection with Id: " + id));
    }

    @RequestMapping(value = {"/{id}/asset"})
    public ModelAndView assetDetails(@PathVariable(value = "id") String id, @RequestParam(value = "assetGroupId", defaultValue = "ROOT") String assetGroupId) {

        Map<String, Object> model = new HashMap<>();
        VirtualFile asset = new VirtualFile();
        asset.setParentDirectoryId(ValidationUtil.isEmpty(assetGroupId) ? "ROOT" : assetGroupId);
        model.put("asset", asset);
        return new ModelAndView("settings/asset", model);
    }

    @RequestMapping(value = {"/{id}/assetGroup"})
    public ModelAndView assetGroupDetails(@PathVariable(value = "id") String id, @RequestParam(value = "assetGroupId", defaultValue = "ROOT") String assetGroupId) {
        Map<String, Object> model = new HashMap<>();
        VirtualFile asset = new VirtualFile(true);
        asset.setParentDirectoryId(ValidationUtil.isEmpty(assetGroupId) ? "ROOT" : assetGroupId);
        model.put("asset", asset);
        return new ModelAndView("settings/asset", model);
    }

    @RequestMapping(value = "/{id}/asset", method = RequestMethod.PUT)
    @ResponseBody
    public Map<String, Object> saveAttribute(@PathVariable(value = "id") String id, VirtualFile asset) {
        Map<String, Object> model = new HashMap<>();
        assetCollectionService.get(id, FindBy.EXTERNAL_ID, false)
                .ifPresent(assetCollection -> {
                    if(asset.getParentDirectoryId().equals("ROOT")) {
                        asset.setParentDirectoryId(assetCollection.getRootId());
                    }
                    asset.setRootDirectoryId(assetCollection.getRootId());
                    asset.setActive("Y");
                    assetService.create(asset);
                    model.put("success", true);
                });
        return model;
    }

    @RequestMapping("/{id}/hierarchy")
    @ResponseBody
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> getAllAsHierarchy(@PathVariable(value = "id") String id) {
        return assetCollectionService.getAssetsHierarchy(id, FindBy.EXTERNAL_ID, false);
    }
}
