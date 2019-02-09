<%--@elvariable id="attributeOption" type="com.bigname.pim.api.domain.AttributeOption"--%>
<%--@elvariable id="attribute" type="com.bigname.pim.api.domain.Attribute"--%>
<%--@elvariable id="parentAttributeOptions" type="java.util.Map<String, String>"--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<div class="popup-content" style="padding-left: 0;padding-right: 0;">
    <div class="body">
        <c:choose>
            <c:when test="${mode eq 'DETAILS'}">
                <form method="post" action="/pim/attributeCollections/{collectionId}/attributes/{attributeId}/attributeOptions/{attributeOptionId}" data-method="PUT"
                      data-success-message='["Successfully updated the attribute option", "Attribute Option Updated"]'
                      data-error-message='["Correct the validation error and try again", "Invalid Data"]'>
                    <div class="row">
                        <div class="col-md-6 col-sm-12">
                            <div class="form-group">
                                <label>Attribute Name</label>
                                <input type="text" disabled="disabled" value="${attribute.name}" class="form-control"/>
                            </div>
                            <div class="form-group">
                                <label>Attribute Option ID</label>
                                <input type="text" disabled="disabled" value="${attributeOption.id}" class="form-control"/>
                            </div>
                            <c:if test="${not empty attribute.parentAttributeId}">
                                <div class="form-group">
                                    <label>Parent Option</label><code class="highlighter-rouge m-l-10">*</code>
                                    <select class="form-control" name="parentOptionFullId" required="true">
                                        <option value="">Select One</option>
                                        <c:forEach var="entry" items="${parentAttributeOptions}">
                                            <option value="${entry.key}"${entry.key eq attributeOption.parentOptionFullId ? ' selected' : ''}>${entry.value}</option>
                                        </c:forEach>
                                    </select>
                                    <br/>
                                    <label class="fancy-checkbox">
                                        <input type="checkbox" name="independent" value="Y"
                                               <c:if test="${attributeOption.independent eq 'Y'}">checked="checked"</c:if>>
                                        <span>Independent Option</span>
                                    </label>
                                </div>
                            </c:if>
                            <div class="form-group">
                                <label>Attribute Option Name</label><code class="highlighter-rouge m-l-10">*</code>
                                <input type="text" name="value" value="${attributeOption.value}" class="form-control" required="true"/>
                            </div>
                        </div>
                    </div>
                    <br>
                </form>
            </c:when>
            <c:otherwise>
                <form method="post" action="/pim/attributeCollections/{collectionId}/attributes/{attributeId}/attributeOptions"
                      data-success-message='["Successfully created the attribute option", "Attribute Option Created"]'
                      data-error-message='["Correct the validation error and try again", "Invalid Data"]'>
                    <div class="row">
                        <div class="col-md-6 col-sm-12">
                            <div class="form-group">
                                <label>Attribute Name</label>
                                <input type="text" disabled="disabled" value="${attribute.name}" class="form-control"/>
                            </div>
                            <div class="form-group js-name">
                                <label>Attribute Option Name</label><code class="highlighter-rouge m-l-10">*</code>
                                <input type="text" name="value"  class="form-control" required="true"/>
                            </div>
                            <div class="form-group js-external-id">
                                <label>Attribute Option ID</label><code class="highlighter-rouge m-l-10">*</code>
                                <input type="text" name="id" class="form-control"/>
                            </div>
                            <c:if test="${not empty attribute.parentAttributeId}">
                                <div class="form-group">
                                    <label>Parent Option</label><code class="highlighter-rouge m-l-10">*</code>
                                    <select class="form-control" name="parentOptionFullId" required="true">
                                        <option value="">Select One</option>
                                        <c:forEach var="entry" items="${parentAttributeOptions}">
                                            <option value="${entry.key}"${entry.key eq attributeOption.parentOptionFullId ? ' selected' : ''}>${entry.value}</option>
                                        </c:forEach>
                                    </select>
                                    <br/>
                                    <label class="fancy-checkbox">
                                        <input type="checkbox" name="independent" value="Y">
                                        <span>Independent Option</span>
                                    </label>
                                </div>

                            </c:if>

                        </div>
                    </div>
                    <br>
                </form>
            </c:otherwise>
        </c:choose>

    </div>
</div>
<img src="/assets/img/tiny.png" onload="$.setPageAttributes({attributeOptionId: '${attributeOption.id}'});$.initAHAH(this)"/>
