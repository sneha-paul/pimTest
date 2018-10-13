<%--@elvariable id="attribute" type="com.bigname.pim.api.domain.FamilyAttribute"--%>
<%--@elvariable id="attributeGroups" type="java.util.List<org.javatuples.Pair<String, String>"--%>
<%--@elvariable id="parentAttributeGroups" type="java.util.List<org.javatuples.Pair<String, String"--%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<div class="popup-content" style="padding:20px">
    <div class="body">
        <form method="post" action="/pim/families/{familyId}/attribute" data-method="PUT"
              data-success-message='["Successfully created the family attribute", "Attribute Created"]'
              data-error-message='["Correct the validation error and try again", "Invalid Data"]'>
            <div class="row">
                <div class="col-md-6 col-sm-12">
                    <div class="form-group">
                        <label for="js-attribute-group-id">Attribute Group</label>
                        <select class="form-control" id="js-attribute-group-id" name="attributeGroup.fullId">
                            <option value="DEFAULT_GROUP">Default Group</option>
                            <option value="">Add a NEW GROUP</option>
                            <c:forEach var="pair" items="${attributeGroups}">
                                <option value="${pair.value0}" <c:if test="${pair.value0 eq 'DEFAULT_GROUP'}">selected</c:if>>${pair.value1}</option>
                            </c:forEach>
                        </select>
                    </div>
                    <div class="js-new-attribute-group" style="display: none;">
                        <div class="form-group js-attribute-group-name">
                            <label for="attribute-group-name">Attribute Group Name</label>
                            <input type="text" id="attribute-group-name" name="attributeGroup.name" class="form-control" />
                            <br/>
                            <label class="fancy-checkbox">
                                <input type="checkbox" class="js-master-group" name="attributeGroup.masterGroup" value="Y" <c:if test="${attribute.attributeGroup.masterGroup eq 'Y'}">checked="checked"</c:if>>
                                <span>Master Group</span>
                            </label>
                        </div>
                        <div class="form-group">
                            <label for="js-parent-group-id">Parent Group</label>
                            <select class="form-control" id="js-parent-group-id" name="attributeGroup.parentGroup.id">
                                <option value="">Select One</option>
                                <c:forEach var="pair" items="${parentAttributeGroups}">
                                    <option value="${pair.value0}" <c:if test="${pair.value0 eq 'DEFAULT_GROUP'}">selected</c:if>>${pair.value1}</option>
                                </c:forEach>
                            </select>
                        </div>
                    </div>
                    <div class="form-group">
                        <label>Attribute Name</label>
                        <input type="text" name="name" value="${attribute.name}" class="form-control" required="true"/>
                    </div>
                    <div class="form-group">
                        <label for="uiType">Attribute UI Type</label>
                        <select class="form-control" id="uiType" name="uiType">
                            <option value="CHECKBOX">Checkbox</option>
                            <option value="DATE_PICKER">Date Picker</option>
                            <option value="DROPDOWN">Dropdown</option>
                            <option value="INPUT_BOX" selected>Input Box</option>
                            <option value="RADIO_BUTTON">Radio Button</option>
                            <option value="TEXTAREA">Textarea</option>
                            <option value="YES_NO">Yes/No</option>
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
            </div>
        </form>
    </div>
</div>
<img src="/assets/img/tiny.png" onload="$.initAHAH(this)"/>
<script src="/assets/js/pages/ui/settings/familyAttribute.js"></script>

