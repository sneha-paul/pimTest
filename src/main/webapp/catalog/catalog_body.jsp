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
                            <li class="nav-item"><a class="nav-link active show" data-toggle="tab" href="#Attributes">Attributes</a></li>
                            <li class="nav-item"><a class="nav-link" data-toggle="tab" href="#RootCategories">RootCategories</a></li>
                        </ul>
                        <div class="tab-content">
                            <div class="tab-pane show active" id="Attributes">
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
                            <div class="tab-pane" id="RootCategories">
                                <div class="row clearfix">
                                    <div class="col-lg-12 col-md-12">
                                        <div class="card">
                                            <div class="body">
                                                <div class="row p-b-25">
                                                    <div class="col-lg-12 col-md-12"><div class="pull-right"><button type="button" class="btn btn-success js-add-catalog"><i class="fa fa-plus"></i> <span class="p-l-5">Add RootCategories</span></button></div></div>
                                                </div>
                                                <table id="only-bodytable" class="table table-hover dataTable table-custom">
                                                    <thead class="thead-dark">
                                                    <tr>
                                                        <th>Category Name</th>
                                                        <th>Category ID</th>
                                                        <th>Actions</th>
                                                    </tr>
                                                    </thead>
                                                    <c:forEach items="${categories.category.content}" var="category">
                                                        <tr>
                                                            <td>${category.categoryName}<i class="table-dragger-handle sindu_handle"></i></td>
                                                            <td>${category.categoryId}</td>
                                                            <td>
                                                                <a href="javascript:void(0);" class="btn btn-sm btn-outline-primary" title="clone"><i class="icon-docs"></i></a>
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

        <div class="modal fade" id="largeModal" tabindex="-1" role="dialog">
            <div class="modal-dialog modal-lg" role="document">
                <div class="modal-content">
                    <div class="modal-header">
                        <h4 class="title" id="largeModalLabel">Available Subcategories</h4>
                    </div>
                    <div class="modal-body">
                        <div class="card">
                            <div class="body">
                                <div class="table-responsive">
                                    <table class="table table-hover dataTable table-custom">
                                        <thead class="thead-dark">
                                        <tr>
                                            <th>Category Name</th>
                                            <th>Category ID</th>
                                            <th>Select</th>
                                        </tr>
                                        </thead>
                                        &lt;%&ndash;<%--<tr>
                                        <td>Star Wars</td>
                                        <td>1977</td>
                                        <td>
                                            <label class="fancy-checkbox">
                                                <input type="checkbox" name="active" value="Y">
                                                <span></span>
                                            </label>
                                        </td>
                                    </tr>
                                        <tr>
                                            <td>Howard The Duck</td>
                                            <td>1986</td>
                                            <td>$16,295,774</td>
                                        </tr>
                                        <tr>
                                            <td>American Graffiti</td>
                                            <td>1973</td>
                                            <td>$115,000,000</td>
                                        </tr>--%>&ndash;%&gt;
                                    </table>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-primary">SAVE CHANGES</button>
                        <button type="button" class="btn btn-danger" data-dismiss="modal">CLOSE</button>
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
