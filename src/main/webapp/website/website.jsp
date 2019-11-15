<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<c:choose>
    <c:when test="${mode eq 'DETAILS'}">
        <tiles:insertDefinition name="mainLayout">
            <tiles:putAttribute name="title" value="PIM - Websites"/>
            <tiles:putAttribute name="body" value="/website/website_body.jsp"/>
        </tiles:insertDefinition>
    </c:when>
    <c:when test="${mode eq 'HISTORY'}">
        <tiles:insertDefinition name="mainLayout">
            <tiles:putAttribute name="title" value="PIM - Websites"/>
            <tiles:putAttribute name="body" value="/website/website_history.jsp"/>
        </tiles:insertDefinition>
    </c:when>
    <c:otherwise>
        <div class="row clearfix">
            <div class="col-lg-12 col-md-12">
                <form method="post" action="/pim/websites" data-method="POST" data-success-message='["Successfully created the website", "Website Created"]' data-error-message='["Correct the validation error and try again", "Invalid Data"]' >
                    <div class="row">
                        <div class="col-md-6 col-sm-12">
                            <div class="form-group js-name">
                                <label>Website Name</label>
                                <input type="text" name="websiteName" class="form-control" />
                            </div>
                            <div class="form-group js-external-id">
                                <label>Website ID</label>
                                <input type="text" name="websiteId" class="form-control" />
                            </div>
                            <div class="form-group">
                                <label>Website URL</label>
                                <input type="url" name="url" class="form-control" />
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