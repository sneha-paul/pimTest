<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles-extras" prefix="tilesx"%>

<tilesx:useAttribute ignore="true" id="pageStyles" name="pageStyles" classname="java.util.List"/>
<tilesx:useAttribute ignore="true" id="pageTopScripts" name="pageTopScripts" classname="java.util.List"/>
<tilesx:useAttribute ignore="true" id="pageBottomScripts" name="pageBottomScripts" classname="java.util.List"/>

<!doctype html>
<html lang="en">
<head>
    <link rel="icon" href="favicon.ico" type="image/x-icon">

    <!-- VENDOR CSS -->
    <link rel="stylesheet" href="/assets/vendor/bootstrap/css/bootstrap.css">
    <link rel="stylesheet" href="/assets/vendor/font-awesome/css/font-awesome.css">
    <link rel="stylesheet" href="/assets/vendor/jquery-datatable/dataTables.bootstrap4.min.css">

    <!-- VENDOR CSS -->
    <link rel="stylesheet" href="/assets/vendor/bootstrap/css/bootstrap.css">
    <link rel="stylesheet" href="/assets/vendor/font-awesome/css/font-awesome.css">

    <link rel="stylesheet" href="/assets/vendor/sweetalert/sweetalert.css"/>
    <link rel="stylesheet" href="/assets/vendor/jquery-datatable/dataTables.bootstrap4.min.css">
    <link rel="stylesheet" href="/assets/vendor/bootstrap-multiselect/bootstrap-multiselect.css">
    <link rel="stylesheet" href="/assets/vendor/parsleyjs/css/parsley.css">
    <link rel="stylesheet" href="/assets/vendor/table-dragger/table-dragger.min.css">

    <!-- MAIN CSS -->
    <link rel="stylesheet" href="/assets/css/main.css">
    <link rel="stylesheet" href="/assets/css/color_skins.css">

    <!-- PAGE LEVEL CSS -->
    <c:forEach var="pageCss" items="${pageStyles}">
        <link type="text/css" rel="stylesheet" media="all" href='<c:out value="${pageCss}"/>'>
    </c:forEach>

    <!-- PAGE LEVEL JS -->
    <c:forEach var="pageJS" items="${pageTopScripts}">
        <script type="text/javascript" src='<c:out value="${pageJS}"/>'></script>
    </c:forEach>

    <style>
        a:focus {outline:0;}
        body, html {height:100%}
        .popup-content {height: 100%}

    </style>

    </head>
<body>

<tiles:insertAttribute ignore="true" name="body"/>

<!-- Javascript -->
<script src="/assets/bundles/libscripts.bundle.js"></script>
<script src="/assets/bundles/vendorscripts.bundle.js"></script>

<script src="/assets/bundles/datatablescripts.bundle.js"></script>
<script src="/assets/vendor/sweetalert/sweetalert.min.js"></script> <!-- SweetAlert Plugin Js -->

<script src="/assets/vendor/bootstrap-multiselect/bootstrap-multiselect.js"></script>
<script src="/assets/vendor/parsleyjs/js/parsley.min.js"></script>

<script src="/assets/bundles/mainscripts.bundle.js"></script>
<script src="/assets/js/pages/tables/jquery-datatable.js"></script>
<script src="/assets/js/pages/tables/jquery-datatable-conditional-paging.js"></script>
<script src="/assets/js/pages/ui/dialogs.js"></script>
<script src="/assets/vendor/table-dragger/table-dragger.min.js"></script>
<script src="/assets/vendor/emodal/eModal.js"></script>
<!-- PAGE LEVEL JS -->
<c:forEach var="customJS" items="${pageBottomScripts}">
    <script src="<c:out value='${customJS}'/>"></script>
</c:forEach>
<tiles:insertAttribute name="footer" ignore="true"/>
<tiles:insertAttribute name="script" ignore="true" />
</body>
</html>
