<%--@elvariable id="asset" type="com.bigname.pim.api.domain.VirtualFile"--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<div class="row clearfix">
    <div class="col-lg-12 col-md-12">
        <div class="card">
            <div class="header">
                <h2>${asset.fileName}
                    <small><code class="highlighter-rouge">${asset.fileId}</code></small>
                    <small class="pull-right m-t--15"><code style="color:#808080">_id: ${asset.id}</code>
                    </small>
                </h2>
            </div>
            <div class="body">
                <ul class="nav nav-tabs-new2">
                    <li class="nav-item"><a class="nav-link" data-toggle="tab" href="#details">Details</a></li>
                    <li class="nav-item"><a class="nav-link active show" data-toggle="tab" href="#assets">Assets</a></li>
                </ul>
                <div class="tab-content">
                    <div class="tab-pane" id="details">
                        <div class="row clearfix m-t-20">
                            <div class="col-lg-12 col-md-12">
                                <div class="card">
                                    <div class="body">
                                        <form method="post"
                                              action="/pim/assetCollections/${assetCollectionId}"
                                              data-method="PUT"
                                              data-success-message='["Successfully updated the asset", "Asset Updated"]'
                                              data-error-message='["Correct the validation error and try again", "Invalid Data"]'>
                                            <div class="row">
                                                <div class="col-md-6 col-sm-12">
                                                    <div class="form-group">
                                                        <label>Asset Group Name</label>
                                                        <input type="text" name="fileName" value="${asset.fileName}" class="form-control" required="true"/>
                                                    </div>
                                                    <div class="form-group js-external-id hidden">
                                                        <label>Asset Group Id</label>
                                                        <input type="text" name="fileId" value="${asset.fileId}" class="form-control" required="true"/>
                                                    </div>

                                                    <div class="form-group">
                                                        <label>Status</label>
                                                        <br/>
                                                        <label for="active" class="fancy-checkbox">
                                                            <input type="checkbox" id="active" name="active" value="Y"
                                                                   <c:if test="${asset.active eq 'Y'}">checked="checked"</c:if>>
                                                            <span>Active</span>
                                                        </label>
                                                    </div>
                                                </div>
                                            </div>
                                            <br>
                                            <input type="hidden" name="group" value="DETAILS"/>
                                            <button type="submit" class="btn btn-primary" onclick="$.submitAction(event, this)">Save</button>
                                            <a href="/pim/assetCollections/${assetCollectionId}">
                                                <button type="button" class="btn btn-danger">Cancel</button>
                                            </a>
                                        </form>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="tab-pane show active" id="assets">
                        <div class="row clearfix">
                            <div class="col-lg-12 col-md-12">
                                <div class="card">
                                    <div class="body">
                                        <div class="row p-b-25">
                                            <div class="col-lg-12 col-md-12">
                                                <div class="pull-right">
                                                    <button type="button" class="btn btn-success js-add-asset-group">
                                                        <i class="fa fa-folder"></i><span style="font-size: 8px;position: relative;top:-8px"><i class="fa fa-plus"></i></span><span class="p-l-5">Add Asset Group</span>
                                                    </button>
                                                    <button type="button" class="btn btn-primary js-add-asset">
                                                        <i class="fa fa-upload"></i><span class="p-l-5">Upload Asset</span>
                                                    </button>
                                                </div>
                                            </div>
                                        </div>
                                        <div class="table-responsive no-filter">
                                            <table id="assetsHierarchy" class="table table-hover dataTable treeDataTable table-custom m-b-0" style="width: 100% !important">
                                                <thead class="thead-dark">
                                                </thead>
                                            </table>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <div class="body">
        <form action="/file-upload" class="dz-clickable" id="my-dropzone">
            <div class="dz-message d-flex flex-column">
                <i class="material-icons text-muted">cloud_upload</i>
                Drag &amp; Drop here or click
            </div>
        </form>

    </div>

</div>
<script>
    $.initPage({
        'collectionId': '${assetCollectionId}',
        'assetId': '${asset.fileId}',
        'assetGroupId': '${asset.id}'
    });
    $(document).ready(function () {
        $('#my-dropzone').dropzone();
        var urlParams = {};
        $.initTreeDataTable1({
            selector: '#assetsHierarchy',
            names: ['assetsHierarchy', 'asset'],
            url: $.getURL('/pim/assetCollections/{collectionId}/{assetGroupId}/hierarchy/'),
            url2: $.getURL('/pim/assetCollections/{collectionId}/assets/'),
            collapsed: false,
            collapsible: true,
            urlParams: urlParams
        });


    });
</script>
<script src="/assets/js/pages/ui/settings/assetCollection.js"></script>

