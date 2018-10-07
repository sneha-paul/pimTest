<%--@elvariable id="product" type="com.bigname.pim.api.domain.Product"--%>
<%--@elvariable id="productFamilies" type="java.util.List<com.bigname.pim.api.domain.ProductFamily>"--%>
<%--@elvariable id="masterGroup" type="com.bigname.pim.api.domain.AttributeGroup"--%>
<%--@elvariable id="detailsMasterGroup" type="com.bigname.pim.api.domain.AttributeGroup"--%>
<%--@elvariable id="featuresMasterGroup" type="com.bigname.pim.api.domain.AttributeGroup"--%>
<%--@elvariable id="defaultDetailsAttributeGroup" type="com.bigname.pim.api.domain.AttributeGroup"--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<c:set var="masterGroups" value="${product.productFamily.getAddonMasterGroups(\"PRODUCT\")}"/>
<c:set var="detailsMasterGroup" value="${product.productFamily.getDetailsMasterGroup(\"PRODUCT\")}"/>
<c:set var="featuresMasterGroup" value="${product.productFamily.getFeaturesMasterGroup(\"PRODUCT\")}"/>
<div class="row clearfix">
    <div class="col-lg-12 col-md-12">
        <div class="card">
            <div class="header">
                <h2>${product.productName} <small><code class="highlighter-rouge">${product.productId}</code></small><small class="pull-right" style="margin-top: -15px"><code style="color:#808080">_id: ${product.id}</code></small></h2>
            </div>
            <div class="body">
                <ul class="nav nav-tabs-new2">
                    <li class="nav-item"><a class="nav-link active show" data-toggle="tab" href="#details">Details</a></li>
                    <li class="nav-item"><a class="nav-link" data-toggle="tab" href="#digitalAssets">Digital Assets</a></li>
                    <c:forEach var="masterGroup" items="${masterGroups}">
                        <li class="nav-item"><a class="nav-link" data-toggle="tab" href="#${masterGroup.id}">${masterGroup.name}</a></li>
                    </c:forEach>
                    <li class="nav-item"><a class="nav-link" data-toggle="tab" href="#productFeatures">Product Features</a></li>
                    <li class="nav-item"><a class="nav-link" data-toggle="tab" href="#productVariants">Product Variants</a></li>
                    <%--<li class="nav-item"><a class="nav-link" data-toggle="tab" href="#productCategories">Categories</a></li>--%>
                </ul>
                <div class="tab-content">
                    <div class="tab-pane show active" id="details">
                        <div class="row clearfix m-t-20">
                            <div class="col-lg-12 col-md-12">

                                <div class="card inner overflowhidden">
                                    <div class="body">
                                        <form method="post" action="/pim/products/${product.productId}" data-method="PUT"
                                              data-success-message='["Successfully updated the product", "Product Updated"]'
                                              data-error-message='["Check the error message(s) and try again", "Invalid Data"]'>

                                            <div class="card inner group overflowhidden">
                                                <div class="body">
                                                    <fieldset>
                                                        <div class="panel panel-default">
                                                            <div class="panel-body">
                                                                <div class="row">
                                                                    <div class="col-md-6 col-sm-12">
                                                                        <div class="form-group">
                                                                            <label for="productName">Product Name</label><code class="highlighter-rouge m-l-10">*</code>
                                                                            <input type="text" id="productName" name="productName" value="${product.productName}" class="form-control" required="true"/>
                                                                        </div>
                                                                        <div class="form-group">
                                                                            <label for="productId">Product ID</label><code class="highlighter-rouge m-l-10">*</code>
                                                                            <input type="text" id="productId" name="productId" class="form-control" value="${product.productId}" required="true"/>
                                                                        </div>
                                                                        <div class="form-group">
                                                                            <label for="productFamilyId">ProductFamily</label>
                                                                            <select class="form-control" id="productFamilyId" name="productFamilyId">
                                                                                <option value="">Select One</option>

                                                                                <c:forEach items="${productFamilies}" var="productFamily">
                                                                                    <option value="${productFamily.id}">${productFamily.productFamilyName}</option>
                                                                                </c:forEach>
                                                                            </select>
                                                                        </div>
                                                                        <script>
                                                                            $('select[name="productFamilyId"]').val('${product.productFamilyId}');
                                                                        </script>
                                                                        <c:if test="${not empty detailsMasterGroup}">
                                                                            <c:set var="defaultDetailsAttributeGroup" value="${detailsMasterGroup.childGroups.get('DEFAULT_GROUP').childGroups.get('DEFAULT_GROUP')}"/>
                                                                            <c:if test="${not empty defaultDetailsAttributeGroup}">
                                                                                <c:forEach items="${defaultDetailsAttributeGroup.attributes}" var="attributeEntry">
                                                                                    <c:set var="attribute" value="${attributeEntry.value}"/>
                                                                                    <div class="form-group">
                                                                                        <label for="${attribute.id}">${attribute.name}</label><code class="highlighter-rouge m-l-10"><c:if test="${attribute.required eq 'Y'}">*</c:if></code>
                                                                                        <c:choose>
                                                                                            <c:when test="${attribute.uiType eq 'DROPDOWN'}">
                                                                                                <select id="${attribute.id}" name="${attribute.id}" class="form-control">
                                                                                                    <option value="">Select One</option>
                                                                                                    <c:forEach items="${attribute.options}" var="optionEntry">
                                                                                                        <c:set var="attributeOption" value="${optionEntry.value}"/>
                                                                                                        <option value="${attributeOption.id}" <c:if test="${attributeOption.id eq product.familyAttributes[attribute.id]}">selected</c:if>>${attributeOption.value}</option>
                                                                                                    </c:forEach>
                                                                                                </select>
                                                                                            </c:when>
                                                                                            <c:when test="${attribute.uiType eq 'TEXTAREA'}">
                                                                                                <textarea id="${attribute.id}" class="form-control" name="${attribute.id}">${product.familyAttributes[attribute.id]}</textarea>
                                                                                            </c:when>
                                                                                            <c:when test="${attribute.uiType eq 'CHECKBOX'}">
                                                                                                <br/>${product.familyAttributes[attribute.id]}
                                                                                                <c:forEach items="${attribute.options}" var="attributeOptionEntry" varStatus="s1">
                                                                                                    <c:set var="attributeOption" value="${attributeOptionEntry.value}"/>
                                                                                                    <c:set var="propertyName" value="${attribute.id}_${attributeOption.id}"/>

                                                                                                    <label class="fancy-checkbox">
                                                                                                        <input type="checkbox" class="js-checkbox" name="${attribute.id}" value="Y" <c:if test="${product.familyAttributes[propertyName] eq 'Y'}">checked="checked"</c:if>>
                                                                                                            <%--<c:choose>
                                                                                                                <c:when test="${product.familyAttributes[propertyName] ne 'Y'}">
                                                                                                                    <input type="hidden" name="${propertyName}" disabled="disabled" value="Y" />
                                                                                                                </c:when>
                                                                                                                <c:otherwise>
                                                                                                                    <input type="hidden" name="${propertyName}" value="N" />
                                                                                                                </c:otherwise>
                                                                                                            </c:choose>--%>
                                                                                                        <span>${attributeOption.value}</span>
                                                                                                    </label>
                                                                                                    <c:if test="${not s1.end}">
                                                                                                        <br/>
                                                                                                    </c:if>
                                                                                                </c:forEach>
                                                                                            </c:when>
                                                                                            <c:when test="${attribute.uiType eq 'RADIO_BUTTON'}">
                                                                                                <br/>${product.familyAttributes[attribute.id]}
                                                                                                <c:forEach items="${attribute.options}" var="attributeOptionEntry" varStatus="s1">
                                                                                                    <c:set var="attributeOption" value="${attributeOptionEntry.value}"/>
                                                                                                    <c:set var="propertyName" value="${attribute.id}_${attributeOption.id}"/>
                                                                                                    <%--<label class="fancy-radio"><input name="gender3" value="male" type="radio" checked=""><span><i></i>Male</span></label>--%>
                                                                                                    <label class="fancy-radio">
                                                                                                        <input type="radio" name="${attribute.id}" value="Y" <c:if test="${product.familyAttributes[propertyName] eq 'Y'}">checked="checked"</c:if>>
                                                                                                            <%--<c:choose>
                                                                                                                <c:when test="${product.familyAttributes[propertyName] ne 'Y'}">
                                                                                                                    <input type="hidden" name="${propertyName}" disabled="disabled" value="Y" />
                                                                                                                </c:when>
                                                                                                                <c:otherwise>
                                                                                                                    <input type="hidden" name="${propertyName}" value="N" />
                                                                                                                </c:otherwise>
                                                                                                            </c:choose>--%>
                                                                                                        <span><i></i>${attributeOption.value}</span>
                                                                                                    </label>
                                                                                                    <c:if test="${not s1.end}">
                                                                                                        <br/>
                                                                                                    </c:if>
                                                                                                </c:forEach>
                                                                                            </c:when>
                                                                                            <c:when test="${attribute.uiType eq 'DATE_PICKER'}">
                                                                                                <div class="input-group date" data-date-autoclose="true" data-provide="datepicker">
                                                                                                    <input type="text" name="${attribute.id}" value="${product.familyAttributes[attribute.id]}" class="form-control datepicker">
                                                                                                    <div class="input-group-append">
                                                                                                        <button class="btn btn-secondary" type="button"><i class="fa fa-calendar"></i></button>
                                                                                                    </div>
                                                                                                </div>
                                                                                            </c:when>
                                                                                            <c:otherwise>
                                                                                                <input type="text" id="${attribute.id}" name="${attribute.id}" value="${product.familyAttributes[attribute.id]}" class="form-control"/>
                                                                                            </c:otherwise>
                                                                                        </c:choose>
                                                                                    </div>
                                                                                </c:forEach>
                                                                            </c:if>
                                                                        </c:if>
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
                                                            </div>
                                                        </div>
                                                    </fieldset>
                                                </div>
                                            </div>
                                            <c:forEach items="${detailsMasterGroup.childGroups.get('DEFAULT_GROUP').childGroups}" var="attributeGroupEntry">
                                                <c:set var="attributeGroup" value="${attributeGroupEntry.value}" />
                                                <c:if test="${attributeGroup.defaultGroup ne 'Y'}">
                                                    <div class="card inner group overflowhidden">
                                                        <div class="body">
                                                            <fieldset>
                                                                <c:if test="${attributeGroup.defaultGroup ne 'Y'}"><legend>${attributeGroup.label}</legend></c:if>
                                                                <div class="panel panel-default">
                                                                    <div class="panel-body">
                                                                        <div class="row">
                                                                            <div class="col-md-6 col-sm-12">
                                                                                <c:forEach items="${attributeGroup.attributes}" var="attributeEntry">
                                                                                    <c:set var="attribute" value="${attributeEntry.value}"/>
                                                                                    <div class="form-group">
                                                                                        <label for="${attribute.id}">${attribute.name}</label><code class="highlighter-rouge m-l-10"><c:if test="${attribute.required eq 'Y'}">*</c:if></code>
                                                                                        <c:choose>
                                                                                            <c:when test="${attribute.uiType eq 'DROPDOWN'}">
                                                                                                <select id="${attribute.id}" name="${attribute.id}" class="form-control">
                                                                                                    <option value="">Select One</option>
                                                                                                    <c:forEach items="${attribute.options}" var="optionEntry">
                                                                                                        <c:set var="attributeOption" value="${optionEntry.value}"/>
                                                                                                        <option value="${attributeOption.id}" <c:if test="${attributeOption.id eq product.familyAttributes[attribute.id]}">selected</c:if>>${attributeOption.value}</option>
                                                                                                    </c:forEach>
                                                                                                </select>
                                                                                            </c:when>
                                                                                            <c:when test="${attribute.uiType eq 'TEXTAREA'}">
                                                                                                <textarea id="${attribute.id}" class="form-control" name="${attribute.id}">${product.familyAttributes[attribute.id]}</textarea>
                                                                                            </c:when>
                                                                                            <c:when test="${attribute.uiType eq 'CHECKBOX'}">
                                                                                                <br/>${product.familyAttributes[attribute.id]}
                                                                                                <c:forEach items="${attribute.options}" var="attributeOptionEntry" varStatus="s1">
                                                                                                    <c:set var="attributeOption" value="${attributeOptionEntry.value}"/>
                                                                                                    <c:set var="propertyName" value="${attribute.id}_${attributeOption.id}"/>

                                                                                                    <label class="fancy-checkbox">
                                                                                                        <input type="checkbox" class="js-checkbox" name="${attribute.id}" value="Y" <c:if test="${product.familyAttributes[propertyName] eq 'Y'}">checked="checked"</c:if>>
                                                                                                            <%--<c:choose>
                                                                                                                <c:when test="${product.familyAttributes[propertyName] ne 'Y'}">
                                                                                                                    <input type="hidden" name="${propertyName}" disabled="disabled" value="Y" />
                                                                                                                </c:when>
                                                                                                                <c:otherwise>
                                                                                                                    <input type="hidden" name="${propertyName}" value="N" />
                                                                                                                </c:otherwise>
                                                                                                            </c:choose>--%>
                                                                                                        <span>${attributeOption.value}</span>
                                                                                                    </label>
                                                                                                    <c:if test="${not s1.end}">
                                                                                                        <br/>
                                                                                                    </c:if>
                                                                                                </c:forEach>
                                                                                            </c:when>
                                                                                            <c:when test="${attribute.uiType eq 'RADIO_BUTTON'}">
                                                                                                <br/>${product.familyAttributes[attribute.id]}
                                                                                                <c:forEach items="${attribute.options}" var="attributeOptionEntry" varStatus="s1">
                                                                                                    <c:set var="attributeOption" value="${attributeOptionEntry.value}"/>
                                                                                                    <c:set var="propertyName" value="${attribute.id}_${attributeOption.id}"/>
                                                                                                    <%--<label class="fancy-radio"><input name="gender3" value="male" type="radio" checked=""><span><i></i>Male</span></label>--%>
                                                                                                    <label class="fancy-radio">
                                                                                                        <input type="radio" name="${attribute.id}" value="Y" <c:if test="${product.familyAttributes[propertyName] eq 'Y'}">checked="checked"</c:if>>
                                                                                                            <%--<c:choose>
                                                                                                                <c:when test="${product.familyAttributes[propertyName] ne 'Y'}">
                                                                                                                    <input type="hidden" name="${propertyName}" disabled="disabled" value="Y" />
                                                                                                                </c:when>
                                                                                                                <c:otherwise>
                                                                                                                    <input type="hidden" name="${propertyName}" value="N" />
                                                                                                                </c:otherwise>
                                                                                                            </c:choose>--%>
                                                                                                        <span><i></i>${attributeOption.value}</span>
                                                                                                    </label>
                                                                                                    <c:if test="${not s1.end}">
                                                                                                        <br/>
                                                                                                    </c:if>
                                                                                                </c:forEach>
                                                                                            </c:when>
                                                                                            <c:when test="${attribute.uiType eq 'DATE_PICKER'}">
                                                                                                <div class="input-group date" data-date-autoclose="true" data-provide="datepicker">
                                                                                                    <input type="text" name="${attribute.id}" value="${product.familyAttributes[attribute.id]}" class="form-control datepicker">
                                                                                                    <div class="input-group-append">
                                                                                                        <button class="btn btn-secondary" type="button"><i class="fa fa-calendar"></i></button>
                                                                                                    </div>
                                                                                                </div>
                                                                                            </c:when>
                                                                                            <c:otherwise>
                                                                                                <input type="text" id="${attribute.id}" name="${attribute.id}" value="${product.familyAttributes[attribute.id]}" class="form-control"/>
                                                                                            </c:otherwise>
                                                                                        </c:choose>
                                                                                    </div>
                                                                                </c:forEach>
                                                                            </div>
                                                                        </div>
                                                                    </div>
                                                                </div>
                                                            </fieldset>
                                                        </div>
                                                    </div>
                                                </c:if>
                                            </c:forEach>
                                            <div class="form-button-group pull-right m-50">
                                                <button type="submit" class="btn btn-primary" onclick="$.submitAction(event, this)">Save</button>
                                                <a href="/pim/products"><button type="button" class="btn btn-danger">Cancel</button></a>
                                            </div>
                                        </form>
                                    </div>
                                </div>

                            </div>
                        </div>
                    </div>
                    <div class="tab-pane" id="digitalAssets">
                        <div class="row clearfix m-t-20">
                            <div class="col-lg-12 col-md-12">
                                <div class="card inner overflowhidden">
                                    <div class="body">
                                        <fieldset><legend>Section Name</legend></fieldset>
                                        <hr style="border:0; margin-top: -5px;border-bottom: 1rem; border-top:1px solid rgba(0,0,0,.1)"/>
                                        <form method="post">
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

                                            <div class="card inner group overflowhidden">
                                                <div class="body">
                                                        <fieldset>
                                                            <legend>Fieldset Title</legend>
                                                            <div class="panel panel-default">
                                                                <div class="panel-body">
                                                                    <div class="row">
                                                                        <div class="col-md-6 col-sm-12">
                                                                            <div class="form-group">
                                                                                <label>Product Name</label><code
                                                                                    class="highlighter-rouge m-l-10">*</code>
                                                                                <input type="text"
                                                                                       name="productName" value=""
                                                                                       class="form-control"
                                                                                       required="true"/>
                                                                            </div>

                                                                            <div class="form-group">
                                                                                <label>Product Name</label><code
                                                                                    class="highlighter-rouge m-l-10">*</code>
                                                                                <input type="text"
                                                                                       name="productName" value=""
                                                                                       class="form-control"
                                                                                       required="true"/>
                                                                            </div>

                                                                            <div class="form-group">
                                                                                <label for="metaDescription">Meta Description</label><code class="highlighter-rouge m-l-10">*</code>
                                                                                <textarea class="form-control" id="metaDescription" name="metaDescription" rows="5" cols="30" required="">${category.metaDescription}</textarea>
                                                                            </div>

                                                                            <div class="form-group">
                                                                                <label for="metaKeywords">Meta Keywords</label>
                                                                                <textarea class="form-control" id="metaKeywords" name="metaKeywords" rows="5" cols="30" required="">${category.metaKeywords}</textarea>
                                                                            </div>

                                                                            <div class="form-group">
                                                                            <label>ProductFamily</label>

                                                                            <select class="form-control" name="productFamilyId">
                                                                                <option value="">Select One</option>
                                                                            </select>
                                                                            </div>

                                                                            <div class="form-group">
                                                                                <label>Status</label>
                                                                                <br/>
                                                                                <label class="fancy-checkbox">
                                                                                    <input type="checkbox" name="active" value="Y" <c:if test="${product.active eq 'Y'}">checked="checked"</c:if>>
                                                                                    <span>Active</span>
                                                                                </label>
                                                                            </div>
                                                                            <div class="form-group">
                                                                                <label>Status</label>
                                                                                <br/>
                                                                                <label class="fancy-checkbox custom-color-blue">
                                                                                    <input type="checkbox" name="active" value="Y" <c:if test="${product.active eq 'Y'}">checked="checked"</c:if>>
                                                                                    <span>Active</span>
                                                                                </label>
                                                                                <br/>
                                                                                <label class="fancy-checkbox">
                                                                                    <input type="checkbox" name="active" value="Y" <c:if test="${product.active eq 'Y'}">checked="checked"</c:if>>
                                                                                    <span>Active</span>
                                                                                </label>
                                                                            </div>
                                                                            <div class="form-group">
                                                                                <label>Status</label>
                                                                                <br/>
                                                                                <label class="fancy-radio"><input name="gender3" value="male" type="radio" checked=""><span><i></i>Male</span></label>
                                                                                <label class="fancy-radio"><input name="gender3" value="male" type="radio" checked=""><span><i></i>Female</span></label>
                                                                            </div>

                                                                            <div class="form-group">
                                                                                <label>Date</label>
                                                                                <div class="input-group date" data-date-autoclose="true" data-provide="datepicker">
                                                                                    <input type="text" class="form-control datepicker">
                                                                                    <div class="input-group-append">
                                                                                        <button class="btn btn-secondary" type="button"><i class="fa fa-calendar"></i></button>
                                                                                    </div>
                                                                                </div>
                                                                            </div>

                                                                            <div class="form-group">
                                                                                <label>Date Range</label>
                                                                                <div class="input-daterange input-group" data-provide="datepicker">
                                                                                    <input type="text" class="input-sm form-control datepicker" name="start">
                                                                                    <div class="input-group-append">
                                                                                        <button class="btn btn-secondary" style="border-bottom-right-radius: 0.25rem;border-top-right-radius: 0.25rem;" type="button"><i class="fa fa-calendar"></i></button>
                                                                                    </div>
                                                                                    <span class="input-group-addon text-center" style="width: 40px;">to</span>
                                                                                    <input type="text" class="input-sm form-control datepicker" name="end">
                                                                                    <div class="input-group-append">
                                                                                        <button class="btn btn-secondary" type="button"><i class="fa fa-calendar"></i></button>
                                                                                    </div>
                                                                                </div>
                                                                            </div>
                                                                        </div>
                                                                    </div>
                                                                </div>
                                                            </div>
                                                        </fieldset>
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
                    <c:forEach var="masterGroup" items="${masterGroups}" >
                        <div class="tab-pane" id="${masterGroup.id}">
                        <div class="row clearfix m-t-20">
                            <div class="col-lg-12 col-md-12">
                                <c:forEach var="sectionGroupEntry" items="${masterGroup.childGroups}">
                                    <c:set var="sectionGroup" value="${sectionGroupEntry.value}"/>
                                    <div class="card inner overflowhidden">
                                        <div class="body">
                                            <c:if test="${sectionGroup.defaultGroup ne 'Y'}">
                                                <fieldset><legend>${sectionGroup.label}</legend></fieldset>
                                                <hr style="border:0; margin-top: -5px;border-bottom: 1rem; border-top:1px solid rgba(0,0,0,.1)"/>
                                            </c:if>
                                            <form method="post" action="/pim/products/${product.productId}" data-method="PUT"
                                                  data-success-message='["Successfully updated the product", "Product Updated"]'
                                                  data-error-message='["Correct the validation error and try again", "Invalid Data"]'>
                                                <c:forEach items="${sectionGroup.childGroups}" var="attributeGroupEntry">
                                                    <c:set var="attributeGroup" value="${attributeGroupEntry.value}" />
                                                    <div class="card inner group overflowhidden">
                                                        <div class="body">
                                                            <fieldset>
                                                                <c:if test="${attributeGroup.defaultGroup ne 'Y'}"><legend>${attributeGroup.label}</legend></c:if>
                                                                <div class="panel panel-default">
                                                                    <div class="panel-body">
                                                                        <div class="row">
                                                                            <div class="col-md-6 col-sm-12">
                                                                                <c:forEach items="${attributeGroup.attributes}" var="attributeEntry">
                                                                                    <c:set var="attribute" value="${attributeEntry.value}"/>
                                                                                    <div class="form-group">
                                                                                        <label for="${attribute.id}">${attribute.name}</label><code class="highlighter-rouge m-l-10"><c:if test="${attribute.required eq 'Y'}">*</c:if></code>
                                                                                        <c:choose>
                                                                                            <c:when test="${attribute.uiType eq 'DROPDOWN'}">
                                                                                                <select id="${attribute.id}" name="${attribute.id}" class="form-control">
                                                                                                    <option value="">Select One</option>
                                                                                                    <c:forEach items="${attribute.options}" var="optionEntry">
                                                                                                        <c:set var="attributeOption" value="${optionEntry.value}"/>
                                                                                                        <option value="${attributeOption.id}" <c:if test="${attributeOption.id eq product.familyAttributes[attribute.id]}">selected</c:if>>${attributeOption.value}</option>
                                                                                                    </c:forEach>
                                                                                                </select>
                                                                                            </c:when>
                                                                                            <c:when test="${attribute.uiType eq 'TEXTAREA'}">
                                                                                                <textarea id="${attribute.id}" class="form-control" name="${attribute.id}">${product.familyAttributes[attribute.id]}</textarea>
                                                                                            </c:when>
                                                                                            <c:when test="${attribute.uiType eq 'CHECKBOX'}">
                                                                                                <br/>${product.familyAttributes[attribute.id]}
                                                                                                <c:forEach items="${attribute.options}" var="attributeOptionEntry" varStatus="s1">
                                                                                                    <c:set var="attributeOption" value="${attributeOptionEntry.value}"/>
                                                                                                    <c:set var="propertyName" value="${attribute.id}_${attributeOption.id}"/>

                                                                                                    <label class="fancy-checkbox">
                                                                                                        <input type="checkbox" class="js-checkbox" name="${attribute.id}" value="Y" <c:if test="${product.familyAttributes[propertyName] eq 'Y'}">checked="checked"</c:if>>
                                                                                                            <%--<c:choose>
                                                                                                                <c:when test="${product.familyAttributes[propertyName] ne 'Y'}">
                                                                                                                    <input type="hidden" name="${propertyName}" disabled="disabled" value="Y" />
                                                                                                                </c:when>
                                                                                                                <c:otherwise>
                                                                                                                    <input type="hidden" name="${propertyName}" value="N" />
                                                                                                                </c:otherwise>
                                                                                                            </c:choose>--%>
                                                                                                        <span>${attributeOption.value}</span>
                                                                                                    </label>
                                                                                                    <c:if test="${not s1.end}">
                                                                                                        <br/>
                                                                                                    </c:if>
                                                                                                </c:forEach>
                                                                                            </c:when>
                                                                                            <c:when test="${attribute.uiType eq 'RADIO_BUTTON'}">
                                                                                                <br/>${product.familyAttributes[attribute.id]}
                                                                                                <c:forEach items="${attribute.options}" var="attributeOptionEntry" varStatus="s1">
                                                                                                    <c:set var="attributeOption" value="${attributeOptionEntry.value}"/>
                                                                                                    <c:set var="propertyName" value="${attribute.id}_${attributeOption.id}"/>
                                                                                                    <%--<label class="fancy-radio"><input name="gender3" value="male" type="radio" checked=""><span><i></i>Male</span></label>--%>
                                                                                                    <label class="fancy-radio">
                                                                                                        <input type="radio" name="${attribute.id}" value="Y" <c:if test="${product.familyAttributes[propertyName] eq 'Y'}">checked="checked"</c:if>>
                                                                                                            <%--<c:choose>
                                                                                                                <c:when test="${product.familyAttributes[propertyName] ne 'Y'}">
                                                                                                                    <input type="hidden" name="${propertyName}" disabled="disabled" value="Y" />
                                                                                                                </c:when>
                                                                                                                <c:otherwise>
                                                                                                                    <input type="hidden" name="${propertyName}" value="N" />
                                                                                                                </c:otherwise>
                                                                                                            </c:choose>--%>
                                                                                                        <span><i></i>${attributeOption.value}</span>
                                                                                                    </label>
                                                                                                    <c:if test="${not s1.end}">
                                                                                                        <br/>
                                                                                                    </c:if>
                                                                                                </c:forEach>
                                                                                            </c:when>
                                                                                            <c:when test="${attribute.uiType eq 'DATE_PICKER'}">
                                                                                                <div class="input-group date" data-date-autoclose="true" data-provide="datepicker">
                                                                                                    <input type="text" name="${attribute.id}" value="${product.familyAttributes[attribute.id]}" class="form-control datepicker">
                                                                                                    <div class="input-group-append">
                                                                                                        <button class="btn btn-secondary" type="button"><i class="fa fa-calendar"></i></button>
                                                                                                    </div>
                                                                                                </div>
                                                                                            </c:when>
                                                                                            <c:otherwise>
                                                                                                <input type="text" id="${attribute.id}" name="${attribute.id}" value="${product.familyAttributes[attribute.id]}" class="form-control"/>
                                                                                            </c:otherwise>
                                                                                        </c:choose>
                                                                                    </div>
                                                                                </c:forEach>
                                                                            </div>
                                                                        </div>
                                                                    </div>
                                                                </div>
                                                            </fieldset>
                                                        </div>
                                                    </div>
                                                </c:forEach>
                                                <div class="form-button-group pull-right m-50">
                                                    <button type="submit" class="btn btn-primary" onclick="$.submitAction(event, this)">Save</button>
                                                    <a href="/pim/products"><button type="button" class="btn btn-danger">Cancel</button></a>
                                                </div>
                                            </form>
                                        </div>
                                    </div>
                                </c:forEach>
                            </div>
                        </div>
                    </div>
                    </c:forEach>
                    <div class="tab-pane" id="productFeatures">
                        <div class="row clearfix m-t-20">
                            <div class="col-lg-12 col-md-12">
                                <c:forEach var="sectionGroupEntry" items="${featuresMasterGroup.childGroups}">
                                    <c:set var="sectionGroup" value="${sectionGroupEntry.value}"/>
                                    <div class="card inner overflowhidden">
                                        <div class="body">
                                            <c:if test="${sectionGroup.defaultGroup ne 'Y'}">
                                                <fieldset><legend>${sectionGroup.label}</legend></fieldset>
                                                <hr style="border:0; margin-top: -5px;border-bottom: 1rem; border-top:1px solid rgba(0,0,0,.1)"/>
                                            </c:if>
                                            <form method="post" action="/pim/products/${product.productId}" data-method="PUT"
                                                  data-success-message='["Successfully updated the product", "Product Updated"]'
                                                  data-error-message='["Correct the validation error and try again", "Invalid Data"]'>
                                                <c:forEach items="${sectionGroup.childGroups}" var="attributeGroupEntry">
                                                    <c:set var="attributeGroup" value="${attributeGroupEntry.value}" />
                                                    <div class="card inner group overflowhidden">
                                                        <div class="body">
                                                            <fieldset>
                                                                <c:if test="${attributeGroup.defaultGroup ne 'Y'}"><legend>${attributeGroup.label}</legend></c:if>
                                                                <div class="panel panel-default">
                                                                    <div class="panel-body">
                                                                        <div class="row">
                                                                            <div class="col-md-6 col-sm-12">
                                                                                <c:forEach items="${attributeGroup.attributes}" var="attributeEntry">
                                                                                    <c:set var="attribute" value="${attributeEntry.value}"/>
                                                                                    <div class="form-group">
                                                                                        <label for="${attribute.id}">${attribute.name}</label><code class="highlighter-rouge m-l-10"><c:if test="${attribute.required eq 'Y'}">*</c:if></code>
                                                                                        <c:choose>
                                                                                            <c:when test="${attribute.uiType eq 'DROPDOWN'}">
                                                                                                <select id="${attribute.id}" name="${attribute.id}" class="form-control">
                                                                                                    <option value="">Select One</option>
                                                                                                    <c:forEach items="${attribute.options}" var="optionEntry">
                                                                                                        <c:set var="attributeOption" value="${optionEntry.value}"/>
                                                                                                        <option value="${attributeOption.id}" <c:if test="${attributeOption.id eq product.familyAttributes[attribute.id]}">selected</c:if>>${attributeOption.value}</option>
                                                                                                    </c:forEach>
                                                                                                </select>
                                                                                            </c:when>
                                                                                            <c:when test="${attribute.uiType eq 'TEXTAREA'}">
                                                                                                <textarea id="${attribute.id}" class="form-control" name="${attribute.id}">${product.familyAttributes[attribute.id]}</textarea>
                                                                                            </c:when>
                                                                                            <c:when test="${attribute.uiType eq 'CHECKBOX'}">
                                                                                                <br/>${product.familyAttributes[attribute.id]}
                                                                                                <c:forEach items="${attribute.options}" var="attributeOptionEntry" varStatus="s1">
                                                                                                    <c:set var="attributeOption" value="${attributeOptionEntry.value}"/>
                                                                                                    <c:set var="propertyName" value="${attribute.id}_${attributeOption.id}"/>

                                                                                                    <label class="fancy-checkbox">
                                                                                                        <input type="checkbox" class="js-checkbox" name="${attribute.id}" value="Y" <c:if test="${product.familyAttributes[propertyName] eq 'Y'}">checked="checked"</c:if>>
                                                                                                            <%--<c:choose>
                                                                                                                <c:when test="${product.familyAttributes[propertyName] ne 'Y'}">
                                                                                                                    <input type="hidden" name="${propertyName}" disabled="disabled" value="Y" />
                                                                                                                </c:when>
                                                                                                                <c:otherwise>
                                                                                                                    <input type="hidden" name="${propertyName}" value="N" />
                                                                                                                </c:otherwise>
                                                                                                            </c:choose>--%>
                                                                                                        <span>${attributeOption.value}</span>
                                                                                                    </label>
                                                                                                    <c:if test="${not s1.end}">
                                                                                                        <br/>
                                                                                                    </c:if>
                                                                                                </c:forEach>
                                                                                            </c:when>
                                                                                            <c:when test="${attribute.uiType eq 'RADIO_BUTTON'}">
                                                                                                <br/>${product.familyAttributes[attribute.id]}
                                                                                                <c:forEach items="${attribute.options}" var="attributeOptionEntry" varStatus="s1">
                                                                                                    <c:set var="attributeOption" value="${attributeOptionEntry.value}"/>
                                                                                                    <c:set var="propertyName" value="${attribute.id}_${attributeOption.id}"/>
                                                                                                    <%--<label class="fancy-radio"><input name="gender3" value="male" type="radio" checked=""><span><i></i>Male</span></label>--%>
                                                                                                    <label class="fancy-radio">
                                                                                                        <input type="radio" name="${attribute.id}" value="Y" <c:if test="${product.familyAttributes[propertyName] eq 'Y'}">checked="checked"</c:if>>
                                                                                                            <%--<c:choose>
                                                                                                                <c:when test="${product.familyAttributes[propertyName] ne 'Y'}">
                                                                                                                    <input type="hidden" name="${propertyName}" disabled="disabled" value="Y" />
                                                                                                                </c:when>
                                                                                                                <c:otherwise>
                                                                                                                    <input type="hidden" name="${propertyName}" value="N" />
                                                                                                                </c:otherwise>
                                                                                                            </c:choose>--%>
                                                                                                        <span><i></i>${attributeOption.value}</span>
                                                                                                    </label>
                                                                                                    <c:if test="${not s1.end}">
                                                                                                        <br/>
                                                                                                    </c:if>
                                                                                                </c:forEach>
                                                                                            </c:when>
                                                                                            <c:when test="${attribute.uiType eq 'DATE_PICKER'}">
                                                                                                <div class="input-group date" data-date-autoclose="true" data-provide="datepicker">
                                                                                                    <input type="text" name="${attribute.id}" value="${product.familyAttributes[attribute.id]}" class="form-control datepicker">
                                                                                                    <div class="input-group-append">
                                                                                                        <button class="btn btn-secondary" type="button"><i class="fa fa-calendar"></i></button>
                                                                                                    </div>
                                                                                                </div>
                                                                                            </c:when>
                                                                                            <c:otherwise>
                                                                                                <input type="text" id="${attribute.id}" name="${attribute.id}" value="${product.familyAttributes[attribute.id]}" class="form-control"/>
                                                                                            </c:otherwise>
                                                                                        </c:choose>
                                                                                    </div>
                                                                                </c:forEach>
                                                                            </div>
                                                                        </div>
                                                                    </div>
                                                                </div>
                                                            </fieldset>
                                                        </div>
                                                    </div>
                                                </c:forEach>
                                                <div class="form-button-group pull-right m-50">
                                                    <button type="submit" class="btn btn-primary" onclick="$.submitAction(event, this)">Save</button>
                                                    <a href="/pim/products"><button type="button" class="btn btn-danger">Cancel</button></a>
                                                </div>
                                            </form>
                                        </div>
                                    </div>
                                </c:forEach>
                            </div>
                        </div>
                    </div>
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
        $('.datepicker').datepicker();

        /*$('form').on('click', '.js-checkbox', function() {
            $(this).parent().find('input').prop('disabled', $(this).prop('checked'));
        });*/
    });
</script>


