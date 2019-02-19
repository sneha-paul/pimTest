<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<div class="row clearfix">
    <div class="col-lg-12 col-md-12">
        <div class="card">
            <div class="header">
                <h2>${config.parameterName} <small><code class="highlighter-rouge">${config.parameterName}</code></small><small class="pull-right m-t--15"><code style="color:#808080">_id: ${config.id}</code></small></h2>
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
                                        <form method="post" action="/pim/configs/${config.parameterName}" data-method="PUT" data-success-message='["Successfully updated the config", "Config Updated"]' data-error-message='["Correct the validation error and try again", "Invalid Data"]'>
                                            <div class="row">
                                                <div class="col-md-6 col-sm-12">
                                                    <div class="form-group">
                                                        <label for="parameterName">Parameter Name</label><code class="highlighter-rouge m-l-10">*</code>
                                                        <input type="text" id="parameterName" name="parameterName" value="${config.parameterName}" class="form-control" />
                                                    </div>
                                                    <div class="form-group ">
                                                        <label for="parameterValue">Parameter Value</label><code class="highlighter-rouge m-l-10">*</code>
                                                        <input type="text" id="parameterValue" name="parameterValue" class="form-control" value="${config.parameterValue}" />
                                                    </div>
                                                    <div class="form-group ">
                                                        <label for="parameterLabel">Parameter Label</label><code class="highlighter-rouge m-l-10">*</code>
                                                        <input type="text" id="parameterLabel" name="parameterLabel" class="form-control" value="${config.parameterLabel}" />
                                                    </div>
                                                    <div class="form-group">
                                                        <label>Status</label>
                                                        <br/>
                                                        <label for="active" class="fancy-checkbox">
                                                            <input type="checkbox" id="active" name="active" value="Y" <c:if test="${config.active eq 'Y'}">checked="checked"</c:if>>
                                                            <span>Active</span>
                                                        </label>
                                                    </div>
                                                </div>
                                            </div>
                                            <br>
                                            <input type="hidden" name="group" value="DETAILS"/>
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
<script>
    $.initPage({
        'configId' : '${config.parameterName}'
    });
</script>

