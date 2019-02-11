<%--@elvariable id="asset" type="com.bigname.pim.api.domain.VirtualFile"--%>
<%--@elvariable id="assetCollection" type="com.bigname.pim.api.domain.AssetCollection"--%>
<%--@elvariable id="folders" type="java.util.List<com.bigname.pim.api.domain.VirtualFile>"--%>
<%--@elvariable id="files" type="java.util.List<com.bigname.pim.api.domain.VirtualFile>"--%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<div class="row clearfix">
    <div class="col-lg-12 col-md-12">
        <div class="card" style="min-height: calc(100vh - 192px);">
            <div class="header">
                <h2>${assetCollection.collectionName}
                    <small><code class="highlighter-rouge">${assetCollection.collectionId}</code></small>
                    <%--<small class="pull-right m-t-15"><code style="color:#808080">_id: ${assetCollection.id}</code></small>--%>
                </h2>
            </div>
            <div class="body">
                <div style="border-bottom: 1px solid #DADADA">
                    <div style="float: right; position: relative; top: -50px;">
                        <button type="button" class="btn btn-icon btn-success btn-round js-create-folder" title="Create New Folder">
                            <i class="fa fa-folder"></i><span style="font-size: 8px;position: relative;top:-8px"><i class="fa fa-plus"></i></span><span class="p-l-5"></span>
                        </button>
                        <button type="button" class="btn btn-icon btn-primary btn-round js-upload-files" title="Upload Files">
                            <i class="fa fa-cloud-upload"></i>
                        </button>
                        <button type="button" class="btn btn-icon btn-secondary btn-round" title="List View">
                            <i class="fa fa-th-list"></i>
                        </button>
                    </div>
                </div>

                <c:if test="${not empty folders}">
                    <div class="p-t-25">
                        <div class="p-b-15"><h6>Folders</h6></div>
                        <div class="row">
                            <div class="col file-container">
                            <c:forEach var="folder" items="${folders}">
                                <div id="${folder.id}" class="btn-label secondary js-folder" data-toggle="tooltip" data-placement="top" title="" data-original-title="${folder.fileName}">
                                    <i class="text-primary fa fa-folder p-r-10"></i>${folder.fileName}
                                </div>
                            </c:forEach>
                            </div>
                        </div>
                    </div>
                </c:if>

                <c:if test="${not empty files}">
                    <div class="p-t-25 file_manager">
                    <div class="p-b-15"><h6>Files</h6></div>
                    <div id="aniimated-thumbnials" class="row">
                        <c:forEach var="file" items="${files}">
                            <div class="col-lg-3 col-md-4 col-sm-12">
                                <div class="card" style="background-color: #fafafa">
                                    <div class="file">
                                        <a href="javascript:void(0);">
                                            <div class="hover">
                                                <button type="button" class="btn btn-icon btn-success">
                                                    <i class="fa fa-edit"></i>
                                                </button>
                                            </div>
                                            <div class="image">
                                                <img src="/uploads/${file.internalFileName}" alt="${file.fileName}" title="${file.fileName}"  class="img-fluid">
                                            </div>
                                            <div class="file-name">
                                                <p class="m-b-5 text-muted">${file.fileName}</p>
                                                <small>Size: ${file.formattedSize} <span class="date text-muted">Dec 11, 2017</span></small>
                                            </div>
                                        </a>
                                    </div>
                                </div>
                            </div>
                        </c:forEach>
                    </div>
                </div>
                </c:if>
            </div>
        </div>
    </div>
</div>
<script>
    $.initPage({
        'collectionId': '${assetCollection.collectionId}',
        'assetGroupId': '${empty asset ? assetCollection.rootId : asset.id}'
    });
    $(document).ready(function () {
        /*var urlParams = {};
        $.initTreeDataTable1({
            selector: '#assetsHierarchy',
            names: ['assetsHierarchy', 'asset'],
            url: $.getURL('/pim/assetCollections/{collectionId}/{assetGroupId}/hierarchy/'),
            url2: $.getURL('/pim/assetCollections/{collectionId}/assets/'),
            collapsed: false,
            collapsible: true,
            urlParams: urlParams
        });*/


    });
</script>
<script src="/assets/js/pages/ui/settings/assetCollection.js"></script>

