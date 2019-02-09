<%--@elvariable id="attribute" type="com.bigname.pim.api.domain.Attribute"--%>
<%--@elvariable id="parentAttribute" type="com.bigname.pim.api.domain.Attribute"--%>
<%--@elvariable id="attributeGroups" type="java.util.List<org.javatuples.Pair<String, String>"--%>
<%--@elvariable id="parentAttributes" type="java.util.Map<String, String>"--%>
<%--@elvariable id="uiTypes" type="java.util.Map<String, String>"--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<div class="row clearfix">
    <div class="col-lg-12 col-md-12">
        <div class="card">
            <div class="header">
                <h2>${attribute.name} <small><code class="highlighter-rouge">${attribute.id}</code></small></h2>
            </div>
            <div class="body">
                <ul class="nav nav-tabs-new2">
                    <li class="nav-item"><a class="nav-link active show" data-toggle="tab" href="#details">Details</a></li>
                    <li class="nav-item"><a class="nav-link js-attributeOptions-tab" data-toggle="tab" href="#attributeOptions">Options</a></li>
                </ul>
                <div class="tab-content">
                    <div class="tab-pane show active" id="details">
                        <div class="row clearfix m-t-20">
                            <div class="col-lg-12 col-md-12">
                                <form method="post" action="/pim/attributeCollections/{collectionId}/attributes/{attributeId}" data-method="PUT"
                                      data-success-message='["Successfully updated the attribute", "Attribute Updated"]'
                                      data-error-message='["Correct the validation error and try again", "Invalid Data"]'>
                                    <div class="row">
                                        <div class="col-md-6 col-sm-12">
                                            <div class="form-group">
                                                <label>Attribute Group ID</label>
                                                <input type="text" disabled="disabled" value="${attribute.attributeGroup.fullId}" class="form-control"/>
                                            </div>
                                            <div class="form-group">
                                                <label>Attribute ID</label>
                                                <input type="text" disabled="disabled" value="${attribute.id}" class="form-control"/>
                                                <input type="hidden" name="fullId" value="${attribute.fullId}" />
                                            </div>
                                            <div class="form-group">
                                                <label>Attribute Name</label><code class="highlighter-rouge m-l-10">*</code>
                                                <input type="text" name="name" value="${attribute.name}" class="form-control" required="true"/>
                                            </div>
                                            <div class="form-group">
                                                <label for="uiType">Attribute UI Type</label><code class="highlighter-rouge m-l-10">*</code>
                                                <select class="form-control" id="uiType" name="uiType">
                                                    <c:forEach var="entry" items="${uiTypes}">
                                                        <option value="${entry.key}"${entry.key eq attribute.uiType ? ' selected' : ''}>${entry.value}</option>
                                                    </c:forEach>
                                                </select>
                                            </div>
                                            <div class="form-group">
                                                <label>Parent Attribute</label>
                                                <select class="form-control" name="parentAttributeId">
                                                    <option value="">Select One</option>
                                                    <c:forEach var="entry" items="${parentAttributes}">
                                                        <option value="${entry.key}" ${entry.key eq attribute.parentAttributeId ? ' selected' : ''}>${entry.value}</option>
                                                    </c:forEach>
                                                </select>
                                            </div>
                                        </div>
                                    </div>
                                    <br>
                                    <input type="hidden" name="group" value="ATTRIBUTES"/>
                                    <button type="submit" class="btn btn-primary"
                                            onclick="$.submitAction(event, this)">Save
                                    </button>
                                    <a href="${breadcrumbs.backURL}">
                                        <button type="button" class="btn btn-danger">Cancel</button>
                                    </a>
                                </form>
                            </div>
                        </div>
                    </div>
                    <div class="tab-pane" id="attributeOptions">
                        <div class="row clearfix">
                            <div class="col-lg-12 col-md-12">
                                <div class="row p-b-25">
                                    <div class="col-lg-12 col-md-12">
                                        <div class="pull-right">
                                            <button type="button"
                                                    class="btn btn-success js-add-attributeOption"><i
                                                    class="fa fa-plus"></i> <span class="p-l-5">Add Option</span>
                                            </button>
                                        </div>
                                    </div>
                                </div>
                                <div class="table-responsive scrollable-dt">
                                    <table id="paginatedAttributeOptionsTable"
                                           class="table table-hover dataTable table-custom" style="width: 100%">
                                        <thead class="thead-dark">

                                        </thead>
                                    </table>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>
<script>
    $.initPage({
        collectionId: '${collectionId}',
        attributeId: '${attribute.id}',
        attributeName: '${attribute.name}',
        attributeFullId: '${attribute.fullId}',
        parentAttributeName: '${not empty parentAttribute ? parentAttribute.name : ""}'
    });
</script>
<script src="/assets/js/pages/ui/settings/attribute.js"></script>

