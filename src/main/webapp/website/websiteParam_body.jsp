<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<div class="row clearfix">
    <div class="col-lg-12 col-md-12">
        <div class="card">
            <div class="header">
                <h2>${name}</h2>
            </div>
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
                                        <form method="post" action="/pim/websites/${website.websiteId}/config/${configId}/param/${caseName}" data-method="PUT" data-success-message='["Successfully updated the parameters", "Parameter Updated"]' data-error-message='["Correct the validation error and try again", "Invalid Data"]'>
                                            <div class="row">
                                                <div class="col-md-6 col-sm-12">
                                                    <div class="form-group">
                                                        <label for="websiteId">Website</label><code class="highlighter-rouge m-l-10">*</code>
                                                        <input type="text" id="websiteId" name="websiteId" class="form-control" value="${website.websiteId}" readonly/>
                                                    </div>
                                                    <div class="form-group">
                                                        <label for="configId">Config</label><code class="highlighter-rouge m-l-10">*</code>
                                                        <input type="text" id="configId" name="configId" class="form-control" value="${configId}" readonly/>
                                                    </div>
                                                    <div class="form-group js-name">
                                                        <label for="paramName">Case Preserved Parameter Name</label><code class="highlighter-rouge m-l-10">*</code>
                                                        <input type="text" id="paramName" name="paramName" value="${caseName}" class="form-control" />
                                                    </div>
                                                    <div class="form-group js-external-id">
                                                        <label for="paramNameCap">Parameter Name</label><code class="highlighter-rouge m-l-10">*</code>
                                                        <input type="text" id="paramNameCap" name="paramNameCap" value="${name}" class="form-control" />
                                                    </div>
                                                    <div class="form-group">
                                                        <label for="paramValue">Parameter Value</label><code class="highlighter-rouge m-l-10">*</code>
                                                        <input type="text" id="paramValue" name="paramValue" class="form-control" value="${value}"/>
                                                    </div>
                                                </div>
                                            </div>
                                            <br>
                                            <input type="hidden" name="group" value="PARAMS"/>
                                            <button type="submit" class="btn btn-primary" onclick="$.submitAction(event, this)">Save</button>
                                            <a href="${breadcrumbs.backURL}"><button type="button" class="btn btn-danger">Cancel</button></a>
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