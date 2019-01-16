<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<div class="popup-content" style="padding:20px">
    <div id="js-file-browser" class="body" style="height: calc(100vh - 400px)">
        <div class="js-wrapper">
            <div style="border-bottom: 1px solid #DADADA">
                <div>
                    <ul class="breadcrumb js-parent-chain" style="background-color: #ffffff; font-size: 16px; margin-bottom: 0">
                        <%--<li class="breadcrumb-item"><a href="/pim/assetCollections/${assetCollection.collectionId}">${assetCollection.collectionId}</a></li>--%>
                        <c:forEach var="entry" items="${parentChain}">
                            <li class="breadcrumb-item"><a href="${entry.key}"><i class="text-primary p-r-10 fa fa-folder"></i>${entry.value}</a></li>
                        </c:forEach>
                    </ul>
                </div>
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

            <div class="p-t-25 js-folders">
                <div class="p-b-15 js-title${not empty folders ? '' : ' js-hidden'}"><h6>Folders</h6></div>
                <div class="row">
                    <div class="col js-container">
                        <c:forEach var="folder" items="${folders}">
                            <div id="${folder.id}" class="btn-label secondary js-folder">
                                <i class="text-primary fa fa-folder p-r-10"></i><span class="name">${folder.name}</span>
                            </div>
                        </c:forEach>
                    </div>
                </div>
                <div class="js-template">
                    <div class="btn-label secondary js-folder">
                        <i class="text-primary fa fa-folder p-r-10"></i><span class="name"></span>
                    </div>
                </div>
            </div>

            <div class="p-t-25 file_manager js-files">
            <div class="p-b-15 js-title${not empty files ? '' : ' js-hidden'}"><h6>Files</h6></div>
            <div class="row js-container">
                <c:forEach var="file" items="${files}">
                    <div class="col-lg-3 col-md-4 col-sm-12 js-file">
                        <div class="card" style="background-color: #fafafa">
                            <div id="${file.id}" class="file">
                                <a href="javascript:void(0);">
                                    <%--<div class="hover">
                                        <button type="button" class="btn btn-icon btn-success js-add">
                                            <i class="fa fa-save"></i>
                                        </button>
                                    </div>--%>
                                    <div class="image" style="height: 100px">
                                        <img src="/uploads/${file.internalFileName}" alt="${file.name}" title="${file.name}"  class="img-fluid">
                                    </div>
                                    <div class="file-name truncate">
                                        <span class="m-b-5 text-muted"><small title="${file.name}">${file.name}</small></span>
                                    </div>
                                </a>
                            </div>
                        </div>
                    </div>
                </c:forEach>
            </div>
            <div class="js-template">
                <div class="col-lg-3 col-md-4 col-sm-12 js-file">
                    <div class="card" style="background-color: #fafafa">
                        <div class="file">
                            <a href="javascript:void(0);">
                                <%--<div class="hover">
                                    <button type="button" class="btn btn-icon btn-success js-add">
                                        <i class="fa fa-save"></i>
                                    </button>
                                </div>--%>
                                <div class="image" style="height: 100px">
                                    <img src="" alt="" title=""  class="img-fluid">
                                </div>
                                <div class="file-name truncate">
                                    <span class="m-b-5 text-muted"><small class="name" title=""></small></span>
                                </div>
                            </a>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        </div>
    </div>
</div>
<img src="/assets/img/tiny.png" onload="$.initAHAH(this)"/>
<script src="/assets/js/pages/ui/settings/assetBrowser.js"></script>