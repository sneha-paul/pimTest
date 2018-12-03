<%--@elvariable id="website" type="com.bigname.pim.api.domain.Website"--%>
<%--@elvariable id="catalog" type="com.bigname.pim.api.domain.Catalog"--%>
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
                    <li class="nav-item"><a class="nav-link active show" data-toggle="tab" href="#DETAILS">Details</a></li>
                    <c:if test="${not empty website}">
                        <li class="nav-item"><a class="nav-link" data-toggle="tab" href="#rootCategories">RootCategories</a></li>
                    </c:if>
                </ul>
                <div class="tab-content">
                    <div class="tab-pane show active" id="DETAILS">
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
                                                        <label for="catalogName">Catalog Name</label><code
                                                            class="highlighter-rouge m-l-10">*</code>
                                                        <input type="text" id="catalogName" name="catalogName"
                                                               value="${catalog.catalogName}" class="form-control">
                                                    </div>

                                                    <div class="form-group">
                                                        <label for="catalogId">Catalog ID</label><code
                                                            class="highlighter-rouge m-l-10">*</code>
                                                        <input type="text" id="catalogId" name="catalogId"
                                                               class="form-control" value="${catalog.catalogId}">
                                                    </div>

                                                    <div class="form-group">
                                                        <label for="description">Catalog Description</label>
                                                        <textarea class="form-control" id="description"
                                                                  name="description" rows="5"
                                                                  cols="30">${catalog.description}</textarea>
                                                    </div>

                                                    <div class="form-group">
                                                        <label>Status</label>
                                                        <br/>
                                                        <label for="active" class="fancy-checkbox">
                                                            <input type="checkbox" id="active" name="active" value="Y"
                                                                   <c:if test="${catalog.active eq 'Y'}">checked="checked"</c:if>>
                                                            <span>Active</span>
                                                        </label>
                                                        <label for="discontinued" class="fancy-checkbox">
                                                            <input type="checkbox" id="discontinued" name="discontinued" value="Y"
                                                                   <c:if test="${catalog.discontinued eq 'Y'}">checked="checked"</c:if>>
                                                            <span>Discontinued</span>
                                                        </label>
                                                    </div>
                                                </div>
                                            </div>
                                            <br>
                                            <input type="hidden" name="group" value="DETAILS"/>
                                            <button type="submit" class="btn btn-primary"
                                                    onclick="$.submitAction(event, this)">Save
                                            </button>
                                            <a href="<c:choose><c:when test="${not empty website}">/pim/websites/${website.websiteId}#catalogs</c:when><c:otherwise>/pim/catalogs</c:otherwise></c:choose>">
                                                <button type="button" class="btn btn-danger">Cancel</button>
                                            </a>
                                        </form>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="tab-pane" id="rootCategories">
                        <div class="row clearfix">
                            <div class="col-lg-12 col-md-12">
                                <div class="card">
                                    <div class="body">
                                        <div class="row p-b-25">
                                            <div class="col-lg-12 col-md-12">
                                                <div class="pull-right">
                                                    <button type="button" class="btn btn-success js-add-category"><i
                                                            class="fa fa-plus"></i> <span class="p-l-5">Add RootCategories</span>
                                                    </button>
                                                </div>
                                            </div>
                                        </div>
                                        <div class="table-responsive">
                                            <table id="paginatedRootCategoriesTable"
                                                   class="table table-hover dataTable table-custom" style="width: 100%">
                                                <thead class="thead-dark"></thead>
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
</div>
<c:choose>
    <c:when test="${not empty website}">
        <script>
            $.initPage({
                'websiteId': '${website.websiteId}',
                'catalogId': '${catalog.catalogId}'

            });
            $(document).ready(function () {
                $.initDataTable({
                    selector: '#paginatedRootCategoriesTable',
                    name: 'rootCategories',
                    type: 'TYPE_2',
                    url: $.getURL('/pim/websites/{websiteId}/catalogs/{catalogId}/rootCategories'),
                    url2: $.getURL('/pim/websites/{websiteId}/catalogs/{catalogId}/categories/'),
                    columns: [
                        {data: 'rootCategoryName', name: 'rootCategoryName', title: 'Category Name'},
                        {data: 'externalId', name: 'rootCategoryId', title: 'Category ID'},
                        {data: 'active', name: 'active', title: 'Status', orderable: false},
                        {data: 'actions', name: 'actions', title: 'Actions', orderable: false}
                    ]
                });

            });
        </script>
    </c:when>
    <c:otherwise>
        <script>
            $.initPage({
            'catalogId': '${catalog.catalogId}'
            });
        </script>
    </c:otherwise>
</c:choose>

<script src="/assets/js/pages/ui/catalog/catalog.js"></script>

