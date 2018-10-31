<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles-extras" prefix="tilesx"%>

<tilesx:useAttribute ignore="true" id="pageStyles" name="pageStyles" classname="java.util.List"/>
<tilesx:useAttribute ignore="true" id="pageTopScripts" name="pageTopScripts" classname="java.util.List"/>
<tilesx:useAttribute ignore="true" id="pageBottomScripts" name="pageBottomScripts" classname="java.util.List"/>


<!doctype html>
<html lang="en">
<head>
    <title><tiles:insertAttribute name="title" ignore="true" /></title>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=Edge">
    <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=0">
    <meta name="description" content="<tiles:insertAttribute name="metaDescription" ignore="true"/>">
    <meta name="keyword" content="<tiles:insertAttribute name="metaKeyword" ignore="true"/>">
    <link href="/assets/img/favicon.png" rel="shortcut icon" type="image/x-icon">

    <%-- VENDOR CSS --%>
    <link rel="stylesheet" href="/assets/vendor/bootstrap/css/bootstrap.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/jstree/3.2.1/themes/default/style.min.css" />
    <link rel="stylesheet" href="/assets/vendor/bootstrap-datepicker/css/bootstrap-datepicker3.min.css">
    <link rel="stylesheet" href="/assets/vendor/font-awesome/css/font-awesome.css">
    <link rel="stylesheet" href="//cdnjs.cloudflare.com/ajax/libs/toastr.js/latest/css/toastr.min.css" />
    <link rel="stylesheet" href="/assets/vendor/nestable/jquery-nestable.css">
    <link rel="stylesheet" href="/assets/vendor/sweetalert/sweetalert.css"/>
    <link rel="stylesheet" href="/assets/vendor/jquery-datatable/dataTables.bootstrap4.min.css">
    <link rel="stylesheet" href="/assets/vendor/bootstrap-multiselect/bootstrap-multiselect.css">
    <link rel="stylesheet" href="/assets/vendor/parsleyjs/css/parsley.css">
    <link rel="stylesheet" href="/assets/vendor/table-dragger/table-dragger.min.css">

    <%-- MAIN CSS --%>
    <link rel="stylesheet" href="/assets/css/main.css">
    <link rel="stylesheet" href="/assets/css/color_skins.css">
    <%-- PAGE LEVEL CSS --%>
    <c:forEach var="pageCss" items="${pageStyles}">
        <link type="text/css" rel="stylesheet" media="all" href='<c:out value="${pageCss}"/>'>
    </c:forEach>

    <script src="/assets/vendor/jquery/jquery-3.3.1.min.js"></script>
    <script src="/assets/vendor/underscore/underscore-min.js"></script>
    <script src="/assets/vendor/bootstrap/js/popper.min.js"></script>
    <script src="//cdnjs.cloudflare.com/ajax/libs/toastr.js/latest/js/toastr.min.js"></script>
    <%--<script src="/assets/vendor/sweetalert/sweetalert.min.js"></script>--%>
    <%--<script src="https://unpkg.com/sweetalert/dist/sweetalert.min.js"></script>--%>
    <script src="https://cdn.jsdelivr.net/npm/sweetalert2@7.28.2/dist/sweetalert2.all.min.js"></script>
    <script src="/assets/vendor/bootstrap/js/bootstrap.js"></script>
    <script src="/assets/js/pages/ui/page.js"></script>

    <%-- PAGE LEVEL JS --%>
    <c:forEach var="pageJS" items="${pageTopScripts}">
        <script type="text/javascript" src='<c:out value="${pageJS}"/>'></script>
    </c:forEach>

    <style>
        a:focus {outline:0;}
        .card.inner {
            border: 1px solid #CCC;
            background-color: #f4f7f6;
        }
        .card.inner-outline {
            border: 1px solid #CCC;
        }

        .card.inner.group {
            border: none;
            box-shadow: none;
            margin-bottom: 0 !important;
        }

        .card.inner.group legend {
            font-size: 1.2rem;
        }
        .card.inner.group > div.body {
            padding-left: 4px;
            padding-bottom: 0 !important;
        }
        .card.inner .form-button-group {
            float: right;
            margin: 10px 10px 30px !important;
        }

        .dropdown-menu .dropdown-item {
            cursor: pointer;
        }

        .parsley-errors-list {
            margin-top: -8px;
            padding-bottom: 8px;
        }

        .fancy-checkbox input.parsley-error[type="checkbox"]+span:before {
            border-color: #efd8d8;
            background-color: #fbf5f5;
        }

        .fancy-radio input.parsley-error[type="radio"]+span i {
            border-color: #efd8d8;
            background-color: #fbf5f5;
        }

        .fancy-checkbox input[type="checkbox"]+span:before {
            background-color: #ffffff;
        }

        .fancy-radio input[type="radio"]+span i {
            background-color: #ffffff;
        }

        .theme-orange .fancy-radio input[type="radio"]:checked + span i:after {
            background-color: #fff
        }

        .theme-orange .fancy-radio input[type="radio"]:checked + span i {
            background-color: #fff
        }

        .theme-orange .fancy-radio input[type="radio"]:checked + span i:after {
            background-color: #f68c1f
        }

        .dd-nodrag {
            cursor: not-allowed !important;
        }

        .js-scope-selector, .js-scopable, .js-channel-selector {
            cursor: pointer;
        }

        .js-locked {
            float: right;
            margin-right: 12px;
            margin-top: -24px;
            position: relative;
            z-index: 2;
            color: grey;
        }
    </style>

