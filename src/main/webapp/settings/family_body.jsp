<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<div class="row clearfix">
    <div class="col-lg-12 col-md-12">
        <div class="card">
            <div class="header">
                <h2>${family.familyName}
                    <small><code class="highlighter-rouge">${family.familyId}</code></small>
                    <small class="pull-right m-t--15"><code style="color:#808080">_id: ${family.id}</code>
                    </small>
                </h2>
            </div>
            <div class="body">
                <ul class="nav nav-tabs-new2">
                    <li class="nav-item"><a class="nav-link active show" data-toggle="tab" href="#DETAILS">Details</a></li>
                    <li class="nav-item"><a class="nav-link" data-toggle="tab" href="#familyAttributes">Attributes</a></li>
                    <li class="nav-item"><a class="nav-link" data-toggle="tab" href="#variantGroups">Variant Groups</a></li>
                    <li class="nav-item"><a class="nav-link" data-toggle="tab" href="#familyAttributesScope">Scope</a></li>
                    <%--<li class="nav-item"><a class="nav-link" data-toggle="tab" href="#channelVariantGroups">Channel</a></li>--%>
                </ul>
                <div class="tab-content">
                    <div class="tab-pane show active" id="DETAILS">
                        <div class="row clearfix m-t-20">
                            <div class="col-lg-12 col-md-12">
                                <div class="card">
                                    <div class="body">
                                        <form method="post"
                                              action="/pim/families/${family.familyId}"
                                              data-method="PUT"
                                              data-success-message='["Successfully updated the family", "Family Updated"]'
                                              data-error-message='["Correct the validation error and try again", "Invalid Data"]'>
                                            <div class="row">
                                                <div class="col-md-6 col-sm-12">
                                                    <div class="form-group">
                                                        <label for="familyName">Family Name</label><code
                                                            class="highlighter-rouge m-l-10">*</code>
                                                        <input type="text" id="familyName" name="familyName"
                                                               value="${family.familyName}"
                                                               class="form-control"/>
                                                    </div>
                                                    <div class="form-group">
                                                        <label for="familyId">Family ID</label><code
                                                            class="highlighter-rouge m-l-10">*</code>
                                                        <input type="text" id="familyId" name="familyId"
                                                               class="form-control"
                                                               value="${family.familyId}"/>
                                                    </div>

                                                    <div class="form-group">
                                                        <label>Status</label>
                                                        <br/>
                                                        <label for="active" class="fancy-checkbox">
                                                            <input type="checkbox" id="active" name="active" value="Y"
                                                                   <c:if test="${family.active eq 'Y'}">checked="checked"</c:if>>
                                                            <span>Active</span>
                                                        </label>
                                                    </div>
                                                </div>
                                            </div>
                                            <br>
                                            <input type="hidden" name="group" value="DETAILS"/>
                                            <button type="submit" class="btn btn-primary"
                                                    onclick="$.submitAction(event, this)">Save
                                            </button>
                                            <a href="/pim/families">
                                                <button type="button" class="btn btn-danger">Cancel</button>
                                            </a>
                                        </form>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="tab-pane" id="familyAttributes">
                        <div class="row clearfix">
                            <div class="col-lg-12 col-md-12">
                                <div class="card">
                                    <div class="body">
                                        <div class="row p-b-25">
                                            <div class="col-lg-12 col-md-12">
                                                <div class="pull-right">
                                                    <button type="button"
                                                            class="btn btn-success js-add-familyAttribute"><i
                                                            class="fa fa-plus"></i> <span class="p-l-5">Add Attribute</span>
                                                    </button>
                                                </div>
                                            </div>
                                        </div>
                                        <div class="table-responsive">
                                            <table id="paginatedFamilyAttributesTable"
                                                   class="table table-hover dataTable table-custom" style="width: 100%">
                                                <thead class="thead-dark">

                                                </thead>

                                            </table>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="tab-pane" id="variantGroups">
                        <div class="row clearfix">
                            <div class="col-lg-12 col-md-12">
                                <div class="card">
                                    <div class="body">
                                        <div class="row p-b-25">
                                            <div class="col-lg-12 col-md-12">
                                                <div class="pull-right">
                                                    <button type="button"
                                                            class="btn btn-success js-add-variantGroup"><i
                                                            class="fa fa-plus"></i> <span class="p-l-5">Add Variant Group</span>
                                                    </button>
                                                </div>
                                            </div>
                                        </div>
                                        <div class="table-responsive">
                                            <table id="paginatedVariantGroupsTable"
                                                   class="table table-hover dataTable table-custom" style="width: 100%">
                                                <thead class="thead-dark">

                                                </thead>

                                            </table>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="tab-pane" id="familyAttributesScope">
                        <div class="row clearfix">
                            <div class="col-lg-12 col-md-12">
                                <div class="card">
                                    <div class="body">
                                        <div class="table-responsive">
                                            <table id="paginatedFamilyAttributesScopeTable"
                                                   class="table table-hover dataTable table-custom" style="width: 100%">
                                                <thead class="thead-dark">

                                                </thead>

                                            </table>
                                        </div>
                                    </div>
                                </div>
                                <br/>
                                <div class="card">
                                    <fieldset><legend>Channel Variant Groups</legend></fieldset>
                                    <div class="body">
                                        <div class="table-responsive">
                                            <table id="paginatedChannelVariantGroupsTable"
                                                   class="table table-hover dataTable table-custom" style="width: 100%">
                                                <thead class="thead-dark">

                                                </thead>

                                            </table>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                    <%--<div class="tab-pane" id="channelVariantGroups">
                        <div class="row clearfix">
                            <div class="col-lg-12 col-md-12">
                                <div class="card">
                                    <div class="body">
                                        <div class="table-responsive">
                                            <table id="paginatedChannelVariantGroupsTable"
                                                   class="table table-hover dataTable table-custom" style="width: 100%">
                                                <thead class="thead-dark">

                                                </thead>

                                            </table>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>--%>
                </div>
            </div>
        </div>
    </div>
