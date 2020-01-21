<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<div class="p-l-0 p-r-0">
    <nav class="sidebar-nav">
        <ul class="main-menu metismenu">
            <li><a href="/pim/dashboard"><i class="icon-speedometer"></i><span>PIM Dashboard</span></a></li>
<%--            <li <c:if test="${active eq 'SCHEDULER'}">class="active"</c:if>><a href="/pim/scheduler"><i class="icon-clock"></i><span>PIM Scheduler</span></a></li>--%>
            <li <c:if test="${active eq 'PRODUCTS'}">class="active"</c:if>><a href="/pim/products"><i class="icon-present"></i><span>Parent Products</span></a></li>
            <li <c:if test="${active eq 'CATEGORIES'}">class="active"</c:if>><a href="/pim/categories"><i class="icon-list"></i><span>All Categories</span></a></li>
            <li <c:if test="${active eq 'CATALOGS'}">class="active"</c:if>><a href="/pim/catalogs"><i class="icon-grid"></i><span>Catalogs</span></a></li>
            <li <c:if test="${active eq 'WEBSITES'}">class="active"</c:if>><a href="/pim/websites"><i class="icon-globe"></i><span>Websites</span></a></li>
            <li <c:if test="${active eq 'USERS'}">class="active"</c:if>><a href="/pim/users"><i class="icon-users"></i><span>Users</span></a></li>
            <c:set var="expanded" value="${active eq 'ASSET_COLLECTIONS' or active eq 'ATTRIBUTE_COLLECTIONS' or active eq 'FAMILIES' or active eq 'PRICING_ATTRIBUTES' or active eq 'EVENTS'}"/>
            <li <c:if test="${expanded}">class="active"</c:if>>
                <a href="#Settings" class="has-arrow" aria-expanded="${expanded}"><i class="icon-settings"></i><span>Settings</span></a>
                <ul aria-expanded="${expanded}" class="collapse <c:if test='${expanded}'>in</c:if>" style="height: 0px;">
                    <li <c:if test="${active eq 'ASSET_COLLECTIONS'}">class="active"</c:if>><a href="/pim/assetCollections"><i class="icon-layers"></i><span>Asset Collections</span></a></li>
                    <li <c:if test="${active eq 'ASSET_FAMILIES'}">class="active"</c:if>><a href="/pim/assetFamilies"><i class="fa fa-group"></i><span>Asset Families</span></a></li>
                    <li <c:if test="${active eq 'ATTRIBUTE_COLLECTIONS'}">class="active"</c:if>><a href="/pim/attributeCollections"><i class="fa fa-cubes"></i><span>Attribute Collections</span></a></li>
                    <li <c:if test="${active eq 'FAMILIES'}">class="active"</c:if>><a href="/pim/families"><i class="fa fa-shopping-basket"></i><span>Product Types</span></a></li>
                    <li <c:if test="${active eq 'PRICING_ATTRIBUTES'}">class="active"</c:if>><a href="/pim/pricingAttributes"><i class="fa fa-money"></i><span>Pricing Attributes</span></a></li>
                    <li <c:if test="${active eq 'EVENTS'}">class="active"</c:if>><a href="/pim/events"><i class="fa fa-chain-broken"></i><span>Events</span></a></li>
                    <li <c:if test="${active eq 'JOBS'}">class="active"</c:if>><a href="/pim/jobs"><i class="fa fa-handshake-o"></i><span>Jobs</span></a></li>
                </ul>
            </li>
        </ul>
    </nav>
</div>

