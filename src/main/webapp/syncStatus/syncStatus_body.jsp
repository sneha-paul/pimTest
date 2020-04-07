<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<div class="row clearfix">
    <div class="col-lg-12 col-md-12">
        <div class="card">
            <div class="body">
                <ul class="nav nav-tabs-new2">
                    <li class="nav-item"><a class="nav-link active show" data-toggle="tab" href="#DETAILS">Details</a></li>
                </ul>
                <div class="tab-content">
                    <div class="tab-pane show active" id="DETAILS">
                        <div class="row clearfix m-t-20">
                            <div class="col-lg-12 col-md-12">
                                <div class="card">
                                    <div class="body">
                                            <div class="row">
                                                <div class="col-md-6 col-sm-12">
                                                    <div class="form-group">User</label><code class="highlighter-rouge m-l-10">*</code>
                                                        <input type="text" id="userName" name="userName" value="${syncStatus.user}" class="form-control" readonly/>
                                                    </div>
                                                    <div class="form-group js-external-id">
                                                        <label for="timeStamp">Time Stamp</label><code class="highlighter-rouge m-l-10">*</code>
                                                        <input type="text" id="timeStamp" name="timeStamp" class="form-control" value="${syncStatus.timeStamp}" readonly/>
                                                    </div>
                                                    <div class="form-group">
                                                        <label for="entity">Entity</label><code class="highlighter-rouge m-l-10">*</code>
                                                        <input type="text" id="entity" name="entity" class="form-control" value="${syncStatus.entity}" readonly/>
                                                    </div>
                                                    <div class="form-group">
                                                        <label for="entityId">Entity Id</label><code class="highlighter-rouge m-l-10">*</code>
                                                        <input type="text" id="entityId" name="entityId" class="form-control" value="${syncStatus.entityId}" readonly/>
                                                    </div>
                                                    <div class="form-group">
                                                        <label for="message">Status</label><code class="highlighter-rouge m-l-10">*</code>
                                                        <textarea type="text" id="message" name="message" class="form-control" disabled>${syncStatus.status}</textarea>
                                                    </div>
                                                    <div class="form-group">
                                                        <label for="exportedTimeStamp">Exported Time</label><code class="highlighter-rouge m-l-10">*</code>
                                                        <c:choose>
                                                            <c:when test="${not empty syncStatus.exportedTimeStamp}">
                                                                <input type="text" id="exportedTimeStamp" name="exportedTimeStamp" class="form-control" value="${syncStatus.exportedTimeStamp}" readonly/>
                                                            </c:when>
                                                            <c:otherwise>
                                                                <input type="text" id="exportedTimeStamp" name="exportedTimeStamp" class="form-control" value="" readonly/>
                                                            </c:otherwise>
                                                        </c:choose>

                                                    </div>
                                                </div>
                                            </div>
                                            <br>
                                            <input type="hidden" name="group" value="DETAILS"/>
                                            <a href="/pim/syncStatuses"><button type="button" class="btn btn-danger">Cancel</button></a>
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

