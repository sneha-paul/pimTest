<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<div class="row clearfix">
    <div class="col-lg-12 col-md-12">
        <div class="card">

            <div class="body">
                <div class="row p-b-25">

                    <div class="col-lg-12 col-md-12"><div class="pull-right"><a href="/pim/categories/create"><button type="button" class="btn btn-success"><i class="fa fa-plus"></i> <span class="p-l-5">Create Category</span></button></a></div></div>
                </div>
                <div class="table-responsive">
                    <table id="paginatedTable" class="table table-hover dataTable table-custom m-b-0">
                        <thead class="thead-dark">
                       <%-- <tr>
                            <th>Category Name</th>
                            <th>Category ID</th>
                            <th>Status</th>
                            <th>Action</th>
                        </tr>--%>
                        </thead>
                       <%-- <tbody>
                        <c:forEach items="${categories}" var="category">
                            <tr>
                                <td class="catalog-title">
                                    <h6>${category.categoryName}</h6>
                                </td>
                                <td>${category.categoryId}</td>
                                <td><c:choose><c:when test="${category.active eq 'Y'}"><span class="badge badge-success">Active</span><</c:when><c:otherwise><span class="badge badge-danger">Inactive</span><</c:otherwise></c:choose>/td>
                                <td class="project-actions">
                                    <a href="/pim/categories/${category.categoryId}" class="btn btn-sm btn-outline-success" title="Details"><i class="icon-eye"></i></a>
                                    <a href="javascript:void(0);" class="btn btn-sm btn-outline-primary" title="clone"><i class="icon-docs"></i></a>
                                    <a href="javascript:void(0);" class="btn btn-sm btn-outline-danger js-sweetalert" title="Disable/Enable" data-type="confirm"><i class="icon-ban"></i></a>
                                </td>
                            </tr>
                        </c:forEach>
                        </tbody>--%>
                    </table>
                </div>
            </div>
        </div>
    </div>
</div>
<script>
    $( document ).ready(function() {
        $.initDataTable({
            selector: '#paginatedTable',
            name: 'categories',
            url: '/pim/categories/',
            columns: [
                { data: 'externalId', name : 'externalId', title : 'Category ID' },
                { data: 'categoryName', name : 'categoryName' , title : 'Category Name'},
                { data: 'active', name : 'active' , title : 'Status', orderable: false},
                { data: 'actions', name : 'actions' , title : 'Actions', orderable: false}
            ]
        });
//        $('#paginatedTable').dataTable().fnSetFilteringEnterPress();
    });
</script>