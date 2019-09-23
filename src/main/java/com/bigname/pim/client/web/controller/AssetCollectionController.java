package com.bigname.pim.client.web.controller;

import com.bigname.pim.api.domain.AssetCollection;
import com.bigname.pim.api.domain.VirtualFile;
import com.bigname.pim.api.service.AssetCollectionService;
import com.bigname.pim.api.service.VirtualFileService;
import com.bigname.pim.client.util.BreadcrumbsBuilder;
import com.m7.xtreme.xcore.util.Criteria;
import com.m7.xtreme.xplatform.model.Breadcrumbs;
import com.m7.xtreme.common.datatable.model.Result;
import com.m7.xtreme.common.util.CollectionsUtil;
import com.m7.xtreme.xcore.exception.EntityNotFoundException;
import com.m7.xtreme.xcore.util.ID;
import com.m7.xtreme.xcore.web.controller.BaseController;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

import static com.m7.xtreme.common.util.ValidationUtil.isEmpty;
import static com.m7.xtreme.common.util.ValidationUtil.isNotEmpty;


/**
 * @author Manu V NarayanaPrasad (manu@blacwood.com)
 * @since 1.0
 */
@Controller
@RequestMapping("pim/assetCollections")
public class AssetCollectionController extends BaseController<AssetCollection, AssetCollectionService> {

    @Value("${upload.file.path}")
    private String filePath;

    private AssetCollectionService assetCollectionService;
    private VirtualFileService assetService;

    public AssetCollectionController(AssetCollectionService assetCollectionService, VirtualFileService assetService) {
        super(assetCollectionService, AssetCollection.class, new BreadcrumbsBuilder());
        this.assetCollectionService = assetCollectionService;
        this.assetService = assetService;
    }

    @RequestMapping()
    public ModelAndView all() {
        Map<String, Object> model = new HashMap<>();
        model.put("active", "ASSET_COLLECTIONS");
        return new ModelAndView("settings/assetCollections", model);
    }

