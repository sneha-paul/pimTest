<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<c:choose>
    <c:when test="${mode eq 'DETAILS'}">
        <div class="row clearfix">
            <div class="col-lg-12 col-md-12">
                <div class="card">
                    <div class="header">
                        <h2>${product.productName} <small><code class="highlighter-rouge">${product.productId}</code></small></h2>
                    </div>
                    <div class="body">
                        <ul class="nav nav-tabs-new2">
                            <li class="nav-item"><a class="nav-link active show" data-toggle="tab" href="#Attributes">Attributes</a></li>
                            <li class="nav-item"><a class="nav-link" data-toggle="tab" href="#ProductAttributes">Product Attributes</a></li>
                        </ul>
                        <div class="tab-content">
                            <div class="tab-pane show active" id="Attributes">
                                <div class="row clearfix m-t-20">
                                    <div class="col-lg-12 col-md-12">
                                        <div class="card">
                                            <div class="body">
                                                <form id="update-form" method="post" action="/pim/products/${product.productId}" novalidate>
                                                    <div class="row">
                                                        <div class="col-md-6 col-sm-12">
                                                            <div class="form-group">
                                                                <label>Product Name</label><code class="highlighter-rouge m-l-10">*</code>
                                                                <input type="text" name="productName" value="${product.productName}" class="form-control" required="true"/>
                                                            </div>
                                                            <div class="form-group">
                                                                <label>Product ID</label><code class="highlighter-rouge m-l-10">*</code>
                                                                <input type="text" name="productId" class="form-control" value="${product.productId}" required="true"/>
                                                            </div>
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
                                                            <div class="form-group">
                                                                <label>Status</label>
                                                                <br/>
                                                                <label class="fancy-checkbox">
                                                                    <input type="checkbox" name="active" value="Y" <c:if test="${product.active eq 'Y'}">checked="checked"</c:if>>
                                                                    <span>Active</span>
                                                                </label>
                                                            </div>
                                                        </div>
                                                    </div>
                                                    <br>
                                                    <button type="submit" class="btn btn-primary">Save</button>
                                                    <a href="/pim/products"><button type="button" class="btn btn-danger">Cancel</button></a>
                                                </form>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                            <div class="tab-pane" id="ProductAttributes">
                                <div class="row clearfix m-t-20">
                                    <div class="col-lg-12 col-md-12">
                                        <div class="card">
                                            <div class="body">
                                                <form id="family-attributes-form" method="post" action="/pim/products/${product.productId}/familyAttributes" novalidate>
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
                                                    <button id="my123" type="submit" class="btn btn-primary">Save</button>
                                                    <script>
                                                        $.bindFormSubmit($('#my123'));
                                                    </script>
                                                    <%--<a href="/pim/products"><button type="button" class="btn btn-danger">Cancel</button></a>--%>
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
        <script>
            $.initPage({
                'productId' : '${product.productId}'
            });
            /*$( document ).ready(function() {
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
            });*/
        </script>
       <%-- <script src="/assets/js/pages/ui/product/product.js"></script>--%>
    </c:when>
    <c:otherwise>
        <div class="row clearfix">
            <div class="col-lg-12 col-md-12">
                <div class="card">
                    <div class="header">
                        <h1>Create Product</h1>
                    </div>
                    <div class="body">
                        <form id="create-form" method="post" action="/pim/products">
                            <div class="row">
                                <div class="col-md-6 col-sm-12">
                                    <div class="form-group">
                                        <label>Product Name</label>
                                        <input type="text" name="productName" class="form-control" required="true"/>
                                    </div>
                                    <div class="form-group">
                                        <label>Product ID</label>
                                        <input type="text" name="productId" class="form-control" required="true"/>
                                    </div>
                                    <div class="form-group">
                                        <label>ProductFamily</label>
                                        <select class="form-control" name="productFamilyId">
                                            <option value="">Select One</option>
                                            <c:forEach items="${productFamilies}" var="productFamily">
                                                <option value="${productFamily.id}">${productFamily.productFamilyName}</option>
                                            </c:forEach>
                                        </select>
                                    </div>
                                </div>
                            </div>
                            <br>
                            <button type="submit" class="btn btn-primary">Create</button>
                            <a href="/pim/products/"><button type="button" class="btn btn-danger">Cancel</button></a>
                        </form>
                    </div>
                </div>
            </div>
        </div>
    </c:otherwise>
</c:choose>

