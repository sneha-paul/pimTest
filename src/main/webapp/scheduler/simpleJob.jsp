<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<div class="row clearfix">
    <div class="col-lg-12 col-md-12">
        <form method="post" action="/pim/scheduler" data-method="POST" data-success-message='["Successfully scheduled new job", "Job Scheduled"]' data-error-message='["Correct the validation error and try again", "Invalid Data"]' >
            <div class="row">
                <div class="col-md-6 col-sm-12">
                    <div class=" ">
                        <label>Job Name</label>
                        <input type="text" name="jobName" class="form-control" />
                    </div>
                    <%--<div class="">
                        <label>Schedule Time</label>
                        <input type="date" name="scheduledStartTime" id="scheduledStartTime" class="form-control" />
                    </div>--%>
                </div>
            </div>
            <br>
            <input type="hidden" name="group" value="CREATE"/>
            <img src="/assets/img/tiny.png" onload="$.initAHAH(this)"/>
        </form>
    </div>
</div>