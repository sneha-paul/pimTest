<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<c:choose>
    <c:when test="${mode eq 'DETAILS'}">
        <tiles:insertDefinition name="mainLayout">
            <tiles:putAttribute name="title" value="PIM - Website Param"/>
            <tiles:putAttribute name="body" value="/website/websiteParam_body.jsp"/>
        </tiles:insertDefinition>
    </c:when>
    <c:otherwise>
        <div class="row clearfix">
            <div class="col-lg-12 col-md-12">
                <form method="post" action="/pim/websites/${website.websiteId}/params" data-method="PUT" data-success-message='["Successfully created the website config parameters", "Website Config Parameters"]' data-error-message='["Correct the validation error and try again", "Invalid Data"]' >
                    <div class="row">
                        <div class="col-md-6 col-sm-12">
                            <div class="form-group js-name">
                                <label>Config</label>
                                <select class="form-control" id="configId" name="configId">
                                    <c:forEach var="entry" items="${configs}">
                                        <option value="${entry}">${entry}</option>
                                    </c:forEach>
                                </select>
                            </div>
                            <div class="form-group">
                                <label>Parameter Name</label>
                                <input type="text" name="paramName" class="form-control" />
                            </div>
                            <div class="form-group">
                                <label>Parameter Value</label>
                                <input type="url" name="paramValue" class="form-control" />
                            </div>
                            <div class="form-group">
                                <label>Website</label>
                                <input type="text" name="websiteId" value="${website.websiteId}" class="form-control" readonly/>
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