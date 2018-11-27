<%--@elvariable id="product" type="com.bigname.pim.api.domain.Product"--%>
<%--@elvariable id="productFamilies" type="java.util.List<com.bigname.pim.api.domain.ProductFamily>"--%>
<%--@elvariable id="masterGroup" type="com.bigname.pim.api.domain.FamilyAttributeGroup"--%>
<%--@elvariable id="detailsMasterGroup" type="com.bigname.pim.api.domain.FamilyAttributeGroup"--%>
<%--@elvariable id="featuresMasterGroup" type="com.bigname.pim.api.domain.FamilyAttributeGroup"--%>
<%--@elvariable id="defaultDetailsAttributeGroup" type="com.bigname.pim.api.domain.FamilyAttributeGroup"--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%--@elvariable id="productVariant" type="com.bigname.pim.api.domain.ProductVariant"--%>
<c:set var="product" value="${productVariant.product}"/>
<c:set var="masterGroups" value="${product.productFamily.addonMasterGroups}"/>
<c:set var="detailsMasterGroup" value="${product.productFamily.detailsMasterGroup}"/>
<c:set var="featuresMasterGroup" value="${product.productFamily.featuresMasterGroup}"/>
<div class="row clearfix">
    <div class="col-lg-12 col-md-12">
        <div class="card">
            <div class="header">
                <h2>${productVariant.productVariantName} <small><code class="highlighter-rouge">${productVariant.productVariantId}</code></small>
                    <small class="pull-right" style="margin-top: -15px;padding-right:10px"><code style="color:#808080">_product id: ${product.id}</code></small>
                </h2>
            </div>
            <div class="body">
                <ul class="nav nav-tabs-new2">
                    <li class="nav-item"><a class="nav-link active show" data-toggle="tab" href="#details">Details</a></li>
                    <li class="nav-item"><a class="nav-link" data-toggle="tab" href="#variantAssets">Variant Assets</a></li>
                    <c:forEach var="masterGroup" items="${masterGroups}">
                        <li class="nav-item"><a class="nav-link" data-toggle="tab" href="#${masterGroup.id}">${masterGroup.name}</a></li>
                    </c:forEach>
                    <li class="nav-item"><a class="nav-link" data-toggle="tab" href="#variantPricing">Pricing</a></li>
                    <li class="nav-item"><a class="nav-link" data-toggle="tab" href="#variantFeatures">Variant Features</a></li>
                </ul>
                <div class="tab-content">
                    <div class="tab-pane show active" id="details">
                        <div class="row clearfix m-t-20">
                            <div class="col-lg-12 col-md-12">

                                <div class="card inner overflowhidden">
                                    <div class="body">
                                        <form method="post" action="/pim/products/${product.productId}/channels/${productVariant.channelId}/variants/${productVariant.productVariantId}" data-method="PUT"
                                              data-success-message='["Successfully updated the product variant", "Product Variant Updated"]'
                                              data-error-message='["Check the error message(s) and try again", "Invalid Data"]'>

                                            <div class="card inner group overflowhidden">
                                                <div class="body">
                                                    <fieldset>
                                                        <div class="panel panel-default">
                                                            <div class="panel-body">
                                                                <div class="row">
                                                                    <div class="col-md-6 col-sm-12">
                                                                        <div class="form-group">
                                                                            <label for="productVariantName">Product Variant Name</label><code class="highlighter-rouge m-l-10">*</code>
                                                                            <input type="text" id="productVariantName" name="productVariantName" value="${productVariant.productVariantName}" class="form-control" required="true"/>
                                                                        </div>
                                                                        <div class="form-group">
                                                                            <label for="productVariantId">Product VariantID</label><code class="highlighter-rouge m-l-10">*</code>
                                                                            <input type="text" id="productVariantId" name="productVariantId" class="form-control" value="${productVariant.productVariantId}" required="true"/>
                                                                        </div>
                                                                        <div class="form-group">
                                                                            <label for="productId">Product ID</label>
                                                                            <input type="text" id="productId" class="form-control" disabled="disabled" value="${product.productId}" required="true"/>
                                                                        </div>
                                                                        <div class="form-group">
                                                                            <label for="productFamilyId">ProductFamily</label>
                                                                            <input type="text" id="productFamilyId" class="form-control" disabled="disabled" value="${product.productFamily.familyName}"/>
                                                                        </div>
                                                                        <c:if test="${not empty detailsMasterGroup}">
                                                                            <c:set var="defaultDetailsAttributeGroup" value="${detailsMasterGroup.childGroups.get('DEFAULT_GROUP').childGroups.get('DEFAULT_GROUP')}"/>
                                                                            <c:if test="${not empty defaultDetailsAttributeGroup}">
                                                                                <c:forEach items="${defaultDetailsAttributeGroup.attributes}" var="attributeEntry">
                                                                                    <c:set var="attribute" value="${attributeEntry.value}"/>
                                                                                    <c:set var="attributeType" value="${attribute.getType(productVariant.channelId)}"/>
                                                                                    <c:set var="disabled" value="${attributeType eq 'COMMON'}"/>
                                                                                    <c:set var="disabledClass" value="${attributeType eq 'COMMON' ? ' js-parent-level' : ''}"/>
                                                                                    <c:set var="attributeValue" value="${productVariant.variantAttributes[attribute.id]}"/>
                                                                                    <c:choose>
                                                                                        <c:when test="${attributeType eq 'COMMON'}">
                                                                                            <c:choose>
                                                                                                <c:when test="${attribute.uiType eq 'YES_NO' or attribute.uiType eq 'CHECKBOX'}">
                                                                                                    <c:set var="disabled" value=' js-disabled'/>
                                                                                                </c:when>
                                                                                                <c:otherwise>
                                                                                                    <c:set var="disabled" value=' disabled="disabled"'/>
                                                                                                </c:otherwise>
                                                                                            </c:choose>
                                                                                            <c:set var="attributeValue" value="${product.scopedFamilyAttributes[productVariant.channelId][attribute.id]}"/>
                                                                                            <c:set var="disabledClass" value=" js-parent-level"/>
                                                                                        </c:when>
                                                                                        <c:when test="${attributeType eq 'AXIS'}">
                                                                                            <c:set var="disabled" value=' disabled="disabled"'/>
                                                                                            <c:set var="disabledClass" value=" js-variant-axis"/>
                                                                                            <c:set var="attributeValue" value="${productVariant.axisAttributes[attribute.id]}"/>
                                                                                        </c:when>
                                                                                    </c:choose>
                                                                                    <c:if test="${attributeType ne 'NOT_APPLICABLE'}">
                                                                                        <div class="form-group">
                                                                                            <c:if test="${attribute.uiType ne 'YES_NO'}">
                                                                                                <label for="${attribute.id}">${attribute.label}</label><code class="highlighter-rouge m-l-10"><c:if test="${attributeType eq 'VARIANT' and attribute.isRequired(productVariant.channelId)}">*</c:if></code>
                                                                                            </c:if>
                                                                                            <c:choose>
                                                                                                <c:when test="${attribute.uiType eq 'DROPDOWN'}">
                                                                                                    <select id="${attribute.id}" name="${attribute.id}" class="form-control${disabledClass}" ${disabled}>
                                                                                                        <option value="">Select One</option>
                                                                                                        <c:forEach items="${attribute.options}" var="optionEntry">
                                                                                                            <c:set var="attributeOption" value="${optionEntry.value}"/>
                                                                                                            <option value="${attributeOption.id}" <c:if test="${attributeOption.id eq attributeValue}">selected</c:if>>${attributeOption.value}</option>
                                                                                                        </c:forEach>
                                                                                                    </select>
                                                                                                </c:when>
                                                                                                <c:when test="${attribute.uiType eq 'TEXTAREA'}">
                                                                                                    <textarea id="${attribute.id}" class="form-control auto-resize${disabledClass}" name="${attribute.id}" ${disabled}>${attributeValue}</textarea>
                                                                                                </c:when>
                                                                                                <c:when test="${attribute.uiType eq 'CHECKBOX'}">
                                                                                                    <br/>
                                                                                                    <c:forEach items="${attribute.options}" var="attributeOptionEntry" varStatus="s1">
                                                                                                        <c:set var="attributeOption" value="${attributeOptionEntry.value}"/>
                                                                                                        <label class="fancy-checkbox">
                                                                                                            <input type="checkbox" class="js-checkbox${disabledClass}" ${disabled} name="${attribute.id}" value="${attributeOption.id}" <c:if test="${attributeValue eq attributeOption.id}">checked="checked"</c:if>>
                                                                                                            <span>${attributeOption.value}</span>
                                                                                                        </label>
                                                                                                        <c:if test="${not s1.end}">
                                                                                                            <br/>
                                                                                                        </c:if>
                                                                                                    </c:forEach>
                                                                                                </c:when>
                                                                                                <c:when test="${attribute.uiType eq 'RADIO_BUTTON'}">
                                                                                                    <br/>
                                                                                                    <c:forEach items="${attribute.options}" var="attributeOptionEntry" varStatus="s1">
                                                                                                        <c:set var="attributeOption" value="${attributeOptionEntry.value}"/>
                                                                                                        <label class="fancy-radio">
                                                                                                            <input type="radio" name="${attribute.id}" ${disabled} value="${attributeOption.id}" <c:if test="${attributeValue eq attributeOption.id}">checked="checked"</c:if>>
                                                                                                            <span><i></i>${attributeOption.value}</span>
                                                                                                        </label>
                                                                                                        <c:if test="${not s1.end}">
                                                                                                            <br/>
                                                                                                        </c:if>
                                                                                                    </c:forEach>
                                                                                                </c:when>
                                                                                                <c:when test="${attribute.uiType eq 'YES_NO'}">
                                                                                                    <br/>
                                                                                                    <label class="fancy-checkbox${disabledClass}">
                                                                                                        <input type="checkbox" class="js-checkbox" name="${attribute.id}" value="Y" <c:if test="${attributeValue eq 'Y'}">checked="checked"</c:if>>
                                                                                                        <span>${attribute.label}</span>
                                                                                                    </label>
                                                                                                </c:when>
                                                                                                <c:when test="${attribute.uiType eq 'DATE_PICKER'}">
                                                                                                    <div class="input-group date" data-date-autoclose="true" data-provide="datepicker">
                                                                                                        <input type="text" id="${attribute.id}" name="${attribute.id}" value="${attributeValue}" ${disabled} class="form-control datepicker${disabledClass}">
                                                                                                        <div class="input-group-append">
                                                                                                            <button class="btn btn-secondary" type="button"><i class="fa fa-calendar"></i></button>
                                                                                                        </div>
                                                                                                    </div>
                                                                                                </c:when>
                                                                                                <c:otherwise>
                                                                                                    <input type="text" id="${attribute.id}" name="${attribute.id}" value="${attributeValue}" ${disabled} class="form-control${disabledClass}"/>
                                                                                                </c:otherwise>
                                                                                            </c:choose>
                                                                                        </div>
                                                                                    </c:if>
                                                                                </c:forEach>
                                                                            </c:if>
                                                                        </c:if>
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
                                                            </div>
                                                        </div>
                                                    </fieldset>
                                                </div>
                                            </div>
                                            <c:forEach items="${detailsMasterGroup.childGroups.get('DEFAULT_GROUP').childGroups}" var="attributeGroupEntry">
                                                <c:set var="attributeGroup" value="${attributeGroupEntry.value}" />
                                                <c:if test="${attributeGroup.defaultGroup ne 'Y' && attributeGroup.isAvailable(productVariant.channelId)}">
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
                                                                                    <c:set var="attributeType" value="${attribute.getType(productVariant.channelId)}"/>
                                                                                    <c:set var="disabled" value=""/>
                                                                                    <c:set var="disabledClass" value=""/>
                                                                                    <c:set var="attributeValue" value="${productVariant.variantAttributes[attribute.id]}"/>
                                                                                    <c:choose>
                                                                                        <c:when test="${attributeType eq 'COMMON'}">
                                                                                            <c:choose>
                                                                                                <c:when test="${attribute.uiType eq 'YES_NO' or attribute.uiType eq 'CHECKBOX'}">
                                                                                                    <c:set var="disabled" value=' js-disabled'/>
                                                                                                </c:when>
                                                                                                <c:otherwise>
                                                                                                    <c:set var="disabled" value=' disabled="disabled"'/>
                                                                                                </c:otherwise>
                                                                                            </c:choose>
                                                                                            <c:set var="attributeValue" value="${product.scopedFamilyAttributes[productVariant.channelId][attribute.id]}"/>
                                                                                            <c:set var="disabledClass" value=" js-parent-level"/>
                                                                                        </c:when>
                                                                                        <c:when test="${attributeType eq 'AXIS'}">
                                                                                            <c:set var="disabled" value=' disabled="disabled"'/>
                                                                                            <c:set var="disabledClass" value=" js-variant-axis"/>
                                                                                            <c:set var="attributeValue" value="${productVariant.axisAttributes[attribute.id]}"/>
                                                                                        </c:when>
                                                                                    </c:choose>

                                                                                    <c:if test="${attributeType ne 'NOT_APPLICABLE'}">
                                                                                        <div class="form-group">
                                                                                            <c:if test="${attribute.uiType ne 'YES_NO' and attributeGroup.label ne attribute.label}">
                                                                                                <label for="${attribute.id}">${attribute.label}</label><code class="highlighter-rouge m-l-10"><c:if test="${attributeType eq 'VARIANT' and attribute.isRequired(productVariant.channelId)}">*</c:if></code>
                                                                                            </c:if>
                                                                                            <c:choose>
                                                                                                <c:when test="${attribute.uiType eq 'DROPDOWN'}">
                                                                                                    <select id="${attribute.id}" name="${attribute.id}" class="form-control${disabledClass}" ${disabled}>
                                                                                                        <option value="">Select One</option>
                                                                                                        <c:forEach items="${attribute.options}" var="optionEntry">
                                                                                                            <c:set var="attributeOption" value="${optionEntry.value}"/>
                                                                                                            <option value="${attributeOption.id}" <c:if test="${attributeOption.id eq attributeValue}">selected</c:if>>${attributeOption.value}</option>
                                                                                                        </c:forEach>
                                                                                                    </select>
                                                                                                </c:when>
                                                                                                <c:when test="${attribute.uiType eq 'TEXTAREA'}">
                                                                                                    <textarea id="${attribute.id}" class="form-control auto-resize${disabledClass}" name="${attribute.id}" ${disabled}>${attributeValue}</textarea>
                                                                                                </c:when>
                                                                                                <c:when test="${attribute.uiType eq 'CHECKBOX'}">
                                                                                                    <br/>
                                                                                                    <c:forEach items="${attribute.options}" var="attributeOptionEntry" varStatus="s1">
                                                                                                        <c:set var="attributeOption" value="${attributeOptionEntry.value}"/>
                                                                                                        <label class="fancy-checkbox${disabled}${disabledClass}">
                                                                                                            <input type="checkbox" class="js-checkbox"  name="${attribute.id}" value="${attributeOption.id}" <c:if test="${attributeValue eq attributeOption.id}">checked="checked"</c:if>>
                                                                                                            <span>${attributeOption.value}</span>
                                                                                                        </label>
                                                                                                        <c:if test="${not s1.end}">
                                                                                                            <br/>
                                                                                                        </c:if>
                                                                                                    </c:forEach>
                                                                                                </c:when>
                                                                                                <c:when test="${attribute.uiType eq 'RADIO_BUTTON'}">
                                                                                                    <br/>
                                                                                                    <c:forEach items="${attribute.options}" var="attributeOptionEntry" varStatus="s1">
                                                                                                        <c:set var="attributeOption" value="${attributeOptionEntry.value}"/>
                                                                                                        <label class="fancy-radio">
                                                                                                            <input type="radio" name="${attribute.id}" ${disabled} value="${attributeOption.id}" <c:if test="${attributeValue eq attributeOption.id}">checked="checked"</c:if>>
                                                                                                            <span><i></i>${attributeOption.value}</span>
                                                                                                        </label>
                                                                                                        <c:if test="${not s1.end}">
                                                                                                            <br/>
                                                                                                        </c:if>
                                                                                                    </c:forEach>
                                                                                                </c:when>
                                                                                                <c:when test="${attribute.uiType eq 'YES_NO'}">
                                                                                                    <br/>
                                                                                                    <label class="fancy-checkbox ${disabled}${disabledClass}">
                                                                                                        <input type="checkbox" class="js-checkbox" name="${attribute.id}" value="Y" <c:if test="${attributeValue eq 'Y'}">checked="checked"</c:if>>
                                                                                                        <span>${attribute.label}</span>
                                                                                                    </label>
                                                                                                </c:when>
                                                                                                <c:when test="${attribute.uiType eq 'DATE_PICKER'}">
                                                                                                    <div class="input-group date" data-date-autoclose="true" data-provide="datepicker">
                                                                                                        <input type="text" id="${attribute.id}" name="${attribute.id}" value="${attributeValue}" ${disabled} class="form-control datepicker${disabledClass}">
                                                                                                        <div class="input-group-append">
                                                                                                            <button class="btn btn-secondary" type="button"><i class="fa fa-calendar"></i></button>
                                                                                                        </div>
                                                                                                    </div>
                                                                                                </c:when>
                                                                                                <c:otherwise>
                                                                                                    <input type="text" id="${attribute.id}" name="${attribute.id}" value="${attributeValue}" ${disabled} class="form-control${disabledClass}"/>
                                                                                                </c:otherwise>
                                                                                            </c:choose>
                                                                                        </div>
                                                                                    </c:if>
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
                                            <div class="form-button-group pull-right">
                                                <input type="hidden" name="group" value="DETAILS"/>
                                                <button type="submit" class="btn btn-primary" onclick="$.submitAction(event, this)">Save</button>
                                                <a href="/pim/products/channels/{${productVariant.channelId}/#productVariants"><button type="button" class="btn btn-danger">Cancel</button></a>
                                            </div>
                                        </form>
                                    </div>
                                </div>

                            </div>
                        </div>
                    </div>
                    <div class="tab-pane" id="variantAssets">
                        <div class="row clearfix">
                            <div class="col-lg-12">
                                <div class="card inner overflowhidden">
                                    <div class="header" style="padding-bottom: 30px">
                                        <div class="pull-right">
                                            <button id="js-add-asset" type="button" class="btn btn-success"><i class="fa fa-plus"></i> <span class="p-l-5">Add an Asset</span></button>
                                        </div>
                                    </div>
                                    <div class="body">
                                        <div id="aniimated-thumbnials" class="list-unstyled row clearfix">
                                            <div class="col-lg-4 col-md-6 col-sm-12 m-b-30">
                                                <div class="digital-asset-container front card overflowhidden">
                                                    <div class="header">
                                                        <h2>10-RW-front<small style="position:absolute;top:15px;left:3px"><code class="highlighter-rouge">DEFAULT</code></small></h2>
                                                        <ul class="header-dropdown">
                                                            <li><span class="tab_btn active bg-success">1</span></li>
                                                            <li class="dropdown">
                                                                <a href="javascript:void(0);" class="dropdown-toggle" data-toggle="dropdown" role="button" aria-haspopup="true" aria-expanded="false"></a>
                                                                <ul class="dropdown-menu dropdown-menu-right">
                                                                    <li><a href="javascript:void(0);" onclick="$(this).closest('.digital-asset-container').flip(true)">Edit</a></li>
                                                                    <li><a href="javascript:void(0);">Delete</a></li>
                                                                    <li><a href="javascript:void(0);">Set as Default</a></li>
                                                                </ul>
                                                            </li>
                                                        </ul>
                                                    </div>
                                                    <div class="body">
                                                        <div class="digital-asset">
                                                            <span class="asset-type" title="video" style=""><i class="fa fa-file-video-o"></i></span>
                                                            <a class="js-asset" href="https://actionenvelope.scene7.com/is/image/ActionEnvelope/4260-15?hei=413&wid=510&fmt=jpeg&qlt=75&bgc=f1f1f1">
                                                                <img class="img-fluid img-thumbnail" src="https://actionenvelope.scene7.com/is/image/ActionEnvelope/4260-15?hei=413&wid=510&fmt=jpeg&qlt=75&bgc=f1f1f1" alt="">
                                                            </a>
                                                        </div>
                                                    </div>
                                                    <div class="footer">
                                                        <ul class="stats">
                                                            <li><a href="javascript:void(0);" data-toggle="popover" data-trigger="hover" data-placement="left" title="" data-content="And here's some amazing content. It's very engaging. Right?" data-original-title="Asset Description" class="text-danger icon-bubbles">Description</a></li>
                                                        </ul>
                                                    </div>
                                                </div>
                                            </div>
                                            <div class="col-lg-4 col-md-6 col-sm-12 m-b-30">
                                                <div class="digital-asset-container card overflowhidden">
                                                    <div class="header">
                                                        <h2>10-RW-front</h2>
                                                        <ul class="header-dropdown">
                                                            <li><span class="tab_btn active">2</span></li>
                                                            <li class="dropdown">
                                                                <a href="javascript:void(0);" class="dropdown-toggle" data-toggle="dropdown" role="button" aria-haspopup="true" aria-expanded="false"></a>
                                                                <ul class="dropdown-menu dropdown-menu-right">
                                                                    <li><a href="javascript:void(0);">Edit</a></li>
                                                                    <li><a href="javascript:void(0);">Delete</a></li>
                                                                    <li><a href="javascript:void(0);">Set as Default</a></li>
                                                                </ul>
                                                            </li>
                                                        </ul>
                                                    </div>
                                                    <div class="body">
                                                        <div class="digital-asset">
                                                            <span class="asset-type" title="video" style=""><i class="fa fa-file-video-o"></i></span>
                                                            <a class="js-asset" href="https://actionenvelope.scene7.com/is/image/ActionEnvelope/4260-15?hei=413&wid=510&fmt=jpeg&qlt=75&bgc=f1f1f1">
                                                                <img class="img-fluid img-thumbnail" src="https://actionenvelope.scene7.com/is/image/ActionEnvelope/4260-15?hei=413&wid=510&fmt=jpeg&qlt=75&bgc=f1f1f1" alt="">
                                                            </a>
                                                        </div>
                                                    </div>
                                                    <div class="footer">
                                                        <ul class="stats">
                                                            <li><a href="javascript:void(0);" data-toggle="popover" data-trigger="hover" data-placement="left" title="" data-content="And here's some amazing content. It's very engaging. Right?" data-original-title="Asset Description" class="text-danger icon-bubbles">Description</a></li>
                                                        </ul>
                                                    </div>
                                                </div>
                                            </div>
                                            <div class="col-lg-4 col-md-6 col-sm-12 m-b-30">
                                                <div class="digital-asset-container card overflowhidden">
                                                    <div class="header">
                                                        <h2>10-RW-front</h2>
                                                        <ul class="header-dropdown">
                                                            <li><span class="tab_btn active">3</span></li>
                                                            <li class="dropdown">
                                                                <a href="javascript:void(0);" class="dropdown-toggle" data-toggle="dropdown" role="button" aria-haspopup="true" aria-expanded="false"></a>
                                                                <ul class="dropdown-menu dropdown-menu-right">
                                                                    <li><a href="javascript:void(0);">Edit</a></li>
                                                                    <li><a href="javascript:void(0);">Delete</a></li>
                                                                    <li><a href="javascript:void(0);">Set as Default</a></li>
                                                                </ul>
                                                            </li>
                                                        </ul>
                                                    </div>
                                                    <div class="body">
                                                        <div class="digital-asset">
                                                            <span class="asset-type" title="video" style=""><i class="fa fa-file-video-o"></i></span>
                                                            <a class="js-asset" href="https://actionenvelope.scene7.com/is/image/ActionEnvelope/4260-15?hei=413&wid=510&fmt=jpeg&qlt=75&bgc=f1f1f1">
                                                                <img class="img-fluid img-thumbnail" src="https://actionenvelope.scene7.com/is/image/ActionEnvelope/4260-15?hei=413&wid=510&fmt=jpeg&qlt=75&bgc=f1f1f1" alt="">
                                                            </a>
                                                        </div>
                                                    </div>
                                                    <div class="footer">
                                                        <ul class="stats">
                                                            <li><a href="javascript:void(0);" data-toggle="popover" data-trigger="hover" data-placement="left" title="" data-content="And here's some amazing content. It's very engaging. Right?" data-original-title="Asset Description" class="text-danger icon-bubbles">Description</a></li>
                                                        </ul>
                                                    </div>
                                                </div>
                                            </div>
                                            <div class="col-lg-4 col-md-6 col-sm-12 m-b-30">
                                                <div class="digital-asset-container card overflowhidden">
                                                    <div class="header">
                                                        <h2>10-RW-front</h2>
                                                        <ul class="header-dropdown">
                                                            <li><span class="tab_btn active">4</span></li>
                                                            <li class="dropdown">
                                                                <a href="javascript:void(0);" class="dropdown-toggle" data-toggle="dropdown" role="button" aria-haspopup="true" aria-expanded="false"></a>
                                                                <ul class="dropdown-menu dropdown-menu-right">
                                                                    <li><a href="javascript:void(0);">Edit</a></li>
                                                                    <li><a href="javascript:void(0);">Delete</a></li>
                                                                    <li><a href="javascript:void(0);">Set as Default</a></li>
                                                                </ul>
                                                            </li>
                                                        </ul>
                                                    </div>
                                                    <div class="body">
                                                        <div class="digital-asset">
                                                            <span class="asset-type" title="video" style=""><i class="fa fa-file-video-o"></i></span>
                                                            <a class="js-asset" href="https://actionenvelope.scene7.com/is/image/ActionEnvelope/4260-15?hei=413&wid=510&fmt=jpeg&qlt=75&bgc=f1f1f1">
                                                                <img class="img-fluid img-thumbnail" src="https://actionenvelope.scene7.com/is/image/ActionEnvelope/4260-15?hei=413&wid=510&fmt=jpeg&qlt=75&bgc=f1f1f1" alt="">
                                                            </a>
                                                        </div>
                                                    </div>
                                                    <div class="footer">
                                                        <ul class="stats">
                                                            <li><a href="javascript:void(0);" data-toggle="popover" data-trigger="hover" data-placement="left" title="" data-content="And here's some amazing content. It's very engaging. Right?" data-original-title="Asset Description" class="text-danger icon-bubbles">Description</a></li>
                                                        </ul>
                                                    </div>
                                                </div>
                                            </div>
                                            <div class="col-lg-4 col-md-6 col-sm-12 m-b-30">
                                                <div class="digital-asset-container card overflowhidden">
                                                    <div class="header">
                                                        <h2>10-RW-front</h2>
                                                        <ul class="header-dropdown">
                                                            <li><span class="tab_btn active">5</span></li>
                                                            <li class="dropdown">
                                                                <a href="javascript:void(0);" class="dropdown-toggle" data-toggle="dropdown" role="button" aria-haspopup="true" aria-expanded="false"></a>
                                                                <ul class="dropdown-menu dropdown-menu-right">
                                                                    <li><a href="javascript:void(0);">Edit</a></li>
                                                                    <li><a href="javascript:void(0);">Delete</a></li>
                                                                    <li><a href="javascript:void(0);">Set as Default</a></li>
                                                                </ul>
                                                            </li>
                                                        </ul>
                                                    </div>
                                                    <div class="body">
                                                        <div class="digital-asset">
                                                            <span class="asset-type" title="video" style=""><i class="fa fa-file-video-o"></i></span>
                                                            <a class="js-asset" href="https://actionenvelope.scene7.com/is/image/ActionEnvelope/4260-15?hei=413&wid=510&fmt=jpeg&qlt=75&bgc=f1f1f1">
                                                                <img class="img-fluid img-thumbnail" src="https://actionenvelope.scene7.com/is/image/ActionEnvelope/4260-15?hei=413&wid=510&fmt=jpeg&qlt=75&bgc=f1f1f1" alt="">
                                                            </a>
                                                        </div>
                                                    </div>
                                                    <div class="footer">
                                                        <ul class="stats">
                                                            <li><a href="javascript:void(0);" data-toggle="popover" data-trigger="hover" data-placement="left" title="" data-content="And here's some amazing content. It's very engaging. Right?" data-original-title="Asset Description" class="text-danger icon-bubbles">Description</a></li>
                                                        </ul>
                                                    </div>
                                                </div>
                                            </div>
                                            <div class="col-lg-4 col-md-6 col-sm-12 m-b-30">
                                                <div class="digital-asset-container card overflowhidden">
                                                    <div class="header">
                                                        <h2>10-RW-front</h2>
                                                        <ul class="header-dropdown">
                                                            <li><span class="tab_btn active">6</span></li>
                                                            <li class="dropdown">
                                                                <a href="javascript:void(0);" class="dropdown-toggle" data-toggle="dropdown" role="button" aria-haspopup="true" aria-expanded="false"></a>
                                                                <ul class="dropdown-menu dropdown-menu-right">
                                                                    <li><a href="javascript:void(0);">Edit</a></li>
                                                                    <li><a href="javascript:void(0);">Delete</a></li>
                                                                    <li><a href="javascript:void(0);">Set as Default</a></li>
                                                                </ul>
                                                            </li>
                                                        </ul>
                                                    </div>
                                                    <div class="body">
                                                        <div class="digital-asset">
                                                            <span class="asset-type" title="video" style=""><i class="fa fa-file-video-o"></i></span>
                                                            <a class="js-asset" href="https://actionenvelope.scene7.com/is/image/ActionEnvelope/4260-15?hei=413&wid=510&fmt=jpeg&qlt=75&bgc=f1f1f1">
                                                                <img class="img-fluid img-thumbnail" src="https://actionenvelope.scene7.com/is/image/ActionEnvelope/4260-15?hei=413&wid=510&fmt=jpeg&qlt=75&bgc=f1f1f1" alt="">
                                                            </a>
                                                        </div>
                                                    </div>
                                                    <div class="footer">
                                                        <ul class="stats">
                                                            <li><a href="javascript:void(0);" data-toggle="popover" data-trigger="hover" data-placement="left" title="" data-content="And here's some amazing content. It's very engaging. Right?" data-original-title="Asset Description" class="text-danger icon-bubbles">Description</a></li>
                                                        </ul>
                                                    </div>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                    <c:forEach var="masterGroup" items="${masterGroups}" >
                        <c:choose>
                            <c:when test="${masterGroup.id ne 'PRICING'}">
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
                                                        <form method="post" action="/pim/products/${product.productId}/channels/${productVariant.channelId}/variants/${productVariant.productVariantId}" data-method="PUT"
                                                              data-success-message='["Successfully updated the product", "Product Updated"]'
                                                              data-error-message='["Check the error message(s) and try again", "Invalid Data"]'>
                                                            <c:forEach items="${sectionGroup.childGroups}" var="attributeGroupEntry">
                                                                <c:set var="attributeGroup" value="${attributeGroupEntry.value}" />
                                                                <c:if test="${attributeGroup.isAvailable(productVariant.channelId)}">
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
                                                                                                    <c:set var="attributeType" value="${attribute.getType(productVariant.channelId)}"/>
                                                                                                    <c:set var="disabled" value=""/>
                                                                                                    <c:set var="disabledClass" value=""/>
                                                                                                    <c:set var="attributeValue" value="${productVariant.variantAttributes[attribute.id]}"/>
                                                                                                    <c:choose>
                                                                                                        <c:when test="${attributeType eq 'COMMON'}">
                                                                                                            <c:choose>
                                                                                                                <c:when test="${attribute.uiType eq 'YES_NO' or attribute.uiType eq 'CHECKBOX'}">
                                                                                                                    <c:set var="disabled" value=' js-disabled'/>
                                                                                                                </c:when>
                                                                                                                <c:otherwise>
                                                                                                                    <c:set var="disabled" value=' disabled="disabled"'/>
                                                                                                                </c:otherwise>
                                                                                                            </c:choose>
                                                                                                            <c:set var="attributeValue" value="${product.scopedFamilyAttributes[productVariant.channelId][attribute.id]}"/>
                                                                                                            <c:set var="disabledClass" value=" js-parent-level"/>
                                                                                                        </c:when>
                                                                                                        <c:when test="${attributeType eq 'AXIS'}">
                                                                                                            <c:set var="disabled" value=' disabled="disabled"'/>
                                                                                                            <c:set var="disabledClass" value=" js-variant-axis"/>
                                                                                                            <c:set var="attributeValue" value="${productVariant.axisAttributes[attribute.id]}"/>
                                                                                                        </c:when>
                                                                                                    </c:choose>

                                                                                                    <c:if test="${attributeType ne 'NOT_APPLICABLE'}">
                                                                                                        <div class="form-group">
                                                                                                            <c:if test="${attribute.uiType ne 'YES_NO' and attributeGroup.label ne attribute.label}">
                                                                                                                <label for="${attribute.id}">${attribute.label}</label><code class="highlighter-rouge m-l-10"><c:if test="${attributeType eq 'VARIANT' and attribute.isRequired(productVariant.channelId)}">*</c:if></code>
                                                                                                            </c:if>
                                                                                                            <c:choose>
                                                                                                                <c:when test="${attribute.uiType eq 'DROPDOWN'}">
                                                                                                                    <select id="${attribute.id}" name="${attribute.id}" class="form-control${disabledClass}" ${disabled}>
                                                                                                                        <option value="">Select One</option>
                                                                                                                        <c:forEach items="${attribute.options}" var="optionEntry">
                                                                                                                            <c:set var="attributeOption" value="${optionEntry.value}"/>
                                                                                                                            <option value="${attributeOption.id}" <c:if test="${attributeOption.id eq attributeValue}">selected</c:if>>${attributeOption.value}</option>
                                                                                                                        </c:forEach>
                                                                                                                    </select>
                                                                                                                </c:when>
                                                                                                                <c:when test="${attribute.uiType eq 'TEXTAREA'}">
                                                                                                                    <textarea id="${attribute.id}" class="form-control auto-resize${disabledClass}" name="${attribute.id}" ${disabled}>${attributeValue}</textarea>
                                                                                                                </c:when>
                                                                                                                <c:when test="${attribute.uiType eq 'CHECKBOX'}">
                                                                                                                    <br/>
                                                                                                                    <c:forEach items="${attribute.options}" var="attributeOptionEntry" varStatus="s1">
                                                                                                                        <c:set var="attributeOption" value="${attributeOptionEntry.value}"/>
                                                                                                                        <label class="fancy-checkbox${disabled}${disabledClass}">
                                                                                                                            <input type="checkbox" class="js-checkbox"  name="${attribute.id}" value="${attributeOption.id}" <c:if test="${attributeValue eq attributeOption.id}">checked="checked"</c:if>>
                                                                                                                            <span>${attributeOption.value}</span>
                                                                                                                        </label>
                                                                                                                        <c:if test="${not s1.end}">
                                                                                                                            <br/>
                                                                                                                        </c:if>
                                                                                                                    </c:forEach>
                                                                                                                </c:when>
                                                                                                                <c:when test="${attribute.uiType eq 'RADIO_BUTTON'}">
                                                                                                                    <br/>
                                                                                                                    <c:forEach items="${attribute.options}" var="attributeOptionEntry" varStatus="s1">
                                                                                                                        <c:set var="attributeOption" value="${attributeOptionEntry.value}"/>
                                                                                                                        <label class="fancy-radio">
                                                                                                                            <input type="radio" name="${attribute.id}" ${disabled} value="${attributeOption.id}" <c:if test="${attributeValue eq attributeOption.id}">checked="checked"</c:if>>
                                                                                                                            <span><i></i>${attributeOption.value}</span>
                                                                                                                        </label>
                                                                                                                        <c:if test="${not s1.end}">
                                                                                                                            <br/>
                                                                                                                        </c:if>
                                                                                                                    </c:forEach>
                                                                                                                </c:when>
                                                                                                                <c:when test="${attribute.uiType eq 'YES_NO'}">
                                                                                                                    <br/>
                                                                                                                    <label class="fancy-checkbox ${disabled}${disabledClass}">
                                                                                                                        <input type="checkbox" class="js-checkbox" name="${attribute.id}" value="Y" <c:if test="${attributeValue eq 'Y'}">checked="checked"</c:if>>
                                                                                                                        <span>${attribute.label}</span>
                                                                                                                    </label>
                                                                                                                </c:when>
                                                                                                                <c:when test="${attribute.uiType eq 'DATE_PICKER'}">
                                                                                                                    <div class="input-group date" data-date-autoclose="true" data-provide="datepicker">
                                                                                                                        <input type="text" id="${attribute.id}" name="${attribute.id}" value="${attributeValue}" ${disabled} class="form-control datepicker${disabledClass}">
                                                                                                                        <div class="input-group-append">
                                                                                                                            <button class="btn btn-secondary" type="button"><i class="fa fa-calendar"></i></button>
                                                                                                                        </div>
                                                                                                                    </div>
                                                                                                                </c:when>
                                                                                                                <c:otherwise>
                                                                                                                    <input type="text" id="${attribute.id}" name="${attribute.id}" value="${attributeValue}" ${disabled} class="form-control${disabledClass}"/>
                                                                                                                </c:otherwise>
                                                                                                            </c:choose>
                                                                                                        </div>
                                                                                                    </c:if>
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
                                                            <div class="form-button-group pull-right">
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
                            </c:when>
                            <c:otherwise>

                            </c:otherwise>
                        </c:choose>

                    </c:forEach>
                    <div class="tab-pane" id="variantPricing">
                        <div class="row clearfix">
                            <div class="col-lg-12 col-md-12">
                                <div class="row p-b-25">
                                    <div class="col-lg-12 col-md-12">
                                        <div class="pull-right">
                                            <button id="js-add-pricing-details" type="button" class="btn btn-sm btn-success"><i class="fa fa-plus"></i> <span class="p-l-5">Add Pricing</span></button>
                                        </div>
                                    </div>
                                </div>
                                <div class="table-responsive">
                                    <table  class="table table-hover dataTable table-custom" style="width: 100% !important;" id="paginatedPricingTable">
                                        <thead class="thead-dark">

                                        </thead>
                                    </table>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="tab-pane" id="variantFeatures">
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
                                            <form method="post" action="/pim/products/${product.productId}/channels/${productVariant.channelId}/variants/${productVariant.productVariantId}" data-method="PUT"
                                                  data-success-message='["Successfully updated the product", "Product Updated"]'
                                                  data-error-message='["Check the error message(s) and try again", "Invalid Data"]'>
                                                <c:forEach items="${sectionGroup.childGroups}" var="attributeGroupEntry">
                                                    <c:set var="attributeGroup" value="${attributeGroupEntry.value}" />
                                                    <c:if test="${attributeGroup.isAvailable(productVariant.channelId)}">
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
                                                                                        <c:set var="attributeType" value="${attribute.getType(productVariant.channelId)}"/>
                                                                                        <c:set var="disabled" value=""/>
                                                                                        <c:set var="disabledClass" value=""/>
                                                                                        <c:set var="attributeValue" value="${productVariant.variantAttributes[attribute.id]}"/>
                                                                                        <c:choose>
                                                                                            <c:when test="${attributeType eq 'COMMON'}">
                                                                                                <c:choose>
                                                                                                    <c:when test="${attribute.uiType eq 'YES_NO' or attribute.uiType eq 'CHECKBOX'}">
                                                                                                        <c:set var="disabled" value=' js-disabled'/>
                                                                                                    </c:when>
                                                                                                    <c:otherwise>
                                                                                                        <c:set var="disabled" value=' disabled="disabled"'/>
                                                                                                    </c:otherwise>
                                                                                                </c:choose>
                                                                                                <c:set var="attributeValue" value="${product.scopedFamilyAttributes[productVariant.channelId][attribute.id]}"/>
                                                                                                <c:set var="disabledClass" value=" js-parent-level"/>
                                                                                            </c:when>
                                                                                            <c:when test="${attributeType eq 'AXIS'}">
                                                                                                <c:set var="disabled" value=' disabled="disabled"'/>
                                                                                                <c:set var="disabledClass" value=" js-variant-axis"/>
                                                                                                <c:set var="attributeValue" value="${productVariant.axisAttributes[attribute.id]}"/>
                                                                                            </c:when>
                                                                                        </c:choose>

                                                                                        <c:if test="${attributeType ne 'NOT_APPLICABLE'}">
                                                                                            <div class="form-group">
                                                                                                <c:if test="${attribute.uiType ne 'YES_NO' and attributeGroup.label ne attribute.label}">
                                                                                                    <label for="${attribute.id}">${attribute.label}</label><code class="highlighter-rouge m-l-10"><c:if test="${attributeType eq 'VARIANT' and attribute.isRequired(productVariant.channelId)}">*</c:if></code>
                                                                                                </c:if>
                                                                                                <c:choose>
                                                                                                    <c:when test="${attribute.uiType eq 'DROPDOWN'}">
                                                                                                        <select id="${attribute.id}" name="${attribute.id}" class="form-control${disabledClass}" ${disabled}>
                                                                                                            <option value="">Select One</option>
                                                                                                            <c:forEach items="${attribute.options}" var="optionEntry">
                                                                                                                <c:set var="attributeOption" value="${optionEntry.value}"/>
                                                                                                                <option value="${attributeOption.id}" <c:if test="${attributeOption.id eq attributeValue}">selected</c:if>>${attributeOption.value}</option>
                                                                                                            </c:forEach>
                                                                                                        </select>
                                                                                                    </c:when>
                                                                                                    <c:when test="${attribute.uiType eq 'TEXTAREA'}">
                                                                                                        <textarea id="${attribute.id}" class="form-control auto-resize${disabledClass}" name="${attribute.id}" ${disabled}>${attributeValue}</textarea>
                                                                                                    </c:when>
                                                                                                    <c:when test="${attribute.uiType eq 'CHECKBOX'}">
                                                                                                        <br/>
                                                                                                        <c:forEach items="${attribute.options}" var="attributeOptionEntry" varStatus="s1">
                                                                                                            <c:set var="attributeOption" value="${attributeOptionEntry.value}"/>
                                                                                                            <label class="fancy-checkbox${disabled}${disabledClass}">
                                                                                                                <input type="checkbox" class="js-checkbox"  name="${attribute.id}" value="${attributeOption.id}" <c:if test="${attributeValue eq attributeOption.id}">checked="checked"</c:if>>
                                                                                                                <span>${attributeOption.value}</span>
                                                                                                            </label>
                                                                                                            <c:if test="${not s1.end}">
                                                                                                                <br/>
                                                                                                            </c:if>
                                                                                                        </c:forEach>
                                                                                                    </c:when>
                                                                                                    <c:when test="${attribute.uiType eq 'RADIO_BUTTON'}">
                                                                                                        <br/>
                                                                                                        <c:forEach items="${attribute.options}" var="attributeOptionEntry" varStatus="s1">
                                                                                                            <c:set var="attributeOption" value="${attributeOptionEntry.value}"/>
                                                                                                            <label class="fancy-radio">
                                                                                                                <input type="radio" name="${attribute.id}" ${disabled} value="${attributeOption.id}" <c:if test="${attributeValue eq attributeOption.id}">checked="checked"</c:if>>
                                                                                                                <span><i></i>${attributeOption.value}</span>
                                                                                                            </label>
                                                                                                            <c:if test="${not s1.end}">
                                                                                                                <br/>
                                                                                                            </c:if>
                                                                                                        </c:forEach>
                                                                                                    </c:when>
                                                                                                    <c:when test="${attribute.uiType eq 'YES_NO'}">
                                                                                                        <br/>
                                                                                                        <label class="fancy-checkbox ${disabled}${disabledClass}">
                                                                                                            <input type="checkbox" class="js-checkbox" name="${attribute.id}" value="Y" <c:if test="${attributeValue eq 'Y'}">checked="checked"</c:if>>
                                                                                                            <span>${attribute.label}</span>
                                                                                                        </label>
                                                                                                    </c:when>
                                                                                                    <c:when test="${attribute.uiType eq 'DATE_PICKER'}">
                                                                                                        <div class="input-group date" data-date-autoclose="true" data-provide="datepicker">
                                                                                                            <input type="text" id="${attribute.id}" name="${attribute.id}" value="${attributeValue}" ${disabled} class="form-control datepicker${disabledClass}">
                                                                                                            <div class="input-group-append">
                                                                                                                <button class="btn btn-secondary" type="button"><i class="fa fa-calendar"></i></button>
                                                                                                            </div>
                                                                                                        </div>
                                                                                                    </c:when>
                                                                                                    <c:otherwise>
                                                                                                        <input type="text" id="${attribute.id}" name="${attribute.id}" value="${attributeValue}" ${disabled} class="form-control${disabledClass}"/>
                                                                                                    </c:otherwise>
                                                                                                </c:choose>
                                                                                            </div>
                                                                                        </c:if>
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
                                                <div class="form-button-group pull-right">
                                                    <input type="hidden" name="group" value="FEATURES"/>
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
                </div>
            </div>
        </div>
    </div>
</div>
<img src="/assets/img/tiny.png" onload="$.initPage({productId : '${productVariant.product.productId}', productVariantId : '${productVariant.productVariantId}', channelId : '${productVariant.channelId}'});$.getScript('/assets/js/pages/ui/product/productVariant.js');"/>

<script>
    var cols = JSON.parse('${pricingGridColumns}');
    cols[cols.length] = {  data : 'actions' , title : 'Actions', orderable: false };
    $( document ).ready(function() {
        $.initDataTable({
            selector: '#paginatedPricingTable',
            name: 'variantPricing',
            type: 'TYPE_4',
            url: $.getURL('/pim/products/{productId}/channels/{channelId}/variants/{productVariantId}/pricing'),
            columns: cols
        });
//        $('#paginatedTable').dataTable().fnSetFilteringEnterPress();
    });
</script>
