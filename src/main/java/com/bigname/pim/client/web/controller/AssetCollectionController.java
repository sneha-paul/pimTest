package com.bigname.pim.client.web.controller;

import com.bigname.common.util.CollectionsUtil;
import com.bigname.common.util.ValidationUtil;
import com.bigname.pim.api.domain.AssetCollection;
import com.bigname.pim.api.domain.Entity;
import com.bigname.pim.api.domain.VirtualFile;
import com.bigname.pim.api.exception.EntityNotFoundException;
import com.bigname.pim.api.service.AssetCollectionService;
import com.bigname.pim.api.service.VirtualFileService;
import com.bigname.pim.client.model.Breadcrumbs;
import com.bigname.pim.util.FindBy;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import javax.servlet.http.HttpServletResponse;
import com.bigname.common.datatable.model.Pagination;
import com.bigname.common.datatable.model.Request;
import com.bigname.common.datatable.model.Result;
import com.bigname.common.datatable.model.SortOrder;
import java.util.stream.Collectors;
import static com.bigname.common.util.ValidationUtil.isEmpty;
import static com.bigname.common.util.ValidationUtil.isNotEmpty;

import com.bigname.pim.util.Pageable;



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

    @RequestMapping(value =  {"/list", "/data"})
    @ResponseBody
    @SuppressWarnings("unchecked")
    public Result<Map<String, String>> all(HttpServletRequest request, HttpServletResponse response, Model model) {
        Request dataTableRequest = new Request(request);
        if(isEmpty(dataTableRequest.getSearch())) {
            return super.all(request, response, model);
        } else {
            Pagination pagination = dataTableRequest.getPagination();
            Result<Map<String, String>> result = new Result<>();
            result.setDraw(dataTableRequest.getDraw());
            Sort sort;
            if(pagination.hasSorts()) {
                sort = Sort.by(new Sort.Order(Sort.Direction.valueOf(SortOrder.fromValue(dataTableRequest.getOrder().getSortDir()).name()), dataTableRequest.getOrder().getName()));
            } else {
                sort = Sort.by(new Sort.Order(Sort.Direction.ASC, "externalId"));
            }
            List<Map<String, String>> dataObjects = new ArrayList<>();
            List<AssetCollection> paginatedResult = assetCollectionService.findAll("collectionName", dataTableRequest.getSearch(), new Pageable(pagination.getPageNumber(), pagination.getPageSize(), sort), false);
            paginatedResult.forEach(e -> dataObjects.add(e.toMap()));
            result.setDataObjects(dataObjects);
            result.setRecordsTotal(Long.toString(paginatedResult.size()));
            result.setRecordsFiltered(Long.toString(paginatedResult.size()));
            return result;
        }
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
    public ModelAndView details(@PathVariable(value = "id", required = false) String id,
                                @RequestParam(name = "reload", required = false) boolean reload) {
        Map<String, Object> model = new HashMap<>();
        model.put("active", "ASSET_COLLECTIONS");
        model.put("mode", id == null ? "CREATE" : "DETAILS");
        model.put("view", "settings/assetCollection" + (reload ? "_body" : ""));
        return id == null ? super.details(model) : assetCollectionService.get(id, FindBy.EXTERNAL_ID, false)
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

        Optional<AssetCollection> assetCollection = assetCollectionService.get(collectionId, FindBy.EXTERNAL_ID, false);
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
                VirtualFile asset = assetService.get(assetId, FindBy.INTERNAL_ID, false).orElse(null);
                model.put("asset", asset);
                List<VirtualFile> assets = isNotEmpty(asset) && asset.getIsDirectory().equals("Y") ? assetService.getFiles(asset.getId()) : new ArrayList<>();
                model.put("folders", assets.stream().filter(a -> "Y".equals(a.getIsDirectory())).collect(Collectors.toList()));
                model.put("files", assets.stream().filter(a -> !"Y".equals(a.getIsDirectory())).collect(Collectors.toList()));
                List<String> parentChain = asset.getParentIds().subList(1, asset.getParentIds().size());// Get all parent Ids except the root
                parentChain.add(asset.getId());
                model.put("parentChain", assetService.getAll(parentChain.toArray(new String[0]), FindBy.INTERNAL_ID, null, false).stream().collect(CollectionsUtil.toLinkedMap(Entity::getId, VirtualFile::getFileName)));
                model.put("breadcrumbs", new Breadcrumbs("Asset Collection",
                        "Asset Collections", "/pim/assetCollections",
                        assetCollection.get().getCollectionName(), ""));
                return new ModelAndView("settings/asset" + (reload ? "Collection_body" : ""), model);
            } else {
                throw new EntityNotFoundException("Unable to find Asset Collection with Id: " + collectionId);
            }
        }

    }

    @RequestMapping(value = "/{id}/assets", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> createAsset(@PathVariable(value = "id") String id, VirtualFile asset) {
        Map<String, Object> model = new HashMap<>();
        model.put("context", CollectionsUtil.toMap("forceUniqueId", true));
        if(isValid(asset, model, assetService, VirtualFile.CreateGroup.class)) {
            assetCollectionService.get(id, FindBy.EXTERNAL_ID, false)
                .ifPresent(assetCollection -> {
                    if(asset.getParentDirectoryId().equals("ROOT")) {
                        asset.setParentDirectoryId(assetCollection.getRootId());
                    }
                    VirtualFile parent = assetService.get(asset.getParentDirectoryId(), FindBy.INTERNAL_ID, false).orElse(null);
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
    public Map<String, Object> uploadFile(@RequestParam("file") MultipartFile file, @RequestParam Map<String, Object> parameterMap,  ModelMap modelMap) throws IOException {
        Map<String, Object> model = new HashMap<>();
        modelMap.addAttribute("file", file);
        String assetGroupId = parameterMap.containsKey("assetGroupId") ? (String) parameterMap.get("assetGroupId") : "";
        VirtualFile directory = assetService.get(assetGroupId, FindBy.INTERNAL_ID).orElseThrow(() -> new EntityNotFoundException("Unable to find the uploading directory wit id:" + assetGroupId));
        VirtualFile asset = new VirtualFile(file, directory.getId(), directory.getRootDirectoryId());
        //TODO validation
        Files.write(Paths.get("/tmp/" + asset.getInternalFileName()), file.getBytes());
        assetService.create(asset);
        model.put("success", true);
        return model;
    }

    @RequestMapping("/{id}/{assetGroupId}/hierarchy")
    @ResponseBody
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> getAllAsHierarchy(@PathVariable(value = "id") String id,
                                                       @PathVariable(value = "assetGroupId") String assetGroupId) {
        return assetCollectionService.getAssetsHierarchy(id, FindBy.EXTERNAL_ID, assetGroupId, false);
    }
}
