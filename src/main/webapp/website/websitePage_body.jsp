<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<div class="row clearfix">
    <div class="col-lg-12 col-md-12">
        <div class="card">
            <div class="header">
                <h2>${websitePage.pageName} <small><code class="highlighter-rouge">${websitePage.pageId}</code></small><small class="pull-right m-t--15"><code style="color:#808080">_website id: ${website.id}</code></small></h2>
            </div>
            <div class="body">
                <ul class="nav nav-tabs-new2">
                    <li class="nav-item"><a class="nav-link active show" data-toggle="tab" href="#DETAILS">Details</a></li>
                    <li class="nav-item"><a class="nav-link js-pageAttributes-tab" data-toggle="tab" href="#pageAttributes">Attributes</a></li>
                </ul>
                <div class="tab-content">
                    <div class="tab-pane show active" id="DETAILS">
                        <div class="row clearfix m-t-20">
                            <div class="col-lg-12 col-md-12">
                                <div class="card">
                                    <div class="body">
                                        <form method="post" action="/pim/websites/pages/${websitePage.pageId}" data-method="PUT" data-success-message='["Successfully updated the page", "Page Updated"]' data-error-message='["Correct the validation error and try again", "Invalid Data"]'>
                                            <div class="row">
                                                <div class="col-sm-12">
                                                    <div class="form-group">
                                                        <label for="pageName">Page Name</label><code class="highlighter-rouge m-l-10">*</code>
                                                        <input type="text" id="pageName" name="pageName" value="${websitePage.pageName}" class="form-control" />
                                                    </div>
                                                    <div class="form-group js-external-id">
                                                        <label for="pageId">Page ID</label><code class="highlighter-rouge m-l-10">*</code>
                                                        <input type="text" id="pageId" name="pageId" class="form-control" value="${websitePage.pageId}" />
                                                    </div>
                                                    <div class="form-group">
                                                        <label for="pageUrl">Page URL</label><code class="highlighter-rouge m-l-10">*</code>
                                                        <input type="url" id="pageUrl" name="pageUrl" class="form-control" value="${websitePage.pageUrl}" />
                                                    </div>
                                                    <div class="form-group">
                                                        <label for="friendlyUrl">Friendly URL</label><code class="highlighter-rouge m-l-10">*</code>
                                                        <input type="url" id="friendlyUrl" name="friendlyUrl" class="form-control" value="${websitePage.friendlyUrl}" />
                                                    </div>
                                                    <div class="form-group">
                                                        <label for="redirectURL">Redirect URL</label><code class="highlighter-rouge m-l-10">*</code>
                                                        <input type="url" id="redirectURL" name="redirectURL" class="form-control" value="${websitePage.redirectURL}" />
                                                    </div>
                                                    <div class="form-group">
                                                        <label for="websiteId">Website</label><code class="highlighter-rouge m-l-10">*</code>
                                                        <input type="url" id="websiteId" name="websiteId" class="form-control" value="${website.websiteId}" readonly/>
                                                    </div>
                                                    <div class="form-group">
                                                        <label>Status</label>
                                                        <br/>
                                                        <label for="active" class="fancy-checkbox">
                                                            <input type="checkbox" id="active" name="active" value="Y" <c:if test="${websitePage.active eq 'Y'}">checked="checked"</c:if>>
                                                            <span>Active</span>
                                                        </label>
                                                    </div>
                                                </div>
                                            </div>
                                            <br>
                                            <input type="hidden" name="group" value="DETAILS"/>
                                            <button type="submit" class="btn btn-primary" onclick="$.submitAction(event, this)">Save</button>
                                            <a href="${breadcrumbs.backURL}"><button type="button" class="btn btn-danger">Cancel</button></a>
                                        </form>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="tab-pane" id="pageAttributes">
                        <div class="row clearfix">
                            <div class="col-lg-12 col-md-12">
                                <div class="card">
                                    <div class="body">
                                        <div class="row p-b-25">
                                            <div class="col-lg-12 col-md-12">
                                                <div class="pull-right">
                                                    <button type="button" class="btn btn-success js-add-pageAttributes"><i class="fa fa-plus"></i> <span class="p-l-5">Add Attribute</span></button>
                                                </div>
                                            </div>
                                        </div>
                                        <div class="table-responsive">
                                            <table id="paginatedPageAttributesTable" class="table table-hover dataTable table-custom" style="width: 100%">
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
        'websiteId' : '${website.websiteId}',
        'pageId' : '${websitePage.pageId}'
    });
</script>
<script src="/assets/js/pages/ui/website/websitePage.js"></script>