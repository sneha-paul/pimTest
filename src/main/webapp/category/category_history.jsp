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
                </ul>
                <div class="tab-content">
                    <div class="tab-pane show active" id="details">
                        <div class="row clearfix m-t-20">
                            <div class="col-lg-12 col-md-12">
                                <div class="card">
                                    <div class="body">
                                        <form method="post" action="/pim/categories/${category.categoryId}" data-method="PUT" data-success-message='["Successfully updated the category", "Category Updated"]' data-error-message='["Correct the validation error and try again", "Invalid Data"]'>
                                            <div class="row">
                                                <div class="col-md-6 col-sm-12">
                                                    <div class="form-group">
                                                        <label for="categoryName">Category Name</label><code class="highlighter-rouge m-l-10">*</code>
                                                        <input type="text" id="categoryName" name="categoryName" value="${category.categoryName}" class="form-control" readonly/>
                                                    </div>

                                                    <div class="form-group js-external-id">
                                                        <label for="categoryId">Category ID</label><code class="highlighter-rouge m-l-10">*</code>
                                                        <input type="text" id="categoryId" name="categoryId" class="form-control" value="${category.categoryId}" readonly/>
                                                    </div>

                                                    <div class="form-group">
                                                        <label for="description">Category Description</label>
                                                        <textarea class="form-control" id="description" name="description" rows="5" cols="30" required="" readonly>${category.description}</textarea>
                                                    </div>

                                                    <div class="form-group">
                                                        <label for="longDescription">Category Long Description</label>
                                                        <textarea class="form-control" id="longDescription" name="longDescription" rows="5" cols="30" required="" readonly>${category.longDescription}</textarea>
                                                    </div>

                                                    <div class="form-group">
                                                        <label>Status</label>
                                                        <br/>
                                                        <label for="active" class="fancy-checkbox">
                                                            <input type="checkbox" id="active" name="active" value="Y" <c:if test="${category.active eq 'Y'}">checked="checked"</c:if> disabled>
                                                            <span>Active</span>
                                                        </label>
                                                        <label for="discontinued" class="fancy-checkbox">
                                                            <input type="checkbox" id="discontinued" name="discontinued" value="Y" <c:if test="${category.discontinued eq 'Y'}">checked="checked"</c:if> disabled>
                                                            <span>Discontinued</span>
                                                        </label>
                                                    </div>
                                                    <div class="js-dateRange" readonly>
                                                        <div class="form-group">
                                                            <label>Active From </label>
                                                            <input type="text" class="form-control dateUI js-start" name="activeFromDate" value="${category.activeFromDate}" disabled="true">
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
                                            <a href="${breadcrumbs.backURL}/${category.categoryId}#categoryHistory"><button type="button" class="btn btn-danger">Cancel</button></a>
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



