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
                <div class="table-responsive">
                    <table id="paginatedAvailableProductVariantsTable" class="table table-hover dataTable table-custom" style="width: 100%">
                        <thead class="thead-dark">

                        </thead>
                    </table>
                </div>
                <br>
                <img src="/assets/img/tiny.png" id="js-tiny" data-obj='${axisAttributes}' onload="$.setPageAttribute('axisAttributes', $(this).data('obj'));$.initAHAH(this)"/>
                <script src="/assets/js/pages/ui/product/createProductVariant.js"></script>
            </div>
        </div>
    </c:otherwise>
</c:choose>