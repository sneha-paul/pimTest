<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<%--@elvariable id="mode" type="java.lang.String"--%>
<c:choose>
    <c:when test="${mode eq 'DETAILS'}">
        <tiles:insertDefinition name="mainLayout">
            <tiles:putAttribute name="title" value="PIM - ProductVariants"/>
            <tiles:putAttribute name="body" value="/product/productVariant_body.jsp"/>
        </tiles:insertDefinition>
    </c:when>
    <c:otherwise>
        <%--@elvariable id="productVariant" type="com.bigname.pim.api.domain.ProductVariant"--%>
        <div class="popup-content" style="padding:20px">
            <div class="body">
                <form method="post" action="/pim/products/${productVariant.product.productId}/variants" data-method="POST"
                      data-success-message='["Successfully created the product variant", "Product Variant Created"]'
                      data-error-message='["Correct the validation error and try again", "Invalid Data"]' >
                    <div class="row">
                        <div class="col-md-6 col-sm-12">
                            <div class="form-group">
                                <label for="productVariantName">Product Variant Name</label>
                                <input type="text" id="productVariantName" name="productVariantName" class="form-control"/>
                            </div>
                            <div class="form-group">
                                <label for="productVariantId">Product Variant ID</label>
                                <input type="text" id="productVariantId" name="productVariantId" class="form-control"/>
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