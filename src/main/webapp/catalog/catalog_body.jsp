<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<c:choose>
    <c:when test="${mode eq 'DETAILS'}">
        <div class="row clearfix">
            <div class="col-lg-12 col-md-12">
                <div class="card">
                    <div class="header">
                        <h2>${catalog.catalogName} <small><code class="highlighter-rouge">${catalog.catalogId}</code></small></h2>
                    </div>
                    <div class="body">
                        <ul class="nav nav-tabs-new2">
                            <li class="nav-item"><a class="nav-link active show" data-toggle="tab" href="#Home-new2">Attributes</a></li>
                        </ul>
                        <div class="tab-content">
                            <div class="tab-pane show active" id="Home-new2">
                                <div class="row clearfix m-t-20">
                                    <div class="col-lg-12 col-md-12">
                                        <div class="card">
                                            <div class="body">
                                                <form id="update-form" method="post" action="/pim/catalogs/${catalog.catalogId}" novalidate>
                                                    <div class="row">
                                                        <div class="col-md-6 col-sm-12">
                                                            <div class="form-group">
                                                                <label>Catalog Name</label><code class="highlighter-rouge m-l-10">*</code>
                                                                <input type="text" name="catalogName" value="${catalog.catalogName}" class="form-control" required>
                                                            </div>

                                                            <div class="form-group">
                                                                <label>Catalog ID</label><code class="highlighter-rouge m-l-10">*</code>
                                                                <input type="text" name="catalogId" class="form-control" value="${catalog.catalogId}" required>
                                                            </div>

                                                            <div class="form-group">
                                                                <label>Catalog Description</label>
                                                                <textarea class="form-control" name="description" rows="5" cols="30" required="">${catalog.description}</textarea>
                                                            </div>

                                                            <div class="form-group">
                                                                <label>Status</label>
                                                                <br/>
                                                                <label class="fancy-checkbox">
                                                                    <input type="checkbox" name="active" value="Y" <c:if test="${catalog.active eq 'Y'}">checked="checked"</c:if>>
                                                                    <span>Active</span>
                                                                </label>
                                                            </div>
                                                        </div>
                                                    </div>
                                                    <br>
                                                    <button type="submit" class="btn btn-primary">Save</button>
                                                    <a href="/pim/catalogs"><button type="button" class="btn btn-danger">Cancel</button></a>
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
    </c:when>
    <c:otherwise>
        <div class="row clearfix">
            <div class="col-lg-12 col-md-12">
                <div class="card">
                    <div class="header">
                        <h1>Create Catalog</h1>
                    </div>
                    <div class="body">
                        <form id="create-form" method="post" action="/pim/catalogs" novalidate>
                            <div class="row">
                                <div class="col-md-6 col-sm-12">
                                    <div class="form-group">
                                        <label>Catalog Name</label>
                                        <input type="text" name="catalogName" class="form-control" required>
                                    </div>

                                    <div class="form-group">
                                        <label>Catalog ID</label>
                                        <input type="text" name="catalogId" class="form-control" required>
                                    </div>

                                    <div class="form-group">
                                        <label>Catalog Description</label>
                                        <textarea class="form-control" name="description" value="${catalog.description}" rows="5" cols="30" required=""></textarea>
                                    </div>
                                </div>
                            </div>
                            <br>
                            <button type="submit" class="btn btn-primary">Create</button>
                            <a href="/pim/catalogs/"><button type="button" class="btn btn-danger">Cancel</button></a>
                        </form>
                    </div>
                </div>
            </div>
        </div>
    </c:otherwise>
</c:choose>
