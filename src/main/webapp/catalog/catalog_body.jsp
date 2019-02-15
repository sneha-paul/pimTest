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
                    <li class="nav-item"><a class="nav-link js-rootCategories-tab" data-toggle="tab" href="#rootCategories">Categories</a></li>
                    <li class="nav-item"><a class="nav-link js-hierarchy-tab" data-toggle="tab" href="#hierarchy">Hierarchy</a></li>
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
                                                        <label for="catalogName">Catalog Name</label><code
                                                            class="highlighter-rouge m-l-10">*</code>
                                                        <input type="text" id="catalogName" name="catalogName"
                                                               value="${catalog.catalogName}" class="form-control">
                                                    </div>

                                                    <div class="form-group js-external-id">
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
                                            <input type="hidden" name="group" value="DETAILS"/>
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
                                                    <button type="button" class="btn btn-sm btn-success js-add-category"><i class="fa fa-plus"></i> <span class="p-l-5">Add RootCategories</span></button>
                                                    <button type="button" class="btn btn-sm btn-secondary js-sorting-mode selected"  title="Sorting Mode"><i class="fa fa-sort-alpha-asc"></i></button>
                                                    <button type="button" class="btn btn-sm btn-outline-secondary js-reordering-mode"  title="Reordering Mode"><i class="fa fa-list-ol"></i></button>
                                                </div>
                                            </div>
                                        </div>
                                        <ul class="nav nav-tabs-new2" style="position: absolute; top: -1000px">
                                            <li class="nav-item"><a class="nav-link active show" data-toggle="tab" href="#sortable">Sortable</a></li>
                                            <li class="nav-item"><a class="nav-link" data-toggle="tab" href="#reorderable">Reorderable</a></li>
                                        </ul>
                                        <div class="tab-content">
                                            <div class="tab-pane show active" id="sortable">
                                                <div class="table-responsive scrollable-dt">
                                                    <table id="paginatedRootCategoriesSortableTable" class="table table-hover dataTable table-custom" style="width: 100%">
                                                        <thead class="thead-dark"></thead>
                                                    </table>
                                                </div>
                                            </div>
                                            <div class="tab-pane" id="reorderable">
                                                <div class="table-responsive scrollable-dt no-filter">
                                                    <table id="paginatedRootCategoriesReorderableTable" class="table table-hover dataTable table-custom m-b-0" style="width: 100% !important">
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
                    <div class="tab-pane" id="hierarchy">
                        <div class="row clearfix">
                            <div class="col-lg-12 col-md-12">
                                <div class="card">
                                    <div class="body">
                                        <div class="table-responsive scrollable-dt no-filter">
                                            <table id="categoriesHierarchy" class="table table-hover dataTable treeDataTable table-custom m-b-0" style="width: 100% !important">
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
    </div>
</div>
<script>
    $.initPage({
        'catalogId': '${catalog.catalogId}',
        'websiteId': '<c:if test="${not empty param.websiteId}">${param.websiteId}</c:if>'
    });
</script>
<img src="/assets/img/tiny.png" onload="$.initAHAH(this)"/>
<script src="/assets/js/pages/ui/catalog/catalog.js"></script>