</head>
<body class="theme-orange">

<%-- Page Loader --%>
<div class="page-loader-wrapper">
    <div class="loader">
        <div class="m-t-30"><img src="/assets/img/logo.png" width="98" alt="BIGNAME PIM"></div>
        <p>Please wait...</p>
    </div>
</div>
<%-- Overlay For Sidebars --%>

<div id="wrapper">
    <nav class="navbar navbar-fixed-top">
        <tiles:insertAttribute name="header"/>
    </nav>

    <div id="left-sidebar" class="sidebar">
        <div class="sidebar-scroll">
            <div class="user-account">
                <img src="/assets/img/seth.jpg" class="rounded-circle user-photo" alt="User Profile Picture">
                <div class="dropdown">
                    <span>Welcome,</span>
                    <a href="javascript:void(0);" class="dropdown-toggle user-name" data-toggle="dropdown"><strong>Seth Newman</strong></a>
                    <ul class="dropdown-menu dropdown-menu-right account">
                        <li><a href="page-profile2.html"><i class="icon-user"></i>My Profile</a></li>
                        <li><a href="app-inbox.html"><i class="icon-envelope-open"></i>Messages</a></li>
                        <li><a href="javascript:void(0);"><i class="icon-settings"></i>Settings</a></li>
                        <li class="divider"></li>
                        <li><a href="/logout"><i class="icon-power"></i>Logout</a></li>
                    </ul>
                </div>
                <hr>
            </div>
            <tiles:insertAttribute name="leftNav"/>
        </div>
    </div>
    <div id="main-content">
        <div class="container-fluid">
            <div class="block-header">
                <div class="row">
                    <div>
                        <c:forEach items="${breadcrumbs.breadcrumbs}" var="breadcrumb" varStatus="crumbStatus">
                            <c:if test="${crumbStatus.first}"><h2><a href="javascript:void(0);" class="btn btn-xs btn-link btn-toggle-fullwidth"><i class="fa fa-arrow-left"></i></a> ${breadcrumbs.title}</h2></c:if>
                            <c:if test="${crumbStatus.first}"><ul class="breadcrumb"></c:if>
                            <li class="breadcrumb-item"><c:if test="${breadcrumb[1] ne ''}"><a href="${breadcrumb[1]}"></c:if> <c:choose><c:when test="${breadcrumb[0] eq 'HOME'}"><i class="icon-home"></i></c:when><c:otherwise>${breadcrumb[0]}</c:otherwise></c:choose><c:if test="${breadcrumb[1] ne ''}"></a></c:if></li>
                            <c:if test="${crumbStatus.last}"></ul></c:if>
                        </c:forEach>

                    </div>
                </div>
            </div>
            <div id="js-body-container">
            <tiles:insertAttribute name="body"/>
            </div>
        </div>
    </div>
</div>
<%-- Javascript --%>

<%--<script src="/assets/bundles/libscripts.bundle.js"></script>--%>
<script src="/assets/js/pages/ui/global.js"></script>

<script src="/assets/bundles/vendorscripts.bundle.js"></script>

<%--<script src="/assets/bundles/datatablescripts.bundle.js"></script>--%>
<script src="/assets/vendor/jquery-datatable/jquery.dataTables.min.js"></script>
<script src="/assets/vendor/jquery-datatable/dataTables.bootstrap4.min.js"></script>
<script src="/assets/js/pages/tables/jquery-datatable-conditional-paging.js"></script>
<script src="/assets/js/pages/tables/jquery-datatable.js"></script>
<script src="/assets/vendor/nestable/jquery.nestable.js"></script>


<script src="/assets/vendor/bootstrap-multiselect/bootstrap-multiselect.js"></script>
<script src="/assets/vendor/bootstrap-datepicker/js/bootstrap-datepicker.min.js"></script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/jstree/3.2.1/jstree.min.js"></script>
<script src="/assets/vendor/parsleyjs/js/parsley.min.js"></script>

<script src="/assets/bundles/mainscripts.bundle.js"></script>


<script src="/assets/js/pages/ui/dialogs.js"></script>
<script src="/assets/vendor/table-dragger/table-dragger.min.js"></script>
<script src="/assets/vendor/emodal/eModal.js"></script>
<%--<script src="/assets/js/pages/ui/website/website.js"></script>--%>
<%-- PAGE LEVEL JS --%>
<c:forEach var="customJS" items="${pageBottomScripts}">
    <script src="<c:out value='${customJS}'/>"></script>
</c:forEach>
<tiles:insertAttribute name="footer" ignore="true"/>
<tiles:insertAttribute name="script" ignore="true" />

</body>

<%--<script>
    $(function(){
            $('a.nav-link[href*="' + window.location.hash + '"]').trigger('click');
    });
</script>--%>
</html>