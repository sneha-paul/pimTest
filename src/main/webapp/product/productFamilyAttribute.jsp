<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<div class="popup-content" style="padding:20px">
    <div class="body">
        <form method="post" action="/pim/productFamilies/${productFamilyId}/${type}/attribute" data-method="PUT"
              data-success-message='["Successfully created the family attribute", "Attribute Created"]'
              data-error-message='["Correct the validation error and try again", "Invalid Data"]'>
            <div class="row">
                <div class="col-md-6 col-sm-12">
                    <div class="form-group">
                        <label for="attributeGroupId">Attribute Group</label>
                        <select class="form-control" id="attributeGroupId" name="attributeGroupId">
                            <option value="DEFAULT_GROUP">Default Group</option>
                        </select>
                    </div>
                    <div class="form-group">
                        <label>Attribute Name</label>
                        <input type="text" name="name" value="${attribute.name}" class="form-control" required="true"/>
                    </div>
                    <div class="form-group">
                        <label>Options</label>
                        <br/>
                        <label class="fancy-checkbox">
                            <input type="checkbox" name="required" value="Y" <c:if test="${attribute.required eq 'Y'}">checked="checked"</c:if>>
                            <span>Required</span>
                        </label>
                        <br/>
                        <label class="fancy-checkbox">
                            <input type="checkbox" name="selectable" value="Y" <c:if test="${attribute.selectable eq 'Y'}">checked="checked"</c:if>>
                            <span>Selectable</span>
                        </label>
                    </div>
                </div>
            </div>
        </form>
    </div>
</div>
<img src="/assets/img/tiny.png" onload="$.initAHAH(this)"/>
<script src="/assets/js/pages/ui/product/productFamilyAttribute.js"></script>

