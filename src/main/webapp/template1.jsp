<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles-extras" prefix="tilesx"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

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
    <%--<link rel="stylesheet" href="/assets/vendor/bootstrap-treeview/bootstrap-treeview.min.css">--%>
    <link rel="stylesheet" href="/assets/vendor/font-awesome/css/font-awesome.css">
    <link rel="stylesheet" href="//cdnjs.cloudflare.com/ajax/libs/toastr.js/latest/css/toastr.min.css" />
    <link rel="stylesheet" href="/assets/vendor/light-gallery/css/lightgallery.css">
    <link rel="stylesheet" href="/assets/vendor/nestable/jquery-nestable.css">
    <link rel="stylesheet" href="/assets/vendor/sweetalert/sweetalert.css"/>
    <link rel="stylesheet" href="/assets/vendor/jquery-datatable/dataTables.bootstrap4.min.css">
    <link rel="stylesheet" href="/assets/vendor/jquery-datatable/rowReorder.bootstrap4.css">
    <link rel="stylesheet" href="/assets/vendor/bootstrap-multiselect/bootstrap-multiselect.css">
    <link rel="stylesheet" href="/assets/vendor/parsleyjs/css/parsley.css">
    <link rel="stylesheet" href="/assets/vendor/table-dragger/table-dragger.min.css">
    <link rel="stylesheet" href="/assets/vendor/dropzone/css/dropzone.css">

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
    <script src="/assets/vendor/jquery-datatable/jquery.dataTables.min.js"></script>
    <script src="/assets/js/pages/ui/GridUtil.js"></script>
    <script src="/assets/vendor/jquery/jquery-sortable.js"></script>


