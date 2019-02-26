<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<div class="row clearfix">
    <div class="col-lg-12 col-md-12">
        <div class="card">
            <div class="header">
                <h2>${category.categoryName} <small><code class="highlighter-rouge">${category.categoryId}</code></small><small class="pull-right m-t--15"><code style="color:#808080">_id: ${category.id}</code></small></h2>
            </div>
            <div class="body">
                <ul class="nav nav-tabs-new2">
                    <li class="nav-item"><a class="nav-link active show" data-toggle="tab" href="#details">Details</a></li>
                    <li class="nav-item"><a class="nav-link" data-toggle="tab" href="#seo">SEO</a></li>
                    <li class="nav-item"><a class="nav-link js-subCategories-tab" data-toggle="tab" href="#subCategories">SubCategories</a></li>
                    <li class="nav-item"><a class="nav-link js-products-tab" data-toggle="tab" href="#products">Parent Products</a></li>
                </ul>
                <div class="tab-content">
                    <div class="tab-pane show active" id="details">
                        <div class="row clearfix m-t-20">
                            <div class="col-lg-12 col-md-12">
                                <div class="card">
                                    <div class="body">
                                        <form method="post" action="/pim/categories/${category.categoryId}" data-method="PUT"
                                              data-success-message='["Successfully updated the category", "Category Updated"]'
                                              data-error-message='["Correct the validation error and try again", "Invalid Data"]'>
                                            <div class="row">
                                                <div class="col-md-6 col-sm-12">
                                                    <div class="form-group">
                                                        <label for="categoryName">Category Name</label><code class="highlighter-rouge m-l-10">*</code>
                                                        <input type="text" id="categoryName" name="categoryName" value="${category.categoryName}" class="form-control" />
                                                    </div>

                                                    <div class="form-group js-external-id">
                                                        <label for="categoryId">Category ID</label><code class="highlighter-rouge m-l-10">*</code>
                                                        <input type="text" id="categoryId" name="categoryId" class="form-control" value="${category.categoryId}" />
                                                    </div>

                                                    <div class="form-group">
                                                        <label for="description">Category Description</label>
                                                        <textarea class="form-control" id="description" name="description" rows="5" cols="30" required="">${category.description}</textarea>
                                                    </div>

                                                    <div class="form-group">
                                                        <label for="longDescription">Category Long Description</label>
                                                        <textarea class="form-control" id="longDescription" name="longDescription" rows="5" cols="30" required="">${category.longDescription}</textarea>
                                                    </div>

                                                    <div class="form-group">
                                                        <label>Status</label>
                                                        <br/>
                                                        <label for="active" class="fancy-checkbox">
                                                            <input type="checkbox" id="active" name="active" value="Y" <c:if test="${category.active eq 'Y'}">checked="checked"</c:if>>
                                                            <span>Active</span>
                                                        </label>
                                                        <label for="discontinued" class="fancy-checkbox">
                                                            <input type="checkbox" id="discontinued" name="discontinued" value="Y" <c:if test="${category.discontinued eq 'Y'}">checked="checked"</c:if>>
                                                            <span>Discontinued</span>
                                                        </label>
                                                    </div>
                                                    <div class="js-dateRange">
                                                        <div class="form-group">
                                                            <label>Active From </label>
                                                            <input type="text" class="form-control dateUI js-start" name="activeFromDate" value="${category.activeFromDate}">
                                                        </div>
                                                        <div class="form-group">
                                                            <label>Active To </label>
                                                            <input type="text" class="form-control dateUI js-end" name="activeToDate" value="${category.activeToDate}">
                                                        </div>
                                                    </div>
                                                    <%--<div class="js-dateRange">
                                                        <div class="form-group">
                                                            <label>Discontinue From </label>
                                                            <input type="text" class="form-control dateUI js-start" name="discontinuedFromDate" value="${category.discontinuedFromDate}">
                                                        </div>
                                                        <div class="form-group">
                                                            <label>Discontinue To </label>
                                                            <input type="text" class="form-control dateUI js-end" name="discontinuedToDate" value="${category.discontinuedToDate}">
                                                        </div>
                                                    </div>--%>
                                                </div>
                                            </div>
                                            <br>
                                            <input type="hidden" name="group" value="DETAILS"/>
                                            <button type="submit" class="btn btn-primary" onclick="$.submitAction(event, this)">Save</button>
                                            <a href="${backURL}"><button type="button" class="btn btn-danger">Cancel</button></a>
                                        </form>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="tab-pane" id="seo">
                        <div class="row clearfix m-t-20">
                            <div class="col-lg-12 col-md-12">
                                <div class="card">
                                    <div class="body">
                                        <form method="post" action="/pim/categories/${category.categoryId}" data-method="PUT" data-success-message='["Successfully updated the category", "Category Updated"]' data-error-message='["Correct the validation error and try again", "Invalid Data"]'>
                                            <div class="row">
                                                <div class="col-md-6 col-sm-12">
                                                    <div class="form-group">
                                                        <label for="metaTitle">Meta Title</label><code class="highlighter-rouge m-l-10">*</code>
                                                        <input type="text" id="metaTitle" name="metaTitle" class="form-control" value="${category.metaTitle}" />
                                                    </div>

                                                    <div class="form-group">
                                                        <label for="metaDescription">Meta Description</label><code class="highlighter-rouge m-l-10">*</code>
                                                        <textarea class="form-control" id="metaDescription" name="metaDescription" rows="5" cols="30" required="">${category.metaDescription}</textarea>
                                                    </div>

                                                    <div class="form-group">
                                                        <label for="metaKeywords">Meta Keywords</label>
                                                        <textarea class="form-control" id="metaKeywords" name="metaKeywords" rows="5" cols="30" required="">${category.metaKeywords}</textarea>
                                                    </div>
                                                </div>
                                            </div>
                                            <br>
                                            <input type="hidden" name="group" value="SEO"/>
                                            <button type="submit" class="btn btn-primary" onclick="$.submitAction(event, this)">Save</button>
                                            <a href="${backURL}"><button type="button" class="btn btn-danger">Cancel</button></a>
                                        </form>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="tab-pane" id="subCategories">
                        <div class="row clearfix">
                            <div class="col-lg-12 col-md-12">
                                <div class="row p-b-25">
                                    <div class="col-lg-12 col-md-12">
                                        <div class="pull-right">
                                            <button type="button" class="btn btn-success js-add-subCategory"><i class="fa fa-plus"></i> <span class="p-l-5">Add SubCategories</span></button>
                                            <button type="button" class="btn btn-sm btn-secondary js-sorting-mode subCategories selected"  title="Sorting Mode"><i class="fa fa-sort-alpha-asc"></i></button>
                                            <button type="button" class="btn btn-sm btn-outline-secondary js-reordering-mode subCategories"  title="Reordering Mode"><i class="fa fa-list-ol"></i></button>
                                        </div>
                                    </div>
                                </div>
                                <ul class="nav nav-tabs-new2" style="position: absolute; top: -1000px">
                                    <li class="nav-item"><a class="nav-link active show" data-toggle="tab" href="#subCategoriesSortable">Sortable</a></li>
                                    <li class="nav-item"><a class="nav-link" data-toggle="tab" href="#subCategoriesReorderable">Reorderable</a></li>
                                </ul>
                                <div class="tab-content">
                                    <div class="tab-pane show active" id="subCategoriesSortable">
                                        <div class="table-responsive scrollable-dt">
                                            <table id="paginatedSubCategoriesSortableTable" class="table table-hover dataTable table-custom" style="width: 100%">
                                                <thead class="thead-dark"></thead>
                                            </table>
                                        </div>
                                    </div>
                                    <div class="tab-pane" id="subCategoriesReorderable">
                                        <div class="table-responsive scrollable-dt no-filter">
                                            <table id="paginatedSubCategoriesReorderableTable" class="table table-hover dataTable table-custom m-b-0" style="width: 100% !important">
                                                <thead class="thead-dark"></thead>
                                            </table>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="tab-pane" id="products">
                        <div class="row clearfix">
                            <div class="col-lg-12 col-md-12">
                                <div class="row p-b-25">
                                    <div class="col-lg-12 col-md-12">
                                        <div class="pull-right">
                                            <button type="button" class="btn btn-success js-add-products"><i class="fa fa-plus"></i> <span class="p-l-5">Add Parent Product</span></button>
                                            <button type="button" class="btn btn-sm btn-secondary js-sorting-mode products selected"  title="Sorting Mode"><i class="fa fa-sort-alpha-asc"></i></button>
                                            <button type="button" class="btn btn-sm btn-outline-secondary js-reordering-mode products"  title="Reordering Mode"><i class="fa fa-list-ol"></i></button>
                                        </div>
                                    </div>
                                </div>
                                <ul class="nav nav-tabs-new2" style="position: absolute; top: -1000px">
                                    <li class="nav-item"><a class="nav-link active show" data-toggle="tab" href="#productsSortable">Sortable</a></li>
                                    <li class="nav-item"><a class="nav-link" data-toggle="tab" href="#productsReorderable">Reorderable</a></li>
                                </ul>
                                <div class="tab-content">
                                    <div class="tab-pane show active" id="productsSortable">
                                        <div class="table-responsive scrollable-dt">
                                            <table id="paginatedProductsSortableTable" class="table table-hover dataTable table-custom" style="width: 100%">
                                                <thead class="thead-dark"></thead>
                                            </table>
                                        </div>
                                    </div>
                                    <div class="tab-pane" id="productsReorderable">
                                        <div class="table-responsive scrollable-dt no-filter">
                                            <table id="paginatedProductsReorderableTable" class="table table-hover dataTable table-custom m-b-0" style="width: 100% !important">
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
<script>
    $.initPage({
        'categoryId' : '${category.categoryId}',
        'parentId' : '<c:if test="${not empty parentId}">${parentId}</c:if>',
        'websiteId': '<c:if test="${not empty param.websiteId}">${param.websiteId}</c:if>',
        'hash': '${param.hash}',
        'catalogId' : '${param.catalogId}'
    });
</script>
<img src="/assets/img/tiny.png" onload="$.initAHAH(this)"/>
<script src="/assets/js/pages/ui/category/category.js"></script>



