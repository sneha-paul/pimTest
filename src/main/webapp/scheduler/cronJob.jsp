<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<%--<c:choose>
    <c:when test="${mode eq 'DETAILS'}">
        <tiles:insertDefinition name="mainLayout">
            <tiles:putAttribute name="title" value="PIM - SimpleJobScheduling"/>
            <tiles:putAttribute name="body" value="/scheduler/simpleJob_body.jsp"/>
        </tiles:insertDefinition>
    </c:when>
    <c:otherwise>--%>
<div class="row clearfix">
    <div class="col-lg-12 col-md-12">
        <form method="post" action="/pim/scheduler/cronJob" data-method="POST" data-success-message='["Successfully scheduled new job", "Job Scheduled"]' data-error-message='["Correct the validation error and try again", "Invalid Data"]' >
            <div class="row">
                <div class="col-md-6 col-sm-12">
                    <div class="">
                        <label>Job Name</label>
                        <input type="text" name="jobName" class="form-control" />
                    </div>
                    <div class="form-group js-name">
                        <label>Cron Expression</label>
                        <input type="text" name="cronExpression" class="form-control" />
                    </div>
                </div>
            </div>
            <br>
            <input type="hidden" name="group" value="CREATE"/>
            <img src="/assets/img/tiny.png" onload="$.initAHAH(this)"/>
        </form>
    </div>
</div>
<%--</c:otherwise>
</c:choose>--%>
