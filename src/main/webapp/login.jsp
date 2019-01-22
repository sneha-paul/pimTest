<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<tiles:insertDefinition name="headlessLayout">
    <tiles:putAttribute name="title" value="BIGNAME - PIM"/>
    <tiles:putAttribute name="body">
        <c:set var="contextPath" value="${pageContext.request.contextPath}"/>
            <div class="card">
                <div class="header">
                    <p class="lead">Login to your account</p>
                </div>
                <div class="body">
                    <form class="form-auth-small" method="POST" action="${contextPath}/login">
                    <c:if test="${param.error ne null}">
                        <span class="text-danger">Invalid username or password</span>
                    </c:if>

                    <c:if test="${param.logout ne null}">
                        <span class="text-success">Logged out Successfully</span>
                    </c:if>

                    <div class="form-group">
                        <label for="username" class="control-label sr-only">Email</label>
                        <input type="text" class="form-control" id="username" name="username" value="manu@blacwood.com" placeholder="User Name">
                    </div>
                    <div class="form-group">
                        <label for="password" class="control-label sr-only">Password</label>
                        <input type="password" class="form-control" name="password" id="password" value="temppass" placeholder="Password">
                    </div>
                        <%--<div class="form-group clearfix">
                            <label class="fancy-checkbox element-left">
                                <input type="checkbox">
                                <span>Remember me</span>
                            </label>
                        </div>--%>
                    <button type="submit" class="btn btn-primary btn-lg btn-block">LOGIN</button>


                <%-------------Registration disabled - Enable in 2nd phase----------------------------%>
                    <div class="bottom" hidden>
                        <span class="helper-text m-b-10"><i class="fa fa-lock"></i> <a href="forgotPassword">Forgot password?</a></span>
                        <span>Don't have an account? <a href="pim/user/create">Register</a></span>
                    </div>


                    </form>
                    <div class="separator-linethrough"><span>OR</span></div>
                    <button class="btn btn-signin-social"><i class="fa fa-google google-color"></i> Sign in with Google</button>
                    <button class="btn btn-signin-social"><i class="fa fa-facebook-official facebook-color"></i> Sign in with Facebook</button>
                </div>
            </div>
    </tiles:putAttribute>
</tiles:insertDefinition>
