<%--@elvariable id="family" type="com.bigname.pim.api.domain.Family"--%>
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
                    <li class="nav-item"><a class="nav-link js-familyAttributes-tab" data-toggle="tab" href="#attributes">Attributes</a></li>
                    <li class="nav-item"><a class="nav-link js-variantGroups-tab" data-toggle="tab" href="#variantGroups">Variant Groups</a></li>
                    <li class="nav-item"><a class="nav-link js-familyAttributesScope-tab" data-toggle="tab" href="#familyAttributesScope">Scope</a></li>
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
                                                    <div class="form-group js-external-id">
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
                    <div class="tab-pane" id="attributes">
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
                                        <div class="table-responsive scrollable-dt">
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
                                        <div class="table-responsive scrollable-dt">
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
                                        <div class="table-responsive scrollable-dt">
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
                                        <div class="table-responsive scrollable-dt">
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
        $('.js-familyAttributes-tab').on('shown.bs.tab.familyAttributes', function (e) {
            $.initGrid({
                selector: '#paginatedFamilyAttributesTable',
                names: ['familyAttributes', 'familyAttribute'],
                dataUrl: $.getURL('/pim/families/{familyId}/attributes/data'),
                columns: [
                    {data: 'name', name: 'name', title: 'Attribute Name', render: function ( data, type, row, meta ) {return '<h6>' + data + '</h6><small style="color:#808080">' + row.externalId + '</code><small>'}},
                    {data: 'uiType', name: 'uiType', title: 'UI Type'},
                    {data: 'group', name: 'group', title: 'Attribute Group'},
                    {data: 'actions', name: 'actions', title: 'Actions', orderable: false}
                ],
                buttons: [$.detailsButton({pageUrl: $.getURL('/pim/families/{familyId}/attributes/')}), $.attributeOptionsTabButton({actionUrl: '/pim/families/{familyId}/attributes/{attributeId}#attributeOptions'})]
            });
            $(this).removeClass('js-familyAttributes-tab').off('shown.bs.tab.familyAttributes');
        });

        var columns = [];
        columns[0] = {data: 'name', name: 'name', title: 'Attribute Name'};
        columns[1] = {
            data: 'scopable',
            name: 'scopable',
            title: 'Scopable',
            selector: 'js-scopable',
            click: function(row) {
                $.scopableClickEvent(row, function(data) {$.refreshDataTable('familyAttributesScopes');});
            },
            render: function (data, type, row, meta) {
                return $.renderScopable(row);
            }
        };
        let idx = 1;
        for(let channelId in channels) {
            if(channels.hasOwnProperty(channelId)) {
                columns[++idx] = {
                    data: 'channel_' + channelId,
                    name: 'channel_' + channelId,
                    title: channels[channelId],
                    orderable: false,
                    render: function (data, type, row, meta) {
                        return $.renderScopeSelector(row, channelId);
                    }
                };
            }
        }

        $('.js-familyAttributesScope-tab').on('shown.bs.tab.familyAttributesScope', function (e) {
            $.initGrid({
             selector: '#paginatedFamilyAttributesScopeTable',
             names: ['familyAttributesScopes', 'familyAttributesScope'],
             dataUrl: $.getURL('/pim/families/{familyId}/attributes/data'),
             actionUrl: $.getURL('/pim/families/{familyId}/attributes'),
             columns: columns
             });

            columns = [];
            columns[0] = {data: 'name', name: 'name', title: 'Attribute Name'};
            idx = 0;
            for(let channelId in channels) {
                if(channels.hasOwnProperty(channelId)) {
                    columns[++idx] = {
                        data: 'channel_' + channelId,
                        name: 'channel_' + channelId,
                        title: channels[channelId],
                        orderable: false,
                        render: function (data, type, row, meta) {
                            return $.renderChannelSelector(row, channelId);
                        }
                    };
                }
            }
            $.initGrid({
                selector: '#paginatedChannelVariantGroupsTable',
                names: ['channelVariantGroups', "channelVariantGroup"],
                dataUrl: $.getURL('/pim/families/{familyId}/variantGroups'),
                searching: false,
                columns: columns
            });

            $(this).removeClass('js-familyAttributesScope-tab').off('shown.bs.tab.familyAttributesScope');
        });



        var urlParams = {familyId: '{familyId}', hash: 'variantGroups'};

        $('.js-variantGroups-tab').on('shown.bs.tab.variantGroups', function (e) {
            $.initAssociationsGrid({
                selector: '#paginatedVariantGroupsTable',
                names: ['variantGroups', 'variantGroup'],
                pageUrl: $.getURL('/pim/families/{familyId}/variantGroups/'),
                dataUrl: $.getURL('/pim/families/{familyId}/variantGroups/list'),
                toggleUrl: '/pim/families/{familyId}/variantGroups/{externalId}/active/{active}',
                urlParams: urlParams,
                columns: [
                    {data: 'name', name: 'name', title: 'Name'},
                    {data: 'externalId', name: 'externalId', title: 'Group ID'},
                    {data: 'variantAxis', name: 'variantAxis', title: 'Variant Axis'}
                ]
            });
            $(this).removeClass('js-variantGroups-tab').off('shown.bs.tab.variantGroups');
        });


    });
</script>
<script src="/assets/js/pages/ui/settings/family.js"></script>

