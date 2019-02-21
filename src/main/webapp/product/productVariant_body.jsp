<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<c:set var="product" value="${productVariant.product}"/>
<c:set var="masterGroups" value="${product.productFamily.addonMasterGroups}"/>
<c:set var="detailsMasterGroup" value="${product.productFamily.detailsMasterGroup}"/>
<c:set var="featuresMasterGroup" value="${product.productFamily.featuresMasterGroup}"/>
<style>.ms-container .ms-selection {float: none !important;}.ms-container .ms-selectable {float: right !important;}</style>
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
                    <li class="nav-item"><a class="nav-link" data-toggle="tab" href="#variantAssets">Digital Assets</a></li>
                    <c:forEach var="masterGroup" items="${masterGroups}">
                        <li class="nav-item"><a class="nav-link" data-toggle="tab" href="#${masterGroup.id}">${masterGroup.name}</a></li>
                    </c:forEach>
                    <li class="nav-item"><a class="nav-link js-variantPricing-tab" data-toggle="tab" href="#variantPricing">Pricing</a></li>
                    <li class="nav-item"><a class="nav-link" data-toggle="tab" href="#variantFeatures">Features</a></li>
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
                                                                            <label for="productVariantName">Child Product Name</label><code class="highlighter-rouge m-l-10">*</code>
                                                                            <input type="text" id="productVariantName" name="productVariantName" value="${productVariant.productVariantName}" class="form-control" required="true"/>
                                                                        </div>
                                                                        <div class="form-group">
                                                                            <label for="productVariantId">Child Product ID</label><code class="highlighter-rouge m-l-10">*</code>
                                                                            <input type="text" id="productVariantId" name="productVariantId" class="form-control" value="${productVariant.productVariantId}" required="true"/>
                                                                        </div>
                                                                        <div class="form-group">
                                                                            <label for="productId">Parent Product ID</label>
                                                                            <input type="text" id="productId" class="form-control" disabled="disabled" value="${product.productId}" required="true"/>
                                                                        </div>
                                                                        <div class="form-group">
                                                                            <label for="productFamilyId">Product Type</label>
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
                                                                            <label for="active" class="fancy-checkbox">
                                                                                <input type="checkbox" id="active" name="active" value="Y"
                                                                                       <c:if test="${productVariant.active eq 'Y'}">checked="checked"</c:if>>
                                                                                <span>Active</span>
                                                                            </label>
                                                                            <label for="discontinued" class="fancy-checkbox">
                                                                                <input type="checkbox" id="discontinued" name="discontinued" value="Y"
                                                                                       <c:if test="${productVariant.discontinued eq 'Y'}">checked="checked"</c:if>>
                                                                                <span>Discontinued</span>
                                                                            </label>
                                                                        </div>
                                                                        <div class="js-dateRange">
                                                                            <div class="form-group">
                                                                                <label>Active From </label>
                                                                                <input type="text" class="form-control dateUI js-start" name="activeFrom" value="${productVariant.activeFrom}">
                                                                            </div>
                                                                            <div class="form-group">
                                                                                <label>Active To </label>
                                                                                <input type="text" class="form-control dateUI js-end" name="activeTo" value="${productVariant.activeTo}">
                                                                            </div>
                                                                        </div>
                                                                       <%-- <div class="js-dateRange">
                                                                            <div class="form-group">
                                                                                <label>Discontinue From </label>
                                                                                <input type="text" class="form-control dateUI js-start" name="discontinuedFrom" value="${productVariant.discontinuedFrom}">
                                                                            </div>
                                                                            <div class="form-group">
                                                                                <label>Discontinue To </label>
                                                                                <input type="text" class="form-control dateUI js-end" name="discontinuedTo" value="${productVariant.discontinuedTo}">
                                                                            </div>
                                                                        </div>--%>
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
                                                <a href="${breadcrumbs.backURL}"><button type="button" class="btn btn-danger">Cancel</button></a>
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
                                        <div id="aniimated-thumbnials" class="js-draggable list-unstyled row clearfix">
                                            <c:forEach var="asset" items="${productVariant.variantAssets['ASSETS']}" varStatus="s">
                                                <c:set var="isDefault" value="${asset.defaultFlag eq 'Y'}" />
                                                <div class="js-drag-item col-xl-4 col-lg-6 col-md-12 col-sm-12 m-b-30" rel="${asset.id}">
                                                    <div class="digital-asset-container front card overflowhidden">
                                                        <div class="header">
                                                            <h2 class="truncate" style="padding-right: 60px;">
                                                                <c:choose>
                                                                    <c:when test="${asset.type eq 'IMAGE'}">
                                                                        <span class="asset-type" title="image" style=""><i class="fa fa-file-image-o"></i></span>
                                                                    </c:when>
                                                                    <c:when test="${asset.type eq 'VIDEO'}">
                                                                        <span class="asset-type" title="video" style=""><i class="fa fa-file-video-o"></i></span>
                                                                    </c:when>
                                                                    <c:otherwise>
                                                                        <span class="asset-type" title="template" style=""><i class="fa fa-file-pdf-o"></i></span>
                                                                    </c:otherwise>
                                                                </c:choose>
                                                                <span data-toggle="tooltip" data-placement="top" title="" data-original-title="${asset.name}">${asset.name}</span><small class="${isDefault ? '' : 'js-invisible'}" style="position:relative;"><code class="highlighter-rouge">DEFAULT</code></small>
                                                            </h2>
                                                            <ul class="header-dropdown">
                                                                <li><span class="tab_btn active ${isDefault ? 'bg-success' : ''}">${s.count}</span></li>
                                                                <li class="dropdown">
                                                                    <a href="javascript:void(0);" class="dropdown-toggle" data-toggle="dropdown" role="button" aria-haspopup="true" aria-expanded="false"></a>
                                                                    <ul class="dropdown-menu dropdown-menu-right">
                                                                        <li><a href="/pim/products/downloadVariantAsset?fileId=${asset.id}">Download</a></li>
                                                                        <li><a href="javascript:$.deleteAsset('${asset.id}');">Delete</a></li>
                                                                        <c:if test="${not isDefault}">
                                                                            <li><a href="javascript:$.setAsDefaultAsset('${asset.id}')">Set as Default</a></li>
                                                                        </c:if>

                                                                    </ul>
                                                                </li>
                                                            </ul>
                                                        </div>
                                                        <div class="body" style="position: relative;top: -30px">
                                                            <div class="digital-asset">
                                                                <div class="image">
                                                                    <a class="js-asset" href="/uploads/${asset.internalName}">
                                                                        <img class="img-fluid" src="/uploads/${asset.internalName}" alt="">
                                                                    </a>
                                                                </div>
                                                            </div>
                                                        </div>
                                                        <div class="footer">
                                                            <ul class="stats">
                                                                <li class="${not empty asset.description ? '' : 'js-hidden'}"><a href="javascript:void(0);" data-toggle="popover" data-trigger="hover" data-placement="left" title="" data-content="${asset.description}" data-original-title="Description" class="text-danger icon-bubbles">Description</a></li>
                                                            </ul>
                                                        </div>
                                                    </div>
                                                </div>
                                            </c:forEach>
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
                                                  data-success-message='["Successfully updated the child product", "Child Product Updated"]'
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
                                                                                        <c:set var="parentAttribute" value="${attribute.parentAttribute}"/>
                                                                                        <c:set var="attributeType" value="${attribute.getType(productVariant.channelId)}"/>
                                                                                        <c:set var="disabled" value=""/>
                                                                                        <c:set var="disabledClass" value=""/>
                                                                                        <c:set var="attributeValue" value="${productVariant.variantAttributes[attribute.id]}"/>
                                                                                        <c:set var="parentAttributeValue" value="${not empty parentAttribute ? productVariant.variantAttributes[parentAttribute.id] : null}"/>
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
                                                                                                    <label for="${attribute.id}">${attribute.label}</label><code class="highlighter-rouge m-l-10"><c:if test="${attributeType eq 'VARIANT' and attribute.isRequired(productVariant.channelId)}">*</c:if></code><c:if test="${not empty parentAttribute}"><small><span class="m-t-5 pull-right text-secondary"><i class="text-primary fa fa-link"></i><span class="p-l-5">${parentAttribute.name}</span></span></small></c:if>
                                                                                                </c:if>
                                                                                                <c:choose>
                                                                                                    <c:when test="${attribute.uiType eq 'DROPDOWN'}">
                                                                                                        <c:choose>
                                                                                                            <c:when test="${not empty parentAttribute}">
                                                                                                                <div class="ig-container">
                                                                                                                <c:forEach items="${parentAttributeValue}" var="parentOption">
                                                                                                                    <div class="input-group mb-3">
                                                                                                                        <div class="input-group-prepend">
                                                                                                                            <label class="input-group-text">${parentAttribute.options[parentOption].value}</label>
                                                                                                                        </div>
                                                                                                                        <select name="${attribute.id}[${parentOption}]" class="custom-select">
                                                                                                                            <option value="">Select One</option>
                                                                                                                            <c:forEach items="${attribute.options}" var="optionEntry">
                                                                                                                                <c:set var="attributeOption" value="${optionEntry.value}"/>
                                                                                                                                <option value="${attributeOption.id}" <c:if test="${attributeOption.id eq attributeValue[parentOption]}">selected</c:if>>${attributeOption.value}</option>
                                                                                                                            </c:forEach>
                                                                                                                        </select>
                                                                                                                    </div>
                                                                                                                </c:forEach>
                                                                                                                </div>
                                                                                                            </c:when>
                                                                                                            <c:otherwise>
                                                                                                                <select id="${attribute.id}" name="${attribute.id}" class="form-control${disabledClass}" ${disabled}>
                                                                                                                    <option value="">Select One</option>
                                                                                                                    <c:forEach items="${attribute.options}" var="optionEntry">
                                                                                                                        <c:set var="attributeOption" value="${optionEntry.value}"/>
                                                                                                                        <option value="${attributeOption.id}" <c:if test="${attributeOption.id eq attributeValue}">selected</c:if>>${attributeOption.value}</option>
                                                                                                                    </c:forEach>
                                                                                                                </select>
                                                                                                            </c:otherwise>
                                                                                                        </c:choose>

                                                                                                    </c:when>
                                                                                                    <c:when test="${attribute.uiType eq 'MULTI_SELECT'}">
                                                                                                        <select id="${attribute.id}" name="${attribute.id}" multiple="multiple" class="form-control${disabledClass}" ${disabled}>
                                                                                                            <c:forEach items="${attribute.options}" var="optionEntry">
                                                                                                                <c:set var="attributeOption" value="${optionEntry.value}"/>
                                                                                                                <option value="${attributeOption.id}" <c:if test="${attribute.id ne 'CARD_SLITS' && attributeValue.size() gt 0 && attributeValue.contains(attributeOption.id)}">selected</c:if>>${attributeOption.value}</option>
                                                                                                            </c:forEach>
                                                                                                        </select>
                                                                                                        <script>
                                                                                                            $('#${attribute.id}').multiSelect({
                                                                                                                selectableHeader: "<div class='custom-header'>Available</div>",
                                                                                                                selectionHeader: "<div class='custom-header'>Selected</div>",
                                                                                                                keepOrder: true,
                                                                                                                dblClick: true
                                                                                                            });
                                                                                                        </script>
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
<img src="/assets/img/tiny.png" onload="$.initPage({productId : '${productVariant.product.productId}', productVariantId : '${productVariant.productVariantId}', channelId : '${productVariant.channelId}'});$.getScript('/assets/js/pages/ui/product/productVariant.js');$.loadEvent()"/>

<script>
    var columns = JSON.parse('${pricingGridColumns}');
</script>
