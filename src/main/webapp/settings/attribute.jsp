<%--@elvariable id="attribute" type="com.bigname.pim.core.domain.Attribute"--%>
<%--@elvariable id="attributeGroups" type="java.util.List<org.javatuples.Pair<String, String>"--%>
<%--@elvariable id="parentAttributes" type="java.util.Map<String, String>"--%>
<%--@elvariable id="uiTypes" type="java.util.Map<String, String>"--%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<c:choose>
    <c:when test="${mode eq 'DETAILS'}">
        <tiles:insertDefinition name="mainLayout">
            <tiles:putAttribute name="title" value="PIM - Attribute"/>
            <tiles:putAttribute name="body" value="/settings/attribute_body.jsp"/>
        </tiles:insertDefinition>
    </c:when>
    <c:otherwise>
        <div class="row clearfix">
            <div class="col-lg-12 col-md-12">
                <form method="post" action="/pim/attributeCollections/{collectionId}/attributes"
                      data-success-message='["Successfully created the attribute", "Attribute Created"]'
                      data-error-message='["Correct the validation error and try again", "Invalid Data"]'>
                    <div class="row">
                        <div class="col-md-6 col-sm-12">
                            <div class="form-group">
                                <label for="js-attribute-group-id">Attribute Group</label><code class="highlighter-rouge m-l-10">*</code>
                                <select class="form-control" id="js-attribute-group-id" name="attributeGroup.fullId">
                                    <%--<option value="">Add a NEW GROUP</option>--%>
                                    <c:forEach var="pair" items="${attributeGroups}">
                                        <option value="${pair.value0}" <c:if test="${pair.value0 eq 'DEFAULT_GROUP'}">selected</c:if>>${pair.value1}</option>
                                    </c:forEach>
                                </select>
                            </div>
                            <div class="js-new-attribute-group" style="display: none;">
                                <div class="form-group js-attribute-group-name">
                                    <label for="attribute-group-name">Attribute Group Name</label>
                                    <input type="text" id="attribute-group-name" name="attributeGroup.name" class="form-control" />
                                </div>
                                <div class="form-group">
                                    <label for="js-parent-group-id">Parent Group</label>
                                    <select class="form-control" id="js-parent-group-id" name="attributeGroup.parentGroup.id">
                                        <option value="">Select One</option>
                                        <c:forEach var="pair" items="${attributeGroups}">
                                            <option value="${pair.value0}">${pair.value1}</option>
                                        </c:forEach>
                                    </select>
                                </div>
                            </div>
                            <div class="form-group js-name">
                                <label>Attribute Name</label><code class="highlighter-rouge m-l-10">*</code>
                                <input type="text" name="name" class="form-control" required="true"/>
                            </div>
                            <div class="form-group js-external-id">
                                <label>Attribute ID</label><code class="highlighter-rouge m-l-10">*</code>
                                <input type="text" name="id" class="form-control" required="true"/>
                            </div>
                            <div class="form-group">
                                <label>Parent Attribute</label>
                                <select class="form-control" name="parentAttributeId">
                                    <option value="">Select One</option>
                                    <c:forEach var="entry" items="${parentAttributes}">
                                        <option value="${entry.key}">${entry.value}</option>
                                    </c:forEach>
                                </select>
                            </div>
                            <div class="form-group">
                                <label for="uiType">Attribute UI Type</label><code class="highlighter-rouge m-l-10">*</code>
                                <select class="form-control" id="uiType" name="uiType">
                                    <c:forEach var="entry" items="${uiTypes}">
                                        <option value="${entry.key}"${entry.key eq attribute.uiType ? ' selected' : ''}>${entry.value}</option>
                                    </c:forEach>
                                </select>
                            </div>
                        </div>
                    </div>
                    <br>
                </form>
            </div>
        </div>
    </c:otherwise>
</c:choose>