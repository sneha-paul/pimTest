<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<c:choose>
    <c:when test="${mode eq 'DETAILS'}">
        <tiles:insertDefinition name="mainLayout">
            <tiles:putAttribute name="title" value="PIM - ProductFamilies"/>
            <tiles:putAttribute name="body" value="/product/productFamily_body.jsp"/>
        </tiles:insertDefinition>
    </c:when>
    <c:otherwise>
        <div class="row clearfix">
            <div class="col-lg-12 col-md-12">
                <div class="card">
                    <div class="body">
                        <form method="post" action="/pim/productFamilies" data-method="POST" data-success-message='["Successfully created the productFamily", "ProductFamily Created"]' data-error-message='["Correct the validation error and try again", "Invalid Data"]' >
                            <div class="row">
                                <div class="col-md-6 col-sm-12">
                                    <div class="form-group">
                                        <label>ProductFamily Name</label>
                                        <input type="text" name="productFamilyName" class="form-control" />
                                    </div>
                                    <div class="form-group">
                                        <label>ProductFamily ID</label>
                                        <input type="text" name="productFamilyId" class="form-control" />
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
        </div>
    </c:otherwise>
</c:choose>