<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<c:choose>
    <c:when test="${mode eq 'DETAILS'}">
        <div class="row clearfix">
            <div class="col-lg-12 col-md-12">
                <div class="card">
                    <div class="header">
                        <h2>${website.websiteName} <small><code class="highlighter-rouge">${website.websiteId}</code></small></h2>
                    </div>
                    <div class="body">
                        <ul class="nav nav-tabs-new2">
                            <li class="nav-item"><a class="nav-link active show" data-toggle="tab" href="#Attributes">Attributes</a></li>
                            <li class="nav-item"><a class="nav-link" data-toggle="tab" href="#Catalogs">Catalogs</a></li>
                        </ul>
                        <div class="tab-content">
                            <div class="tab-pane show active" id="Attributes">
                                <div class="row clearfix m-t-20">
                                    <div class="col-lg-12 col-md-12">
                                        <div class="card">
                                            <div class="body">
                                                <form id="update-form" method="post" action="/pim/websites/${website.websiteId}" novalidate>
                                                    <div class="row">
                                                        <div class="col-md-6 col-sm-12">
                                                            <div class="form-group">
                                                                <label>Website Name</label><code class="highlighter-rouge m-l-10">*</code>
                                                                <input type="text" name="websiteName" value="${website.websiteName}" class="form-control" required="true"/>
                                                            </div>
                                                            <div class="form-group">
                                                                <label>Website URL</label><code class="highlighter-rouge m-l-10">*</code>
                                                                <input type="url" name="url" class="form-control" value="${website.url}" required="true"/>
                                                            </div>
                                                            <div class="form-group">
                                                                <label>Website ID</label><code class="highlighter-rouge m-l-10">*</code>
                                                                <input type="text" name="websiteId" class="form-control" value="${website.websiteId}" required="true"/>
                                                            </div>

                                                            <div class="form-group">
                                                                <label>Status</label>
                                                                <br/>
                                                                <label class="fancy-checkbox">
                                                                    <input type="checkbox" name="active" value="Y" <c:if test="${website.active eq 'Y'}">checked="checked"</c:if>>
                                                                    <span>Active</span>
                                                                </label>
                                                            </div>
                                                        </div>
                                                    </div>
                                                    <br>
                                                    <button type="submit" class="btn btn-primary">Save</button>
                                                    <a href="/pim/websites"><button type="button" class="btn btn-danger">Cancel</button></a>
                                                </form>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                            <div class="tab-pane" id="Catalogs">
                                <div class="row clearfix">
                                    <div class="col-lg-12 col-md-12">
                                        <div class="card">
                                            <div class="body">
                                                <div class="row p-b-25">
                                                    <div class="col-lg-12 col-md-12"><div class="pull-right"><button type="button" class="btn btn-success js-add-catalog"><i class="fa fa-plus"></i> <span class="p-l-5">Add Catalog</span></button></div></div>
                                                </div>
                                                <table id="only-bodytable" class="table table-hover dataTable table-custom">
                                                    <thead class="thead-dark">
                                                    <tr>
                                                        <th>Catalog Name</th>
                                                        <th>Catalog ID</th>
                                                        <th>Status</th>
                                                        <th>Actions</th>
                                                    </tr>
                                                    </thead>
                                                    <c:forEach items="${website.catalogs.content}" var="websiteCatalog">
                                                        <c:set var="catalog" value="${websiteCatalog.child}" />
                                                    <tr>
                                                        <td>${catalog.catalogName}<i class="table-dragger-handle sindu_handle"></i></td>
                                                        <td>${catalog.catalogId}</td>
                                                        <td><c:choose><c:when test="${websiteCatalog.active eq 'Y'}"><span class="badge badge-success">Active</span><</c:when><c:otherwise><span class="badge badge-danger">Inactive</span><</c:otherwise></c:choose>/td>
                                                        <td>
                                                            <a href="javascript:void(0);" class="btn btn-sm btn-outline-danger js-sweetalert" title="Disable/Enable" data-type="confirm"><i class="icon-ban"></i></a>
                                                        </td>
                                                    </tr>
                                                    </c:forEach>
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
        </div>
        <script>
            $.initPage({
                'websiteId' : '${website.websiteId}'
            });
        </script>
        <script src="/assets/js/pages/ui/website/website.js"></script>
    </c:when>
    <c:otherwise>
        <div class="row clearfix">
            <div class="col-lg-12 col-md-12">
                <div class="card">
                    <div class="header">
                        <h1>Create Website</h1>
                    </div>
                    <div class="body">
                        <form:form id="create-form" method="post" action="/pim/websites" modelAttribute="website">
                            <div class="row">
                                <div class="col-md-6 col-sm-12">
                                    <div class="form-group">
                                        <label>Website Name</label>
                                        <form:input type="text" path="websiteName" class="form-control" required="true"/>
                                        <form:errors path="websiteName" cssClass="error"/>
                                    </div>
                                    <div class="form-group">
                                        <label>Website URL</label>
                                        <form:input type="url" path="url" class="form-control" required="true"/>
                                        <form:errors path="url" cssClass="error"/>
                                    </div>
                                    <div class="form-group">
                                        <label>Website ID</label>
                                        <form:input type="text" path="websiteId" class="form-control" required="true"/>
                                        <form:errors path="websiteId" cssClass="error"/>
                                    </div>
                                </div>
                            </div>
                            <br>
                            <button type="submit" class="btn btn-primary">Create</button>
                            <a href="/pim/websites/"><button type="button" class="btn btn-danger">Cancel</button></a>
                        </form:form>
                    </div>
                </div>
            </div>
        </div>
    </c:otherwise>
</c:choose>

