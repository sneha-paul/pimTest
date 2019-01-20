<%--@elvariable id="channels" type="java.util.Map"--%>
<%--@elvariable id="product" type="com.bigname.pim.api.domain.Product"--%>
<%--@elvariable id="productFamilies" type="java.util.List<com.bigname.pim.api.domain.ProductFamily>"--%>
<%--@elvariable id="masterGroup" type="com.bigname.pim.api.domain.FamilyAttributeGroup"--%>
<%--@elvariable id="detailsMasterGroup" type="com.bigname.pim.api.domain.FamilyAttributeGroup"--%>
<%--@elvariable id="featuresMasterGroup" type="com.bigname.pim.api.domain.FamilyAttributeGroup"--%>
<%--@elvariable id="defaultDetailsAttributeGroup" type="com.bigname.pim.api.domain.FamilyAttributeGroup"--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<c:set var="masterGroups" value="${product.productFamily.addonMasterGroups}"/>
<c:set var="detailsMasterGroup" value="${product.productFamily.detailsMasterGroup}"/>
<c:set var="featuresMasterGroup" value="${product.productFamily.featuresMasterGroup}"/>

<div class="row clearfix">
    <div class="col-lg-12 col-md-12">
        <div class="card">
            <div class="header">
                <div>
                    <div class="pull-right">
                        <label style="font-weight: 400">Channel:</label>
                        <div class="btn-group m-t--5 m-r-15 js-channel-selector" style="z-index: 98">
                            <button class="btn btn-link btn-sm dropdown-toggle" style="min-width: 110px" type="button" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                                <span style="font-size: 14px">${channels[product.channelId]}</span>
                            </button>
                            <c:forEach var="channelEntry" items="${channels.entrySet()}" varStatus="s1">
                                <c:if test="${s1.first}"><div class="dropdown-menu"></c:if>
                                    <span class="dropdown-item <c:choose><c:when test='${channelEntry.key ne product.channelId}'>js-channel</c:when><c:otherwise>bg-primary text-white</c:otherwise></c:choose>"  data-channel-id="${channelEntry.key}">${channelEntry.value}</span>
                                <c:if test="${s1.last}"></div></c:if>
                            </c:forEach>
                        </div>
                    </div>
                </div>
                <h2>${product.productName} <small><code class="highlighter-rouge">${product.productId}</code></small>
                    <small class="pull-right" style="margin-top: -15px;padding-right:10px"><code style="color:#808080">_id: ${product.id}</code></small>
                </h2>
            </div>
            <div class="body">
                <ul class="nav nav-tabs-new2">
                    <li class="nav-item"><a class="nav-link active show" data-toggle="tab" href="#details">Details</a></li>
                    <li class="nav-item"><a class="nav-link" data-toggle="tab" href="#digitalAssets">Digital Assets</a></li>
                    <c:forEach var="masterGroup" items="${masterGroups}">
                        <c:if test="${masterGroup.id ne 'PRICING'}">
                            <li class="nav-item"><a class="nav-link" data-toggle="tab" href="#${masterGroup.id}">${masterGroup.name}</a></li>
                        </c:if>
                    </c:forEach>
                    <li class="nav-item"><a class="nav-link" data-toggle="tab" href="#productFeatures">Product Features</a></li>
                    <li class="nav-item"><a class="nav-link" data-toggle="tab" href="#productVariants">Product Variants</a></li>
                    <li class="nav-item"><a class="nav-link" data-toggle="tab" href="#productCategories">Categories</a></li>
                </ul>
                <div class="tab-content">
                    <div class="tab-pane show active" id="details">
                        <div class="row clearfix m-t-20">
                            <div class="col-lg-12 col-md-12">

                                <div class="card inner overflowhidden">
                                    <div class="body">
                                        <form method="post" action="/pim/products/${product.productId}/channels/${product.channelId}" data-method="PUT"
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
                                                                        <div class="form-group js-external-id">
                                                                            <label for="productId">Product ID</label><code class="highlighter-rouge m-l-10">*</code>
                                                                            <input type="text" id="productId" name="productId" class="form-control" value="${product.productId}" required="true"/>
                                                                        </div>
                                                                        <div class="form-group">
                                                                            <label for="productFamilyId">ProductFamily</label>
                                                                            <input id="productFamilyId" class="form-control" type="text" disabled="disabled" value="${product.productFamily.familyName}"/>
                                                                        </div>
                                                                        <c:if test="${not empty detailsMasterGroup}">
                                                                            <c:set var="defaultDetailsAttributeGroup" value="${detailsMasterGroup.childGroups.get('DEFAULT_GROUP').childGroups.get('DEFAULT_GROUP')}"/>
                                                                            <c:if test="${not empty defaultDetailsAttributeGroup}">
                                                                                <c:forEach items="${defaultDetailsAttributeGroup.attributes}" var="attributeEntry">
                                                                                    <c:set var="attribute" value="${attributeEntry.value}"/>
                                                                                    <c:set var="attributeType" value="${attribute.getType(product.channelId)}"/>
                                                                                    <c:if test="${attributeType eq 'COMMON'}">
                                                                                        <div class="form-group">
                                                                                        <c:if test="${attribute.uiType ne 'YES_NO'}">
                                                                                            <label for="${attribute.id}">${attribute.label}</label><code class="highlighter-rouge m-l-10"><c:if test="${attributeType eq 'COMMON' and attribute.isRequired(product.channelId)}">*</c:if></code>
                                                                                        </c:if>
                                                                                        <c:choose>
                                                                                            <c:when test="${attribute.uiType eq 'DROPDOWN'}">
                                                                                                <select id="${attribute.id}" name="${attribute.id}" class="form-control">
                                                                                                    <option value="">Select One</option>
                                                                                                    <c:forEach items="${attribute.options}" var="optionEntry">
                                                                                                        <c:set var="attributeOption" value="${optionEntry.value}"/>
                                                                                                        <option value="${attributeOption.id}" <c:if test="${attributeOption.id eq product.channelFamilyAttributes[attribute.id]}">selected</c:if>>${attributeOption.value}</option>
                                                                                                    </c:forEach>
                                                                                                </select>
                                                                                            </c:when>
                                                                                            <c:when test="${attribute.uiType eq 'TEXTAREA'}">
                                                                                                <textarea id="${attribute.id}" class="form-control auto-resize" name="${attribute.id}">${product.channelFamilyAttributes[attribute.id]}</textarea>
                                                                                            </c:when>
                                                                                            <c:when test="${attribute.uiType eq 'CHECKBOX'}">
                                                                                                <br/>
                                                                                                <c:forEach items="${attribute.options}" var="attributeOptionEntry" varStatus="s1">
                                                                                                    <c:set var="attributeOption" value="${attributeOptionEntry.value}"/>
                                                                                                    <label class="fancy-checkbox">
                                                                                                        <input type="checkbox" class="js-checkbox" name="${attribute.id}" value="${attributeOption.id}" <c:if test="${product.channelFamilyAttributes[attribute.id] eq attributeOption.id}">checked="checked"</c:if>>
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
                                                                                                        <input type="radio" name="${attribute.id}" value="${attributeOption.id}" <c:if test="${product.channelFamilyAttributes[attribute.id] eq attributeOption.id}">checked="checked"</c:if>>
                                                                                                        <span><i></i>${attributeOption.value}</span>
                                                                                                    </label>
                                                                                                    <c:if test="${not s1.end}">
                                                                                                        <br/>
                                                                                                    </c:if>
                                                                                                </c:forEach>
                                                                                            </c:when>
                                                                                            <c:when test="${attribute.uiType eq 'YES_NO'}">
                                                                                                <br/>
                                                                                                <label class="fancy-checkbox">
                                                                                                    <input type="checkbox" class="js-checkbox" name="${attribute.id}" value="Y" <c:if test="${product.channelFamilyAttributes[attribute.id] eq 'Y'}">checked="checked"</c:if>>
                                                                                                    <span>${attribute.label}</span>
                                                                                                </label>
                                                                                            </c:when>
                                                                                            <c:when test="${attribute.uiType eq 'DATE_PICKER'}">
                                                                                                <div class="input-group date" data-date-autoclose="true" data-provide="datepicker">
                                                                                                    <input type="text" id="${attribute.id}" name="${attribute.id}" value="${product.channelFamilyAttributes[attribute.id]}" class="form-control datepicker">
                                                                                                    <div class="input-group-append">
                                                                                                        <button class="btn btn-secondary" type="button"><i class="fa fa-calendar"></i></button>
                                                                                                    </div>
                                                                                                </div>
                                                                                            </c:when>
                                                                                            <c:otherwise>
                                                                                                <input type="text" id="${attribute.id}" name="${attribute.id}" value="${product.channelFamilyAttributes[attribute.id]}" class="form-control"/>
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
                                                <c:if test="${attributeGroup.defaultGroup ne 'Y' && attributeGroup.isAvailable(product.channelId)}">
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
                                                                                    <c:set var="attributeType" value="${attribute.getType(product.channelId)}"/>
                                                                                    <c:if test="${attributeType eq 'COMMON'}">
                                                                                        <div class="form-group">
                                                                                        <c:if test="${attribute.uiType ne 'YES_NO' and attributeGroup.label ne attribute.label}">
                                                                                            <label for="${attribute.id}">${attribute.label}</label><code class="highlighter-rouge m-l-10"><c:if test="${attributeType eq 'COMMON' and attribute.isRequired(product.channelId)}">*</c:if></code>
                                                                                        </c:if>
                                                                                        <c:choose>
                                                                                            <c:when test="${attribute.uiType eq 'DROPDOWN'}">
                                                                                                <select id="${attribute.id}" name="${attribute.id}" class="form-control">
                                                                                                    <option value="">Select One</option>
                                                                                                    <c:forEach items="${attribute.options}" var="optionEntry">
                                                                                                        <c:set var="attributeOption" value="${optionEntry.value}"/>
                                                                                                        <option value="${attributeOption.id}" <c:if test="${attributeOption.id eq product.channelFamilyAttributes[attribute.id]}">selected</c:if>>${attributeOption.value}</option>
                                                                                                    </c:forEach>
                                                                                                </select>
                                                                                            </c:when>
                                                                                            <c:when test="${attribute.uiType eq 'TEXTAREA'}">
                                                                                                <textarea id="${attribute.id}" class="form-control auto-resize" name="${attribute.id}">${product.channelFamilyAttributes[attribute.id]}</textarea>
                                                                                            </c:when>
                                                                                            <c:when test="${attribute.uiType eq 'CHECKBOX'}">
                                                                                                <br/>
                                                                                                <c:forEach items="${attribute.options}" var="attributeOptionEntry" varStatus="s1">
                                                                                                    <c:set var="attributeOption" value="${attributeOptionEntry.value}"/>
                                                                                                    <label class="fancy-checkbox">
                                                                                                        <input type="checkbox" class="js-checkbox" name="${attribute.id}" value="${attributeOption.id}" <c:if test="${product.channelFamilyAttributes[attribute.id] eq attributeOption.id}">checked="checked"</c:if>>
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
                                                                                                        <input type="radio" name="${attribute.id}" value="${attributeOption.id}" <c:if test="${product.channelFamilyAttributes[attribute.id] eq attributeOption.id}">checked="checked"</c:if>>
                                                                                                        <span><i></i>${attributeOption.value}</span>
                                                                                                    </label>
                                                                                                    <c:if test="${not s1.end}">
                                                                                                        <br/>
                                                                                                    </c:if>
                                                                                                </c:forEach>
                                                                                            </c:when>
                                                                                            <c:when test="${attribute.uiType eq 'YES_NO'}">
                                                                                                <br/>
                                                                                                <label class="fancy-checkbox">
                                                                                                    <input type="checkbox" class="js-checkbox" name="${attribute.id}" value="Y" <c:if test="${product.channelFamilyAttributes[attribute.id] eq 'Y'}">checked="checked"</c:if>>
                                                                                                    <span>${attribute.label}</span>
                                                                                                </label>
                                                                                            </c:when>
                                                                                            <c:when test="${attribute.uiType eq 'DATE_PICKER'}">
                                                                                                <div class="input-group date" data-date-autoclose="true" data-provide="datepicker">
                                                                                                    <input type="text" id="${attribute.id}" name="${attribute.id}" value="${product.channelFamilyAttributes[attribute.id]}" class="form-control datepicker">
                                                                                                    <div class="input-group-append">
                                                                                                        <button class="btn btn-secondary" type="button"><i class="fa fa-calendar"></i></button>
                                                                                                    </div>
                                                                                                </div>
                                                                                            </c:when>
                                                                                            <c:otherwise>
                                                                                                <input type="text" id="${attribute.id}" name="${attribute.id}" value="${product.channelFamilyAttributes[attribute.id]}" class="form-control"/>
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
                                                <a href="/pim/products"><button type="button" class="btn btn-danger">Cancel</button></a>
                                            </div>
                                        </form>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="tab-pane" id="digitalAssets">
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
                                            <c:forEach var="asset" items="${product.channelAssets['ASSETS']}" varStatus="s">
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
                                                                        <li><a href="javascript:void(0);" onclick="$(this).closest('.digital-asset-container').flip(true)">Edit</a></li>
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
                        <c:if test="${masterGroup.id ne 'PRICING'}">
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
                                                    <form method="post" action="/pim/products/${product.productId}/channels/${product.channelId}" data-method="PUT"
                                                          data-success-message='["Successfully updated the product", "Product Updated"]'
                                                          data-error-message='["Check the error message(s) and try again", "Invalid Data"]'>
                                                        <c:forEach items="${sectionGroup.childGroups}" var="attributeGroupEntry">
                                                            <c:set var="attributeGroup" value="${attributeGroupEntry.value}" />
                                                            <c:if test="${attributeGroup.isAvailable(product.channelId)}">
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
                                                                                            <c:set var="attributeType" value="${attribute.getType(product.channelId)}"/>
                                                                                            <c:if test="${attributeType eq 'COMMON'}">
                                                                                                <div class="form-group">
                                                                                                    <c:if test="${attribute.uiType ne 'YES_NO' and attributeGroup.label ne attribute.label}">
                                                                                                        <label for="${attribute.id}">${attribute.label}</label><code class="highlighter-rouge m-l-10"><c:if test="${attributeType eq 'COMMON' and attribute.isRequired(product.channelId)}">*</c:if></code>
                                                                                                    </c:if>
                                                                                                    <c:choose>
                                                                                                        <c:when test="${attribute.uiType eq 'DROPDOWN'}">
                                                                                                            <select id="${attribute.id}" name="${attribute.id}" class="form-control">
                                                                                                                <option value="">Select One</option>
                                                                                                                <c:forEach items="${attribute.options}" var="optionEntry">
                                                                                                                    <c:set var="attributeOption" value="${optionEntry.value}"/>
                                                                                                                    <option value="${attributeOption.id}" <c:if test="${attributeOption.id eq product.channelFamilyAttributes[attribute.id]}">selected</c:if>>${attributeOption.value}</option>
                                                                                                                </c:forEach>
                                                                                                            </select>
                                                                                                        </c:when>
                                                                                                        <c:when test="${attribute.uiType eq 'TEXTAREA'}">
                                                                                                            <textarea id="${attribute.id}" class="form-control auto-resize" name="${attribute.id}">${product.channelFamilyAttributes[attribute.id]}</textarea>
                                                                                                        </c:when>
                                                                                                        <c:when test="${attribute.uiType eq 'CHECKBOX'}">
                                                                                                            <br/>
                                                                                                            <c:forEach items="${attribute.options}" var="attributeOptionEntry" varStatus="s1">
                                                                                                                <c:set var="attributeOption" value="${attributeOptionEntry.value}"/>
                                                                                                                <c:set var="checked" value="${false}"/>
                                                                                                                <c:forEach items="${product.channelFamilyAttributes[attribute.id]}" var="value">
                                                                                                                    <c:if test="${value eq attributeOption.id}">
                                                                                                                        <c:set var="checked" value="${true}"/>
                                                                                                                    </c:if>
                                                                                                                </c:forEach>
                                                                                                                <label class="fancy-checkbox">
                                                                                                                    <input type="checkbox" class="js-checkbox" name="${attribute.id}" value="${attributeOption.id}" <c:if test="${checked eq true}">checked="checked"</c:if>>
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
                                                                                                                    <input type="radio" name="${attribute.id}" value="${attributeOption.id}" <c:if test="${product.channelFamilyAttributes[attribute.id] eq attributeOption.id}">checked="checked"</c:if>>
                                                                                                                    <span><i></i>${attributeOption.value}</span>
                                                                                                                </label>
                                                                                                                <c:if test="${not s1.end}">
                                                                                                                    <br/>
                                                                                                                </c:if>
                                                                                                            </c:forEach>
                                                                                                        </c:when>
                                                                                                        <c:when test="${attribute.uiType eq 'YES_NO'}">
                                                                                                            <br/>
                                                                                                            <label class="fancy-checkbox">
                                                                                                                <input type="checkbox" class="js-checkbox" name="${attribute.id}" value="Y" <c:if test="${product.channelFamilyAttributes[attribute.id] eq 'Y'}">checked="checked"</c:if>>
                                                                                                                <span>${attribute.label}</span>
                                                                                                            </label>
                                                                                                        </c:when>
                                                                                                        <c:when test="${attribute.uiType eq 'DATE_PICKER'}">
                                                                                                            <div class="input-group date" data-date-autoclose="true" data-provide="datepicker">
                                                                                                                <input type="text" id="${attribute.id}" name="${attribute.id}" value="${product.channelFamilyAttributes[attribute.id]}" class="form-control datepicker">
                                                                                                                <div class="input-group-append">
                                                                                                                    <button class="btn btn-secondary" type="button"><i class="fa fa-calendar"></i></button>
                                                                                                                </div>
                                                                                                            </div>
                                                                                                        </c:when>
                                                                                                        <c:otherwise>
                                                                                                            <input type="text" id="${attribute.id}" name="${attribute.id}" value="${product.channelFamilyAttributes[attribute.id]}" class="form-control"/>
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
                        </c:if>
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
                                            <form method="post" action="/pim/products/${product.productId}/channels/${product.channelId}" data-method="PUT"
                                                  data-success-message='["Successfully updated the product", "Product Updated"]'
                                                  data-error-message='["Check the error message(s) and try again", "Invalid Data"]'>
                                                <c:forEach items="${sectionGroup.childGroups}" var="attributeGroupEntry">
                                                    <c:set var="attributeGroup" value="${attributeGroupEntry.value}" />
                                                    <c:if test="${attributeGroup.isAvailable(product.channelId)}">
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
                                                                                    <c:set var="attributeType" value="${attribute.getType(product.channelId)}"/>
                                                                                    <c:if test="${attributeType eq 'COMMON'}">
                                                                                        <div class="form-group">
                                                                                            <c:if test="${attribute.uiType ne 'YES_NO' and attributeGroup.label ne attribute.label}">
                                                                                                <label for="${attribute.id}">${attribute.label}</label><code class="highlighter-rouge m-l-10"><c:if test="${attributeType eq 'COMMON' and attribute.isRequired(product.channelId)}">*</c:if></code>
                                                                                            </c:if>
                                                                                            <c:choose>
                                                                                                <c:when test="${attribute.uiType eq 'DROPDOWN'}">
                                                                                                    <select id="${attribute.id}" name="${attribute.id}" class="form-control">
                                                                                                        <option value="">Select One</option>
                                                                                                        <c:forEach items="${attribute.options}" var="optionEntry">
                                                                                                            <c:set var="attributeOption" value="${optionEntry.value}"/>
                                                                                                            <option value="${attributeOption.id}" <c:if test="${attributeOption.id eq product.channelFamilyAttributes[attribute.id]}">selected</c:if>>${attributeOption.value}</option>
                                                                                                        </c:forEach>
                                                                                                    </select>
                                                                                                </c:when>
                                                                                                <c:when test="${attribute.uiType eq 'TEXTAREA'}">
                                                                                                    <textarea id="${attribute.id}" class="form-control auto-resize" name="${attribute.id}">${product.channelFamilyAttributes[attribute.id]}</textarea>
                                                                                                </c:when>
                                                                                                <c:when test="${attribute.uiType eq 'CHECKBOX'}">
                                                                                                    <br/>
                                                                                                    <c:forEach items="${attribute.options}" var="attributeOptionEntry" varStatus="s1">
                                                                                                        <c:set var="attributeOption" value="${attributeOptionEntry.value}"/>
                                                                                                        <label class="fancy-checkbox">
                                                                                                            <input type="checkbox" class="js-checkbox" name="${attribute.id}" value="${attributeOption.id}" <c:if test="${product.channelFamilyAttributes[attribute.id] eq attributeOption.id}">checked="checked"</c:if>>
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
                                                                                                            <input type="radio" name="${attribute.id}" value="${attributeOption.id}" <c:if test="${product.channelFamilyAttributes[attribute.id] eq attributeOption.id}">checked="checked"</c:if>>
                                                                                                            <span><i></i>${attributeOption.value}</span>
                                                                                                        </label>
                                                                                                        <c:if test="${not s1.end}">
                                                                                                            <br/>
                                                                                                        </c:if>
                                                                                                    </c:forEach>
                                                                                                </c:when>
                                                                                                <c:when test="${attribute.uiType eq 'YES_NO'}">
                                                                                                    <br/>
                                                                                                    <label class="fancy-checkbox">
                                                                                                        <input type="checkbox" class="js-checkbox" name="${attribute.id}" value="Y" <c:if test="${product.channelFamilyAttributes[attribute.id] eq 'Y'}">checked="checked"</c:if>>
                                                                                                        <span>${attribute.label}</span>
                                                                                                    </label>
                                                                                                </c:when>
                                                                                                <c:when test="${attribute.uiType eq 'DATE_PICKER'}">
                                                                                                    <div class="input-group date" data-date-autoclose="true" data-provide="datepicker">
                                                                                                        <input type="text" id="${attribute.id}" name="${attribute.id}" value="${product.channelFamilyAttributes[attribute.id]}" class="form-control datepicker">
                                                                                                        <div class="input-group-append">
                                                                                                            <button class="btn btn-secondary" type="button"><i class="fa fa-calendar"></i></button>
                                                                                                        </div>
                                                                                                    </div>
                                                                                                </c:when>
                                                                                                <c:otherwise>
                                                                                                    <input type="text" id="${attribute.id}" name="${attribute.id}" value="${product.channelFamilyAttributes[attribute.id]}" class="form-control"/>
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
                                        <div class="table-responsive scrollable-dt">
                                            <table id="paginatedProductVariantsTable" class="table table-hover dataTable table-custom c_list" style="width: 100%">
                                                <thead class="thead-dark"></thead>
                                            </table>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="tab-pane" id="productCategories">
                        <div class="row clearfix">
                            <div class="col-lg-12 col-md-12">
                                <div class="card">
                                    <div class="body">
                                        <div class="row p-b-25">
                                            <div class="col-lg-12 col-md-12">
                                                <div class="pull-right">
                                                    <button type="button" class="btn btn-success js-add-categories"><i class="fa fa-plus"></i> <span class="p-l-5">Add Categories</span></button>
                                                </div>
                                            </div>
                                        </div>
                                        <div class="table-responsive">
                                            <table id="paginatedCategoriesTable" class="table table-hover dataTable table-custom" style="width: 100%">
                                                <thead class="thead-dark"></thead>
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
<img src="/assets/img/tiny.png" onload="$.initPage({productId : '${product.productId}', channelId : '${product.channelId}', hash : '${param.hash}'});$.getScript('/assets/js/pages/ui/product/product.js');"/>

<%--<script src="/assets/js/pages/ui/product/product.js"></script>--%>
<script>
    $.initPage({
        'productId' : '${product.productId}',
        'categoryId' : '${not empty param.categoryId ? param.categoryId : ""}',
        'parentId' : '<c:if test="${not empty param.parentId}">${param.parentId}</c:if>',
        'websiteId': '${not empty param.websiteId ? param.websiteId : ""}',
        'hash': '${param.hash}',
        'catalogId' : '${param.catalogId}'
    });

</script>


