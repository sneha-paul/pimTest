<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<c:choose>
    <c:when test="${mode eq 'DETAILS'}">
        <tiles:insertDefinition name="mainLayout">
            <tiles:putAttribute name="title" value="PIM - Settings - Asset Collection"/>
            <tiles:putAttribute name="body" value="/settings/assetCollection_body.jsp"/>
        </tiles:insertDefinition>
    </c:when>
    <c:otherwise>
        <%--@elvariable id="asset" type="com.bigname.pim.core.domain.VirtualFile"--%>
        <c:set var="isDirectory" value="${asset.isDirectory eq 'Y'}" />
        <div class="popup-content" style="padding:20px">
            <c:choose>
                <c:when test="${isDirectory}">
                    <div class="body">
                        <form method="post" action="/pim/assetCollections/{collectionId}/assets" data-method="POST"
                              data-success-message='["Successfully created the folder", "Folder Created"]'
                              data-error-message='["Correct the validation error and try again", "Invalid Data"]'>
                            <input type="hidden" name="isDirectory" value="Y"/>
                            <input type="hidden" name="parentDirectoryId" value="${asset.parentDirectoryId}"/>
                            <div class="row">
                                <div class="col-md-6 col-sm-12">
                                    <div class="form-group js-name">
                                        <label>Folder Name</label>
                                        <input type="text" name="fileName" value="${asset.fileName}" class="form-control" required="true"/>
                                    </div>
                                    <div class="form-group js-external-id" style="display: none">
                                        <label>Folder Id</label>
                                        <input type="text" name="fileId" value="${asset.fileId}" class="form-control" required="true"/>
                                    </div>
                                </div>
                            </div>
                            <br>
                            <input type="hidden" name="group" value="ASSETS"/>
                        </form>
                    </div>
                </c:when>
                <c:otherwise>
                    <div class="body">
                        <form id="my-dropzone"  action="/pim/assetCollections/upload" class="dropzone" method="POST" ></form>
                    </div>
                </c:otherwise>
            </c:choose>

        </div>
        <img src="/assets/img/tiny.png" onload="$.initAHAH(this)"/>
        <script src="/assets/js/pages/ui/settings/asset.js"></script>
    </c:otherwise>
</c:choose>