<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<%--@elvariable id="mode" type="java.lang.String"--%>
<c:choose>
    <c:when test="${mode eq 'DETAILS'}">
        <tiles:insertDefinition name="mainLayout">
            <tiles:putAttribute name="title" value="PIM - Family - Variant Groups"/>
            <tiles:putAttribute name="body" value="/settings/variantGroup_body.jsp"/>
        </tiles:insertDefinition>
    </c:when>
    <c:otherwise>
        <div class="popup-content" style="padding:20px">
            <div class="body">
                <form method="post" action="/pim/families/{familyId}/variantGroups" data-method="POST"
                      data-success-message='["Successfully created the variant group", "Variant Group Created"]'
                      data-error-message='["Correct the validation error and try again", "Invalid Data"]' >
                    <div class="row">
                        <div class="col-md-6 col-sm-12">
                            <div class="form-group js-name">
                                <label for="groupName">Group Name</label>
                                <input id="groupName" type="text" name="name" class="form-control"/>
                            </div>
                            <div class="form-group js-external-id">
                                <label for="groupId">Group ID</label>
                                <input type="text" id="groupId" name="groupId" class="form-control"/>
                            </div>
                            <div class="form-group">
                                <label for="level">Variant Levels</label>
                                <select class="form-control" id="level" name="level">
                                    <option value="1">1</option>
                                    <%--<option value="2">2</option>--%>
                                </select>
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