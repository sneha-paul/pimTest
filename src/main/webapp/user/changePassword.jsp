<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<div class="row clearfix">
    <div class="col-lg-12 col-md-12">
        <div class="card">
            <div class="header">
                <h2>${user.userName} <small><code class="highlighter-rouge">${user.email}</code></small><small class="pull-right m-t--15"><code style="color:#808080">_id: ${user.id}</code></small></h2>
            </div>
            <div class="body">
                <form method="post" action="/pim/users/changePassword/${user.email}" data-method="PUT" data-success-message='["Successfully updated password"]' data-error-message='["Correct the validation error and try again", "Invalid Data"]' >
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
                    <button type="submit" class="btn btn-primary" onclick="$.submitAction(event, this)">Save</button>
                    <a href="${breadcrumbs.backURL}"><button type="button" class="btn btn-danger">Cancel</button></a>
                    <img src="/assets/img/tiny.png" onload="$.initAHAH(this)"/>
                </form>
            </div>
        </div>
    </div>
</div>