<%-- PAGE LEVEL JS --%>
    <c:forEach var="pageJS" items="${pageTopScripts}">
        <script type="text/javascript" src='<c:out value="${pageJS}"/>'></script>
    </c:forEach>

    <style>

        body.dragging, body.dragging * {
            cursor: move !important;
        }

        .dragged {
            position: absolute;
            opacity: 0.5;
            z-index: 2000;
        }

        .dt-clear {
            position: absolute;
            right: 44px;
            top:10px;
            font-weight: bold;
            cursor: pointer;
        }

        .dataTables_filter input {
            padding-right:20px;
        }


        .popup-content {
            padding: 20px;
        }

        .modal-dt > div:first-of-type > div:nth-of-type(2) {
            height: calc(55vh + 50px);
            overflow: auto;
            padding-bottom:25px;
            border: 1px solid #00000;
        }
        .modal-dt > div:first-of-type > div:nth-of-type(3) {
            position: fixed;
            bottom: 14px;
            width: 68%;
        }

        .modal-dt .dataTable {
            margin-top:2px !important;
            background-color: #f8f8f8;
        }

        .modal-dt > div:first-of-type > div:nth-of-type(2)::-webkit-scrollbar-track {
            -webkit-box-shadow: inset 0 0 6px rgba(0,0,0,0.3);
            background-color: #ffffff;
            padding: 1px;
            box-shadow: none;
        }

        .modal-dt > div:first-of-type > div:nth-of-type(2)::-webkit-scrollbar {
            width: 10px;
            background-color: #f8f8f8;
        }

        .modal-dt > div:first-of-type > div:nth-of-type(2)::-webkit-scrollbar-thumb {
            background-color: #7f7f7f;
            border: 2px solid #ffffff;
            border-radius: 5px;
        }

        div.js-draggable .placeholder:before {
            position: absolute;
        }

        div.js-draggable .placeholder{
            vertical-align: middle;
            display: inline-block;
            background-color: #CCC;
            position: relative;
            border: 2px dashed #000;
            line-height: 1.428571429;
            background-color: #fff;
            border-radius: 4px;
            padding: 4px;
        }

        a:focus {outline:0;}
        .disable-select {
            -webkit-user-select: none;
            -moz-user-select: none;
            -ms-user-select: none;
            user-select: none;
        }
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

        .fancy-checkbox.js-disabled input[type="checkbox"]+span:before {
            opacity: .4;
            background-color: #e9ecef;
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
            text-align: center;
        }

        .js-locked, .js-level-locked, .js-axis-locked {
            float: right;
            margin-right: 12px;
            margin-top: -24px;
            position: relative;
            z-index: 2;
            color: #dc3545;
            cursor: pointer;
        }

        select[disabled="disabled"] {
            -webkit-appearance: none;
            -moz-appearance: none;

        }

        select[disabled="disabled"]::-ms-expand {
            display: none;
        }

        .search-btn {
            border-top-left-radius: 0;
            border-bottom-left-radius: 0;
            margin-top: -3px;
            margin-left: -4px;
        }

        .js-handle {
            /*text-align: center;*/
        }
        td.js-handle {
            cursor: move;
        }

        .js-locked.js-checkbox, .js-level-locked.js-checkbox, .js-axis-locked.js-checkbox {
            float: left;
            margin-left: 5px;
            margin-top: -31px;
            position: absolute;
        }

        .js-level-locked {
            color: #007bff;
        }

        .js-axis-locked {
            color: #fd7e14;
        }

        textarea {
            min-height: 54px !important;
            max-height: 260px !important;
        }

        .tab_btn {
            height: 32px;
            width: 32px;
            border-radius: 32px;
            line-height: 32px;
            background: #01b2c6;
            color: #fff;
            font-weight: 700;
            text-align: center;
            font-size: 12px;
            display: inline-block;
        }

        .btn-round {
            border-radius: 40px;
            width: 40px;
            height: 40px;
        }
        .digital-asset-container {
            max-height:520px;
        }

        .digital-asset {
            padding-bottom: 20px;
        }
            .digital-asset .asset-type {
                position:absolute;
                top:86px;
                right:30px;
            }
            /*.digital-asset .js-asset img {
                width:100%;
            }*/
        .card .footer .stats {
            cursor: default;
            list-style: none;
            padding: 0;
            display: inline-block;
            float: right;
            margin: 0;
            line-height: 35px;
        }

        .card .footer .stats li:first-child {
            border-left: 0;
            margin-left: 0;
            padding-left: 0;
        }

        .card .footer .stats li {
            border-left: solid 1px rgba(160, 160, 160, 0.3);
            display: inline-block;
            font-weight: 400;
            letter-spacing: 0.25em;
            line-height: 1;
            margin: 0 0 0 2em;
            padding: 0 0 0 2em;
            text-transform: uppercase;
            font-size: 13px;
        }

        .card .footer .stats li a {
            color: #777;
        }

        .card .footer {
            padding: 0 30px 50px 30px;
        }

        .js-pricing span.input-group-text {
            min-width: 88px;
        }

        .js-pricing span.input-group-text>div {
            width: 100%;
            text-align: right;
        }

        .js-tree-view span.icon.glyphicon {
            display: none;
        }
        .js-tree-view span.icon.node-icon.fa.fa-caret-right {
            margin-right: -5px;
        }

        .js-tree-view span.icon.node-icon.fa.fa-folder {
            margin-right: 11px;
        }

        .js-tree-view span.icon.node-icon.fa.fa-caret-down {
            margin-right: 0;
        }

        .js-external-id input {
            text-transform: uppercase;
        }

        /*table#paginatedPricingTable thead th {
            text-align: right;
            padding-right: 30px;
        }

        table#paginatedPricingTable thead th:first-child {
            text-align: left;
        }

        table#paginatedPricingTable tbody td {
            text-align: right;
            padding-right: 10px;
        }

        table#paginatedPricingTable tbody td:first-child {
            text-align: left;
            padding-right: 0;
        }*/

        .btn:focus, .btn:active {
            outline: none !important;
            box-shadow: none !important;
        }

        .treeDataTable .expanded-icons, tr.details .collapsed-icons {
            display: none;
        }
        .treeDataTable .collapsed-icons, tr.details .expanded-icons {
            display: block;
        }

        .treeDataTable .js-ctrl {
            visibility: hidden;
        }
        .treeDataTable tr.parent-node .js-ctrl {
            visibility: visible;
        }

        .no-filter .dataTables_filter {
            visibility: hidden;
        }

        .dropzone {
            border: 2px dashed #dedede;
            border-radius: 5px;
            background: #f5f5f5;
        }

        .dropzone i{
            /*font-size: 5rem;*/
        }

        .dropzone .dz-message {
            color: rgba(0,0,0,.54);
            font-weight: 500;
            font-size: initial;
            text-transform: uppercase;
        }

        .btn-label {
            display: inline-block;
            font-weight: 400;
            text-align: left;
            white-space: nowrap;
            vertical-align: middle;
            user-select: none;
            border: 1px solid transparent;
            padding: 0.375rem 0.75rem;
            font-size: 1rem;
            line-height: 1.5;
            border-radius: 0.25rem;
            width:14%;
            overflow: hidden;
            text-overflow: ellipsis;
            margin-right: 25px;
            margin-bottom: 25px;
            cursor: pointer;
        }

        .truncate {
            white-space: nowrap;
            overflow: hidden;
            text-overflow: ellipsis;
        }

        .dt-column {
            max-width:25%;
            white-space: nowrap;
            overflow: hidden;
            text-overflow: ellipsis;
        }

        .popup-content .btn-label {
            width:25%;
            font-size: .8rem;
        }

        .js-template, .js-hidden {
            display: none !important;
        }

        .js-invisible {
            visibility: hidden;
        }

        .btn-label.secondary {
            color: #6c757d;
            background-color: #fafafa;
            background-image: none;
            border-color: #dadada;
        }

        .file_manager .file {
            position: relative;
            border-radius: .55rem;
            overflow: hidden;
        }

        .file_manager .file .hover {
            position: absolute;
            right: 10px;
            top: 10px;
            display: none;
            transition: all 0.2s ease-in-out;
        }

        .file_manager .file a:hover .hover {
            display: block;
            transition: all 0.2s ease-in-out;
        }

        .file_manager .file .image, .file_manager .file .icon {
            width:100%;
            overflow: hidden;
            background-size: cover;
            background-position: top;
            text-align: center;
        }

        .file_manager .file .image, .digital-asset .image {
            background: url(/assets/img/transparent_bg.png);
            background-repeat: repeat;
            background-size: 100px 100px;
            height:200px;
            overflow: hidden;
        }

        .digital-asset .image {
            text-align: center;
            height: 350px;
        }

        .file_manager .file .image img/*, .digital-asset .image img*/ {
            max-width:100%;
            max-height: 220px;
        }

        /*.digital-asset .image img {
            max-height: 330px;
        }*/

        .file_manager .file .file-name {
            padding: 10px;
            border-top: 1px solid #f7f7f7;
        }

        .file_manager .file .file-name small .date {
            float: right;
        }

        .file_manager .selected .file .image {
            border-top: 4px solid #007bff;
            border-left: 4px solid #007bff;
            border-right: 4px solid #007bff;
        }

        .file_manager .selected .file .file-name {
            border-top: none;
            border-left: 4px solid #007bff;
            border-right: 4px solid #007bff;
            border-bottom: 4px solid #007bff;
        }

    </style>

