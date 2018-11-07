<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<tiles:insertDefinition name="headlessLayout">
    <tiles:putAttribute name="title" value="BIGNAME - PIM"/>
    <tiles:putAttribute name="body">
        <div class="card">
            <div class="header">
                <p class="lead">Create an account</p>
            </div>
           <%-- <div class="body">
                <form class="form-auth-small">
                    <div class="form-group">
                        <label for="signup-email" class="control-label sr-only">Email</label>
                        <input type="email" class="form-control" id="signup-email" placeholder="Your email">
                    </div>
                    <div class="form-group">
                        <label for="signup-password" class="control-label sr-only">Password</label>
                        <input type="password" class="form-control" id="signup-password" placeholder="Password">
                    </div>
                    <button type="submit" class="btn btn-primary btn-lg btn-block">REGISTER</button>
                    <div class="bottom">
                        <span class="helper-text">Already have an account? <a href="/login">Login</a></span>
                    </div>
                </form>
                <div class="separator-linethrough"><span>OR</span></div>
                <button class="btn btn-signin-social"><i class="fa fa-google google-color"></i> Sign in with Google</button>
                <button class="btn btn-signin-social"><i class="fa fa-facebook-official facebook-color"></i> Sign in with Facebook</button>
            </div>--%>
            <div class="body">
                <form class="form-auth-small" method="post" action="/pim/users" data-method="POST">
                    <div class="form-group">
                        <label for="userName" class="control-label sr-only">Name</label>
                        <input type="text" class="form-control" id="userName" name="userName" placeholder="First Name  Last Name">
                    </div>
                    <div class="form-group">
                        <label for="email" class="control-label sr-only">Email</label>
                        <input type="email" class="form-control" id="email" name="email" placeholder="Your email">
                    </div>
                    <div class="form-group">
                        <label for="password" class="control-label sr-only">Password</label>
                        <input type="password" class="form-control" id="password" name="password" placeholder="Password">
                    </div>
                        <%--<div class="form-group">
                            <label for="signup-passwordConfirmation" class="control-label sr-only">Password Confirmation</label>
                            <input type="password" class="form-control" id="signup-passwordConfirmation" placeholder="Password">
                        </div>--%>
                    <input type="hidden" name="group" value="CREATE"/>
                    <button type="submit" class="btn btn-primary btn-lg btn-block" onclick="$.submitAction(event, this)">REGISTER</button>
                    <div class="bottom">
                        <span class="helper-text">Already have an account? <a href="/login">Login</a></span>
                    </div>

                </form>
                <div class="separator-linethrough"><span>OR</span></div>
                <button class="btn btn-signin-social"><i class="fa fa-google google-color"></i> Sign in with Google</button>
                <button class="btn btn-signin-social"><i class="fa fa-facebook-official facebook-color"></i> Sign in with Facebook</button>
            </div>
        </div>
    </tiles:putAttribute>
</tiles:insertDefinition>