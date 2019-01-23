<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<c:choose>
    <c:when test="${mode eq 'DETAILS'}">
        <tiles:insertDefinition name="mainLayout">
            <tiles:putAttribute name="title" value="PIM - Catalogs"/>
            <tiles:putAttribute name="body" value="/catalog/catalog_body.jsp"/>
        </tiles:insertDefinition>
    </c:when>
    <c:otherwise>
        <div class="row clearfix">
            <div class="col-lg-12 col-md-12">
                    <div class="body">
                        <form method="post" action="/pim/catalogs" data-method="POST" data-success-message='["Successfully created the website", "Catalog Created"]' data-error-message='["Correct the validation error and try again", "Invalid Data"]' >
                            <div class="row">
                                <div class="col-md-6 col-sm-12">
                                    <div class="form-group js-name">
                                        <label>Catalog Name</label>
                                        <input type="text" name="catalogName" class="form-control" />
                                    </div>
                                    <div class="form-group js-external-id">
                                        <label>Catalog ID</label>
                                        <input type="text" name="catalogId" class="form-control" />
                                    </div>
                                    <div class="form-group">
                                        <label>Catalog Description</label>
                                        <textarea name="description" rows="5" cols="30" class="form-control" ></textarea>
                                    </div>
                                </div>
                            </div>
                            <br>
                            <input type="hidden" name="group" value="CREATE"/>
                            <img src="/assets/img/tiny.png" onload="$.initAHAH(this)"/>
                        </form>
                    </div>
            </div>
        </div>
    </c:otherwise>
</c:choose>
