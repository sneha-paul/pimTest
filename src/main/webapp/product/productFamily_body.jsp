<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<c:choose>
    <c:when test="${mode eq 'DETAILS'}">
        <div class="row clearfix">
            <div class="col-lg-12 col-md-12">
                <div class="card">
                    <div class="header">
                        <h2>${productFamily.productFamilyName} <small><code class="highlighter-rouge">${productFamily.productFamilyId}</code></small></h2>
                    </div>
                    <div class="body">
                        <ul class="nav nav-tabs-new2">
                            <li class="nav-item"><a class="nav-link active show" data-toggle="tab" href="#Details">Details</a></li>
                            <li class="nav-item"><a class="nav-link" data-toggle="tab" href="#ProductAttributes">Product Attributes</a></li>
                            <li class="nav-item"><a class="nav-link" data-toggle="tab" href="#VariantAttributes">Variant Attributes</a></li>
                            <li class="nav-item"><a class="nav-link" data-toggle="tab" href="#ProductFeatures">Product Features</a></li>
                            <li class="nav-item"><a class="nav-link" data-toggle="tab" href="#VariantFeatures">Variant Features</a></li>
                        </ul>
                        <div class="tab-content">
                            <div class="tab-pane show active" id="Details">
                                <div class="row clearfix m-t-20">
                                    <div class="col-lg-12 col-md-12">
                                        <div class="card">
                                            <div class="body">
                                                <form id="update-form" method="post" action="/pim/productFamilies/${productFamily.productFamilyId}" data-method="PUT" data-success-message='["Successfully updated the productFamily", "ProductFamily Updated"]' data-error-message='["Correct the validation error and try again", "Invalid Data"]'>
                                                    <div class="row">
                                                        <div class="col-md-6 col-sm-12">
                                                            <div class="form-group">
                                                                <label>Family Name</label><code class="highlighter-rouge m-l-10">*</code>
                                                                <input type="text" name="productFamilyName" value="${productFamily.productFamilyName}" class="form-control" required="true"/>
                                                            </div>
                                                            <div class="form-group">
                                                                <label>Family ID</label><code class="highlighter-rouge m-l-10">*</code>
                                                                <input type="text" name="productFamilyId" class="form-control" value="${productFamily.productFamilyId}" required="true"/>
                                                            </div>

                                                            <div class="form-group">
                                                                <label>Status</label>
                                                                <br/>
                                                                <label class="fancy-checkbox">
                                                                    <input type="checkbox" name="active" value="Y" <c:if test="${productFamily.active eq 'Y'}">checked="checked"</c:if>>
                                                                    <span>Active</span>
                                                                </label>
                                                            </div>
                                                        </div>
                                                    </div>
                                                    <br>
                                                    <button type="submit" class="btn btn-primary">Save</button>
                                                    <a href="/pim/productFamilies"><button type="button" class="btn btn-danger">Cancel</button></a>
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
                                                            <button type="button" class="btn btn-success js-add-productAttribute"><i class="fa fa-plus"></i> <span class="p-l-5">Add Product Attribute</span></button>
                                                        </div>
                                                    </div>
                                                </div>
                                                <div class="table-responsive">
                                                    <table id="paginatedProductAttributesTable" class="table table-hover dataTable table-custom" style="width: 100%">
                                                        <thead class="thead-dark">

                                                        </thead>

                                                    </table>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                            <div class="tab-pane" id="VariantAttributes">
                                <div class="row clearfix">
                                    <div class="col-lg-12 col-md-12">
                                        <div class="card">
                                            <div class="body">
                                                <div class="row p-b-25">
                                                    <div class="col-lg-12 col-md-12">
                                                        <div class="pull-right">
                                                            <button type="button" class="btn btn-success js-add-variantAttribute"><i class="fa fa-plus"></i> <span class="p-l-5">Add Variant Attribute</span></button>
                                                        </div>
                                                    </div>
                                                </div>
                                                <div class="table-responsive">
                                                    <table id="paginatedVariantAttributesTable" class="table table-hover dataTable table-custom" style="width: 100%">
                                                        <thead class="thead-dark">

                                                        </thead>

                                                    </table>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                            <div class="tab-pane" id="ProductFeatures">
                                <div class="row clearfix">
                                    <div class="col-lg-12 col-md-12">
                                        <div class="card">
                                            <div class="body">
                                                <div class="row p-b-25">
                                                    <div class="col-lg-12 col-md-12">
                                                        <div class="pull-right">
                                                            <button type="button" class="btn btn-success js-add-productFeature"><i class="fa fa-plus"></i> <span class="p-l-5">Add Product Feature</span></button>
                                                        </div>
                                                    </div>
                                                </div>
                                                <div class="table-responsive">
                                                    <table id="paginatedProductFeaturesTable" class="table table-hover dataTable table-custom" style="width: 100%">
                                                        <thead class="thead-dark">

                                                        </thead>

                                                    </table>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                            <div class="tab-pane" id="VariantFeatures">
                                <div class="row clearfix">
                                    <div class="col-lg-12 col-md-12">
                                        <div class="card">
                                            <div class="body">
                                                <div class="row p-b-25">
                                                    <div class="col-lg-12 col-md-12">
                                                        <div class="pull-right">
                                                            <button type="button" class="btn btn-success js-add-variantFeature"><i class="fa fa-plus"></i> <span class="p-l-5">Add Variant Feature</span></button>
                                                        </div>
                                                    </div>
                                                </div>
                                                <div class="table-responsive">
                                                    <table id="paginatedVariantFeaturesTable" class="table table-hover dataTable table-custom" style="width: 100%">
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
                'productFamilyId' : '${productFamily.productFamilyId}'
            });
            $( document ).ready(function() {
                $.initDataTable({
                    selector: '#paginatedProductAttributesTable',
                    name: 'productAttributes',
                    type: 'TYPE_2',
                    url: $.getURL('/pim/productFamilies/{productFamilyId}/PRODUCT/attributes'),
                    columns: [
                        { data: 'name', name : 'name' , title : 'Attribute Name'},
                        { data: 'type', name : 'type', title : 'Type' },
                        { data: 'required', name : 'required' , title : 'Required'}
                    ]
                });

                $.initDataTable({
                    selector: '#paginatedVariantAttributesTable',
                    name: 'variantAttributes',
                    type: 'TYPE_2',
                    url: $.getURL('/pim/productFamilies/{productFamilyId}/VARIANT/attributes'),
                    columns: [
                        { data: 'name', name : 'name' , title : 'Attribute Name'},
                        { data: 'type', name : 'type', title : 'Type' },
                        { data: 'required', name : 'required' , title : 'Required'}
                    ]
                });

                $.initDataTable({
                    selector: '#paginatedProductFeaturesTable',
                    name: 'productFeatures',
                    type: 'TYPE_2',
                    url: $.getURL('/pim/productFamilies/{productFamilyId}/PRODUCT/features'),
                    columns: [
                        { data: 'name', name : 'name' , title : 'Feature Name'},
                        { data: 'label', name : 'label', title : 'Label' },
                        { data: 'required', name : 'required' , title : 'Required'},
                        { data: 'selectable', name : 'selectable' , title : 'Selectable'}
                    ]
                });

                $.initDataTable({
                    selector: '#paginatedVariantFeaturesTable',
                    name: 'variantFeatures',
                    type: 'TYPE_2',
                    url: $.getURL('/pim/productFamilies/{productFamilyId}/VARIANT/features'),
                    columns: [
                        { data: 'name', name : 'name' , title : 'Feature Name'},
                        { data: 'label', name : 'label', title : 'Label' },
                        { data: 'required', name : 'required' , title : 'Required'},
                        { data: 'selectable', name : 'selectable' , title : 'Selectable'}
                    ]
                });
//        $('#paginatedTable').dataTable().fnSetFilteringEnterPress();
            });
        </script>
        <script src="/assets/js/pages/ui/product/productFamily.js"></script>
    </c:when>
    <c:otherwise>
        <div class="row clearfix">
            <div class="col-lg-12 col-md-12">
                <div class="card">
                    <div class="header">
                        <h1>Create Product Family</h1>
                    </div>
                    <div class="body">
                        <form:form id="create-form" method="post" action="/pim/productFamilies" modelAttribute="productFamily">
                            <div class="row">
                                <div class="col-md-6 col-sm-12">
                                    <div class="form-group">
                                        <label>Family Name</label>
                                        <form:input type="text" path="productFamilyName" class="form-control" required="true"/>
                                        <form:errors path="productFamilyName" cssClass="error"/>
                                    </div>
                                    <div class="form-group">
                                        <label>Family ID</label>
                                        <form:input type="text" path="productFamilyId" class="form-control" required="true"/>
                                        <form:errors path="productFamilyId" cssClass="error"/>
                                    </div>
                                </div>
                            </div>
                            <br>
                            <button type="submit" class="btn btn-primary">Create</button>
                            <a href="/pim/productFamilies/"><button type="button" class="btn btn-danger">Cancel</button></a>
                        </form:form>
                    </div>
                </div>
            </div>
        </div>
    </c:otherwise>
</c:choose>

