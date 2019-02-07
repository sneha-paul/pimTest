<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles-extras" prefix="tilesx"%>

<tilesx:useAttribute ignore="true" id="pageStyles" name="pageStyles" classname="java.util.List"/>
<tilesx:useAttribute ignore="true" id="pageTopScripts" name="pageTopScripts" classname="java.util.List"/>
<tilesx:useAttribute ignore="true" id="pageBottomScripts" name="pageBottomScripts" classname="java.util.List"/>


<!doctype html>
<html lang="en">
<head>
    <title><tiles:insertAttribute name="title" ignore="true" defaultValue="BIGNAME - PIM" /></title>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=Edge">
    <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=0">
    <meta name="description" content="<tiles:insertAttribute name="metaDescription" ignore="true"/>">
    <meta name="keyword" content="<tiles:insertAttribute name="metaKeyword" ignore="true"/>">
    <link href="/assets/img/favicon.png" rel="shortcut icon" type="image/x-icon">
    <!-- VENDOR CSS -->
    <link rel="stylesheet" href="/assets/vendor/bootstrap/css/bootstrap.css">
    <link rel="stylesheet" href="/assets/vendor/font-awesome/css/font-awesome.css">
    <link rel="stylesheet" type="text/css" href="/assets/vendor/sweetalert/sweetalert.css"/>

    <!-- MAIN CSS -->
    <link rel="stylesheet" href="/assets/css/main.css">
    <link rel="stylesheet" href="/assets/css/color_skins.css">

    <script src="/assets/vendor/jquery/jquery-3.3.1.min.js"></script>
    <script src="/assets/vendor/underscore/underscore-min.js"></script>
    <script src="/assets/vendor/bootstrap/js/bootstrap.js"></script>

    <script src="https://cdn.jsdelivr.net/npm/sweetalert2@7.28.2/dist/sweetalert2.all.min.js"></script>
    <script src="//cdnjs.cloudflare.com/ajax/libs/toastr.js/latest/js/toastr.min.js"></script>

    <script src="/assets/js/pages/ui/page.js"></script>
    <script src="/assets/js/pages/ui/global.js"></script><%-- should be called after page.js--%>

    <style>
        a:focus {outline:0;}
        .auth-box.wide {
            width: 75%;
        }
        .js-hidden {
            display: none;
        }
        .scrollable {
            overflow: auto;
        }
        .scrollable::-webkit-scrollbar-track {
            -webkit-box-shadow: inset 0 0 6px rgba(0,0,0,0.3);
            background-color: #ffffff;
            padding: 1px;
            box-shadow: none;
        }

        .scrollable::-webkit-scrollbar {
            width: 10px;
            background-color: #f8f8f8;
        }

        .scrollable::-webkit-scrollbar-thumb {
            background-color: #7f7f7f;
            border: 2px solid #ffffff;
            border-radius: 5px;
        }
    </style>

</head>
<body class="theme-blue">
    <!-- WRAPPER -->
    <div id="wrapper">
        <div class="vertical-align-wrap">
            <div class="vertical-align-middle auth-main">
                <div class="auth-box <tiles:insertAttribute name="layout" ignore="true"/>">
                    <div class="top">
                        <img style="margin:0 0 -7px 11px" src="/assets/img/bnlogo-white.png" alt="BIGNAME">
                    </div>
                    <div id="js-body-container">
                        <tiles:insertAttribute name="body"/>
                    </div>
                </div>
            </div>
        </div>
    </div>
</body>
</html>