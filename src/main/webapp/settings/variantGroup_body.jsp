<%--@elvariable id="variantGroup" type="com.bigname.pim.api.domain.VariantGroup"--%>
<%--@elvariable id="variantGroupAttributes" type="java.util.Map<String, java.util.List<com.bigname.pim.api.domain.FamilyAttribute>"--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<div class="row clearfix">
    <div class="col-lg-12 col-md-12">
        <div class="card">
            <div class="header">
                <h2>${variantGroup.name}
                    <small><code class="highlighter-rouge">${variantGroup.id}</code></small>
                    <%--<small class="pull-right m-t--15"><code style="color:#808080">_id: ${variantGroup.id}</code>--%>
                    <%--</small>--%>
                </h2>
            </div>
            <div class="body">
                <ul class="nav nav-tabs-new2">
                    <li class="nav-item"><a class="nav-link active show" data-toggle="tab" href="#details">Details</a></li>
                    <li class="nav-item"><a class="nav-link" data-toggle="tab" href="#axisAttributes">Axis Attributes</a></li>
                    <li class="nav-item"><a class="nav-link" data-toggle="tab" href="#variantAttributes">Variant Attributes</a></li>
                </ul>
                <div class="tab-content">
                    <div class="tab-pane show active" id="details">
                        <div class="row clearfix m-t-20">
                            <div class="col-lg-12 col-md-12">
                                <div class="card">
                                    <div class="body">
                                        <form method="post"
                                              action="/pim/families/${variantGroup.familyId}/variantGroups/${variantGroup.id}"
                                              data-method="PUT"
                                              data-success-message='["Successfully updated the variant group", "Variant Group Updated"]'
                                              data-error-message='["Correct the validation error and try again", "Invalid Data"]'>
                                            <div class="row">
                                                <div class="col-md-6 col-sm-12">
                                                    <div class="form-group">
                                                        <label for="name">Group Name</label><code
                                                            class="highlighter-rouge m-l-10">*</code>
                                                        <input type="text" id="name" name="name"
                                                               value="${variantGroup.name}"
                                                               class="form-control"/>
                                                    </div>
                                                    <div class="form-group">
                                                        <label for="id">Group ID</label><code
                                                            class="highlighter-rouge m-l-10">*</code>
                                                        <input type="text" id="id" name="id"
                                                               class="form-control"
                                                               value="${variantGroup.id}"/>
                                                    </div>

                                                    <div class="form-group">
                                                        <label>Status</label>
                                                        <br/>
                                                        <label for="active" class="fancy-checkbox">
                                                            <input type="checkbox" id="active" name="active" value="Y"
                                                                   <c:if test="${variantGroup.active eq 'Y'}">checked="checked"</c:if>>
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
                                            <a href="/pim/families/${variantGroup.familyId}#variantGroups">
                                                <button type="button" class="btn btn-danger">Cancel</button>
                                            </a>
                                        </form>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="tab-pane" id="axisAttributes">
                        <div class="row clearfix">
                            <div class="col-lg-12 col-md-12">
                                <div class="card">
                                    <div class="body">
                                        <div class="row p-b-25">
                                            <div class="col-lg-12 col-md-12">
                                                <div class="pull-right">
                                                    <button type="button"
                                                            class="btn btn-success js-add-axisAttribute"><i
                                                            class="fa fa-plus"></i> <span class="p-l-5">Add Axis Attribute</span>
                                                    </button>
                                                </div>
                                            </div>
                                        </div>
                                        <div class="table-responsive">
                                            <table id="paginatedAxisAttributesTable"
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
                    <div class="tab-pane" id="variantAttributes">
                        <div class="taskboard">
                            <div class="row clearfix">
                                <div class="col-lg-4 col-md-12">
                                    <div class="card bg-dark planned_task">
                                        <div class="header">
                                            <h2>Product Level Attributes</h2>
                                            <%--<ul class="header-dropdown">
                                                <li><a href="javascript:void(0);" data-toggle="modal" data-target="#addcontact"><i class="icon-plus"></i></a></li>
                                            </ul>--%>
                                        </div>
                                        <div class="body">
                                            <div id="product" class="dd" data-plugin="nestable">
                                                <ol class="dd-list">
                                                    <c:forEach var="attribute" items="${variantGroupAttributes['PRODUCT_ATTRIBUTES']}">
                                                    <li class="dd-item" data-id="${attribute.id}">
                                                        <div class="dd-handle">
                                                            <h6>${attribute.name}</h6>
                                                        </div>
                                                    </li>
                                                    </c:forEach>
                                                </ol>
                                            </div>
                                        </div>
                                    </div>
                                </div>

                                <div class="col-lg-4 col-md-12">
                                    <div class="card bg-dark progress_task">
                                        <div class="header">
                                            <h2>Variant Attributes</h2>
                                            <%--<ul class="header-dropdown">
                                                <li><a href="javascript:void(0);" data-toggle="modal" data-target="#addcontact"><i class="icon-plus"></i></a></li>
                                            </ul>--%>
                                        </div>
                                        <div class="body">
                                            <div id="variant" class="dd" data-plugin="nestable">
                                                <ol class="dd-list">
                                                    <c:forEach var="attribute" items="${variantGroupAttributes['AXIS_ATTRIBUTES_L1']}">
                                                        <li class="dd-item dd-nodrag" data-id="${attribute.id}">
                                                            <div class="dd-handle">
                                                                <h6>${attribute.name}</h6><small class="pull-right m-t--25"><code class="highlighter-rouge">VARIANT AXIS</code></small>
                                                            </div>
                                                        </li>
                                                    </c:forEach>
                                                </ol>
                                            </div>
                                        </div>
                                    </div>
                                </div>

                                <%--<div class="col-lg-4 col-md-12">
                                    <div class="card bg-dark completed_task">
                                        <div class="header">
                                            <h2>Level 2 Variant Attributes</h2>
                                            <ul class="header-dropdown">
                                                <li><a href="javascript:void(0);" data-toggle="modal" data-target="#addcontact"><i class="icon-plus"></i></a></li>
                                            </ul>
                                        </div>
                                        <div class="body">
                                            <div class="dd" data-plugin="nestable">
                                                <ol class="dd-list">
                                                    <li class="dd-item" data-id="1">
                                                        <div class="dd-handle">
                                                            <h6>Job title</h6>
                                                        </div>
                                                    </li>
                                                    <li class="dd-item" data-id="2">
                                                        <div class="dd-handle">
                                                            <h6>Event Done</h6>
                                                        </div>
                                                    </li>
                                                </ol>
                                            </div>
                                        </div>
                                    </div>
                                </div>--%>
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
        'familyId': '${variantGroup.familyId}',
        'variantGroupId': '${variantGroup.id}'
    });
    $(document).ready(function () {
        $.initDataTable({
            selector: '#paginatedAxisAttributesTable',
            name: 'axisAttributes',
            type: 'TYPE_2',
            buttonGroup: 'GROUP_4A',
            url: $.getURL('/pim/families/{familyId}/variantGroups/{variantGroupId}/axisAttributes'),
            columns: [
                {data: 'name', name: 'name', title: 'Attribute Name'},
                {data: 'id', name: 'id', title: 'Attribute ID'},
                {data: 'actions', name: 'actions', title: 'Actions'}
            ]
        });



        $('#product,#variant').nestable({maxDepth: 1});
//        $('#variant').nestable({maxDepth: 1});

        $('#product').on('change', function () {
         var $this = $(this);
         console.log($(this).nestable('serialize'));
         var serializedData = window.JSON.stringify($($this).nestable('serialize'));

         console.log(serializedData);
         });

        $('#variant').on('change', function () {
            console.log($(this).nestable('serialize'));
            var $this = $(this);
            var serializedData = window.JSON.stringify($($this).nestable('serialize'));

            console.log(serializedData);
        });



        /*$('.dd4').nestable();

         $('.dd4').on('change', function () {
         var $this = $(this);
         var serializedData = window.JSON.stringify($($this).nestable('serialize'));
         });*/

        /*$.initDataTable({
            selector: '#paginatedVariantAttributesTable',
            name: 'variantAttributes',
            type: 'TYPE_1A',
            url: $.getURL('/pim/families/{familyId}/variantGroups/'),
            columns: [
                {data: 'name', name: 'name', title: 'Name'},
                {data: 'externalId', name: 'externalId', title: 'Group ID'},
                {data: 'variantAxis', name: 'variantAxis', title: 'Variant Axis'},
                { data: 'active', name : 'active' , title : 'Status', orderable: false},
                { data: 'actions', name : 'actions' , title : 'Actions', orderable: false}
            ]
        });*/

//        $('#paginatedTable').dataTable().fnSetFilteringEnterPress();
    });
</script>
<script src="/assets/js/pages/ui/settings/variantGroup.js"></script>