    @RequestMapping(value =  {"/list", "/data"})
    @ResponseBody
    @SuppressWarnings("unchecked")
    public Result<Map<String, String>> all(HttpServletRequest request) {
        return all(request, "collectionName");
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
            assetCollectionService.update(ID.EXTERNAL_ID(id), assetCollection);
            model.put("success", true);
        }
        return model;
    }

    @RequestMapping(value = {"/{id}", "/create"})
    public ModelAndView details(@PathVariable(value = "id", required = false) String id,
                                @RequestParam(name = "reload", required = false) boolean reload) {
        Map<String, Object> model = new HashMap<>();
        model.put("active", "ASSET_COLLECTIONS");
        model.put("mode", id == null ? "CREATE" : "DETAILS");
        model.put("view", "settings/assetCollection" + (reload ? "_body" : ""));
        return id == null ? super.details(model) : assetCollectionService.get(ID.EXTERNAL_ID(id),false)
                .map(assetCollection -> {
                    model.put("assetCollection", assetCollection);
                    List<VirtualFile> assets = assetService.getFiles(assetCollection.getRootId());
                    model.put("folders", assets.stream().filter(a -> "Y".equals(a.getIsDirectory())).collect(Collectors.toList()));
                    model.put("files", assets.stream().filter(a -> !"Y".equals(a.getIsDirectory())).collect(Collectors.toList()));
                    return super.details(id, model);
                }).orElseThrow(() -> new EntityNotFoundException("Unable to find Asset Collection with Id: " + id));
    }

    @RequestMapping(value = {"/{collectionId}/assets/{assetId}", "/{collectionId}/assets"})
    public ModelAndView assetDetails(@PathVariable(value = "collectionId") String collectionId,
                                     @PathVariable(value = "assetId", required = false) String assetId,
                                     @RequestParam(value = "parentId", defaultValue = "ROOT") String parentId,
                                     @RequestParam(value = "assetGroup", defaultValue = "false") boolean assetGroup,
                                     @RequestParam(value = "assetGroupId", required = false) String assetGroupId,
                                     @RequestParam(name = "reload", required = false) boolean reload) {
        Map<String, Object> model = new HashMap<>();

        Optional<AssetCollection> assetCollection = assetCollectionService.get(ID.EXTERNAL_ID(collectionId), false);
        model.put("active", "ASSET_COLLECTIONS");
        if(assetId == null) {
            model.put("mode", "CREATE");
            VirtualFile asset = new VirtualFile(assetGroup);
            asset.setParentDirectoryId(assetGroupId);
            model.put("asset", asset);
            return new ModelAndView("settings/asset" + (reload ? "_body" : ""), model);
        } else {
            if(assetCollection.isPresent()) {
                model.put("mode", "DETAILS");
                model.put("assetCollection", assetCollection.get());
                VirtualFile asset = assetService.get(ID.INTERNAL_ID(assetId), false).orElse(null);
                model.put("asset", asset);
                List<VirtualFile> assets = isNotEmpty(asset) && asset.getIsDirectory().equals("Y") ? assetService.getFiles(asset.getId()) : new ArrayList<>();
                model.put("folders", assets.stream().filter(a -> "Y".equals(a.getIsDirectory())).collect(Collectors.toList()));
                model.put("files", assets.stream().filter(a -> !"Y".equals(a.getIsDirectory())).collect(Collectors.toList()));
                List<String> parentChain = asset.getParentIds().subList(1, asset.getParentIds().size());// Get all parent Ids except the root
                parentChain.add(asset.getId());
//                model.put("parentChain", assetService.getAll(parentChain.toArray(new String[0]), FindBy.INTERNAL_ID, null, false).stream().collect(CollectionsUtil.toLinkedMap(Entity::getId, VirtualFile::getFileName)));
                List<String> crumbs = new ArrayList<>();
                crumbs.addAll(Arrays.asList("Asset Collections", "/pim/assetCollections"));
                crumbs.addAll(Arrays.asList(assetCollection.get().getCollectionName(), "/pim/assetCollections/" + collectionId));
                for (VirtualFile virtualFile : assetService.getAll(parentChain.stream().map(e -> ID.INTERNAL_ID(e)).collect(Collectors.toList()), null, false)) {
                    crumbs.addAll(Arrays.asList(virtualFile.getFileName(), "/pim/assetCollections/" + collectionId + "/assets/" + virtualFile.getId()));
                }
                crumbs.set(crumbs.size() - 1, "");
                model.put("breadcrumbs", new Breadcrumbs("Asset Collection", crumbs.toArray(new String[0])));

                return new ModelAndView("settings/asset" + (reload ? "Collection_body" : ""), model);
            } else {
                throw new EntityNotFoundException("Unable to find Asset Collection with Id: " + collectionId);
            }
        }

    }


    @RequestMapping("/browser")
    public ModelAndView assetBrowser(@RequestParam(value="directoryId", defaultValue = "") String directoryId) {
        Map<String, Object> model = new HashMap<>();
        model.putAll(getAssetsData(directoryId));
//        List<VirtualFile> assets = assetService.getFiles("0d95fa63-b3f3-4cf6-9744-eb5cc9184eb4");
//        List<VirtualFile> assets = assetService.getFiles("1ac04962-477a-4cf8-939b-10e4c634b010");
//        model.put("folders", assets.stream().filter(a -> "Y".equals(a.getIsDirectory())).collect(Collectors.toList()));
//        model.put("files", assets.stream().filter(a -> !"Y".equals(a.getIsDirectory())).collect(Collectors.toList()));
        return new ModelAndView("settings/assetBrowser", model);
    }

    @ResponseBody
    @RequestMapping("/browser/data")
    public Map<String, Object> getAssetsData(@RequestParam(value="directoryId", defaultValue = "") String directoryId) {
        boolean success = false;
        Map<String, Object> model = new HashMap<>();
        VirtualFile directory = isEmpty(directoryId) ? null : assetService.get(ID.INTERNAL_ID(directoryId)).orElse(null);
        if(directory == null) {
            model.put("folders", assetCollectionService.getAll(Sort.by(new Sort.Order(Sort.Direction.ASC, "collectionName"))).stream().map(AssetCollection::toMap1).collect(Collectors.toList()));
            model.put("files", new ArrayList<>());
            model.put("parentChain", new ArrayList<>());
        } else {
            List<VirtualFile> assets = assetService.getFiles(directoryId);
            model.put("folders", assets.stream().filter(a -> "Y".equals(a.getIsDirectory())).map(VirtualFile::toMap).collect(Collectors.toList()));
            model.put("files", assets.stream().filter(a -> !"Y".equals(a.getIsDirectory())).map(VirtualFile::toMap).collect(Collectors.toList()));

            List<Object> list = new ArrayList<>();


            if(!directory.getParentIds().isEmpty()) {
                list.add(assetCollectionService.findOne(Criteria.where("rootId").eq(directory.getParentIds().remove(0))).map(AssetCollection::toMap1)); // Root Directory, so add the associated asset collection
                List<String> parentChain = directory.getParentIds();
                parentChain.add(directory.getId());
                list.addAll(assetService.getAll(parentChain.stream().map(e -> ID.INTERNAL_ID(e)).collect(Collectors.toList()), null, false).stream().map(VirtualFile::toMap).collect(Collectors.toList()));
            } else {
                list.add(assetCollectionService.findOne(Criteria.where("rootId").eq(directory.getId())).map(AssetCollection::toMap1));
            }
            model.put("parentChain", list);
        }
        success = true;
        model.put("success", success);
        return model;
    }

    @RequestMapping(value = "/{id}/assets", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> createAsset(@PathVariable(value = "id") String id, VirtualFile asset) {
        Map<String, Object> model = new HashMap<>();
        model.put("context", CollectionsUtil.toMap("forceUniqueId", true));
        if(isValid(asset, model, assetService, VirtualFile.CreateGroup.class)) {
            assetCollectionService.get(ID.EXTERNAL_ID(id), false)
                .ifPresent(assetCollection -> {
                    if(asset.getParentDirectoryId().equals("ROOT")) {
                        asset.setParentDirectoryId(assetCollection.getRootId());
                    }
                    VirtualFile parent = assetService.get(ID.INTERNAL_ID(asset.getParentDirectoryId()), false).orElse(null);
                    if(isNotEmpty(parent)) {
                        asset.setParentIds(parent.getParentIds()).add(parent.getId());
                    }
                    asset.setRootDirectoryId(assetCollection.getRootId());
                    asset.setActive("Y");
                    assetService.create(asset);
                    model.put("success", true);
                });
        }
        return model;
    }

    @ResponseBody
    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> uploadFile(@RequestParam("file") MultipartFile file, @RequestParam Map<String, Object> parameterMap, ModelMap modelMap) throws IOException {
        Map<String, Object> model = new HashMap<>();
        boolean success = false;
        try {
            modelMap.addAttribute("file", file);
            String assetGroupId = parameterMap.containsKey("assetGroupId") ? (String) parameterMap.get("assetGroupId") : "";
            VirtualFile directory = assetService.get(ID.INTERNAL_ID(assetGroupId)).orElseThrow(() -> new EntityNotFoundException("Unable to find the uploading directory wit id:" + assetGroupId));
            VirtualFile asset = new VirtualFile(file, directory.getId(), directory.getRootDirectoryId());
            //TODO validation
            Files.write(Paths.get(filePath + asset.getInternalFileName()), file.getBytes());
            assetService.create(asset);
            success = true;
        } catch (Exception e) {
            e.printStackTrace();
            model.put("error", "Another file with the same name already exists");
        }
        model.put("success", success);
        return new ResponseEntity<>(model, success ? HttpStatus.OK : HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @RequestMapping("/{id}/{assetGroupId}/hierarchy")
    @ResponseBody
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> getAllAsHierarchy(@PathVariable(value = "id") String id,
                                                       @PathVariable(value = "assetGroupId") String assetGroupId) {
        return assetCollectionService.getAssetsHierarchy(ID.EXTERNAL_ID(id), assetGroupId, false);
    }

    @RequestMapping("/downloadAsset")
    public ResponseEntity<Resource> downloadImage(@RequestParam(value = "fileId") String fileId, HttpServletRequest request)  {
        VirtualFile asset = assetService.get(ID.INTERNAL_ID(fileId), false).orElse(null);
        return downloadAsset(asset.getInternalFileName(), request);
    }

}