<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<tiles:insertDefinition name="headlessLayout">
    <tiles:putAttribute name="title" value="BIGNAME - PIM"/>
    <tiles:putAttribute name="body">
        <div class="card">
            <div class="header">
                <p class="lead">Recover my password</p>
            </div>
            <div class="body">
                <p>Please enter your email address below to receive instructions for resetting password.</p>
                <form class="form-auth-small" action="index.html">
                    <div class="form-group">
                        <input type="password" class="form-control" id="signup-password" placeholder="Password">
                    </div>
                    <button type="submit" class="btn btn-primary btn-lg btn-block">RESET PASSWORD</button>
                    <div class="bottom">
                        <span class="helper-text">Know your password? <a href="/login">Login</a></span>
                    </div>
                </form>
            </div>
        </div>
    </tiles:putAttribute>
</tiles:insertDefinition>

