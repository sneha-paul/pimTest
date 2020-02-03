<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<c:choose>
    <c:when test="${mode eq 'DETAILS'}">
        <tiles:insertDefinition name="mainLayout">
            <tiles:putAttribute name="title" value="PIM - WebsitePages"/>
            <tiles:putAttribute name="body" value="/website/websitePage_body.jsp"/>
        </tiles:insertDefinition>
    </c:when>
    <c:otherwise>
        <div class="row clearfix">
            <div class="col-lg-12 col-md-12">
                <form method="post" action="/pim/websites/{websiteId}/pages" data-method="POST" data-success-message='["Successfully created the page", "Page Created"]' data-error-message='["Correct the validation error and try again", "Invalid Data"]' >
                    <div class="row">
                        <div class="col-sm-12">
                            <div class="form-group js-name">
                                <label>Page Name</label>
                                <input type="text" name="pageName" class="form-control" />
                            </div>
                            <div class="form-group js-external-id">
                                <label>Page ID</label>
                                <input type="text" name="pageId" class="form-control" />
                            </div>
                            <div class="form-group">
                                <label>Page URL</label>
                                <input type="pageUrl" name="pageUrl" class="form-control" />
                            </div>
                            <div class="form-group">
                                <label>Friendly URL</label>
                                <input type="friendlyUrl" name="friendlyUrl" class="form-control" />
                            </div>
                            <div class="form-group">
                                <label>Redirection URL</label>
                                <input type="redirectURL" name="redirectURL" class="form-control" />
                            </div>
                            <div class="form-group">
                                <label>Website</label>
                                <input type="wesbiteId" name="websiteId" class="form-control" value="${website.websiteId}" readonly/>
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