</head>
<body class="theme-blue">

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
                <img src="/assets/img/<sec:authentication property="principal.avatar" />" class="rounded-circle user-photo" alt="User Profile Picture">
                <div class="dropdown">
                    <span>Welcome,</span>
                    <a href="javascript:void(0);" class="dropdown-toggle user-name" data-toggle="dropdown"><strong><sec:authentication property="principal.userName" /></strong></a>
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
<script src="https://cdn.rawgit.com/nnattawat/flip/master/dist/jquery.flip.min.js"></script>
<script src="/assets/js/pages/ui/global.js"></script><%-- should be called after page.js--%>

<script src="/assets/bundles/vendorscripts.bundle.js"></script>

<%--<script src="/assets/bundles/datatablescripts.bundle.js"></script>--%>

<script src="/assets/vendor/jquery-datatable/dataTables.bootstrap4.min.js"></script>
<script src="/assets/vendor/jquery-datatable/dataTables.rowReorder.js"></script>

<script src="/assets/js/pages/tables/jquery-datatable-conditional-paging.js"></script>
<script src="/assets/js/pages/tables/jquery-datatable.js"></script>
<script src="/assets/vendor/nestable/jquery.nestable.js"></script>
<script src="/assets/vendor/dropzone/js/dropzone.js"></script>


<script src="/assets/vendor/bootstrap-multiselect/bootstrap-multiselect.js"></script>
<script src="/assets/vendor/bootstrap-datepicker/js/bootstrap-datepicker.min.js"></script>
<%--<script src="/assets/vendor/bootstrap-treeview/bootstrap-treeview.js"></script>--%>
<%--<script src="https://cdnjs.cloudflare.com/ajax/libs/jstree/3.2.1/jstree.min.js"></script>--%>
<%--<script src="https://cdnjs.cloudflare.com/ajax/libs/jquery-mockjax/1.6.2/jquery.mockjax.js"></script>--%>
<script src="/assets/vendor/parsleyjs/js/parsley.min.js"></script>

<script src="/assets/bundles/mainscripts.bundle.js"></script>


<script src="/assets/js/pages/ui/dialogs.js"></script>
<script src="/assets/vendor/table-dragger/table-dragger.min.js"></script>
<script src="/assets/vendor/emodal/eModal.js"></script>
<script src="/assets/vendor/light-gallery/js/lightgallery-all.min.js"></script>
<%--<script src="/assets/js/pages/ui/website/website.js"></script>--%>
<%-- PAGE LEVEL JS --%>
<c:forEach var="customJS" items="${pageBottomScripts}">
    <script src="<c:out value='${customJS}'/>"></script>
</c:forEach>
<tiles:insertAttribute name="footer" ignore="true"/>
<tiles:insertAttribute name="script" ignore="true" />

</body>

<script>
    $(function(){
            $('a.nav-link[href*="' + window.location.hash + '"]').trigger('click');
    });
</script>
</html>