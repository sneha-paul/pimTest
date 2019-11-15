<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<div class="row clearfix">
    <div class="col-lg-12 col-md-12">
        <div class="card">
            <div class="header">
                <h2>${website.websiteName} <small><code class="highlighter-rouge">${website.websiteId}</code></small><small class="pull-right m-t--15"><code style="color:#808080">_id: ${website.id}</code></small></h2>
            </div>
            <div class="body">
                <ul class="nav nav-tabs-new2">
                    <li class="nav-item"><a class="nav-link active show" data-toggle="tab" href="#HISTORY_DETAILS">Details</a></li>
                </ul>
                <div class="tab-content">
                    <div class="tab-pane show active" id="HISTORY_DETAILS">
                        <div class="row clearfix m-t-20">
                            <div class="col-lg-12 col-md-12">
                                <div class="card">
                                    <div class="body">
                                        <form method="post">
                                            <div class="row">
                                                <div class="col-md-6 col-sm-12">
                                                    <div class="form-group">
                                                        <label for="websiteName">Website Name</label><code class="highlighter-rouge m-l-10">*</code>
                                                        <input type="text" id="websiteName" name="websiteName" value="${website.websiteName}" class="form-control" readonly/>
                                                    </div>
                                                    <div class="form-group js-external-id">
                                                        <label for="websiteId">Website ID</label><code class="highlighter-rouge m-l-10">*</code>
                                                        <input type="text" id="websiteId" name="websiteId" class="form-control" value="${website.websiteId}" readonly/>
                                                    </div>
                                                    <div class="form-group">
                                                        <label for="websiteUrl">Website URL</label><code class="highlighter-rouge m-l-10">*</code>
                                                        <input type="url" id="websiteUrl" name="url" class="form-control" value="${website.url}" readonly/>
                                                    </div>
                                                    <div class="form-group">
                                                        <label>Status</label>
                                                        <br/>
                                                        <label for="active" class="fancy-checkbox">
                                                            <input type="checkbox" id="active" name="active" value="Y" <c:if test="${website.active eq 'Y'}">checked="checked"</c:if> disabled>
                                                            <span>Active</span>
                                                        </label>
                                                    </div>
                                                </div>
                                            </div>
                                            <br>
                                            <%--<a href="${breadcrumbs.backURL}"><button type="button" class="btn btn-danger">Cancel</button></a>--%>
                                            <a href="${breadcrumbs.backURL}/${website.websiteId}#websiteHistory"><button type="button" class="btn btn-danger">Cancel</button></a>
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
