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
                                <label for="productName">Product Name</label>
                                <input type="text" name="productName" id="productName" class="form-control"/>
                            </div>
                            <div class="form-group">
                                <label for="productId">Product ID</label>
                                <input type="text" id="productId" name="productId" class="form-control"/>
                            </div>
                            <div class="form-group">
                                <label for="productFamilyId">Product Family</label>
                                <select class="form-control js-linked" data-link="variantGroupId" id="productFamilyId" name="productFamilyId">
                                    <option value="">Select One</option>
                                    <jsp:useBean id="productFamilyVariantGroups" scope="request" type="java.util.List<org.javatuples.Triplet<java.lang.String, java.lang.String, java.lang.String>>"/>
                                    <c:forEach items="${productFamilyVariantGroups}" var="productFamily">
                                        <option value="${productFamily.value0}" data-values="${productFamily.value2}">${productFamily.value1}</option>
                                    </c:forEach>
                                </select>
                            </div>
                            <div class="form-group">
                                <label for="variantGroupId">Variant Group</label>
                                <select class="form-control js-linked" id="variantGroupId" name="variantGroupId">
                                    <option value="">Select One</option>
                                </select>
                            </div>
                        </div>
                    </div>
                    <br>
                    <input type="hidden" name="group" value="CREATE"/>
                    <img src="/assets/img/tiny.png" onload="$.initAHAH(this);"/>
                    <script src="/assets/js/pages/ui/product/product.js"></script>
                </form>
            </div>
        </div>
    </c:otherwise>
</c:choose>