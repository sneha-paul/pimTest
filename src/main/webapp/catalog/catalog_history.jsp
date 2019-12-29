<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<div class="row clearfix">
    <div class="col-lg-12 col-md-12">
        <div class="card">
            <div class="header">
                <h2>${catalog.catalogName}
                    <small><code class="highlighter-rouge">${catalog.catalogId}</code></small>
                    <small class="pull-right m-t--15"><code style="color:#808080">_id: ${catalog.id}</code></small>
                </h2>
            </div>
            <div class="body">
                <ul class="nav nav-tabs-new2">
                    <li class="nav-item"><a class="nav-link active show" data-toggle="tab" href="#details">Details</a></li>
                </ul>
                <div class="tab-content">
                    <div class="tab-pane show active" id="details">
                        <div class="row clearfix m-t-20">
                            <div class="col-lg-12 col-md-12">
                                <div class="card">
                                    <div class="body">
                                        <form method="post" action="/pim/catalogs/${catalog.catalogId}"
                                              data-method="PUT"
                                              data-success-message='["Successfully updated the catalog", "Catalog Updated"]'
                                              data-error-message='["Correct the validation error and try again", "Invalid Data"]'>
                                            <div class="row">
                                                <div class="col-md-6 col-sm-12">
                                                    <div class="form-group">
                                                        <label for="catalogName">Catalog Name</label><code class="highlighter-rouge m-l-10">*</code>
                                                        <input type="text" id="catalogName" name="catalogName" value="${catalog.catalogName}" class="form-control" readonly>
                                                    </div>

                                                    <div class="form-group js-external-id">
                                                        <label for="catalogId">Catalog ID</label><code class="highlighter-rouge m-l-10">*</code>
                                                        <input type="text" id="catalogId" name="catalogId" class="form-control" value="${catalog.catalogId}" readonly>
                                                    </div>

                                                    <div class="form-group">
                                                        <label for="description">Catalog Description</label>
                                                        <textarea class="form-control" id="description" name="description" rows="5" cols="30" readonly>${catalog.description}</textarea>
                                                    </div>

                                                    <div class="form-group">
                                                        <label>Status</label>
                                                        <br/>
                                                        <label for="active" class="fancy-checkbox">
                                                            <input type="checkbox" id="active" name="active" value="Y" <c:if test="${catalog.active eq 'Y'}">checked="checked"</c:if> disabled>
                                                            <span>Active</span>
                                                        </label>
                                                        <%-- discontinued commented as per client request --%>

                                                        <%--<label for="discontinued" class="fancy-checkbox">
                                                            <input type="checkbox" id="discontinued" name="discontinued" value="Y"
                                                                   <c:if test="${catalog.discontinued eq 'Y'}">checked="checked"</c:if>>
                                                            <span>Discontinued</span>
                                                        </label>--%>
                                                    </div>
                                                    <%--<div class="js-dateRange">
                                                        <div class="form-group">
                                                            <label>Discontinue From </label>
                                                            <input type="text" class="form-control dateUI js-start" name="discontinuedFrom" value="${catalog.discontinuedFrom}">
                                                        </div>
                                                        <div class="form-group">
                                                            <label>Discontinue To </label>
                                                            <input type="text" class="form-control dateUI js-end" name="discontinuedTo" value="${catalog.discontinuedTo}">
                                                        </div>
                                                    </div>--%>
                                                </div>
                                            </div>
                                            <br>
                                            <a href="${breadcrumbs.backURL}/${catalog.catalogId}#catalogHistory"><button type="button" class="btn btn-danger">Cancel</button></a>
                                        </form>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>


