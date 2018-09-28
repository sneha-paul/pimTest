<%--@elvariable id="productVariant" type="com.bigname.pim.api.domain.ProductVariant"--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
        <div class="row clearfix">
            <div class="col-lg-12 col-md-12">
                <div class="card">
                    <div class="header">
                        <h2>${productVariant.productVariantName} <small><code class="highlighter-rouge">${productVariant.productVariantId}</code></small></h2>
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
                                                <form method="post" action="/pim/products/${productVariant.product.productId}/variants/${productVariant.productVariantId}" data-method="PUT"
                                                      data-success-message='["Successfully updated the productVariant", "ProductVariant Updated"]'
                                                      data-error-message='["Correct the validation error and try again", "Invalid Data"]'>
                                                    <div class="row">
                                                        <div class="col-md-6 col-sm-12">
                                                            <div class="form-group">
                                                                <label>ProductVariant Name</label><code class="highlighter-rouge m-l-10">*</code>
                                                                <input type="text" name="productVariantName" value="${productVariant.productVariantName}" class="form-control"/>
                                                            </div>
                                                            <div class="form-group">
                                                                <label>ProductVariant ID</label><code class="highlighter-rouge m-l-10">*</code>
                                                                <input type="text" name="productVariantId" class="form-control" value="${productVariant.productVariantId}"/>
                                                            </div>
                                                            <div class="form-group">
                                                                <label>ProductFamily</label>
                                                                <input type="text" name="productFamilyName" disabled="disabled" class="form-control" value="${productVariant.product.productFamily.productFamilyId}"/>

                                                            </div>

                                                            <div class="form-group">
                                                                <label>Status</label>
                                                                <br/>
                                                                <label class="fancy-checkbox">
                                                                    <input type="checkbox" name="active" value="Y" <c:if test="${productVariant.active eq 'Y'}">checked="checked"</c:if>>
                                                                    <span>Active</span>
                                                                </label>
                                                            </div>
                                                        </div>
                                                    </div>
                                                    <br>
                                                    <button type="submit" class="btn btn-primary">Save</button>
                                                    <a href="/pim/productVariants"><button type="button" class="btn btn-danger">Cancel</button></a>
                                                </form>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                                <%--<div class="tab-pane" id="ProductAttributes">
                                    <div class="row clearfix">
                                        <div class="col-lg-12 col-md-12">
                                            <div class="card">
                                                <div class="body">
                                                    <form id="family-attributes-form" method="post" action="/pim/products/${product.productId}/familyAttributes" novalidate>
                                                        <div class="row">
                                                            <c:if test="${not empty productVariant.productFamily}">
                                                                <c:forEach items="${productVariant.productFamily.productFamilyAttributes}" var="attribute">
                                                                    <div class="col-md-6 col-sm-12">
                                                                        <div class="form-group">
                                                                            <label>${attribute.label}</label><code class="highlighter-rouge m-l-10">*</code>
                                                                            <input type="text" name="${attribute.name}" value="${productVariant.familyAttributes[attribute.name]}" class="form-control"/>
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
                                                            &lt;%&ndash;<a href="/pim/products"><button type="button" class="btn btn-danger">Cancel</button></a>&ndash;%&gt;
                                                    </form>

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
                'productId' : '${productVariant.productId}',
                'productVariantId' : '${productVariant.productVariantId}'
            });
        </script>

