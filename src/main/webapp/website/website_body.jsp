<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<div class="row clearfix">
    <div class="col-lg-12 col-md-12">
        <div class="card">
            <div class="header">
                <h2>${website.websiteName} <small><code class="highlighter-rouge">${website.websiteId}</code></small><small class="pull-right m-t--15"><code style="color:#808080">_id: ${website.id}</code></small></h2>
            </div>
            <div class="body">
                <ul class="nav nav-tabs-new2">
                    <li class="nav-item"><a class="nav-link active show" data-toggle="tab" href="#DETAILS">Details</a></li>
                    <li class="nav-item"><a class="nav-link js-catalogs-tab" data-toggle="tab" href="#catalogs">Catalogs</a></li>
                    <li class="nav-item"><a class="nav-link js-websitePages-tab" data-toggle="tab" href="#websitePages">Pages</a></li>
                    <li class="nav-item"><a class="nav-link js-websiteConfigParam-tab" data-toggle="tab" href="#websiteConfigParam">Configs</a></li>
                    <li class="nav-item"><a class="nav-link js-redirects-tab" data-toggle="tab" href="#redirects">Redirects</a></li>
                    <%--<li class="nav-item"><a class="nav-link js-websiteHistory-tab" data-toggle="tab" href="#websiteHistory">History</a></li>--%>
                </ul>
                <div class="tab-content">
                    <div class="tab-pane show active" id="DETAILS">
                        <div class="row clearfix m-t-20">
                            <div class="col-lg-12 col-md-12">
                                <div class="card">
                                    <div class="body">
                                        <form method="post" action="/pim/websites/${website.websiteId}" data-method="PUT" data-success-message='["Successfully updated the website", "Website Updated"]' data-error-message='["Correct the validation error and try again", "Invalid Data"]'>
                                            <div class="row">
                                                <div class="col-md-6 col-sm-12">
                                                    <div class="form-group">
                                                        <label for="websiteName">Website Name</label><code class="highlighter-rouge m-l-10">*</code>
                                                        <input type="text" id="websiteName" name="websiteName" value="${website.websiteName}" class="form-control" />
                                                    </div>
                                                    <div class="form-group js-external-id">
                                                        <label for="websiteId">Website ID</label><code class="highlighter-rouge m-l-10">*</code>
                                                        <input type="text" id="websiteId" name="websiteId" class="form-control" value="${website.websiteId}" />
                                                    </div>
                                                    <div class="form-group">
                                                        <label for="websiteUrl">Website URL</label><code class="highlighter-rouge m-l-10">*</code>
                                                        <input type="url" id="websiteUrl" name="url" class="form-control" value="${website.url}" />
                                                    </div>
                                                    <div class="form-group">
                                                        <label>Status</label>
                                                        <br/>
                                                        <label for="active" class="fancy-checkbox">
                                                            <input type="checkbox" id="active" name="active" value="Y" <c:if test="${website.active eq 'Y'}">checked="checked"</c:if>>
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
                    <div class="tab-pane" id="catalogs">
                        <div class="row clearfix">
                            <div class="col-lg-12 col-md-12">
                                <div class="card">
                                    <div class="body">
                                        <div class="row p-b-25">
                                            <div class="col-lg-12 col-md-12">
                                                <div class="pull-right">
                                                    <button type="button" class="btn btn-success js-add-catalog"><i class="fa fa-plus"></i> <span class="p-l-5">Add Catalog</span></button>
                                                    <button id="js-sync-websiteCatalogs" type="button" class="btn btn-primary"><i class="fa fa-plus"></i> <span class="p-l-5">Sync Catalog</span></button>
                                                </div>
                                            </div>
                                        </div>
                                        <div class="table-responsive">
                                            <table id="paginatedCatalogsTable" class="table table-hover dataTable table-custom" style="width: 100%">
                                                <thead class="thead-dark">

                                                </thead>

                                            </table>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="tab-pane" id="websiteHistory">
                        <div class="row clearfix">
                            <div class="col-lg-12 col-md-12">
                                <div class="card">
                                    <div class="body">
                                        <div class="table-responsive">
                                            <table id="paginatedWebsiteHistoryTable" class="table table-hover dataTable table-custom" style="width: 100%">
                                                <thead class="thead-dark">

                                                </thead>

                                            </table>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="tab-pane" id="websitePages">
                        <div class="row clearfix">
                            <div class="col-lg-12 col-md-12">
                                <div class="card">
                                    <div class="body">
                                        <div class="row p-b-25">
                                            <div class="col-lg-12 col-md-12">
                                                <div class="pull-right">
                                                    <button type="button" class="btn btn-success js-add-pages"><i class="fa fa-plus"></i> <span class="p-l-5">Add Pages</span></button>
                                                </div>
                                            </div>
                                        </div>
                                        <div class="table-responsive">
                                            <table id="paginatedWebsitePageTable" class="table table-hover dataTable table-custom" style="width: 100%">
                                                <thead class="thead-dark">

                                                </thead>

                                            </table>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="tab-pane" id="websiteConfigParam">
                        <div class="row clearfix">
                            <div class="col-lg-12 col-md-12">
                                <div class="card">
                                    <div class="body">
                                        <div class="row p-b-25">
                                            <div class="col-lg-12 col-md-12">
                                                <div class="pull-right">
                                                    <button type="button" class="btn btn-success js-add-websiteParam"><i class="fa fa-plus"></i> <span class="p-l-5">Add Parameters</span></button>
                                                </div>
                                            </div>
                                        </div>
                                        <div class="table-responsive">
                                            <table id="paginatedWebsiteConfigParamTable" class="table table-hover dataTable table-custom" style="width: 100%">
                                                <thead class="thead-dark">

                                                </thead>

                                            </table>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>


                    <div class="tab-pane" id="redirects">
                        <div class="row clearfix">
                            <div class="col-lg-12 col-md-12">
                                <div class="card">
                                    <div class="body">
                                        <div class="row p-b-25">
                                            <div class="col-lg-12 col-md-12">
                                                <div class="pull-right">
                                                    <button type="button" class="btn btn-success js-add-redirects"><i class="fa fa-plus"></i> <span class="p-l-5">Add Url</span></button>
                                                </div>
                                            </div>
                                        </div>
                                        <div class="table-responsive">
                                            <table id="paginatedRedirectsTable" class="table table-hover dataTable table-custom" style="width: 100%">
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
        'websiteUrl' : '${website.url}'
    });
</script>
<script src="/assets/js/pages/ui/website/website.js"></script>