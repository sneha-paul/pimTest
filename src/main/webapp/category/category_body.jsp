<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<c:choose>
    <c:when test="${mode eq 'DETAILS'}">
        <div class="row clearfix">
            <div class="col-lg-12 col-md-12">
                <div class="card">
                    <div class="header">
                        <h2>${category.categoryName} <small><code class="highlighter-rouge">${category.categoryId}</code></small></h2>
                    </div>
                    <div class="body">
                        <ul class="nav nav-tabs-new2">
                            <li class="nav-item"><a class="nav-link active show" data-toggle="tab" href="#Attributes">Attributes</a></li>
                            <li class="nav-item"><a class="nav-link" data-toggle="tab" href="#SubCategories">SubCategories</a></li>
                        </ul>
                        <div class="tab-content">
                            <div class="tab-pane show active" id="Attributes">
                                <div class="row clearfix m-t-20">
                                    <div class="col-md-6">
                                        <div class="card">
                                            <div class="body">
                                                <form id="update-form" method="post" action="/pim/categories/${category.categoryId}" novalidate>
                                                    <div class="row">
                                                        <div class="col-md-6 col-sm-12">
                                                            <div class="form-group">
                                                                <label>Category Name</label><code class="highlighter-rouge m-l-10">*</code>
                                                                <input type="text" name="categoryName" value="${category.categoryName}" class="form-control" required>
                                                            </div>

                                                            <div class="form-group">
                                                                <label>Category ID</label><code class="highlighter-rouge m-l-10">*</code>
                                                                <input type="text" name="categoryId" class="form-control" value="${category.categoryId}" required>
                                                            </div>

                                                            <div class="form-group">
                                                                <label>Category Description</label>
                                                                <textarea class="form-control" name="description" rows="5" cols="30" required="">${category.description}</textarea>
                                                            </div>

                                                            <div class="form-group">
                                                                <label>Status</label>
                                                                <br/>
                                                                <label class="fancy-checkbox">
                                                                    <input type="checkbox" name="active" value="Y" <c:if test="${category.active eq 'Y'}">checked="checked"</c:if>>
                                                                    <span>Active</span>
                                                                </label>
                                                            </div>
                                                        </div>
                                                    </div>
                                                    <br>
                                                    <button type="submit" class="btn btn-primary">Save</button>
                                                    <a href="/pim/categories"><button type="button" class="btn btn-danger">Cancel</button></a>
                                                </form>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                            <div class="tab-pane" id="SubCategories">
                                <div class="row clearfix">
                                    <div class="col-lg-12 col-md-12">
                                        <div class="card">
                                            <div class="body">
                                                <div class="row p-b-25">
                                                    <div class="col-lg-12 col-md-12"><div class="pull-right"><button type="button" class="btn btn-success js-add-subCategory"><i class="fa fa-plus"></i> <span class="p-l-5">Add SubCategories</span></button></div></div>
                                                </div>
                                                <table id="only-bodytable" class="table table-hover dataTable table-custom">
                                                    <thead class="thead-dark">
                                                    <tr>
                                                        <th>Category Name</th>
                                                        <th>Category ID</th>
                                                        <th>Actions</th>
                                                    </tr>
                                                    </thead>
                                                    <c:forEach items="${categories.subCategories.content}" var="category">
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
        <script>
            $.initPage({
                'categoryId' : '${category.categoryId}'
            });
        </script>
        <script src="/assets/js/pages/ui/category/category.js"></script>
       <%--<div class="modal fade" id="largeModal" tabindex="-1" role="dialog">
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
                                        &lt;%&ndash;<tr>
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
                                        </tr>&ndash;%&gt;
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
        </div>--%>
    </c:when>
    <c:otherwise>
        <div class="row clearfix">
            <div class="col-lg-12 col-md-12">
                <div class="card">
                    <div class="header">
                        <h1>Create Category</h1>
                    </div>
                    <div class="body">
                        <form id="create-form" method="post" action="/pim/categories" novalidate>
                            <div class="row">
                                <div class="col-md-6 col-sm-12">
                                    <div class="form-group">
                                        <label>Category Name</label>
                                        <input type="text" name="categoryName" class="form-control" required>
                                    </div>

                                    <div class="form-group">
                                        <label>Category ID</label>
                                        <input type="text" name="categoryId" class="form-control" required>
                                    </div>

                                    <div class="form-group">
                                        <label>Category Description</label>
                                        <textarea class="form-control" name="description" value="${category.description}" rows="5" cols="30" required=""></textarea>
                                    </div>
                                </div>
                            </div>
                            <br>
                            <button type="submit" class="btn btn-primary">Create</button>
                            <a href="/pim/categories/"><button type="button" class="btn btn-danger">Cancel</button></a>
                        </form>
                    </div>
                </div>
            </div>
        </div>
    </c:otherwise>
</c:choose>
