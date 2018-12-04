<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
        <div class="row clearfix">
            <div class="col-lg-12 col-md-12">
                <div class="card">
                    <div class="header">
                        <h2>${category.categoryName} <small><code class="highlighter-rouge">${category.categoryId}</code></small><small class="pull-right m-t--15"><code style="color:#808080">_id: ${category.id}</code></small></h2>
                    </div>
                    <div class="body">
                        <ul class="nav nav-tabs-new2">
                            <li class="nav-item"><a class="nav-link active show" data-toggle="tab" href="#DETAILS">Details</a></li>
                            <li class="nav-item"><a class="nav-link" data-toggle="tab" href="#SEO">SEO</a></li>
                            <li class="nav-item"><a class="nav-link" data-toggle="tab" href="#SUB_CATEGORIES">SubCategories</a></li>
                            <li class="nav-item"><a class="nav-link" data-toggle="tab" href="#PRODUCTS">Products</a></li>
                        </ul>
                        <div class="tab-content">
                            <div class="tab-pane show active" id="DETAILS">
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

                                                            <div class="form-group">
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
                                                        </div>
                                                    </div>
                                                    <br>
                                                    <input type="hidden" name="group" value="DETAILS"/>
                                                    <button type="submit" class="btn btn-primary" onclick="$.submitAction(event, this)">Save</button>
                                                    <a href="/pim/categories"><button type="button" class="btn btn-danger">Cancel</button></a>
                                                </form>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                            <div class="tab-pane" id="SEO">
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
                                                    <a href="/pim/categories"><button type="button" class="btn btn-danger">Cancel</button></a>
                                                </form>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                            <div class="tab-pane" id="SUB_CATEGORIES">
                                <div class="row clearfix">
                                    <div class="col-lg-12 col-md-12">
                                        <div class="card">
                                            <div class="body">
                                                <div class="row p-b-25">
                                                    <div class="col-lg-12 col-md-12">
                                                        <div class="pull-right">
                                                            <button type="button" class="btn btn-success js-add-subCategory"><i class="fa fa-plus"></i> <span class="p-l-5">Add SubCategories</span></button>
                                                        </div>
                                                    </div>
                                                </div>
                                                <div class="table-responsive">
                                                    <table id="paginatedSubCategoriesTable" class="table table-hover dataTable table-custom" style="width: 100%">
                                                        <thead class="thead-dark"></thead>
                                                    </table>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                            <div class="tab-pane" id="PRODUCTS">
                                <div class="row clearfix">
                                    <div class="col-lg-12 col-md-12">
                                        <div class="card">
                                            <div class="body">
                                                <div class="row p-b-25">
                                                    <div class="col-lg-12 col-md-12">
                                                        <div class="pull-right">
                                                            <button type="button" class="btn btn-success js-add-products"><i class="fa fa-plus"></i> <span class="p-l-5">Add Products</span></button>
                                                        </div>
                                                    </div>
                                                </div>
                                                <div class="table-responsive">
                                                    <table id="paginatedProductsTable" class="table table-hover dataTable table-custom" style="width: 100%">
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
                'categoryId' : '${category.categoryId}'
            });
            $( document ).ready(function() {
                $.initDataTable({
                    selector: '#paginatedSubCategoriesTable',
                    name: 'subCategories',
                    type: 'TYPE_2',
                    url: $.getURL('/pim/categories/{categoryId}/subCategories'),
                    columns: [
                        { data: 'subCategoryName', name : 'subCategoryName' , title : 'Category Name'},
                        { data: 'subCategoryId', name : 'subCategoryId', title : 'Category ID' },
                        { data: 'active', name : 'active' , title : 'Status', orderable: false},
                        { data: 'actions', name : 'actions' , title : 'Actions', orderable: false}
                    ]
                });
//        $('#paginatedTable').dataTable().fnSetFilteringEnterPress();
            });
        </script>
        <script src="/assets/js/pages/ui/category/category.js"></script>
        <script>
            $.initPage({
                'categoryId' : '${category.categoryId}'
            });
           $( document ).ready(function() {
                $.initDataTable({
                    selector: '#paginatedProductsTable',
                    name: 'products',
                    type: 'TYPE_2',
                    url: $.getURL('/pim/categories/{categoryId}/products'),
                    columns: [
                        { data: 'productName', name : 'productName' , title : 'Product Name'},
                        { data: 'productId', name : 'productId', title : 'Product ID' },
                        { data: 'active', name : 'active' , title : 'Status', orderable: false},
                        { data: 'actions', name : 'actions' , title : 'Actions', orderable: false}
                    ]
                });
//        $('#paginatedTable').dataTable().fnSetFilteringEnterPress();
            });
        </script>


