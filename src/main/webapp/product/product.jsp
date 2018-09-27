<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<c:choose>
    <c:when test="${mode eq 'DETAILS'}">
        <tiles:insertDefinition name="mainLayout">
            <tiles:putAttribute name="title" value="PIM - Products"/>
            <tiles:putAttribute name="body" value="/product/product_body.jsp"/>
        </tiles:insertDefinition>
    </c:when>
    <c:otherwise>
        <div class="popup-content" style="padding:20px">
            <div class="body">
                <form method="post" action="/pim/products" data-method="POST"
                      data-success-message='["Successfully created the product", "Product Created"]'
                      data-error-message='["Correct the validation error and try again", "Invalid Data"]' >
                    <div class="row">
                        <div class="col-md-6 col-sm-12">
                            <div class="form-group">
                                <label>Product Name</label>
                                <input type="text" name="productName" class="form-control"/>
                            </div>
                            <div class="form-group">
                                <label for="productId">Product ID</label>
                                <input type="text" id="productId" name="productId" class="form-control"/>
                            </div>
                            <div class="form-group">
                                <label for="productFamilyId">ProductFamily</label>
                                <select class="form-control" id="productFamilyId" name="productFamilyId">
                                    <option value="">Select One</option>
                                    <jsp:useBean id="productFamilies" scope="request" type="java.util.List"/>
                                    <c:forEach items="${productFamilies}" var="productFamily">
                                        <option value="${productFamily.externalId}">${productFamily.productFamilyName}</option>
                                    </c:forEach>
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