<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<div class="p-l-0 p-r-0">
    <nav class="sidebar-nav">
        <ul class="main-menu metismenu">
            <li><a href="index.html"><i class="icon-speedometer"></i><span>PIM Dashboard</span></a></li>
            <li <c:if test="${active eq 'PRODUCTS'}">class="active"</c:if>><a href="/pim/products"><i class="icon-list"></i>Products</a></li>
            <li <c:if test="${active eq 'CATEGORIES'}">class="active"</c:if>><a href="/pim/categories"><i class="icon-list"></i>Categories</a></li>
            <li <c:if test="${active eq 'CATALOGS'}">class="active"</c:if>><a href="/pim/catalogs"><i class="icon-badge"></i>Catalogs</a></li>
            <li <c:if test="${active eq 'WEBSITES'}">class="active"</c:if>><a href="/pim/websites"><i class="icon-badge"></i>Websites</a></li>
            <li <c:if test="${active eq 'SETTINGS'}">class="active"</c:if>><a href="/pim/attributeCollections"><i class="icon-settings"></i>Settings</a></li>
            <li <c:if test="${active eq 'FAMILIES'}">class="active"</c:if>><a href="/pim/families"><i class="icon-list"></i>Families</a></li>
            <li <c:if test="${active eq 'USERS'}">class="active"</c:if>><a href="app-users.html"><i class="icon-user"></i>Users</a></li>
        </ul>
    </nav>
</div>