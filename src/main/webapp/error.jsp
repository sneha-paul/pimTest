<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<tiles:insertDefinition name="headlessLayout">
    <tiles:putAttribute name="title" value="BIGNAME - PIM"/>
    <tiles:putAttribute name="body">

        <div class="card">
            <div class="header">
                <h3>
                    <span class="clearfix title">
                        <span class="number left">${status}</span> <span class="text"><br/>${error}</span>
                    </span>
                </h3>
            </div>
            <div class="body">
                <c:choose>
                    <c:when test="${status eq 404}">
                        <p>The page you were looking for could not be found, please <a href="javascript:void(0);">click here</a> to report this issue.</p>
                        <div class="margin-top-30">
                            <a href="javascript:history.go(-1)" class="btn btn-default"><i class="fa fa-arrow-left"></i> <span>Go Back</span></a>
                            <a href="/" class="btn btn-primary"><i class="fa fa-home"></i> <span>Home</span></a>
                        </div>
                    </c:when>
                    <c:when test="${status eq 403}">
                        <p>You don't have permission to access this page on this server.</p>
                        <div class="margin-top-30">
                            <a href="javascript:history.go(-1)" class="btn btn-default"><i class="fa fa-arrow-left"></i> <span>Go Back</span></a>
                            <a href="/" class="btn btn-primary"><i class="fa fa-home"></i> <span>Home</span></a>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <p>Apparently we're experiencing an error. But don't worry, we will solve it shortly.
                            <br>Please <a href="javascript:void(0);">click here</a> to report this issue.</p>
                        <div class="margin-top-30">
                            <a href="javascript:$('.js-error.btn').toggle();$('.js-error-details').fadeToggle()" class="js-error btn btn-danger"><i class="fa fa-warning"></i> <span>Show Error</span></a>
                            <a href="javascript:$('.js-error.btn').toggle();$('.js-error-details').fadeToggle()" class="js-error btn btn-danger" style="display: none"><i class="fa fa-warning"></i> <span>Hide Error</span></a>
                            <a href="/" class="btn btn-primary"><i class="fa fa-home"></i> <span>Home</span></a>
                        </div>
                        <div class="js-error-details m-t-25 scrollable" style="max-height: 500px; display: none">
                            <p>${message}
                            </br><p><code>${trace}</code></p>
                        </div>
                    </c:otherwise>
                </c:choose>
<!--
----------------------------------------------------------------- Start error details ----------------------------------------------------------------------------------------------
${message}
${timestamp}
${exception}
${trace}
----------------------------------------------------------------- End error details   ---------------------------------------------------------------------------------------------->
            </div>
        </div>
    </tiles:putAttribute>
</tiles:insertDefinition>