</div>
<script>
    var channels = JSON.parse('${channels}'); //TODO - get this from controller

    $.initPage({
        'channels': channels,
        'familyId': '${family.familyId}'
    });
    $(document).ready(function () {
        $.initDataTable({
            selector: '#paginatedFamilyAttributesTable',
            name: 'familyAttributes',
            type: 'TYPE_2',
            buttonGroup: 'GROUP_4A',
            url: $.getURL('/pim/families/{familyId}/attributes'),
            columns: [
                {data: 'name', name: 'name', title: 'Attribute Name', render: function ( data, type, row, meta ) {return '<h6>' + data + '</h6><small style="color:#808080">' + row.id + '</code><small>'}},
                {data: 'uiType', name: 'uiType', title: 'UI Type'},
                {data: 'group', name: 'group', title: 'Attribute Group'},
                {data: 'scopable', name: 'scopable', title: 'Scopable'},
                {data: 'actions', name: 'actions', title: 'Actions', orderable: false}
            ]
        });

        var columns = [];
        columns[0] = {data: 'name', name: 'name', title: 'Attribute Name'};
        columns[1] = {data: 'scopable', name: 'scopable', title: 'Scopable'};
        var idx = 1;
        for(var channelId in channels) {
            if(channels.hasOwnProperty(channelId)) {
                columns[++idx] = {
                    data: 'channel_' + channelId,
                    name: 'channel_' + channelId,
                    title: channels[channelId],
                    orderable: false
                };
            }
        }

        $.initDataTable({
            selector: '#paginatedFamilyAttributesScopeTable',
            name: 'familyAttributesScope',
            type: 'TYPE_2',
            buttonGroup: 'GROUP_4B',
            url: $.getURL('/pim/families/{familyId}/attributes'),
            columns: columns
        });

        columns = [];
        columns[0] = {data: 'name', name: 'name', title: 'Attribute Name'};
        var idx = 0;
        for(var channelId in channels) {
            if(channels.hasOwnProperty(channelId)) {
                columns[++idx] = {
                    data: 'channel_' + channelId,
                    name: 'channel_' + channelId,
                    title: channels[channelId],
                    orderable: false
                };
            }
        }

        $.initDataTable({
            selector: '#paginatedChannelVariantGroupsTable',
            name: 'channelVariantGroups',
            type: 'TYPE_2',
            buttonGroup: 'GROUP_4C',
            url: $.getURL('/pim/families/{familyId}/variantGroups'),
            paging: false,
            columns: columns
        });

        $.initDataTable({
            selector: '#paginatedVariantGroupsTable',
            name: 'variantGroups',
            type: 'TYPE_1A',
            url: $.getURL('/pim/families/{familyId}/variantGroups/'),
            columns: [
                {data: 'name', name: 'name', title: 'Name'},
                {data: 'externalId', name: 'externalId', title: 'Group ID'},
                {data: 'variantAxis', name: 'variantAxis', title: 'Variant Axis'},
                { data: 'active', name : 'active' , title : 'Status', orderable: false},
                { data: 'actions', name : 'actions' , title : 'Actions', orderable: false}
            ]
        });

//        $('#paginatedTable').dataTable().fnSetFilteringEnterPress();
    });
</script>
<script src="/assets/js/pages/ui/settings/family.js"></script>

