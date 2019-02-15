<%--@elvariable id="productFamilies" type="java.util.List<com.bigname.pim.api.domain.Family>"--%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<c:choose>
    <c:when test="${mode eq 'DETAILS'}">
        <tiles:insertDefinition name="mainLayout">
            <tiles:putAttribute name="title" value="PIM - Parent Products"/>
            <tiles:putAttribute name="body" value="/product/product_body.jsp"/>
        </tiles:insertDefinition>
    </c:when>
    <c:otherwise>
        <div class="popup-content" style="padding:20px">
            <div class="body">
                <form method="post" action="/pim/products" data-method="POST"
                      data-success-message='["Successfully created the parent product", "Parent Product Created"]'
                      data-error-message='["Correct the validation error and try again", "Invalid Data"]' >
                    <div class="row">
                        <div class="col-md-6 col-sm-12">
                            <div class="form-group js-name">
                                <label for="productName">Parent Product Name</label>
                                <input type="text" name="productName" id="productName" class="form-control"/>
                            </div>
                            <div class="form-group js-external-id">
                                <label for="productId">Parent Product ID</label>
                                <input type="text" id="productId" name="productId" class="form-control"/>
                            </div>
                            <div class="form-group">
                                <label for="productFamilyId">Product Type</label>
                                <select class="form-control" id="productFamilyId" name="productFamilyId">
                                    <option value="">Select One</option>
                                    <c:forEach items="${productFamilies}" var="productFamily">
                                        <option value="${productFamily.familyId}">${productFamily.familyName}</option>
                                    </c:forEach>
                                </select>
                            </div>
                        </div>
                    </div>
                    <br>
                    <input type="hidden" name="group" value="CREATE"/>
                    <img src="/assets/img/tiny.png" onload="$.initAHAH(this);"/>
                    <script src="/assets/js/pages/ui/product/createProduct.js"></script>
                </form>
            </div>
        </div>
    </c:otherwise>
</c:choose>