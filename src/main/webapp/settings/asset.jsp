<%--@elvariable id="asset" type="com.bigname.pim.api.domain.VirtualFile"--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<c:set var="isDirectory" value="${asset.isDirectory eq 'Y'}" />
<div class="popup-content" style="padding:20px">
    <div class="body">
        <form method="post" action="/pim/assetCollections/{collectionId}/asset" data-method="PUT"
              data-success-message='["Successfully created the asset", ${isDirectory ? "\"Asset Group Created\"" : "\"Asset Created\""}]'
              data-error-message='["Correct the validation error and try again", "Invalid Data"]'>
            <input type="hidden" name="isDirectory" value="${asset.isDirectory}"/>
            <input type="hidden" name="parentDirectoryId" value="${asset.parentDirectoryId}"/>
            <div class="row">
                <div class="col-md-6 col-sm-12">
                    <div class="form-group js-name">
                        <label>${isDirectory ? "Asset Group Name" : "Asset Name"}</label>
                        <input type="text" name="fileName" value="${asset.fileName}" class="form-control" required="true"/>
                    </div>
                    <div class="form-group js-external-id hidden">
                        <label>${isDirectory ? "Asset Group Id" : "Asset Id"}</label>
                        <input type="text" name="fileId" value="${asset.fileId}" class="form-control" required="true"/>
                    </div>
                </div>
            </div>
            <br>
            <input type="hidden" name="group" value="ASSETS"/>
        </form>
    </div>
</div>
<img src="/assets/img/tiny.png" onload="$.initAHAH(this)"/>
<script src="/assets/js/pages/ui/settings/asset.js"></script>

