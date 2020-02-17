<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<div class="row clearfix">
    <div class="col-lg-12 col-md-12">
        <div class="card">
            <div class="header">
                <h2>${attributeName} <small><code class="highlighter-rouge">${attributeId}</code></small><small class="pull-right m-t--15"><code style="color:#808080">_website id: ${website.id}</code></small></h2>
            </div>
            <div class="body">
                <ul class="nav nav-tabs-new2">
                    <li class="nav-item"><a class="nav-link active show" data-toggle="tab" href="#details">Details</a></li>
                </ul>
                <div class="tab-content">
                    <div class="tab-pane show active" id="details">
                        <div class="row clearfix m-t-20">
                            <div class="col-lg-12 col-md-12">
                                <form method="post" action="/pim/websites/${website.websiteId}/pages/${websitePage.pageId}/attributes/${attributeId}" data-method="PUT"
                                      data-success-message='["Successfully updated the page attribute", "Page Attribute Updated"]'
                                      data-error-message='["Correct the validation error and try again", "Invalid Data"]'>
                                    <div class="row">
                                        <div class="col-md-6 col-sm-12">
                                            <div class="form-group">
                                                <label>Attribute ID</label>
                                                <input type="text" name="attributeId" value="${attributeId}" class="form-control" readonly/>
                                            </div>
                                            <div class="form-group">
                                                <label>Attribute Name</label>
                                                <input type="text" name="attributeName" value="${attributeName}" class="form-control"/>
                                            </div>
                                            <div class="form-group">
                                                <label>Attribute Value</label>
                                                <input type="text" name="attributeValue" value="${attributeValue}" class="form-control"/>
                                            </div>
                                        </div>
                                    </div>
                                    <br>
                                    <input type="hidden" name="group" value="PAGE-ATTRIBUTES"/>
                                    <button type="submit" class="btn btn-primary" onclick="$.submitAction(event, this)">Save
                                    </button>
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

