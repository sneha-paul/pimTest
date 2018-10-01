<%--@elvariable id="attribute" type="com.bigname.pim.api.domain.Attribute"--%>
<%--@elvariable id="attributeGroups" type="java.util.List<org.javatuples.Pair<String, String>"--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<div class="popup-content" style="padding:20px">
    <div class="body">
        <form method="post" action="/pim/productFamilies/{productFamilyId}/attribute" data-method="PUT"
              data-success-message='["Successfully created the family attribute", "Attribute Created"]'
              data-error-message='["Correct the validation error and try again", "Invalid Data"]'>
            <input type="hidden" name="type" value="PRODUCT"/>
            <div class="row">
                <div class="col-md-6 col-sm-12">
                    <div class="form-group">
                        <label for="js-attribute-group-id">Attribute Group</label>
                        <select class="form-control" id="js-attribute-group-id" name="attributeGroup.id">
                            <option value="">Add a NEW GROUP</option>
                            <c:forEach var="pair" items="${attributeGroups}">
                                <option value="${pair.value0}" <c:if test="${pair.value0 eq 'DEFAULT_GROUP'}">selected</c:if>>${pair.value1}</option>
                            </c:forEach>
                        </select>
                    </div>

                    <div class="form-group">
                        <label>Attribute Name</label>
                        <input type="text" name="name" value="${attribute.name}" class="form-control" required="true"/>
                    </div>
                    <div class="form-group">
                        <label for="uiType">Attribute UI Type</label>
                        <select class="form-control" id="uiType" name="uiType">
                            <option value="INPUT_BOX">Input Box</option>
                            <option value="DROPDOWN">Dropdown</option>
                            <option value="CHECKBOX">Checkbox</option>
                            <option value="RADIO_BUTTON">Checkbox</option>
                            <option value="TEXTAREA">Textarea</option>
                        </select>
                    </div>
                    <div class="form-group">
                        <label>Options</label>
                        <br/>
                        <label class="fancy-checkbox">
                            <input type="checkbox" name="required" value="Y" <c:if test="${attribute.required eq 'Y'}">checked="checked"</c:if>>
                            <span>Required</span>
                        </label>
                    </div>
                </div>
                <div class="col-md-6 col-sm-12">
                    <div class="form-group js-attribute-group-name" style="display: none">
                        <label for="attribute-group-name">Attribute Group Name</label>
                        <input type="text" id="attribute-group-name" name="attributeGroup.name" class="form-control" />
                        <br/>
                        <label class="fancy-checkbox">
                            <input type="checkbox" name="attributeGroup.masterGroup" value="Y" <c:if test="${attribute.attributeGroup.masterGroup eq 'Y'}">checked="checked"</c:if>>
                            <span>Master Group</span>
                        </label>
                    </div>
                </div>
            </div>
        </form>
    </div>
</div>
<img src="/assets/img/tiny.png" onload="$.initAHAH(this)"/>
<script src="/assets/js/pages/ui/product/productFamilyAttribute.js"></script>

