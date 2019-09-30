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
    <link rel="stylesheet" type="text/css" href="/assets/vendor/bootstrap/css/bootstrap.css">
    <link rel="stylesheet" type="text/css" href="https://cdnjs.cloudflare.com/ajax/libs/jstree/3.2.1/themes/default/style.min.css" />
    <link rel="stylesheet" type="text/css" href="/assets/vendor/bootstrap-datepicker/css/bootstrap-datepicker3.min.css">
    <link rel="stylesheet" type="text/css" href="/assets/vendor/font-awesome/css/font-awesome.css">
    <link rel="stylesheet" type="text/css" href="//cdnjs.cloudflare.com/ajax/libs/toastr.js/latest/css/toastr.min.css" />
    <link rel="stylesheet" type="text/css" href="/assets/vendor/light-gallery/css/lightgallery.css">
    <link rel="stylesheet" type="text/css" href="/assets/vendor/nestable/jquery-nestable.css">
    <link rel="stylesheet" type="text/css" href="/assets/vendor/sweetalert/sweetalert.css"/>
    <link rel="stylesheet" type="text/css" href="/assets/vendor/jquery-datatable/dataTables.bootstrap4.min.css">
    <link rel="stylesheet" type="text/css" href="/assets/vendor/jquery-datatable/rowReorder.bootstrap4.css">
    <link rel="stylesheet" type="text/css" href="/assets/vendor/jquery-multiselect/css/multi-select.css">
    <link rel="stylesheet" type="text/css" href="/assets/vendor/bootstrap-multiselect/bootstrap-multiselect.css">
    <link rel="stylesheet" type="text/css" href="/assets/vendor/parsleyjs/css/parsley.css">
    <link rel="stylesheet" type="text/css" href="/assets/vendor/table-dragger/table-dragger.min.css">
    <link rel="stylesheet" type="text/css" href="/assets/vendor/dropzone/css/dropzone.css">
    <link rel="stylesheet" type="text/css" href="/assets/vendor/gijgo/css/gijgo.min.css" />
    <link rel="stylesheet" type="text/css" href="/assets/vendor/handsontable/handsontable.full.min.css"  media="screen">
    <%-- MAIN CSS --%>
    <link rel="stylesheet" href="/assets/css/main.css">
    <link rel="stylesheet" href="/assets/css/color_skins.css">
    <link rel="stylesheet" href="/assets/css/pim.css">
    <%-- PAGE LEVEL CSS --%>
    <c:forEach var="pageCss" items="${pageStyles}">
        <link type="text/css" rel="stylesheet" media="all" href='<c:out value="${pageCss}"/>'>
    </c:forEach>
    <script src="/assets/vendor/jquery/jquery-3.3.1.min.js"></script>
    <script src="/assets/vendor/underscore/underscore-min.js"></script>
    <script src="/assets/vendor/bootstrap/js/popper.min.js"></script>
    <script src="//cdnjs.cloudflare.com/ajax/libs/toastr.js/latest/js/toastr.min.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/sweetalert2@7.28.2/dist/sweetalert2.all.min.js"></script>
    <script src="/assets/vendor/bootstrap/js/bootstrap.js"></script>
    <script src="/assets/vendor/jquery-multiselect/js/jquery.multi-select.js"></script>
    <script src="/assets/vendor/bootstrap-datepicker/js/bootstrap-datepicker.min.js"></script>
    <script src="/assets/vendor/gijgo/js/gijgo.min.js" type="text/javascript"></script>
    <script src="/assets/js/pages/ui/page.js"></script>
    <script src="/assets/vendor/jquery-datatable/jquery.dataTables.min.js"></script>
    <script src="/assets/js/pages/ui/GridUtil.js"></script>
    <script src="/assets/vendor/jquery/jquery-sortable.js"></script>
    <%-- PAGE LEVEL JS --%>
    <c:forEach var="pageJS" items="${pageTopScripts}">
        <script type="text/javascript" src='<c:out value="${pageJS}"/>'></script>
    </c:forEach>
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
            <div class="block-header p-l-25">
                <div class="row">
                    <div>
                        <c:if test="${not empty breadcrumbs}"><h2>${breadcrumbs.title}</h2></c:if>
                        <c:forEach items="${breadcrumbs.breadcrumbs}" var="breadcrumb" varStatus="crumbStatus">
                            <c:if test="${crumbStatus.first}"><ul class="breadcrumb" style="position: relative; top: 17px"></c:if>
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
<script src="/assets/vendor/handsontable/handsontable.full.min.js"></script>
<script src="https://cdn.rawgit.com/nnattawat/flip/master/dist/jquery.flip.min.js"></script>
<script src="/assets/js/pages/ui/global.js"></script><%-- should be called after page.js--%>
<script src="/assets/bundles/vendorscripts.bundle.js"></script>
<script src="/assets/vendor/jquery-datatable/dataTables.bootstrap4.js"></script>
<script src="/assets/vendor/jquery-datatable/dataTables.rowReorder.js"></script>
<script src="/assets/js/pages/tables/jquery-datatable-conditional-paging.js"></script>
<script src="/assets/js/pages/tables/jquery-datatable.js"></script>
<script src="/assets/vendor/nestable/jquery.nestable.js"></script>
<script src="/assets/vendor/dropzone/js/dropzone.js"></script>
<script src="/assets/vendor/parsleyjs/js/parsley.min.js"></script>


<script src="/assets/bundles/mainscripts.bundle.js"></script>


<script src="/assets/js/pages/ui/dialogs.js"></script>
<script src="/assets/vendor/table-dragger/table-dragger.min.js"></script>
<script src="/assets/vendor/emodal/eModal.js"></script>
<script src="/assets/vendor/light-gallery/js/lightgallery-all.min.js"></script>
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
    // $('.main-menu li a').mouseenter( function(){
    //     $(this).closest('#left-sidebar').addClass('expanded-bar');
    // });
    // $('.main-menu li a').mouseleave( function(){
    //     $(this).closest('#left-sidebar').removeClass('expanded-bar');
    // });

</script>
</html>