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
                    <li class="nav-item"><a class="nav-link" data-toggle="tab" href="#Catalogs">Catalogs</a></li>
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
                                                    <div class="form-group">
                                                        <label for="websiteUrl">Website URL</label><code class="highlighter-rouge m-l-10">*</code>
                                                        <input type="url" id="websiteUrl" name="url" class="form-control" value="${website.url}" />
                                                    </div>
                                                    <div class="form-group">
                                                        <label for="websiteId">Website ID</label><code class="highlighter-rouge m-l-10">*</code>
                                                        <input type="text" id="websiteId" name="websiteId" class="form-control" value="${website.websiteId}" />
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
                                            <a href="/pim/websites"><button type="button" class="btn btn-danger">Cancel</button></a>
                                        </form>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="tab-pane" id="Catalogs">
                        <div class="row clearfix">
                            <div class="col-lg-12 col-md-12">
                                <div class="card">
                                    <div class="body">
                                        <div class="row p-b-25">
                                            <div class="col-lg-12 col-md-12">
                                                <div class="pull-right">
                                                    <button type="button" class="btn btn-success js-add-catalog"><i class="fa fa-plus"></i> <span class="p-l-5">Add Catalog</span></button>
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
                </div>
            </div>
        </div>
    </div>
</div>
<script>
    $.initPage({
        'websiteId' : '${website.websiteId}'
    });
    $( document ).ready(function() {
        $.initDataTable({
            selector: '#paginatedCatalogsTable',
            name: 'catalogs',
            type: 'TYPE_2',
            url: $.getURL('/pim/websites/{websiteId}/catalogs'),
            columns: [
                { data: 'catalogName', name : 'catalogName' , title : 'Catalog Name'},
                { data: 'catalogId', name : 'catalogId', title : 'Catalog ID' },
                { data: 'active', name : 'active' , title : 'Status', orderable: false},
                { data: 'actions', name : 'actions' , title : 'Actions', orderable: false}
            ]
        });
//        $('#paginatedTable').dataTable().fnSetFilteringEnterPress();
    });
</script>
<script src="/assets/js/pages/ui/website/website.js"></script>