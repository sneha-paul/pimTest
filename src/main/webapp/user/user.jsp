<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<c:choose>
    <c:when test="${mode eq 'DETAILS'}">
        <tiles:insertDefinition name="mainLayout">
            <tiles:putAttribute name="title" value="PIM - Users"/>
            <tiles:putAttribute name="body" value="/user/user_body.jsp"/>
        </tiles:insertDefinition>
    </c:when>
    <c:otherwise>
        <div class="row clearfix">
            <div class="col-lg-12 col-md-12">
                <div class="card">
                    <div class="body">
                        <form method="post" action="/pim/users" data-method="POST" data-success-message='["Successfully created the user", "User Created"]' data-error-message='["Correct the validation error and try again", "Invalid Data"]' >
                            <div class="row">
                                <div class="col-md-6 col-sm-12">
                                    <div class="form-group">
                                        <label>Name</label>
                                        <input type="text" class="form-control" id="userName" name="userName">
                                    </div>
                                    <div class="form-group">
                                        <label>Email</label>
                                        <input type="email" class="form-control" id="email" name="email">
                                    </div>
                                    <div class="form-group">
                                        <label>Password</label>
                                        <input type="password" class="form-control" id="password" name="password">
                                    </div>
                                </div>
                            </div>
                            <br>
                            <input type="hidden" name="group" value="CREATE"/>
                            <img src="/assets/img/tiny.png" onload="$.initAHAH(this)"/>
                        </form>
                    </div>
                </div>
            </div>
        </div>
    </c:otherwise>
</c:choose>