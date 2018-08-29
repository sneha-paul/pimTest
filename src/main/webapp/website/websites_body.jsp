<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<div class="row clearfix">
    <div class="col-lg-12 col-md-12">
        <div class="card">

            <div class="body">
                <div class="row p-b-25">
                    <div class="col-lg-12 col-md-12"><div class="pull-right"><a href="/pim/websites/create"><button type="button" class="btn btn-success"><i class="fa fa-plus"></i> <span class="p-l-5">Create Website</span></button></a></div></div>
                </div>
                <div class="table-responsive">
                    <table class="table table-hover dataTable table-custom m-b-0">
                        <thead class="thead-dark">
                        <tr>
                            <th>Website Name</th>
                            <th>Website ID</th>
                            <th>Status</th>
                            <th>Action</th>
                        </tr>
                        </thead>
                        <tbody>
                        <c:forEach items="${websites}" var="website">
                            <tr>
                                <td class="project-title">
                                    <h6>${website.websiteName}</h6>
                                    <small>${website.url}</small>
                                </td>
                                <td>${website.websiteId}</td>
                                <td><c:choose><c:when test="${website.active eq 'Y'}"><span class="badge badge-success">Active</span><</c:when><c:otherwise><span class="badge badge-danger">Inactive</span><</c:otherwise></c:choose>/td>
                                <td class="project-actions">
                                    <a href="/pim/websites/${website.websiteId}" class="btn btn-sm btn-outline-success" title="Details"><i class="icon-eye"></i></a>
                                    <a href="javascript:void(0);" class="btn btn-sm btn-outline-primary" title="clone"><i class="icon-docs"></i></a>
                                    <a href="javascript:void(0);" class="btn btn-sm btn-outline-danger js-sweetalert" title="Disable/Enable" data-type="confirm"><i class="icon-ban"></i></a>
                                </td>
                            </tr>
                        </c:forEach>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    </div>
</div>