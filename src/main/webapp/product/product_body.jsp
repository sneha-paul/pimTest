<%--@elvariable id="product" type="com.bigname.pim.api.domain.Product"--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<div class="row clearfix">
    <div class="col-lg-12 col-md-12">
        <div class="card">
            <div class="header">
                <h2>${product.productName} <small><code class="highlighter-rouge">${product.productId}</code></small></h2>
            </div>
            <div class="body">
                <ul class="nav nav-tabs-new2">
                    <li class="nav-item"><a class="nav-link active show" data-toggle="tab" href="#details">Details</a></li>
                    <li class="nav-item"><a class="nav-link" data-toggle="tab" href="#SEO">SEO</a></li>
                    <%--<li class="nav-item"><a class="nav-link" data-toggle="tab" href="#digitalAssets">Digital Assets</a></li>--%>
                    <li class="nav-item"><a class="nav-link" data-toggle="tab" href="#productFeatures">Product Features</a></li>
                    <%--<li class="nav-item"><a class="nav-link" data-toggle="tab" href="#productFeatures">Product Features</a></li>--%>
                    <li class="nav-item"><a class="nav-link" data-toggle="tab" href="#productVariants">Product Variants</a></li>
                    <%--<li class="nav-item"><a class="nav-link" data-toggle="tab" href="#productCategories">Categories</a></li>--%>
                </ul>
                <div class="tab-content">
                    <div class="tab-pane show active" id="details">
                        <div class="row clearfix m-t-20">
                            <div class="col-lg-12 col-md-12">
                                <div class="card">
                                    <div class="body">
                                        <form method="post" action="/pim/products/${product.productId}" data-method="PUT"
                                              data-success-message='["Successfully updated the product", "Product Updated"]'
                                              data-error-message='["Correct the validation error and try again", "Invalid Data"]'>
                                            <div class="row">
                                                <div class="col-md-6 col-sm-12">
                                                    <div class="form-group">
                                                        <label>Product Name</label><code class="highlighter-rouge m-l-10">*</code>
                                                        <input type="text" name="productName" value="${product.productName}" class="form-control" required="true"/>
                                                    </div>
                                                </div>
                                                <div class="col-md-6 col-sm-12">
                                                    <div class="form-group">
                                                        <label>Product ID</label><code class="highlighter-rouge m-l-10">*</code>
                                                        <input type="text" name="productId" class="form-control" value="${product.productId}" required="true"/>
                                                    </div>
                                                </div>
                                                <div class="col-md-6 col-sm-12">
                                                    <div class="form-group">
                                                        <label>ProductFamily</label>

                                                        <select class="form-control" name="productFamilyId">
                                                            <option value="">Select One</option>
                                                            <c:forEach items="${productFamilies}" var="productFamily">
                                                                <option value="${productFamily.id}">${productFamily.productFamilyName}</option>
                                                            </c:forEach>
                                                        </select>
                                                    </div>
                                                    <script>
                                                        $('select[name="productFamilyId"]').val('${product.productFamilyId}');
                                                    </script>
                                                </div>
                                                <div class="col-md-6 col-sm-12">
                                                    <div class="form-group">
                                                        <label>Status</label>
                                                        <br/>
                                                        <label class="fancy-checkbox">
                                                            <input type="checkbox" name="active" value="Y" <c:if test="${product.active eq 'Y'}">checked="checked"</c:if>>
                                                            <span>Active</span>
                                                        </label>
                                                    </div>
                                                </div>
                                                <c:if test="${not empty product.productFamily}">
                                                    <c:set var="attributeGroup" value="${product.productFamily.productFamilyAttributes['DEFAULT_GROUP']}" />
                                                    <c:if test="${not empty attributeGroup}">
                                                        <c:forEach items="${attributeGroup.attributes}" var="attributeEntry">
                                                            <c:set var="attribute" value="${attributeEntry.value}"/>
                                                            <div class="col-md-6 col-sm-12">
                                                                <div class="form-group">
                                                                    <label>${attribute.name}</label><code class="highlighter-rouge m-l-10"><c:if test="${attribute.required eq 'Y'}">*</c:if></code>
                                                                    <c:choose>
                                                                        <c:when test="${attribute.uiType eq 'DROPDOWN'}">
                                                                            <select name="${attribute.id}" class="form-control">
                                                                                <option value="">Select One</option>
                                                                                <c:forEach items="${attribute.options}" var="option">
                                                                                    <option value="${option.id}" <c:if test="${option.id eq product.familyAttributes[attribute.id]}">selected</c:if>>${option.value}</option>
                                                                                </c:forEach>
                                                                            </select>
                                                                        </c:when>
                                                                        <c:when test="${attribute.uiType eq 'TEXTAREA'}">
                                                                            <textarea class="form-control" name="${attribute.id}">${product.familyAttributes[attribute.id]}</textarea>
                                                                        </c:when>
                                                                        <c:when test="${attribute.uiType eq 'CHECKBOX'}">

                                                                            <br/>${product.familyAttributes[attribute.id]}
                                                                            <c:forEach items="${attribute.options}" var="option">
                                                                                <c:set var="propertyName" value="${attribute.id}_${option.id}"/>

                                                                                <label class="fancy-checkbox">
                                                                                    <input type="checkbox" class="js-checkbox" name="${propertyName}" value="Y" <c:if test="${product.familyAttributes[propertyName] eq 'Y'}">checked="checked"</c:if>>
                                                                                    <%--<c:choose>
                                                                                        <c:when test="${product.familyAttributes[propertyName] ne 'Y'}">
                                                                                            <input type="hidden" name="${propertyName}" disabled="disabled" value="Y" />
                                                                                        </c:when>
                                                                                        <c:otherwise>
                                                                                            <input type="hidden" name="${propertyName}" value="N" />
                                                                                        </c:otherwise>
                                                                                    </c:choose>--%>
                                                                                    <span>${option.value}</span>
                                                                                </label>
                                                                            </c:forEach>
                                                                        </c:when>
                                                                        <c:otherwise>
                                                                            <input type="text" name="${attribute.id}" value="${product.familyAttributes[attribute.id]}" class="form-control"/>
                                                                        </c:otherwise>
                                                                    </c:choose>
                                                                </div>
                                                            </div>
                                                        </c:forEach>
                                                    </c:if>
                                                </c:if>
                                            </div>
                                            <br>
                                            <input type="hidden" name="group" value="DETAILS"/>
                                            <button type="submit" class="btn btn-primary" onclick="$.submitAction(event, this)">Save</button>
                                            <a href="/pim/products"><button type="button" class="btn btn-danger">Cancel</button></a>
                                        </form>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="tab-pane" id="SEO">
                        <div class="row clearfix m-t-20">
                            <div class="col-lg-12 col-md-12">
                                <div class="card">
                                    <div class="body">
                                        <form method="post" action="/pim/categories/${category.categoryId}" data-method="PUT" data-success-message='["Successfully updated the category", "Category Updated"]' data-error-message='["Correct the validation error and try again", "Invalid Data"]'>
                                            <div class="row">
                                                <div class="col-md-6 col-sm-12">
                                                    <div class="form-group">
                                                        <label for="metaTitle">Meta Title</label><code class="highlighter-rouge m-l-10">*</code>
                                                        <input type="text" id="metaTitle" name="metaTitle" class="form-control" value="${category.metaTitle}" />
                                                    </div>

                                                    <div class="form-group">
                                                        <label for="metaDescription">Meta Description</label><code class="highlighter-rouge m-l-10">*</code>
                                                        <textarea class="form-control" id="metaDescription" name="metaDescription" rows="5" cols="30" required="">${category.metaDescription}</textarea>
                                                    </div>

                                                    <div class="form-group">
                                                        <label for="metaKeywords">Meta Keywords</label>
                                                        <textarea class="form-control" id="metaKeywords" name="metaKeywords" rows="5" cols="30" required="">${category.metaKeywords}</textarea>
                                                    </div>
                                                </div>
                                            </div>
                                            <br>
                                            <input type="hidden" name="group" value="SEO"/>
                                            <button type="submit" class="btn btn-primary" onclick="$.submitAction(event, this)">Save</button>
                                            <a href="/pim/categories"><button type="button" class="btn btn-danger">Cancel</button></a>
                                        </form>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                    <%--<div class="tab-pane" id="digitalAssets"> <!-- TODO -->
                        <div class="row clearfix m-t-20">
                            <div class="col-lg-12 col-md-12">
                                <div class="card">
                                    <div class="body">
                                        <form method="post" action="/pim/categories/${category.categoryId}" data-method="PUT" data-success-message='["Successfully updated the category", "Category Updated"]' data-error-message='["Correct the validation error and try again", "Invalid Data"]'>
                                            <div class="row">
                                                <div class="col-md-6 col-sm-12">
                                                    <div class="form-group">
                                                        <label for="metaTitle">Meta Title</label><code class="highlighter-rouge m-l-10">*</code>
                                                        <input type="text" id="metaTitle" name="metaTitle" class="form-control" value="${category.metaTitle}" />
                                                    </div>

                                                    <div class="form-group">
                                                        <label for="metaDescription">Meta Description</label><code class="highlighter-rouge m-l-10">*</code>
                                                        <textarea class="form-control" id="metaDescription" name="metaDescription" rows="5" cols="30" required="">${category.metaDescription}</textarea>
                                                    </div>

                                                    <div class="form-group">
                                                        <label for="metaKeywords">Meta Keywords</label>
                                                        <textarea class="form-control" id="metaKeywords" name="metaKeywords" rows="5" cols="30" required="">${category.metaKeywords}</textarea>
                                                    </div>
                                                </div>
                                            </div>
                                            <br>
                                            <input type="hidden" name="group" value="SEO"/>
                                            <button type="submit" class="btn btn-primary" onclick="$.submitAction(event, this)">Save</button>
                                            <a href="/pim/categories"><button type="button" class="btn btn-danger">Cancel</button></a>
                                        </form>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>--%>
                    <div class="tab-pane" id="productFeatures">
                        <div class="row clearfix m-t-20">
                            <div class="col-lg-12 col-md-12">
                                <div class="card">
                                    <div class="body">
                                        <form method="post" action="/pim/products/${product.productId}/familyAttributes" data-method="PUT"
                                              data-success-message='["Successfully saved the attributes", "Attributes Saved"]'
                                              data-error-message='["Correct the validation error and try again", "Invalid Data"]'>
                                            <div class="row">
                                                <c:if test="${not empty product.productFamily}">
                                                    <c:forEach items="${product.productFamily.productFamilyAttributes}" var="attributeGroupEntry">
                                                        <c:if test="${attributeGroupEntry.key ne 'DEFAULT_GROUP'}">
                                                            <c:forEach items="${attributeGroupEntry.value.attributes}" var="attributeEntry">
                                                                <c:set var="attribute" value="${attributeEntry.value}"/>
                                                                <div class="col-md-6 col-sm-12">
                                                                    <div class="form-group">
                                                                        <label>${attribute.name}</label><code class="highlighter-rouge m-l-10">*</code>
                                                                        <c:choose>
                                                                            <c:when test="${attribute.selectable eq 'Y'}">
                                                                                <select name="${attribute.id}" class="form-control">
                                                                                    <option value="">Select One</option>
                                                                                    <c:forEach items="${attribute.options}" var="option">
                                                                                        <option value="${option.id}" <c:if test="${option.id eq product.familyAttributes[attribute.id]}">selected</c:if>>${option.value}</option>
                                                                                    </c:forEach>
                                                                                </select>
                                                                            </c:when>
                                                                            <c:otherwise>
                                                                                <input type="text" name="${attribute.id}" value="${product.familyAttributes[attribute.id]}" class="form-control"/>
                                                                            </c:otherwise>
                                                                        </c:choose>

                                                                    </div>
                                                                </div>
                                                            </c:forEach>
                                                        </c:if>
                                                    </c:forEach>
                                                </c:if>
                                            </div>
                                            <br>
                                            <input type="hidden" name="group" value="FAMILY_ATTRIBUTES"/>
                                            <button type="submit" class="btn btn-primary" onclick="$.submitAction(event, this)">Save</button>
                                        </form>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                    <%--<div class="tab-pane" id="productFeatures"> <!-- TODO -->
                        <div class="row clearfix m-t-20">
                            <div class="col-lg-12 col-md-12">
                                <div class="card">
                                    <div class="body">

                                        <form method="post" action="/pim/products/${product.productId}/familyAttributes" data-method="PUT"
                                              data-success-message='["Successfully saved the attributes", "Attributes Saved"]'
                                              data-error-message='["Correct the validation error and try again", "Invalid Data"]'>
                                            <div class="row">
                                                <c:if test="${not empty product.productFamily}">
                                                    <c:forEach items="${product.productFamily.productFamilyAttributes}" var="attribute">
                                                        <div class="col-md-6 col-sm-12">
                                                            <div class="form-group">
                                                                <label>${attribute.label}</label><code class="highlighter-rouge m-l-10">*</code>
                                                                <input type="text" name="${attribute.name}" value="${product.familyAttributes[attribute.name]}" class="form-control"/>
                                                            </div>
                                                        </div>
                                                    </c:forEach>
                                                </c:if>
                                            </div>
                                            <br>
                                            <button type="submit" class="btn btn-primary" onclick="$.submitAction(event, this)">Save</button>
                                        </form>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>--%>
                    <div class="tab-pane" id="productVariants">
                        <div class="row clearfix">
                            <div class="col-lg-12 col-md-12">
                                <div class="card">
                                    <div class="body">
                                        <div class="row p-b-25">
                                            <div class="col-lg-12 col-md-12">
                                                <div class="pull-right">
                                                    <button id="js-create-variant" type="button" class="btn btn-success"><i class="fa fa-plus"></i> <span class="p-l-5">Create Product Variant</span></button>
                                                </div>
                                            </div>
                                        </div>
                                        <div class="table-responsive">
                                            <table id="paginatedProductVariantsTable" class="table table-hover dataTable table-custom" style="width: 100%">
                                                <thead class="thead-dark"></thead>
                                            </table>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                    <%--<div class="tab-pane" id="productCategories">
                        <div class="row clearfix">
                            <div class="col-lg-12 col-md-12">
                                <div class="card">
                                    <div class="body">
                                        <div class="row p-b-25">
                                            <div class="col-lg-12 col-md-12">
                                                <div class="pull-right">
                                                    <button type="button" class="btn btn-success js-add-subCategory"><i class="fa fa-plus"></i> <span class="p-l-5">Add SubCategories</span></button>
                                                </div>
                                            </div>
                                        </div>
                                        <div class="table-responsive">
                                            <table id="paginatedProductCategoriesTable" class="table table-hover dataTable table-custom" style="width: 100%">
                                                <thead class="thead-dark"></thead>
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
    $.initPage({
        'productId' : '${product.productId}'
    });
    $( document ).ready(function() {
        $.initDataTable({
            selector: '#paginatedProductVariantsTable',
            name: 'productVariants',
            type: 'TYPE_1',
            url: $.getURL('/pim/products/{productId}/variants/'),
            columns: [
                { data: 'productVariantName', name : 'productVariantName' , title : 'Variant Name'},
                { data: 'externalId', name : 'externalId', title : 'Variant ID' },
                { data: 'active', name : 'active' , title : 'Status', orderable: false},
                { data: 'actions', name : 'actions' , title : 'Actions', orderable: false}
            ]
        });
//        $('#paginatedTable').dataTable().fnSetFilteringEnterPress();
        $.addModal({
            selector: '#js-create-variant',
            url: $.getURL('/pim/products/{productId}/variants/create'),
            name:'create-variant',
            title:'Create Product Variant',
            buttons: [
                {text: 'SAVE', style: 'primary', close: false, click: function(){$.submitForm($(this).closest('.modal-content').find('form'), function(){$.reloadDataTable('productVariants');$.closeModal();});}},
                {text: 'CLOSE', style: 'danger', close: true, click: function(){}}
            ]
        });

        /*$('form').on('click', '.js-checkbox', function() {
            $(this).parent().find('input').prop('disabled', $(this).prop('checked'));
        });*/
    });
</script>


