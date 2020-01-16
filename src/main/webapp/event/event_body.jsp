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
                                        <form method="post" action="/pim/events/${event.id}" data-method="PUT" data-success-message='["Successfully updated the event", "Event Updated"]' data-error-message='["Correct the validation error and try again", "Invalid Data"]'>
                                            <div class="row">
                                                <div class="col-md-6 col-sm-12">
                                                    <div class="form-group">User</label><code class="highlighter-rouge m-l-10">*</code>
                                                        <input type="text" id="userName" name="userName" value="${event.user}" class="form-control" readonly/>
                                                    </div>
                                                    <div class="form-group js-external-id">
                                                        <label for="timeStamp">Time Stamp</label><code class="highlighter-rouge m-l-10">*</code>
                                                        <input type="text" id="timeStamp" name="timeStamp" class="form-control" value="${event.timeStamp}" readonly/>
                                                    </div>
                                                    <div class="form-group">
                                                        <label for="message">Message</label><code class="highlighter-rouge m-l-10">*</code>
                                                        <textarea type="text" id="message" name="message" class="form-control" disabled>${event.details}</textarea>
                                                    </div>
                                                    <div class="form-group">
                                                        <label for="entity">Entity</label><code class="highlighter-rouge m-l-10">*</code>
                                                        <input type="text" id="entity" name="entity" class="form-control" value="${event.entity}" readonly/>
                                                    </div>
                                                    <div class="form-group">
                                                        <label for="eventType">Event Type</label><code class="highlighter-rouge m-l-10">*</code>
                                                        <input type="text" id="eventType" name="eventType" class="form-control" value="${event.eventType}" readonly/>
                                                    </div>
                                                </div>
                                            </div>
                                            <br>
                                            <input type="hidden" name="group" value="DETAILS"/>
                                            <a href="/pim/events"><button type="button" class="btn btn-danger">Cancel</button></a>
                                        </form>
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

