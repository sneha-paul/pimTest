<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<div class="row clearfix">
    <div class="col-lg-12 col-md-12">
        <div class="card">
            <div class="body">
                <ul class="nav nav-tabs-new2">
                    <li class="nav-item"><a class="nav-link active show" data-toggle="tab" href="#DETAILS">Details</a></li>
                    <li class="nav-item"><a class="nav-link js-logs-tab" data-toggle="tab" href="#logs">Logs</a></li>
                </ul>
                <div class="tab-content">
                    <div class="tab-pane show active" id="DETAILS">
                        <div class="row clearfix m-t-20">
                            <div class="col-lg-12 col-md-12">
                                <div class="card">
                                    <div class="body">
                                        <form method="post" action="/pim/jobs/${jobInstance.id}" data-method="PUT">
                                            <div class="row">
                                                <div class="col-md-6 col-sm-12">
                                                    <div class="form-group">User</label><code class="highlighter-rouge m-l-10">*</code>
                                                        <input type="text" id="userName" name="userName" value="${jobInstance.user}" class="form-control" readonly/>
                                                    </div>
                                                    <div class="form-group">
                                                        <label for="jobName">Job Name</label><code class="highlighter-rouge m-l-10">*</code>
                                                        <input type="text" id="jobName" name="jobName" class="form-control" value="${jobInstance.jobName}" readonly/>
                                                    </div>
                                                    <div class="form-group">
                                                        <label for="scheduledStartTime">Scheduled Time</label><code class="highlighter-rouge m-l-10">*</code>
                                                        <input type="text" id="scheduledStartTime" name="scheduledStartTime" class="form-control" value="${jobInstance.scheduledStartTime}" readonly/>
                                                    </div>
                                                    <div class="form-group">
                                                        <label for="completedTime">Completed Time</label><code class="highlighter-rouge m-l-10">*</code>
                                                        <input type="text" id="completedTime" name="completedTime" class="form-control" value="${jobInstance.completedTime}" readonly/>
                                                    </div>
                                                    <div class="form-group">
                                                        <label for="status">Status</label><code class="highlighter-rouge m-l-10">*</code>
                                                        <input type="text" id="status" name="status" class="form-control" value="${jobInstance.status}" readonly/>
                                                    </div>
                                                </div>
                                            </div>
                                            <br>
                                            <input type="hidden" name="group" value="DETAILS"/>
                                            <a href="/pim/jobs"><button type="button" class="btn btn-danger">Cancel</button></a>
                                        </form>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="tab-pane" id="logs">
                        <div class="row clearfix">
                            <div class="col-lg-12 col-md-12">
                                <div class="card">
                                    <div class="body">
                                        <div class="table-responsive">
                                            <table id="paginatedLogsTable" class="table table-hover dataTable table-custom" style="width: 100%">
                                                <thead class="thead-dark">

                                                </thead>

                                            </table>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>
<script>
    $.initPage({
        'jobInstanceId' : '${jobInstance.id}'
    });
</script>
<script src="/assets/js/pages/ui/job/job.js"></script>