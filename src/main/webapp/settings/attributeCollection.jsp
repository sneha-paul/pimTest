<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<c:choose>
    <c:when test="${mode eq 'DETAILS'}">
        <tiles:insertDefinition name="mainLayout">
            <tiles:putAttribute name="title" value="PIM - Settings - Attribute Collections"/>
            <tiles:putAttribute name="body" value="/settings/attributeCollection_body.jsp"/>
        </tiles:insertDefinition>
    </c:when>
    <c:otherwise>
        <div class="row clearfix">
            <div class="col-lg-12 col-md-12">
                <form method="post" action="/pim/attributeCollections" data-method="POST" data-success-message='["Successfully created the attribute collection", "Collection Created"]' data-error-message='["Correct the validation error and try again", "Invalid Data"]' >
                    <div class="row">
                        <div class="col-md-6 col-sm-12">
                            <div class="form-group js-name">
                                <label for="collectionName">Collection Name</label>
                                <input type="text" name="collectionName" id="collectionName" class="form-control" />
                            </div>
                            <div class="form-group js-external-id">
                                <label for="collectionId">Collection ID</label>
                                <input type="text" name="collectionId" id="collectionId" class="form-control" />
                            </div>
                        </div>
                    </div>
                    <br>
                    <input type="hidden" name="group" value="CREATE"/>
                    <img src="/assets/img/tiny.png" onload="$.initAHAH(this)"/>
                </form>
            </div>
        </div>
    </c:otherwise>
</c:choose>