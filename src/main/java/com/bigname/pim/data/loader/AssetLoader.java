package com.bigname.pim.data.loader;

import com.bigname.pim.api.domain.AssetCollection;
import com.bigname.pim.api.domain.VirtualFile;
import com.bigname.pim.api.service.AssetCollectionService;
import com.bigname.pim.api.service.VirtualFileService;
import com.m7.xtreme.common.util.ConversionUtil;
import com.m7.xtreme.xcore.util.ID;
import org.javatuples.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static com.m7.xtreme.common.util.ValidationUtil.isEmpty;
import static com.m7.xtreme.common.util.ValidationUtil.isNotEmpty;
import static org.apache.commons.lang.StringUtils.trim;


/**
 * @author Manu V NarayanaPrasad (manu@blacwood.com)
 * @since 1.0
 */
@Component
public class AssetLoader {

    private static final Logger LOGGER = LoggerFactory.getLogger(AssetLoader.class);

    @Autowired
    AssetCollectionService assetCollectionService;

    @Autowired
    VirtualFileService assetService;

    @Value("${loader.product.asset.src.location:/assets/src/}")
    private String productAssetSrcLocation;

    @Value("${upload.file.path}")
    private String filePath;

    public AssetCollection createCollection(String collectionName) {
        if(isNotEmpty(collectionName)) {
            collectionName = trim(collectionName);
            Optional<AssetCollection> _assetCollection = assetCollectionService.getAssetCollection(collectionName);
            if (_assetCollection.isPresent()) {
                return _assetCollection.get();
            } else {
                AssetCollection assetCollection = new AssetCollection();
                assetCollection.setCollectionName(collectionName);
                assetCollection.setCollectionId(ConversionUtil.toId(collectionName));
                if (isValid(assetCollectionService.validate(assetCollection, new HashMap<>(), AssetCollection.CreateGroup.class))) {
                    assetCollection.setActive("Y");
                    assetCollection.setRootId(assetService.create(VirtualFile.getRootInstance()).getId());
                    return assetCollectionService.create(assetCollection);
                }
            }
        }
        return null;
    }

    public VirtualFile createFolder(String folderName, String parentId) {
        if(isNotEmpty(folderName) && isNotEmpty(parentId)) {
            folderName = trim(folderName);
            parentId = trim(parentId);
            VirtualFile parentFolder = assetService.get(ID.EXTERNAL_ID(parentId), false).orElse(null);
            if (isNotEmpty(parentFolder)) {
                VirtualFile folder = assetService.getFile(folderName, parentId).orElse(null);
                if (isNotEmpty(folder)) {
                    return folder;
                } else {
                    folder = new VirtualFile(true);
                    folder.setFileName(folderName);
                    folder.setFileId(ConversionUtil.toId(folderName));
                    folder.setParentDirectoryId(parentId);
                    if (isValid(assetService.validate(folder, new HashMap<>(), VirtualFile.CreateGroup.class))) {
                        folder.setParentIds(parentFolder.getParentIds()).add(parentFolder.getId());
                    }
                    folder.setRootDirectoryId(parentFolder.getRootDirectoryId());
                    folder.setActive("Y");
                    folder.setActive("Y");
                    return assetService.create(folder);
                }
            }
        }
        return null;
    }

    public VirtualFile uploadFile(String folderId, String fileLocation, String fileName, String rootId) {
        if(fileLocation.isEmpty()) {
            fileLocation = productAssetSrcLocation;
        }
        File file = new File(fileLocation + fileName);
        VirtualFile asset = new VirtualFile();
        asset.setActive("Y");
        asset.setFileId(asset.getId());
        asset.setFileName(fileName);
        asset.setParentDirectoryId(folderId);
        asset.setRootDirectoryId(rootId);
        asset.setSize(file.length());
        if(asset.getFileName().contains(".")) {
            asset.setExtension(fileName.substring(fileName.lastIndexOf(".") + 1));
            asset.setType("IMAGE");
        }
        assetService.create(asset);
        Path dest = Paths.get(filePath + asset.getInternalFileName());
        Path source = file.toPath();
        try {
            Files.copy(source, dest);
        } catch (IOException e) {
            LOGGER.info("Error uploading the file:" + fileName + "," + asset.getInternalFileName());
        }
        return asset;
    }

    protected boolean isValid(Map<String, Pair<String, Object>> validationResult) {
        return isEmpty(validationResult.get("fieldErrors"));
    }

    public String getSourceLocation() {
        return productAssetSrcLocation;
    }
}
