<%--@elvariable id="pricingAttribute" type="com.bigname.pim.api.domain.PricingAttribute"--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<div class="popup-content" style="padding:20px">
    <div class="body">
        <form method="post" action="/pim/products/${productVariant.product.productId}/variants/${productVariant.productVariantId}/pricingDetails" data-method="PUT"
                data-success-message='["Successfully added the pricing details", "Pricing Details Added"]'
                data-error-message='["Check the error message(s) and try again", "Invalid Data"]'>
            <div class="row">
                <div class="col-md-12 col-sm-12">
                    <div class="form-group">
                        <div class="input-group mb-3">
                            <c:choose>
                                <c:when test="${empty pricingAttribute}">
                                    <div class="input-group-prepend">
                                        <label class="input-group-text" for="pricingAttribute">Pricing Attribute</label>
                                    </div>
                                    <select class="custom-select" id="pricingAttribute" name="pricingAttributeId">
                                        <option value="">Select One</option>
                                            <%--@elvariable id="availablePricingAttributes" type="java.util.List<com.bigname.pim.api.domain.PricingAttribute>"--%>
                                        <c:forEach var="pricingAttribute" items="${availablePricingAttributes}">
                                            <option value="${pricingAttribute.pricingAttributeId}">${pricingAttribute.pricingAttributeName}</option>
                                        </c:forEach>
                                    </select>
                                </c:when>
                                <c:otherwise>
                                    <div class="input-group-prepend">
                                        <label class="input-group-text" for="pricingAttribute">Pricing Attribute</label>
                                    </div>
                                    <input type="text" class="form-control" id="pricingAttribute" name="pricingAttribute" style="background-color: #fafafa !important;" value="${pricingAttribute.pricingAttributeName}" disabled="disabled"/>
                                    <input type="hidden" name="pricingAttributeId" value="${pricingAttribute.pricingAttributeId}" />
                                </c:otherwise>

                            </c:choose>

                        </div>
                        <c:if test="${not empty pricingAttribute}">
                            <span class="js-locked icon-lock" style="top: -19px;" title="This property is not editable"></span>
                        </c:if>
                    </div>
                </div>
            </div>
            <div class="row js-qty-breaks">
                <%--@elvariable id="pricingDetails" type="java.util.Map"--%>
                <c:forEach var="pricingDetail" items="${pricingDetails}">
                    <div class="js-pricing col-md-4 col-sm-6 col-xs-12">
                        <div class="form-group">
                            <div class="input-group input-group-sm mb-3">
                                <div class="input-group-prepend">
                                    <span class="input-group-text"><div class="js-qty" contenteditable="true">${pricingDetail.key} Qty</div></span>
                                </div>
                                <input type="text" name="q.${pricingDetail.key}" value="${pricingDetail.value}" style="padding-right: 15px !important; font-family: 'Courier New', monospace" class="js-name form-control align-right">
                            </div>
                        </div>
                    </div>
                </c:forEach>
                <div class="col-md-4 col-sm-6 col-xs-12">
                    <button class="btn btn-sm btn-outline-success js-add-qty-break"><i class="fa fa-plus"></i> <span class="p-l-5">Add Quantity Break</span></button>
                    <div class="js-pricing col-md-4 col-sm-6 col-xs-12 displaynone">
                        <div class="form-group">
                            <div class="input-group input-group-sm mb-3">
                                <div class="input-group-prepend">
                                    <span class="input-group-text"><div class="js-qty" contenteditable="true">Qty</div></span>
                                </div>
                                <input type="text" name="q" value="" style="padding-right: 15px !important; font-family: 'Courier New', monospace" class="js-name form-control align-right">
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            <br>
            <input type="hidden" name="channelId" value="${productVariant.channelId}"/>
            <input type="hidden" name="group" value="PRICING_DETAILS"/>
        </form>
    </div>
</div>
<img src="/assets/img/tiny.png" onload="$.initAHAH(this)"/>
<script src="/assets/js/pages/ui/product/pricingDetails.js"></script>

