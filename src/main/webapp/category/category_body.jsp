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
                            <li class="nav-item"><a class="nav-link active show" data-toggle="tab" href="#Home-new2">Attributes</a></li>
                        </ul>
                        <div class="tab-content">
                            <div class="tab-pane show active" id="Home-new2">
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
