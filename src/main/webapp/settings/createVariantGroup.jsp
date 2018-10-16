<%--@elvariable id="availableVariantAxisAttributes" type="java.util.List<com.bigname.pim.api.domain.FamilyAttribute>"--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<div class="popup-content" style="padding:20px">
    <div class="body">
        <form method="post" action="/pim/families/{familyId}/variantGroup" data-method="PUT"
              data-success-message='["Successfully created the variant group", "Variant Group Created"]'
              data-error-message='["Correct the validation error and try again", "Invalid Data"]'>
            <div class="row">
                <div class="col-md-6 col-sm-12">
                    <div class="form-group">
                        <label>Variant Group Name</label>
                        <input type="text" name="name" value="" class="form-control"/>
                    </div>
                    <div class="form-group">
                        <label>Variant Level(s)</label>
                        <select class="form-control" name="level">
                            <option value="1">1</option>
                            <option value="2">2</option>
                        </select>
                    </div>
                    <div class="form-group">
                        <label>Variant Axis Attributes</label>
                        <select class="form-control" id="variantAxis1" multiple="multiple" name="variantAxis1">
                            <c:forEach var="attribute" items="${availableVariantAxisAttributes}">
                                <option value="${attribute.fullId}">${attribute.label}</option>
                            </c:forEach>
                        </select>
                    </div>
                </div>
            </div>
        </form>
    </div>
</div>
<img src="/assets/img/tiny.png" onload="$.initAHAH(this)"/>
<%--<script src="/assets/js/pages/ui/settings/familyVariantGroup.js"></script>--%>

