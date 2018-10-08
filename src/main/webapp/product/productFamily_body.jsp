<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<div class="row clearfix">
    <div class="col-lg-12 col-md-12">
        <div class="card">
            <div class="header">
                <h2>${productFamily.productFamilyName}
                    <small><code class="highlighter-rouge">${productFamily.productFamilyId}</code></small>
                    <small class="pull-right m-t--15"><code style="color:#808080">_id: ${productFamily.id}</code>
                    </small>
                </h2>
            </div>
            <div class="body">
                <ul class="nav nav-tabs-new2">
                    <li class="nav-item"><a class="nav-link active show" data-toggle="tab" href="#DETAILS">Details</a>
                    </li>
                    <li class="nav-item"><a class="nav-link" data-toggle="tab" href="#ProductAttributes">Product
                        Attributes</a></li>
                </ul>
                <div class="tab-content">
                    <div class="tab-pane show active" id="DETAILS">
                        <div class="row clearfix m-t-20">
                            <div class="col-lg-12 col-md-12">
                                <div class="card">
                                    <div class="body">
                                        <form method="post"
                                              action="/pim/productFamilies/${productFamily.productFamilyId}"
                                              data-method="PUT"
                                              data-success-message='["Successfully updated the productFamily", "ProductFamily Updated"]'
                                              data-error-message='["Correct the validation error and try again", "Invalid Data"]'>
                                            <div class="row">
                                                <div class="col-md-6 col-sm-12">
                                                    <div class="form-group">
                                                        <label for="productFamilyName">Family Name</label><code
                                                            class="highlighter-rouge m-l-10">*</code>
                                                        <input type="text" id="productFamilyName" name="productFamilyName"
                                                               value="${productFamily.productFamilyName}"
                                                               class="form-control"/>
                                                    </div>
                                                    <div class="form-group">
                                                        <label for="productFamilyId">Family ID</label><code
                                                            class="highlighter-rouge m-l-10">*</code>
                                                        <input type="text" id="productFamilyId" name="productFamilyId"
                                                               class="form-control"
                                                               value="${productFamily.productFamilyId}"/>
                                                    </div>

                                                    <div class="form-group">
                                                        <label>Status</label>
                                                        <br/>
                                                        <label for="active" class="fancy-checkbox">
                                                            <input type="checkbox" id="active" name="active" value="Y"
                                                                   <c:if test="${productFamily.active eq 'Y'}">checked="checked"</c:if>>
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
                                            <a href="/pim/productFamilies">
                                                <button type="button" class="btn btn-danger">Cancel</button>
                                            </a>
                                        </form>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="tab-pane" id="ProductAttributes">
                        <div class="row clearfix">
                            <div class="col-lg-12 col-md-12">
                                <div class="card">
                                    <div class="body">
                                        <div class="row p-b-25">
                                            <div class="col-lg-12 col-md-12">
                                                <div class="pull-right">
                                                    <button type="button"
                                                            class="btn btn-success js-add-productAttribute"><i
                                                            class="fa fa-plus"></i> <span class="p-l-5">Add Product Attribute</span>
                                                    </button>
                                                </div>
                                            </div>
                                        </div>
                                        <div class="table-responsive">
                                            <table id="paginatedProductAttributesTable"
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
    $.initPage({
        'productFamilyId': '${productFamily.productFamilyId}'
    });
    $(document).ready(function () {
        $.initDataTable({
            selector: '#paginatedProductAttributesTable',
            name: 'productAttributes',
            type: 'TYPE_2',
            buttonGroup: 'GROUP_4',
            url: $.getURL('/pim/productFamilies/{productFamilyId}/PRODUCT/attributes'),
            columns: [
                {data: 'name', name: 'name', title: 'Attribute Name'},
                {data: 'id', name: 'id', title: 'Attribute ID'},
                {data: 'group', name: 'group', title: 'Attribute Group'},
                {data: 'selectable', name: 'selectable', title: 'Selectable'},
                {data: 'actions', name: 'actions', title: 'Actions'}
            ]
        });
//        $('#paginatedTable').dataTable().fnSetFilteringEnterPress();
    });
</script>
<script src="/assets/js/pages/ui/product/productFamily.js"></script>

