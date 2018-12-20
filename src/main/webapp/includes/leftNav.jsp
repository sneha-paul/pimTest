<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<div class="p-l-0 p-r-0">
    <nav class="sidebar-nav">
        <ul class="main-menu metismenu">
            <li><a href="/pim/dashboard"><i class="icon-speedometer"></i><span>PIM Dashboard</span></a></li>
            <li <c:if test="${active eq 'PRODUCTS'}">class="active"</c:if>><a href="/pim/products"><i class="icon-present"></i>Products</a></li>
            <li <c:if test="${active eq 'CATEGORIES'}">class="active"</c:if>><a href="/pim/categories"><i class="icon-list"></i>Categories</a></li>
            <li <c:if test="${active eq 'CATALOGS'}">class="active"</c:if>><a href="/pim/catalogs"><i class="icon-grid"></i>Catalogs</a></li>
            <li <c:if test="${active eq 'WEBSITES'}">class="active"</c:if>><a href="/pim/websites"><i class="icon-globe"></i>Websites</a></li>
            <%--<li <c:if test="${active eq 'SETTINGS'}">class="active"</c:if>><a href="/pim/attributeCollections"><i class="icon-settings"></i>Attribute Collections</a></li>
            <li <c:if test="${active eq 'FAMILIES'}">class="active"</c:if>><a href="/pim/families"><i class="icon-list"></i>Families</a></li>
            <li <c:if test="${active eq 'SETTINGS'}">class="active"</c:if>><a href="/pim/pricingAttributes"><i class="icon-user"></i>Pricing Attributes</a></li>--%>
            <li <c:if test="${active eq 'USERS'}">class="active"</c:if>><a href="/pim/users"><i class="icon-users"></i>Users</a></li>
            <c:set var="expanded" value="${active eq 'ATTRIBUTE_COLLECTIONS' or active eq 'FAMILIES' or active eq 'PRICING_ATTRIBUTES'}"/>
            <li <c:if test="${expanded}">class="active"</c:if>>
                <a href="#Settings" class="has-arrow" aria-expanded="${expanded}"><i class="icon-settings"></i><span>Settings</span></a>
                <ul aria-expanded="${expanded}" class="collapse <c:if test='${expanded}'>in</c:if>" style="height: 0px;">
                    <li <c:if test="${active eq 'ATTRIBUTE_COLLECTIONS'}">class="active"</c:if>><a href="/pim/attributeCollections">Attribute Collections</a></li>
                    <li <c:if test="${active eq 'FAMILIES'}">class="active"</c:if>><a href="/pim/families">Families</a></li>
                    <li <c:if test="${active eq 'PRICING_ATTRIBUTES'}">class="active"</c:if>><a href="/pim/pricingAttributes">Pricing Attributes</a></li>
                </ul>
            </li>
        </ul>
    </nav>
</div>