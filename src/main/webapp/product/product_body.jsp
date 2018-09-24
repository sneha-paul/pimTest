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
                                                                <input type="text" name="productFamilyId" class="form-control" value="${product.productFamilyId}"  readonly required="true"/>
                                                               <%-- <select class="form-control" id="">
                                                                    <option value="">Select One</option>
                                                                    <c:forEach items="${productFamilies}" var="productFamily">
                                                                        <option value="${productFamily.externalId}">${productFamily.productFamilyName}</option>
                                                                    </c:forEach>
                                                                </select>--%>
                                                            </div>

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
                                <div class="row clearfix">
                                    <div class="col-lg-12 col-md-12">
                                        <div class="card">
                                            <div class="body">



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

        </script>
    </c:when>
    <c:otherwise>
        <div class="row clearfix">
            <div class="col-lg-12 col-md-12">
                <div class="card">
                    <div class="header">
                        <h1>Create Product</h1>
                    </div>
                    <div class="body">
                        <form:form id="create-form" method="post" action="/pim/products" modelAttribute="product">
                            <div class="row">
                                <div class="col-md-6 col-sm-12">
                                    <div class="form-group">
                                        <label>Product Name</label>
                                        <form:input type="text" path="productName" class="form-control" required="true"/>
                                        <form:errors path="productName" cssClass="error"/>
                                    </div>
                                    <div class="form-group">
                                        <label>Product ID</label>
                                        <form:input type="text" path="productId" class="form-control" required="true"/>
                                        <form:errors path="productId" cssClass="error"/>
                                    </div>
                                    <div class="form-group">
                                        <label>ProductFamily</label>
                                        <select class="form-control" id="" name="productFamilyId">
                                            <option value="">Select One</option>
                                            <c:forEach items="${productFamilies}" var="productFamily">
                                                <option value="${productFamily.externalId}">${productFamily.productFamilyName}</option>
                                            </c:forEach>
                                        </select>
                                    </div>
                                </div>
                            </div>
                            <br>
                            <button type="submit" class="btn btn-primary">Create</button>
                            <a href="/pim/products/"><button type="button" class="btn btn-danger">Cancel</button></a>
                        </form:form>
                    </div>
                </div>
            </div>
        </div>
    </c:otherwise>
</c:choose>

