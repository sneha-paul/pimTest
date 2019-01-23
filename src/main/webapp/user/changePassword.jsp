<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<div class="row clearfix">
    <div class="col-lg-12 col-md-12">
        <form method="post" action="/pim/users/changePassword" data-method="PUT" data-success-message='["Successfully updated password"]' data-error-message='["Correct the validation error and try again", "Invalid Data"]' >
            <input type="hidden" name="id" value="${user.email}"/>
            <div class="row">
                <div class="col-md-6 col-sm-12">
                    <div class="form-group">
                        <label>New Password</label>
                        <input type="password" name="password" class="form-control" />
                    </div>
                    <div class="form-group">
                        <label>Confirm Password</label>
                        <input type="password" name="confirmPassword" class="form-control" />
                    </div>
                </div>
            </div>
            <br>
            <input type="hidden" name="group" value="CHANGE-PASSWORD"/>
            <img src="/assets/img/tiny.png" onload="$.initAHAH(this)"/>
        </form>
    </div>
</div>

